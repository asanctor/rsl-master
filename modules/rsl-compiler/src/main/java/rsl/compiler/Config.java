package rsl.compiler;


/**
 * @author rroels
 *
 * This class contains some settings that are used throughout the compiler submodule.
 */

final class Config {

    /**
     * If true, the compiler will write the generated code to a folder.
     * Useful to debug/check the generated code.
     */
    static final boolean outputClassCode = true;

    /**
     * Specifies the folder where generated code will be placed if outputClassCode is set to true.
     * The path is relative to the compiler JAR.
     */
    static final String outputClassCodeDir = "debug/generated_classes/";

}
