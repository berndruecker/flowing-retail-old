# moddle descriptor - XML Extensions

When reading from / writing to XML, additional meta-data is neccesary to correctly map the data model to a XML document.

This document contains the list of supported extensions that are understood by [moddle-xml](https://github.com/bpmn-io/moddle-xml).


## Package Definition

Extensions to the package definition allows you to configure namespacing and element serialization. 


### Namespace URL

Specify the `url` field in a package definition to define the associated XML namespace url.

```json
{
  "prefix": "s",
  "url": "http://sample"
}
```

This results in 

```xml
<s:Root xmlns:s="http://sample" />
```

### Element Name Serialization

Specify `alias=lowerCase` to map elements to their lower case names in xml.

The above output becomes

```xml
<s:root xmlns:s="http://sample" />
```

when this property is specified.


## Property definition

XML distinguishes between child elements, body text and attributes. moddle allows you to map your data to these places via special qualifiers.


### Qualifiers

Use any of the following qualifiers to configure how a property is mapped to XML.

| Qualifier | Values | Description |
| ------------- | ------------- | ----- |
| `isAttr=false` | `Boolean` | serializes as an attribute |
| `isBody=false` | `Boolean` | serializes as the body of the element |
| `serialize` | `String` | adds additional notes on how to serialize. Supported value(s): `xsi:type` serializes as data type rather than element |