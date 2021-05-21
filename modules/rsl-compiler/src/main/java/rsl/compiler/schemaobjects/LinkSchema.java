package rsl.compiler.schemaobjects;

import java.util.Map;

/**
 * @author rroels
 *
 * LinkSchema is an object that contains an internal representation of link schemas parsed from schema files.
 * A JSONLinkSchemaDeserializer is used to translate JSON into a LinkSchema object. The LinkSchema object inherits from
 *  SchemaObjectWithProperties which implements everything related to the storing and retrieving of properties.
 */
public class LinkSchema extends SchemaObjectWithProperties {

    // storage for properties is automatically added because it extends SchemaWithProperties!

    private LinkRestrictionType sourceRestrictionsType;
    private String[] sourceRestrictionsList;

    private LinkRestrictionType targetRestrictionsType;
    private String[] targetRestrictionsList;

    public enum LinkRestrictionType {
        WHITELIST, BLACKLIST
    }

    public LinkRestrictionType getSourceRestrictionsType() {
        return sourceRestrictionsType;
    }

    public void setSourceRestrictionsType(LinkRestrictionType sourceRestrictionsType) {
        this.sourceRestrictionsType = sourceRestrictionsType;
    }

    public String[] getSourceRestrictionsList() {
        return sourceRestrictionsList;
    }

    public void setSourceRestrictionsList(String[] sourceRestrictionsList) {
        this.sourceRestrictionsList = sourceRestrictionsList;
    }

    public LinkRestrictionType getTargetRestrictionsType() {
        return targetRestrictionsType;
    }

    public void setTargetRestrictionsType(LinkRestrictionType targetRestrictionsType) {
        this.targetRestrictionsType = targetRestrictionsType;
    }

    public String[] getTargetRestrictionsList() {
        return targetRestrictionsList;
    }

    public void setTargetRestrictionsList(String[] targetRestrictionsList) {
        this.targetRestrictionsList = targetRestrictionsList;
    }




}
