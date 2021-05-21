package rsl.compiler.schemaobjects;

/**
 * @author rroels
 *
 * ModelSchema is an object that contains an internal representation of model schemas parsed from schema files.
 * A JSONModelSchemaDeserializer is used to translate JSON into a ModelSchema object. The ModelSchema object inherits from
 *  SchemaObject which provides the common fields and related methods (e.g. name field).
 */
public class ModelSchema extends SchemaObject {

    private String version;

    private ResourceSchema[] resources;
    private SelectorSchema[] selectors;
    private LinkSchema[] links;

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public ResourceSchema[] getResources() {
        return resources;
    }
    public void setResources(ResourceSchema[] resources) {
        this.resources = resources;
    }

    public SelectorSchema[] getSelectors() {
        return selectors;
    }
    public void setSelectors(SelectorSchema[] selectors) {
        this.selectors = selectors;
    }

    public LinkSchema[] getLinks() {
        return links;
    }
    public void setLinks(LinkSchema[] links) {
        this.links = links;
    }

}
