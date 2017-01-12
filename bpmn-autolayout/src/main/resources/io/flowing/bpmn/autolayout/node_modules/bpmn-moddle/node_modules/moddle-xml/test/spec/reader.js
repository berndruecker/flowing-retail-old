'use strict';

var Reader = require('../../lib/reader'),
    Helper = require('../helper'),
    readFile = Helper.readFile,
    createModelBuilder = Helper.createModelBuilder;


describe('Reader', function() {

  var createModel = createModelBuilder('test/fixtures/model/');


  describe('api', function() {

    var model = createModel([ 'properties' ]);

    it('should provide result with context', function(done) {

      // given
      var reader = new Reader(model);
      var rootHandler = reader.handler('props:ComplexAttrs');

      var xml = '<props:complexAttrs xmlns:props="http://properties"></props:complexAttrs>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result, context) {

        // then
        expect(err).not.to.exist;

        expect(result).to.exist;
        expect(context).to.exist;

        done();
      });
    });


    it('should provide error with context', function(done) {

      // given
      var reader = new Reader(model);
      var rootHandler = reader.handler('props:ComplexAttrs');

      // when
      reader.fromXML('this-is-garbage', rootHandler, function(err, result, context) {

        // then
        expect(err).to.exist;

        expect(result).not.to.exist;
        expect(context).to.exist;

        done();
      });
    });

  });


  describe('should import', function() {

    var model = createModel([ 'properties' ]);
    var extendedModel = createModel([ 'properties', 'properties-extended' ]);

    describe('data types', function() {

      it('simple', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:ComplexAttrs');

        var xml = '<props:complexAttrs xmlns:props="http://properties">' +
                    '<props:attrs integerValue="10" />' +
                  '</props:complexAttrs>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'props:ComplexAttrs',
            attrs: {
              $type: 'props:Attributes',
              integerValue: 10
            }
          });

          done(err);
        });
      });


      it('simple / xsi:type', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:ComplexAttrs');

        var xml = '<props:complexAttrs xmlns:props="http://properties" ' +
                                      'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
                    '<props:attrs xsi:type="props:SubAttributes" integerValue="10" />' +
                  '</props:complexAttrs>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'props:ComplexAttrs',
            attrs: {
              $type: 'props:SubAttributes',
              integerValue: 10
            }
          });

          done(err);
        });
      });


      it('simple / xsi:type / default ns', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:ComplexAttrs');

        var xml = '<complexAttrs xmlns="http://properties" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
                    '<attrs xsi:type="SubAttributes" integerValue="10" />' +
                  '</complexAttrs>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'props:ComplexAttrs',
            attrs: {
              $type: 'props:SubAttributes',
              integerValue: 10
            }
          });

          done(err);
        });
      });


      it('simple / xsi:type / different ns prefix', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:ComplexAttrs');

        var xml = '<a:complexAttrs xmlns:a="http://properties" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
                    '<a:attrs xsi:type="a:SubAttributes" integerValue="10" />' +
                  '</a:complexAttrs>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'props:ComplexAttrs',
            attrs: {
              $type: 'props:SubAttributes',
              integerValue: 10
            }
          });

          done(err);
        });
      });


      it('collection / no xsi:type', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:ComplexAttrsCol');

        var xml = '<props:complexAttrsCol xmlns:props="http://properties">' +
                    '<props:attrs integerValue="10" />' +
                    '<props:attrs booleanValue="true" />' +
                  '</props:complexAttrsCol>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'props:ComplexAttrsCol',
            attrs: [
              { $type: 'props:Attributes', integerValue: 10 },
              { $type: 'props:Attributes', booleanValue: true }
            ]
          });

          done(err);
        });
      });


      it('collection / xsi:type / from other namespace)', function(done) {

        var datatypeModel = createModel(['datatype', 'datatype-external']);

        // given
        var reader = new Reader(datatypeModel);
        var rootHandler = reader.handler('dt:Root');

        var xml =
          '<dt:root xmlns:dt="http://datatypes" xmlns:do="http://datatypes2" ' +
                   'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<dt:otherBounds xsi:type="dt:Rect" y="100" />' +
            '<dt:otherBounds xsi:type="do:Rect" x="200" />' +
          '</dt:root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'dt:Root',
            otherBounds: [
              { $type: 'dt:Rect', y: 100 },
              { $type: 'do:Rect', x: 200 }
            ]
          });

          done(err);
        });
      });


      it('collection / xsi:type / from other namespace / default ns)', function(done) {

        var datatypeModel = createModel(['datatype', 'datatype-external']);

        // given
        var reader = new Reader(datatypeModel);
        var rootHandler = reader.handler('dt:Root');

        var xml =
          '<root xmlns="http://datatypes" xmlns:do="http://datatypes2" ' +
                'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<otherBounds xsi:type="Rect" y="100" />' +
            '<otherBounds xsi:type="do:Rect" x="200" />' +
          '</root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'dt:Root',
            otherBounds: [
              { $type: 'dt:Rect', y: 100 },
              { $type: 'do:Rect', x: 200 }
            ]
          });

          done(err);
        });
      });


      it('collection / xsi:type / type alias', function(done) {

        var datatypeModel = createModel(['datatype', 'datatype-aliased']);

        // given
        var reader = new Reader(datatypeModel);
        var rootHandler = reader.handler('dt:Root');

        var xml =
          '<root xmlns="http://datatypes" xmlns:da="http://datatypes-aliased" ' +
                'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<otherBounds xsi:type="dt:Rect" y="100" />' +
            '<otherBounds xsi:type="da:tRect" z="200" />' +
          '</root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'dt:Root',
            otherBounds: [
              { $type: 'dt:Rect', y: 100 },
              { $type: 'da:Rect', z: 200 }
            ]
          });

          done(err);
        });
      });


      it('collection / xsi:type / unknown type', function(done) {

        var datatypeModel = createModel([ 'datatype' ]);

        // given
        var reader = new Reader(datatypeModel);
        var rootHandler = reader.handler('dt:Root');

        var xml =
          '<root xmlns="http://datatypes" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
            '<otherBounds xsi:type="Unknown" y="100" />' +
          '</root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          expect(err).to.exist;

          expect(err.message).to.contain('unparsable content <otherBounds> detected');

          done();
        });
      });

    });


    describe('attributes', function() {

      it('with special characters', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:BaseWithId');

        var xml = '<props:baseWithId xmlns:props="http://properties" id="&#60;&#62;&#10;&#38;" />';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:BaseWithId',
            id: '<>\n&'
          });

          done(err);
        });
      });


      it('inherited', function(done) {

        // given
        var reader = new Reader(extendedModel);
        var rootHandler = reader.handler('ext:Root');

        // when
        reader.fromXML('<ext:root xmlns:ext="http://extended" id="FOO" />', rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({ $type: 'ext:Root', id: 'FOO' });

          done(err);
        });

      });

    });


    describe('simple nested properties', function() {

      it('parse boolean property', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:SimpleBodyProperties');

        var xml = '<props:simpleBodyProperties xmlns:props="http://properties">' +
                    '<props:intValue>5</props:intValue>' +
                  '</props:simpleBodyProperties>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:SimpleBodyProperties',
            intValue: 5
          });

          done(err);
        });
      });


      it('parse boolean property', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:SimpleBodyProperties');

        var xml = '<props:simpleBodyProperties xmlns:props="http://properties">' +
                    '<props:boolValue>false</props:boolValue>' +
                  '</props:simpleBodyProperties>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:SimpleBodyProperties',
            boolValue: false
          });

          done(err);
        });
      });


      it('parse string isMany prooperty', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:SimpleBodyProperties');

        var xml = '<props:simpleBodyProperties xmlns:props="http://properties">' +
                    '<props:str>A</props:str>' +
                    '<props:str>B</props:str>' +
                    '<props:str>C</props:str>' +
                  '</props:simpleBodyProperties>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:SimpleBodyProperties',
            str: [ 'A', 'B', 'C' ]
          });

          done(err);
        });
      });
    });


    describe('body text', function() {

      it('parse body text property', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:SimpleBody');

        var xml = '<props:simpleBody xmlns:props="http://properties">textContent</props:simpleBody>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:SimpleBody',
            body: 'textContent'
          });

          done(err);
        });
      });


      it('parse body CDATA property', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:SimpleBody');

        var xml = '<props:simpleBody xmlns:props="http://properties">' +
                    '<![CDATA[<h2>HTML markup</h2>]]>' +
                  '</props:simpleBody>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:SimpleBody',
            body: '<h2>HTML markup</h2>'
          });

          done(err);
        });
      });

    });


    describe('alias', function() {

      it('lowerCase', function(done) {

        // given
        var reader = new Reader(model);
        var rootHandler = reader.handler('props:Root');

        // when
        reader.fromXML('<props:root xmlns:props="http://properties" />', rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({ $type: 'props:Root' });

          done(err);
        });

      });


      it('none', function(done) {

        // given
        var noAliasModel = createModel(['noalias']);

        var reader = new Reader(noAliasModel);
        var rootHandler = reader.handler('na:Root');

        // when
        reader.fromXML('<na:Root xmlns:na="http://noalias" />', rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({ $type: 'na:Root' });

          done(err);
        });
      });

    });


    describe('reference', function() {

      it('single', function(done) {

        // given
        var reader = new Reader(extendedModel);
        var rootHandler = reader.handler('props:Root');

        var xml =
          '<props:root xmlns:props="http://properties">' +
            '<props:containedCollection id="C_5">' +
              '<props:complex id="C_1" />' +
              '<props:complex id="C_2" />' +
            '</props:containedCollection>' +
            '<props:referencingSingle id="C_4" referencedComplex="C_1" />' +
          '</props:root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:Root',
            any: [
              {
                $type: 'props:ContainedCollection',
                id: 'C_5',
                children: [
                  { $type: 'props:Complex', id: 'C_1' },
                  { $type: 'props:Complex', id: 'C_2' }
                ]
              },
              { $type: 'props:ReferencingSingle', id: 'C_4' }
            ]
          });

          var referenced = result.any[0].children[0];
          var referencingSingle = result.any[1];

          expect(referencingSingle.referencedComplex).to.equal(referenced);

          done(err);
        });
      });


      it('collection', function(done) {

        // given
        var reader = new Reader(extendedModel);
        var rootHandler = reader.handler('props:Root');

        var xml =
          '<props:root xmlns:props="http://properties">' +
            '<props:containedCollection id="C_5">' +
              '<props:complex id="C_1" />' +
              '<props:complex id="C_2" />' +
            '</props:containedCollection>' +
            '<props:referencingCollection id="C_4">' +
              '<props:references>C_2</props:references>' +
              '<props:references>C_5</props:references>' +
            '</props:referencingCollection>' +
          '</props:root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:Root',
            any: [
              {
                $type: 'props:ContainedCollection',
                id: 'C_5',
                children: [
                  { $type: 'props:Complex', id: 'C_1' },
                  { $type: 'props:Complex', id: 'C_2' }
                ]
              },
              { $type: 'props:ReferencingCollection', id: 'C_4' }
            ]
          });

          var containedCollection = result.any[0];
          var complex_c2 = containedCollection.children[1];

          var referencingCollection = result.any[1];

          expect(referencingCollection.references).to.jsonEqual([ complex_c2, containedCollection ]);

          done(err);
        });
      });


      it('attribute collection', function(done) {

        // given
        var reader = new Reader(extendedModel);
        var rootHandler = reader.handler('props:Root');

        var xml =
          '<props:root xmlns:props="http://properties">' +
            '<props:containedCollection id="C_5">' +
              '<props:complex id="C_1" />' +
              '<props:complex id="C_2" />' +
              '<props:complex id="C_3" />' +
            '</props:containedCollection>' +
            '<props:attributeReferenceCollection id="C_4" refs="C_2 C_3 C_5" />' +
          '</props:root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          // then
          expect(result).to.jsonEqual({
            $type: 'props:Root',
            any: [
              {
                $type: 'props:ContainedCollection',
                id: 'C_5',
                children: [
                  { $type: 'props:Complex', id: 'C_1' },
                  { $type: 'props:Complex', id: 'C_2' },
                  { $type: 'props:Complex', id: 'C_3' }
                ]
              },
              { $type: 'props:AttributeReferenceCollection', id: 'C_4' }
            ]
          });

          var containedCollection = result.any[0];
          var complex_c2 = containedCollection.children[1];
          var complex_c3 = containedCollection.children[2];

          var attrReferenceCollection = result.any[1];

          expect(attrReferenceCollection.refs).to.jsonEqual([ complex_c2, complex_c3, containedCollection ]);

          done(err);
        });
      });

    });

  });


  describe('internal', function() {

    var extendedModel = createModel([ 'properties', 'properties-extended' ]);

    describe('should identify references', function() {

      it('on attribute', function(done) {

        // given
        var reader = new Reader(extendedModel);
        var rootHandler = reader.handler('props:ReferencingSingle');

        var xml = '<props:referencingSingle xmlns:props="http://properties" id="C_4" referencedComplex="C_1" />';

        // when
        reader.fromXML(xml, rootHandler, function(err, result, context) {

          // then
          var expectedReference = {
            element: {
              $type: 'props:ReferencingSingle',
              id: 'C_4'
            },
            property: 'props:referencedComplex',
            id: 'C_1'
          };

          var references = context.references;

          expect(references).to.jsonEqual([ expectedReference ]);

          done(err);
        });
      });


      it('embedded', function(done) {

        // given
        var reader = new Reader(extendedModel);
        var rootHandler = reader.handler('props:ReferencingCollection');

        var xml = '<props:referencingCollection xmlns:props="http://properties" id="C_4">' +
                    '<props:references>C_2</props:references>' +
                    '<props:references>C_5</props:references>' +
                  '</props:referencingCollection>';

        reader.fromXML(xml, rootHandler, function(err, result, context) {

          var expectedTarget = {
            $type: 'props:ReferencingCollection',
            id: 'C_4'
          };

          var expectedReference1 = {
            property: 'props:references',
            id: 'C_2',
            element: expectedTarget
          };

          var expectedReference2 = {
            property: 'props:references',
            id: 'C_5',
            element: expectedTarget
          };

          var references = context.references;

          expect(references).to.jsonEqual([ expectedReference1, expectedReference2 ]);

          done(err);
        });
      });

    });

  });


  describe('error handling', function() {

    var model = createModel([ 'properties' ]);
    var extendedModel = createModel([ 'properties', 'properties-extended' ]);

    it('should handle non-xml text files', function(done) {

      var data = readFile('test/fixtures/error/no-xml.txt');

      var reader = new Reader(model);
      var rootHandler = reader.handler('props:ComplexAttrs');

      // when
      reader.fromXML(data, rootHandler, function(err, result) {

        expect(err).to.exist;
        expect(result).not.to.exist;

        done();
      });

    });


    it('should handle non-xml binary file', function(done) {

      var data = readFile('test/fixtures/error/binary.png');

      var reader = new Reader(model);
      var rootHandler = reader.handler('props:ComplexAttrs');

      // when
      reader.fromXML(data, rootHandler, function(err, result) {

        expect(err).to.exist;
        expect(result).not.to.exist;

        done();
      });

    });


    it('should handle invalid root element', function(done) {

      var xml = '<props:referencingCollection xmlns:props="http://properties" id="C_4">' +
                  '<props:references>C_2</props:references>' +
                  '<props:references>C_5</props:references>' +
                '</props:referencingCollection>';

      var reader = new Reader(model);
      var rootHandler = reader.handler('props:ComplexAttrs');

      var expectedError =
        'unparsable content <props:references> detected\n\t' +
            'line: 0\n\t' +
            'column: 88\n\t' +
            'nested error: unknown type <props:References>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        expect(err).to.exist;
        expect(err.message).to.eql(expectedError);

        expect(result).not.to.exist;

        done();
      });
    });


    it('should handle invalid child element', function(done) {

      var xml = '<props:referencingCollection xmlns:props="http://properties" id="C_4">' +
                  '<props:references>C_2</props:references>' +
                  '<props:invalid>C_5</props:invalid>' +
                '</props:referencingCollection>';

      var reader = new Reader(model);
      var rootHandler = reader.handler('props:ReferencingCollection');

      var expectedError =
        'unparsable content <props:invalid> detected\n\t' +
            'line: 0\n\t' +
            'column: 125\n\t' +
            'nested error: unknown type <props:Invalid>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        expect(err).to.exist;
        expect(err.message).to.eql(expectedError);

        expect(result).not.to.exist;

        done();
      });
    });


    it('should handle invalid child element / non-model schema', function(done) {

      var xml = '<props:referencingCollection xmlns:props="http://properties" xmlns:other="http://other">' +
                  '<other:foo>C_2</other:foo>' +
                '</props:referencingCollection>';

      var reader = new Reader(model);
      var rootHandler = reader.handler('props:ReferencingCollection');

      var expectedError =
        'unparsable content <other:foo> detected\n\t' +
            'line: 0\n\t' +
            'column: 99\n\t' +
            'nested error: unrecognized element <other:foo>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        expect(err).to.exist;
        expect(err.message).to.eql(expectedError);

        expect(result).not.to.exist;

        done();
      });
    });


    it('should handle duplicate id', function(done) {

      var xml = '<props:root xmlns:props="http://properties" id="root">' +
                  '<props:baseWithId id="root" />' +
                '</props:root>';

      var reader = new Reader(model);
      var rootHandler = reader.handler('props:Root');

      var expectedError =
        'unparsable content <props:baseWithId> detected\n\t' +
            'line: 0\n\t' +
            'column: 84\n\t' +
            'nested error: duplicate ID <root>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        expect(err).to.exist;
        expect(err.message).to.eql(expectedError);

        expect(result).not.to.exist;

        done();
      });
    });


    describe('references', function() {

      describe('should log warning', function() {

        it('on unresolvable reference', function(done) {

          // given
          var reader = new Reader(extendedModel);
          var rootHandler = reader.handler('props:Root');

          var xml =
            '<props:root xmlns:props="http://properties">' +
              '<props:referencingSingle id="C_4" referencedComplex="C_1" />' +
            '</props:root>';

          // when
          reader.fromXML(xml, rootHandler, function(err, result, context) {

            if (err) {
              return done(err);
            }

            // then
            expect(result).to.jsonEqual({
              $type: 'props:Root',
              any: [
                { $type: 'props:ReferencingSingle', id: 'C_4' }
              ]
            });

            var referencingSingle = result.any[0];

            expect(referencingSingle.referencedComplex).not.to.exist;

            // expect warning to be logged
            expect(context.warnings).to.eql([
              {
                message : 'unresolved reference <C_1>',
                element : referencingSingle,
                property : 'props:referencedComplex',
                value : 'C_1'
              }
            ]);

            done();
          });
        });


        it('on unresolvable collection reference', function(done) {

          // given
          var reader = new Reader(extendedModel);
          var rootHandler = reader.handler('props:Root');

          var xml =
            '<props:root xmlns:props="http://properties">' +
              '<props:containedCollection id="C_5">' +
                '<props:complex id="C_2" />' +
              '</props:containedCollection>' +
              '<props:referencingCollection id="C_4">' +
                '<props:references>C_1</props:references>' +
                '<props:references>C_2</props:references>' +
              '</props:referencingCollection>' +
            '</props:root>';

          // when
          reader.fromXML(xml, rootHandler, function(err, result, context) {

            if (err) {
              return done(err);
            }

            // then
            expect(result).to.jsonEqual({
              $type: 'props:Root',
              any: [
                {
                  $type: 'props:ContainedCollection',
                  id: 'C_5',
                  children: [
                    { $type: 'props:Complex', id: 'C_2' }
                  ]
                },
                { $type: 'props:ReferencingCollection', id: 'C_4' }
              ]
            });

            // expect invalid reference not to be included
            var c2 = result.any[0].children[0];
            var referencingCollection = result.any[1];

            expect(referencingCollection.references).to.jsonEqual([ c2 ]);

            // expect warning to be logged
            expect(context.warnings).to.jsonEqual([
              {
                message: 'unresolved reference <C_1>',
                element: referencingCollection,
                property : 'props:references',
                value : 'C_1'
              }
            ]);

            done();
          });
        });

      });

    });

  });


  describe('lax error handling', function() {

    var model = createModel([ 'properties' ]);


    it('should ignore namespaced invalid child', function(done) {

      // given
      var reader = new Reader({ model: model, lax: true });
      var rootHandler = reader.handler('props:ComplexAttrs');

      var xml = '<props:complexAttrs xmlns:props="http://properties">' +
                  '<props:unknownElement foo="bar">' +
                    '<props:unknownChild />' +
                  '</props:unknownElement>' +
                '</props:complexAttrs>';

      reader.fromXML(xml, rootHandler, function(err, result, context) {

        if (err) {
          return done(err);
        }

        // then
        expect(context.warnings.length).to.eql(1);

        var warning = context.warnings[0];

        expect(warning.message).to.eql(
          'unparsable content <props:unknownElement> detected\n\t' +
            'line: 0\n\t' +
            'column: 84\n\t' +
            'nested error: unknown type <props:UnknownElement>');

        // then
        expect(result).to.jsonEqual({
          $type: 'props:ComplexAttrs'
        });

        done();
      });
    });


    it('should ignore invalid child', function(done) {

      // given
      var reader = new Reader({ model: model, lax: true });
      var rootHandler = reader.handler('props:ComplexAttrs');

      var xml = '<props:complexAttrs xmlns:props="http://properties">' +
                  '<unknownElement foo="bar" />' +
                '</props:complexAttrs>';

      reader.fromXML(xml, rootHandler, function(err, result, context) {

        if (err) {
          return done(err);
        }

        // then
        expect(context.warnings.length).to.eql(1);

        var warning = context.warnings[0];

        expect(warning.message).to.eql(
          'unparsable content <unknownElement> detected\n\t' +
            'line: 0\n\t' +
            'column: 80\n\t' +
            'nested error: unrecognized element <unknownElement>');

        // then
        expect(result).to.jsonEqual({
          $type: 'props:ComplexAttrs'
        });

        done();
      });
    });


    it('should ignore invalid attribute', function(done) {

      // given
      var reader = new Reader({ model: model, lax: true });
      var rootHandler = reader.handler('props:ComplexAttrs');

      var xml = '<props:complexAttrs xmlns:props="http://properties" unknownAttribute="FOO" />';

      reader.fromXML(xml, rootHandler, function(err, result, context) {

        if (err) {
          return done(err);
        }

        expect(context.warnings).to.eql([]);

        // then
        expect(result).to.jsonEqual({
          $type: 'props:ComplexAttrs'
        });

        expect(result.$attrs).to.jsonEqual({
          'xmlns:props': 'http://properties',
          unknownAttribute: 'FOO'
        });

        done();
      });
    });

  });


  describe('extension handling', function() {

    var extensionModel = createModel([ 'extensions' ]);


    describe('attributes', function() {

      it('should read extension attributes', function(done) {

        // given
        var reader = new Reader(extensionModel);
        var rootHandler = reader.handler('e:Root');

        var xml = '<e:root xmlns:e="http://extensions" xmlns:other="http://other" other:foo="BAR" />';

        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result.$attrs).to.jsonEqual({
            'xmlns:e': 'http://extensions',
            'xmlns:other': 'http://other',
            'other:foo' : 'BAR'
          });

          done();
        });
      });


      it('should read default ns', function(done) {

        // given
        var reader = new Reader(extensionModel);
        var rootHandler = reader.handler('e:Root');

        var xml = '<root xmlns="http://extensions" />';

        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result.$attrs).to.jsonEqual({
            'xmlns': 'http://extensions'
          });

          done();
        });

      });
    });


    describe('elements', function() {

      it('should read self-closing extension elements', function(done) {

        // given
        var reader = new Reader(extensionModel);
        var rootHandler = reader.handler('e:Root');

        var xml =
          '<e:root xmlns:e="http://extensions" xmlns:other="http://other">' +
            '<e:id>FOO</e:id>' +
            '<other:meta key="FOO" value="BAR" />' +
            '<other:meta key="BAZ" value="FOOBAR" />' +
          '</e:root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'e:Root',
            id: 'FOO',
            extensions: [
              {
                $type: 'other:meta',
                key: 'FOO',
                value: 'BAR'
              },
              {
                $type: 'other:meta',
                key: 'BAZ',
                value: 'FOOBAR'
              }
            ]
          });

          done();
        });
      });


      it('should read extension element body', function(done) {

        // given
        var reader = new Reader(extensionModel);
        var rootHandler = reader.handler('e:Root');

        var xml =
          '<e:root xmlns:e="http://extensions" xmlns:other="http://other">' +
            '<e:id>FOO</e:id>' +
            '<other:note>' +
              'a note' +
            '</other:note>' +
          '</e:root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'e:Root',
            id: 'FOO',
            extensions: [
              {
                $type: 'other:note',
                $body: 'a note'
              }
            ]
          });

          done();
        });
      });


      it('should read nested extension element', function(done) {

        // given
        var reader = new Reader(extensionModel);
        var rootHandler = reader.handler('e:Root');

        var xml =
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

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {

          if (err) {
            return done(err);
          }

          // then
          expect(result).to.jsonEqual({
            $type: 'e:Root',
            id: 'FOO',
            extensions: [
              {
                $type: 'other:nestedMeta',
                $children: [
                  { $type: 'other:meta', key: 'k1', value: 'v1' },
                  { $type: 'other:meta', key: 'k2', value: 'v2' },
                  { $type: 'other:additionalNote', $body: 'this is some text' }
                ]
              }
            ]
          });

          done();
        });
      });


      describe('descriptor', function() {

        it('should exist', function(done) {

          // given
          var reader = new Reader(extensionModel);
          var rootHandler = reader.handler('e:Root');

          var xml =
            '<e:root xmlns:e="http://extensions" xmlns:other="http://other">' +
              '<e:id>FOO</e:id>' +
              '<other:note>' +
                'a note' +
              '</other:note>' +
            '</e:root>';

          // when
          reader.fromXML(xml, rootHandler, function(err, result) {

            if (err) {
              return done(err);
            }

            var note = result.extensions[0];

            // then
            expect(note.$descriptor).to.exist;

            done();
          });
        });


        it('should contain namespace information', function(done) {

          // given
          var reader = new Reader(extensionModel);
          var rootHandler = reader.handler('e:Root');

          var xml =
            '<e:root xmlns:e="http://extensions" xmlns:other="http://other">' +
              '<e:id>FOO</e:id>' +
              '<other:note>' +
                'a note' +
              '</other:note>' +
            '</e:root>';

          // when
          reader.fromXML(xml, rootHandler, function(err, result) {

            if (err) {
              return done(err);
            }

            var note = result.extensions[0];

            // then
            expect(note.$descriptor).to.eql({
              name: 'other:note',
              isGeneric: true,
              ns: {
                prefix: 'other',
                localName: 'note',
                uri: 'http://other'
              }
            });

            done();
          });
        });

      });

    });

  });


  describe('parent -> child relationship', function() {

    var model = createModel([ 'properties' ]);
    var extendedModel = createModel([ 'properties', 'properties-extended' ]);
    var extensionModel = createModel([ 'extensions' ]);


    it('should expose $parent on model elements', function(done) {

      // given
      var reader = new Reader(model);
      var rootHandler = reader.handler('props:ComplexAttrs');

      var xml = '<props:complexAttrs xmlns:props="http://properties" ' +
                                    'xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">' +
                  '<props:attrs xsi:type="props:Attributes" integerValue="10" />'  +
                '</props:complexAttrs>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        if (err) {
          return done(err);
        }

        // then
        expect(result.$parent).not.to.exist;
        expect(result.attrs.$parent).to.equal(result);

        done();
      });
    });


    it('should expose $parent on references', function(done) {

      // given
      var reader = new Reader(extendedModel);
      var rootHandler = reader.handler('props:Root');

      var xml =
        '<props:root xmlns:props="http://properties">' +
          '<props:containedCollection id="C_5">' +
            '<props:complex id="C_1" />' +
            '<props:complex id="C_2" />' +
          '</props:containedCollection>' +
          '<props:referencingSingle id="C_4" referencedComplex="C_1" />' +
        '</props:root>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        if (err) {
          return done(err);
        }

        var containedCollection = result.any[0];
        var referencedComplex = result.any[1].referencedComplex;

        // then
        expect(referencedComplex.$parent).to.equal(containedCollection);

        done();
      });
    });


    it('should expose $parent on extension elements', function(done) {

      // given
      var reader = new Reader(extensionModel);
      var rootHandler = reader.handler('e:Root');

      var xml =
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

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        if (err) {
          return done(err);
        }

        var child = result.extensions[0];
        var nested = child.$children[0];

        expect(child.$parent).to.equal(result);
        expect(nested.$parent).to.equal(child);

        expect(result).to.jsonEqual({
          $type: 'e:Root',
          id: 'FOO',
          extensions: [
            {
              $type: 'other:nestedMeta',
              $children: [
                { $type: 'other:meta', key: 'k1', value: 'v1' },
                { $type: 'other:meta', key: 'k2', value: 'v2' },
                { $type: 'other:additionalNote', $body: 'this is some text' }
              ]
            }
          ]
        });

        done();
      });
    });

  });


  describe('qualified extensions', function() {

    var extensionModel = createModel([ 'extension/base', 'extension/custom' ]);


    it('should read typed extension property', function(done) {

      // given
      var reader = new Reader(extensionModel);
      var rootHandler = reader.handler('b:Root');

      var xml =
        '<b:Root xmlns:b="http://base" xmlns:c="http://custom">' +
          '<c:CustomGeneric count="10" />' +
        '</b:Root>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        if (err) {
          return done(err);
        }

        expect(result).to.jsonEqual({
          $type: 'b:Root',
          generic: {
            $type: 'c:CustomGeneric',
            count: 10
          }
        });

        done();
      });

    });


    it('should read typed extension attribute', function(done) {

      // given
      var reader = new Reader(extensionModel);
      var rootHandler = reader.handler('b:Root');

      var xml =
        '<b:Root xmlns:b="http://base" xmlns:c="http://custom" ' +
                'c:customAttr="666">' +
        '</b:Root>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        if (err) {
          return done(err);
        }

        expect(result).to.jsonEqual({
          $type: 'b:Root',
          customAttr: 666
        });

        done();
      });

    });


    it('should read generic collection', function(done) {

      // given
      var reader = new Reader(extensionModel);
      var rootHandler = reader.handler('b:Root');

      var xml =
        '<b:Root xmlns:b="http://base" xmlns:c="http://custom" ' +
                'xmlns:other="http://other">' +
          '<c:Property key="foo" value="FOO" />' +
          '<c:Property key="bar" value="BAR" />' +
          '<other:Xyz>content</other:Xyz>' +
        '</b:Root>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result) {

        if (err) {
          return done(err);
        }

        expect(result).to.jsonEqual({
          $type: 'b:Root',
          genericCollection: [
            {
              $type: 'c:Property',
              key: 'foo',
              value: 'FOO'
            },
            {
              $type: 'c:Property',
              key: 'bar',
              value: 'BAR'
            },
            {
              $type: 'other:Xyz',
              $body: 'content'
            }
          ]
        });

        done();
      });

    });


    describe('validation', function() {

      it('should not fail parsing unknown attribute', function(done) {

        // given
        var reader = new Reader(extensionModel);
        var rootHandler = reader.handler('b:Root');

        var xml =
          '<b:Root xmlns:b="http://base" xmlns:c="http://custom" ' +
                  'xmlns:other="http://other" c:unknownAttribute="XXX">' +
          '</b:Root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {
          expect(err).not.to.exist;

          done();
        });

      });


      it('should fail parsing unknown element', function(done) {

        // given
        var reader = new Reader(extensionModel);
        var rootHandler = reader.handler('b:Root');

        var xml =
          '<b:Root xmlns:b="http://base" xmlns:c="http://custom" ' +
                  'xmlns:other="http://other">' +
            '<c:NonExisting />' +
          '</b:Root>';

        // when
        reader.fromXML(xml, rootHandler, function(err, result) {
          expect(err).to.exist;

          done();
        });

      });
    });

  });


  describe('fake ids', function() {

    var fakeIdsModel = createModel([ 'fake-id' ]);


    it('should ignore (non-id) id attribute', function(done) {

      // given
      var reader = new Reader(fakeIdsModel);
      var rootHandler = reader.handler('fi:Root');

      var xml =
        '<fi:Root xmlns:fi="http://fakeid">' +
          '<fi:ChildWithFakeId id="FOO" />' +
        '</fi:Root>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result, context) {

        if (err) {
          return done(err);
        }

        // then
        expect(result).to.jsonEqual({
          $type: 'fi:Root',
          children: [
            {
              $type: 'fi:ChildWithFakeId',
              id: 'FOO'
            }
          ]
        });

        expect(context.elementsById).to.be.empty;

        done();
      });

    });


    it('should not-resolve (non-id) id references', function(done) {

      // given
      var reader = new Reader(fakeIdsModel);
      var rootHandler = reader.handler('fi:Root');

      var xml =
        '<fi:Root xmlns:fi="http://fakeid">' +
          '<fi:ChildWithFakeId id="FOO" />' +
          '<fi:ChildWithFakeId ref="FOO" />' +
        '</fi:Root>';

      // when
      reader.fromXML(xml, rootHandler, function(err, result, context) {

        if (err) {
          return done(err);
        }

        // then
        expect(result).to.jsonEqual({
          $type: 'fi:Root',
          children: [
            {
              $type: 'fi:ChildWithFakeId',
              id: 'FOO'
            },
            {
              $type: 'fi:ChildWithFakeId'
            }
          ]
        });

        expect(context.warnings).to.have.length(1);
        expect(context.warnings[0].message).to.eql('unresolved reference <FOO>');

        done();
      });

    });

  });

});