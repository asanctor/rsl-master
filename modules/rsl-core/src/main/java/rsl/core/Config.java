package rsl.core;

/**
 * @author rroels
 *
 * This class contains some settings that are used throughout the rsl-core submodule.
 */
final class Config {

    // specifies where RSL will make the database file
    // ObjectDB requires the file to end in .odb or .objectdb !
    static final String dbDir = "db/";
    static final String dbName = "database.odb";

    // specifies where RSL will search for the app-specific model files
    static final String schemaDir = "schemas/";

    // name of the shared schema
    static final String sharedSchemaFileName = "shared.json";

    // name of the shared model
    static final String sharedSchemaJARName = "shared.jar";

    // specifies where RSL will place JARs generated from model schema files
    static final String schemaJARPath = "schemas/compiled/";

    // specifies where the files hosted by RSL will be saved
    static final String hostedValueDir = "hosted/";

}
