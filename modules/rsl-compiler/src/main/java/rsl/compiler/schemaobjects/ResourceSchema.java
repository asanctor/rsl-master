package rsl.compiler.schemaobjects;

/**
 * @author rroels
 *
 * ResourceSchema is an object that contains an internal representation of resource schemas parsed from schema files.
 * A JSONResourceSchemaDeserializer is used to translate JSON into a ResourceSchema object. The ResourceSchema object
 * inherits from SchemaObjectWithProperties which implements everything related to the storing and retrieving of properties.
 */
public class ResourceSchema extends SchemaObjectWithProperties {

    // properties are added because it extends SchemaWithProperties!

}
