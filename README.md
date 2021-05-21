# RSL

A new implementation of the RSL metamodel for hypermedia systems.

To compile, install gradle and execute the following command in the project root:

`gradle clean build`

To run unit tests, use:

`gradle test`

To generate JAR files out of the schemas for embedded use, place your schemas in the 'build/schemas' folder and run:

`gradle compileSchemas`

The results will be placed in 'build/schemas/compiled'.

For more documentation, please see to the HTML files in the 'documentation/' folder.