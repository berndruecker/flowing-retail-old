'use strict';

var Writer = require('../../lib/writer'),
    Helper = require('../helper');

var _ = require('lodash');


describe('Writer', function() {

  var createModel = Helper.createModelBuilder('test/fixtures/model/');

  function createWriter(model, options) {
    return new Writer(_.extend({ preamble: false }, options || {}));
  }


  describe('should export', function() {

    describe('base', function() {

      var model = createModel([ 'properties' ]);

      it('should write xml preamble', function() {
        // given
        var writer = new Writer({ preamble: true });
        var root = model.create('props:Root');

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<?xml version="1.0" encoding="UTF-8"?>\n' +
          '<props:root xmlns:props="http://properties" />');
      });
    });


    describe('datatypes', function() {

      var datatypesModel = createModel(['datatype', 'datatype-external', 'datatype-aliased']);

      it('via xsi:type', function() {

        // given
        var writer = createWriter(datatypesModel);

        var root = datatypesModel.create('dt:Root');

        root.set('bounds', datatypesModel.create('dt:Rect', { y: 100 }));

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<dt:root xmlns:dt="http://datatypes" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<dt:bounds xsi:type="dt:Rect" y="100" />' +
          '</dt:root>');
      });


      it('via xsi:type / no namespace', function() {

        // given
        var writer = createWriter(datatypesModel);

        var root = datatypesModel.create('dt:Root', { xmlns: 'http://datatypes' });

        root.set('bounds', datatypesModel.create('dt:Rect', { y: 100 }));

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<root xmlns="http://datatypes" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<bounds xsi:type="Rect" y="100" />' +
          '</root>');
      });


      it('via xsi:type / other namespace', function() {

        // given
        var writer = createWriter(datatypesModel);

        var root = datatypesModel.create('dt:Root', { 'xmlns:a' : 'http://datatypes' });

        root.set('bounds', datatypesModel.create('dt:Rect', { y: 100 }));

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<a:root xmlns:a="http://datatypes" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<a:bounds xsi:type="a:Rect" y="100" />' +
          '</a:root>');
      });


      it('via xsi:type / in collection / other namespace)', function() {

        // given
        var writer = createWriter(datatypesModel);

        var root = datatypesModel.create('dt:Root');

        var otherBounds = root.get('otherBounds');

        otherBounds.push(datatypesModel.create('dt:Rect', { y: 200 }));
        otherBounds.push(datatypesModel.create('do:Rect', { x: 100 }));

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<dt:root xmlns:dt="http://datatypes" ' +
                   'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" ' +
                   'xmlns:do="http://datatypes2">' +
            '<dt:otherBounds xsi:type="dt:Rect" y="200" />' +
            '<dt:otherBounds xsi:type="do:Rect" x="100" />' +
          '</dt:root>');
      });


      it('via xsi:type / in collection / type prefix)', function() {

        // given
        var writer = createWriter(datatypesModel);

        var root = datatypesModel.create('dt:Root');

        var otherBounds = root.get('otherBounds');

        otherBounds.push(datatypesModel.create('da:Rect', { z: 200 }));
        otherBounds.push(datatypesModel.create('dt:Rect', { y: 100 }));

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<dt:root xmlns:dt="http://datatypes" ' +
                   'xmlns:da="http://datatypes-aliased" ' +
                   'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<dt:otherBounds xsi:type="da:tRect" z="200" />' +
            '<dt:otherBounds xsi:type="dt:Rect" y="100" />' +
          '</dt:root>');
      });


      it('via xsi:type / body property', function() {

        var propertiesModel = createModel([ 'properties' ]);

        // given
        var writer = createWriter(propertiesModel);

        var body = propertiesModel.create('props:SimpleBody', { body: '${ foo < bar }' });
        var root = propertiesModel.create('props:WithBody', { someBody: body });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<props:withBody xmlns:props="http://properties" ' +
                          'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<props:someBody xsi:type="props:SimpleBody">' +
              '<![CDATA[${ foo < bar }]]>' +
            '</props:someBody>' +
          '</props:withBody>');
      });


      it('via xsi:type / body property / formated', function() {

        var propertiesModel = createModel([ 'properties' ]);

        // given
        var writer = createWriter(propertiesModel, { format: true });

        var body = propertiesModel.create('props:SimpleBody', { body: '${ foo < bar }' });
        var root = propertiesModel.create('props:WithBody', { someBody: body });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<props:withBody xmlns:props="http://properties" ' +
                          'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">\n' +
          '  <props:someBody xsi:type="props:SimpleBody"><![CDATA[${ foo < bar }]]></props:someBody>\n' +
          '</props:withBody>\n');
      });

    });


    describe('attributes', function() {

      it('with line breaks', function() {

        // given
        var model = createModel([ 'properties' ]);

        var writer = createWriter(model);

        var root = model.create('props:BaseWithId', {
          id: 'FOO\nBAR'
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<props:baseWithId xmlns:props="http://properties" id="FOO&#10;BAR" />');
      });


      it('inherited', function() {

        // given
        var extendedModel = createModel([ 'properties', 'properties-extended' ]);

        var writer = createWriter(extendedModel);

        var root = extendedModel.create('ext:Root', {
          id: 'FOO'
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<ext:root xmlns:ext="http://extended" id="FOO" />');
      });


      it('extended', function() {

        // given
        var extendedModel = createModel([ 'extension/base', 'extension/custom' ]);

        var writer = createWriter(extendedModel);

        var root = extendedModel.create('b:SubRoot', {
          customAttr: 1,
          subAttr: 'FOO',
          ownAttr: 'OWN'
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
            '<b:SubRoot xmlns:b="http://base" ' +
                       'xmlns:c="http://custom" ' +
                       'ownAttr="OWN" ' +
                       'c:customAttr="1" ' +
                       'subAttr="FOO" />');
      });


      it('ignore undefined attribute values', function() {

        // given
        var model = createModel([ 'properties' ]);

        var writer = createWriter(model);

        var root = model.create('props:Base', {
          id: undefined
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<props:base xmlns:props="http://properties" />');
      });


      it('ignore null attribute values', function() {

        // given
        var model = createModel([ 'properties' ]);

        var writer = createWriter(model);

        var root = model.create('props:Base', {
          id: null
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<props:base xmlns:props="http://properties" />');
      });

    });


    describe('simple properties', function() {

      var model = createModel([ 'properties' ]);

      it('attribute', function() {

        // given
        var writer = createWriter(model);

        var attributes = model.create('props:Attributes', { integerValue: 1000 });

        // when
        var xml = writer.toXML(attributes);

        // then
        expect(xml).to.eql('<props:attributes xmlns:props="http://properties" integerValue="1000" />');
      });


      it('attribute, escaping special characters', function() {

        // given
        var writer = createWriter(model);

        var complex = model.create('props:Complex', { id: '<>\n&' });

        // when
        var xml = writer.toXML(complex);

        // then
        expect(xml).to.eql('<props:complex xmlns:props="http://properties" id="&#60;&#62;&#10;&#38;" />');
      });


      it('write integer property', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:SimpleBodyProperties', {
          intValue: 5
        });

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<props:simpleBodyProperties xmlns:props="http://properties">' +
            '<props:intValue>5</props:intValue>' +
          '</props:simpleBodyProperties>';

        // then
        expect(xml).to.eql(expectedXml);
      });


      it('write boolean property', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:SimpleBodyProperties', {
          boolValue: false
        });

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<props:simpleBodyProperties xmlns:props="http://properties">' +
            '<props:boolValue>false</props:boolValue>' +
          '</props:simpleBodyProperties>';

        // then
        expect(xml).to.eql(expectedXml);
      });


      it('write string isMany property', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:SimpleBodyProperties', {
          str: [ 'A', 'B', 'C' ]
        });

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<props:simpleBodyProperties xmlns:props="http://properties">' +
            '<props:str>A</props:str>' +
            '<props:str>B</props:str>' +
            '<props:str>C</props:str>' +
          '</props:simpleBodyProperties>';

        // then
        expect(xml).to.eql(expectedXml);
      });

    });


    describe('embedded properties',  function() {

      var model = createModel([ 'properties' ]);

      var extendedModel = createModel([ 'properties', 'properties-extended' ]);

      it('single', function() {

        // given
        var writer = createWriter(model);

        var complexCount = model.create('props:ComplexCount', { id: 'ComplexCount_1' });
        var embedding = model.create('props:Embedding', { embeddedComplex: complexCount });

        // when
        var xml = writer.toXML(embedding);

        var expectedXml =
          '<props:embedding xmlns:props="http://properties">' +
            '<props:complexCount id="ComplexCount_1" />' +
          '</props:embedding>';

        // then
        expect(xml).to.eql(expectedXml);
      });

      it('property name', function() {

        // given
        var writer = createWriter(model);

        var propertyValue = model.create('props:BaseWithId', { id: 'PropertyValue' });
        var container = model.create('props:WithProperty', { propertyName: propertyValue });

        // when
        var xml = writer.toXML(container);

        var expectedXml =
          '<props:withProperty xmlns:props="http://properties">' +
            '<props:propertyName id="PropertyValue" />' +
          '</props:withProperty>';

        // then
        expect(xml).to.eql(expectedXml);
      });


      it('collection', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:Root');

        var attributes = model.create('props:Attributes', { id: 'Attributes_1' });
        var simpleBody = model.create('props:SimpleBody');
        var containedCollection = model.create('props:ContainedCollection');

        var any = root.get('any');

        any.push(attributes);
        any.push(simpleBody);
        any.push(containedCollection);

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<props:root xmlns:props="http://properties">' +
            '<props:attributes id="Attributes_1" />' +
            '<props:simpleBody />' +
            '<props:containedCollection />' +
          '</props:root>';

        // then
        expect(xml).to.eql(expectedXml);
      });


      it('collection / different ns', function() {

        // given
        var writer = createWriter(extendedModel);

        var root = extendedModel.create('ext:Root');

        var attributes1 = extendedModel.create('props:Attributes', { id: 'Attributes_1' });
        var attributes2 = extendedModel.create('props:Attributes', { id: 'Attributes_2' });
        var extendedComplex = extendedModel.create('ext:ExtendedComplex', { numCount: 100 });

        var any = root.get('any');

        any.push(attributes1);
        any.push(attributes2);
        any.push(extendedComplex);

        var elements = root.get('elements');
        elements.push(extendedModel.create('ext:Base'));

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<ext:root xmlns:ext="http://extended" xmlns:props="http://properties">' +
            '<props:attributes id="Attributes_1" />' +
            '<props:attributes id="Attributes_2" />' +
            '<ext:extendedComplex numCount="100" />' +
            '<ext:base />' +
          '</ext:root>';

        // then
        expect(xml).to.eql(expectedXml);
      });

    });


    describe('virtual properties', function() {

      var model = createModel([ 'virtual' ]);

      it('should not serialize virtual property', function() {
        // given
        var writer = createWriter(model);

        var root = model.create('virt:Root', {
          child: model.create('virt:Child')
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<virt:Root xmlns:virt="http://virtual" />');
      });

    });


    describe('body text', function() {

      var model = createModel([ 'properties' ]);

      it('write body text property', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:SimpleBody', {
          body: 'textContent'
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<props:simpleBody xmlns:props="http://properties">textContent</props:simpleBody>');
      });


      it('write body CDATA property', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:SimpleBody', {
          body: '<h2>HTML markup</h2>'
        });

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<props:simpleBody xmlns:props="http://properties">' +
            '<![CDATA[<h2>HTML markup</h2>]]>' +
          '</props:simpleBody>';

        // then
        expect(xml).to.eql(expectedXml);
      });


      it('write body CDATA property in subsequent calls', function() {

        // given
        var writer = createWriter(model);

        var root1 = model.create('props:SimpleBody', {
          body: '<>'
        });
        var root2 = model.create('props:SimpleBody', {
          body: '<>'
        });

        // when
        var xml1 = writer.toXML(root1);
        var xml2 = writer.toXML(root2);

        var expectedXml =
          '<props:simpleBody xmlns:props="http://properties">' +
            '<![CDATA[<>]]>' +
          '</props:simpleBody>';

        // then
        expect(xml1).to.eql(expectedXml);
        expect(xml2).to.eql(expectedXml);
      });


      it('write body CDATA property with special chars', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:SimpleBody', {
          body: '&\n<>'
        });

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<props:simpleBody xmlns:props="http://properties">' +
            '<![CDATA[&\n<>]]>' +
          '</props:simpleBody>';

        // then
        expect(xml).to.eql(expectedXml);
      });

    });


    describe('alias', function() {

      var model = createModel([ 'properties' ]);

      var noAliasModel = createModel(['noalias']);

      it('lowerCase', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:Root');

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<props:root xmlns:props="http://properties" />');
      });


      it('none', function() {

        // given
        var writer = createWriter(noAliasModel);

        var root = noAliasModel.create('na:Root');

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<na:Root xmlns:na="http://noalias" />');
      });
    });


    describe('ns', function() {

      var model = createModel([ 'properties' ]);
      var extendedModel = createModel([ 'properties', 'properties-extended' ]);

      it('single package', function() {

        // given
        var writer = createWriter(model);

        var root = model.create('props:Root');

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<props:root xmlns:props="http://properties" />');
      });


      it('multiple packages', function() {

        // given
        var writer = createWriter(extendedModel);

        var root = extendedModel.create('props:Root');

        root.get('any').push(extendedModel.create('ext:ExtendedComplex'));

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<props:root xmlns:props="http://properties" ' +
                      'xmlns:ext="http://extended">' +
            '<ext:extendedComplex />' +
          '</props:root>';

        // then
        expect(xml).to.eql(expectedXml);
      });


      it('default ns', function() {

        // given
        var writer = createWriter(extendedModel);

        var root = extendedModel.create('props:Root', { xmlns: 'http://properties' });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<root xmlns="http://properties" />');
      });


      it('default ns / attributes', function() {

        // given
        var writer = createWriter(extendedModel);

        var root = extendedModel.create('props:Root', { xmlns: 'http://properties', id: 'Root' });

        var any = root.get('any');
        any.push(extendedModel.create('ext:ExtendedComplex'));
        any.push(extendedModel.create('props:Attributes', { id: 'Attributes_2' }));

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml)
          .to.eql('<root xmlns="http://properties" xmlns:ext="http://extended" id="Root">' +
                     '<ext:extendedComplex />' +
                     '<attributes id="Attributes_2" />' +
                   '</root>');
      });


      it('default ns / extension attributes', function() {

        // given
        var writer = createWriter(extendedModel);

        var root = extendedModel.create('props:Root', {
          xmlns: 'http://properties',
          'xmlns:foo': 'http://fooo',
          id: 'Root',
          'foo:bar': 'BAR'
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<root xmlns="http://properties" xmlns:foo="http://fooo" id="Root" foo:bar="BAR" />');
      });


      it('explicit ns / attributes', function() {

        // given
        var writer = createWriter(extendedModel);

        var root = extendedModel.create('props:Root', { 'xmlns:foo': 'http://properties', id: 'Root' });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<foo:root xmlns:foo="http://properties" id="Root" />');
      });

    });


    describe('reference', function() {

      var model = createModel([ 'properties' ]);

      it('single', function() {

        // given
        var writer = createWriter(model);

        var complex = model.create('props:Complex', { id: 'Complex_1' });
        var referencingSingle = model.create('props:ReferencingSingle', { referencedComplex: complex });

        // when
        var xml = writer.toXML(referencingSingle);

        // then
        expect(xml).to.eql('<props:referencingSingle xmlns:props="http://properties" referencedComplex="Complex_1" />');
      });


      it('collection', function() {

        // given
        var writer = createWriter(model);

        var complexCount = model.create('props:ComplexCount', { id: 'ComplexCount_1' });
        var complexNesting = model.create('props:ComplexNesting', { id: 'ComplexNesting_1' });

        var referencingCollection = model.create('props:ReferencingCollection', {
          references: [ complexCount, complexNesting ]
        });

        // when
        var xml = writer.toXML(referencingCollection);

        // then
        expect(xml).to.eql(
          '<props:referencingCollection xmlns:props="http://properties">' +
            '<props:references>ComplexCount_1</props:references>' +
            '<props:references>ComplexNesting_1</props:references>' +
          '</props:referencingCollection>');
      });


      it('attribute collection', function() {

        // given
        var writer = createWriter(model);

        var complexCount = model.create('props:ComplexCount', { id: 'ComplexCount_1' });
        var complexNesting = model.create('props:ComplexNesting', { id: 'ComplexNesting_1' });

        var attrReferenceCollection = model.create('props:AttributeReferenceCollection', {
          refs: [ complexCount, complexNesting ]
        });

        // when
        var xml = writer.toXML(attrReferenceCollection);

        // then
        expect(xml).to.eql('<props:attributeReferenceCollection xmlns:props="http://properties" refs="ComplexCount_1 ComplexNesting_1" />');
      });

    });


    it('redefined properties', function() {

      // given
      var model = createModel([ 'redefine' ]);

      var writer = createWriter(model);

      var element = model.create('r:Extension', {
        id: 1,
        name: 'FOO',
        value: 'BAR'
      });

      var expectedXml = '<r:Extension xmlns:r="http://redefine">' +
                          '<r:id>1</r:id>' +
                          '<r:name>FOO</r:name>' +
                          '<r:value>BAR</r:value>' +
                        '</r:Extension>';

      // when
      var xml = writer.toXML(element);

      // then
      expect(xml).to.eql(expectedXml);
    });


    it('replaced properties', function() {

      // given
      var model = createModel([ 'replace' ]);

      var writer = createWriter(model);

      var element = model.create('r:Extension', {
        id: 1,
        name: 'FOO',
        value: 'BAR'
      });

      var expectedXml = '<r:Extension xmlns:r="http://replace">' +
                          '<r:name>FOO</r:name>' +
                          '<r:value>BAR</r:value>' +
                          '<r:id>1</r:id>' +
                        '</r:Extension>';

      // when
      var xml = writer.toXML(element);

      // then
      expect(xml).to.eql(expectedXml);
    });

  });


  describe('extension handling', function() {

    var extensionModel = createModel([ 'extensions' ]);


    describe('attributes', function() {

      it('should write xsi:schemaLocation', function() {

        // given
        var writer = createWriter(extensionModel);

        var root = extensionModel.create('e:Root', {
          'xsi:schemaLocation': 'http://fooo ./foo.xsd'
        });

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<e:root xmlns:e="http://extensions" ' +
                  'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" ' +
                  'xsi:schemaLocation="http://fooo ./foo.xsd" />';

        // then
        expect(xml).to.eql(expectedXml);
      });


      it('should write extension attributes', function() {

        // given
        var writer = createWriter(extensionModel);

        var root = extensionModel.create('e:Root', {
          'xmlns:foo': 'http://fooo',
          'foo:bar': 'BAR'
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<e:root xmlns:e="http://extensions" xmlns:foo="http://fooo" foo:bar="BAR" />');
      });


      it('should write manually added custom namespace', function() {

        // given
        var writer = createWriter(extensionModel);

        var root = extensionModel.create('e:Root', {
          'xmlns:foo': 'http://fooo'
        });

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<e:root xmlns:e="http://extensions" ' +
                  'xmlns:foo="http://fooo" />';

        // then
        expect(xml).to.eql(expectedXml);
      });


      it('should ignore unknown namespace prefix', function() {

        // given
        var writer = createWriter(extensionModel);

        var root = extensionModel.create('e:Root', {
          'foo:bar': 'BAR'
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql('<e:root xmlns:e="http://extensions" />');
      });

    });


    describe('elements', function() {

      it('should write self-closing extension elements', function() {

        // given
        var writer = createWriter(extensionModel);

        var meta1 = extensionModel.createAny('other:meta', 'http://other', {
          key: 'FOO',
          value: 'BAR'
        });

        var meta2 = extensionModel.createAny('other:meta', 'http://other', {
          key: 'BAZ',
          value: 'FOOBAR'
        });

        var root = extensionModel.create('e:Root', {
          id: 'FOO',
          extensions: [ meta1, meta2 ]
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<e:root xmlns:e="http://extensions" xmlns:other="http://other">' +
            '<e:id>FOO</e:id>' +
            '<other:meta key="FOO" value="BAR" />' +
            '<other:meta key="BAZ" value="FOOBAR" />' +
          '</e:root>');
      });


      it('should write extension element body', function() {

        // given
        var writer = createWriter(extensionModel);

        var note = extensionModel.createAny('other:note', 'http://other', {
          $body: 'a note'
        });

        var root = extensionModel.create('e:Root', {
          id: 'FOO',
          extensions: [ note ]
        });

        // when
        var xml = writer.toXML(root);

        // then
        expect(xml).to.eql(
          '<e:root xmlns:e="http://extensions" xmlns:other="http://other">' +
            '<e:id>FOO</e:id>' +
            '<other:note>' +
              'a note' +
            '</other:note>' +
          '</e:root>');
      });


      it('should write nested extension element', function() {

        // given
        var writer = createWriter(extensionModel);

        var meta1 = extensionModel.createAny('other:meta', 'http://other', {
          key: 'k1',
          value: 'v1'
        });

        var meta2 = extensionModel.createAny('other:meta', 'http://other', {
          key: 'k2',
          value: 'v2'
        });

        var additionalNote = extensionModel.createAny('other:additionalNote', 'http://other', {
          $body: 'this is some text'
        });

        var nestedMeta = extensionModel.createAny('other:nestedMeta', 'http://other', {
          $children: [ meta1, meta2, additionalNote ]
        });

        var root = extensionModel.create('e:Root', {
          id: 'FOO',
          extensions: [ nestedMeta ]
        });

        // when
        var xml = writer.toXML(root);

        var expectedXml =
          '<e:root xmlns:e="http://extensions" xmlns:other="http://other">' +
            '<e:id>FOO</e:id>' +
            '<other:nestedMeta>' +
              '<other:meta key="k1" value="v1" />' +
              '<other:meta key="k2" value="v2" />' +
              '<other:additionalNote>' +
                'this is some text' +
              '</other:additionalNote>' +
            '</other:nestedMeta>' +
          '</e:root>';

        // then
        expect(xml).to.eql(expectedXml);
      });
    });

  });


  describe('qualified extensions', function() {

    var extensionModel = createModel([ 'extension/base', 'extension/custom' ]);


    it('should write typed extension property', function() {

      // given
      var writer = createWriter(extensionModel);

      var customGeneric = extensionModel.create('c:CustomGeneric', { count: 10 });

      var root = extensionModel.create('b:Root', {
        generic: customGeneric
      });

      // when
      var xml = writer.toXML(root);

      var expectedXml =
        '<b:Root xmlns:b="http://base" xmlns:c="http://custom">' +
          '<c:CustomGeneric count="10" />' +
        '</b:Root>';

      // then
      expect(xml).to.eql(expectedXml);
    });


    it('should write typed extension attribute', function() {

      // given
      var writer = createWriter(extensionModel);

      var root = extensionModel.create('b:Root', { customAttr: 666 });

      // when
      var xml = writer.toXML(root);

      var expectedXml =
        '<b:Root xmlns:b="http://base" xmlns:c="http://custom" c:customAttr="666" />';

      // then
      expect(xml).to.eql(expectedXml);
    });


    it('should write generic collection', function() {

      // given
      var writer = createWriter(extensionModel);

      var property1 = extensionModel.create('c:Property', { key: 'foo', value: 'FOO' });
      var property2 = extensionModel.create('c:Property', { key: 'bar', value: 'BAR' });

      var any = extensionModel.createAny('other:Xyz', 'http://other', {
        $body: 'content'
      });

      var root = extensionModel.create('b:Root', {
        genericCollection: [ property1, property2, any ]
      });

      var xml = writer.toXML(root);

      var expectedXml =
        '<b:Root xmlns:b="http://base" xmlns:c="http://custom" ' +
                'xmlns:other="http://other">' +
          '<c:Property key="foo" value="FOO" />' +
          '<c:Property key="bar" value="BAR" />' +
          '<other:Xyz>content</other:Xyz>' +
        '</b:Root>';

      // then
      expect(xml).to.eql(expectedXml);

    });

  });

});
