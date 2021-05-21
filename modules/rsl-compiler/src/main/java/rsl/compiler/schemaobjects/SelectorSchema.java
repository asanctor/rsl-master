package rsl.compiler.schemaobjects;

import java.util.Map;

/**
 * @author rroels
 *
 * SelectorSchema is an object that contains an internal representation of selector schemas parsed from schema files.
 * A JSONSelectorSchemaDeserializer is used to translate JSON into a SelectorSchema object. The SelectorSchema object
 * inherits from SchemaObjectWithProperties which implements everything related to the storing and retrieving of properties.
 */
public class SelectorSchema extends SchemaObjectWithProperties {

    private String refersTo;

    public String getRefersTo() {
        return refersTo;
    }

    public void setRefersTo(String refersTo) {
        this.refersTo = refersTo;
    }

}
