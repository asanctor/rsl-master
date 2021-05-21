package rsl.compiler;

import java.util.HashMap;
import java.util.List;

/**
 * @author rroels
 *
 * DataPrimitives is used to translate data types mentioned in the schemas to the corresponding Java class.
 * The {@code types} hashmap maps the type names (strings) to Java classes (Objects of type {@code class}).
 * This is used by the code generator to give the proper Java data types to e.g. properties defined in the schema.
 */
public class DataPrimitives {

    private static HashMap<String, Class> types = new HashMap<>();

    static {
        types.put("int16", short.class);
        types.put("int32", int.class);
        types.put("int64", long.class);
        types.put("string", String.class);
        types.put("boolean", boolean.class);
        types.put("byte[]", Byte[].class);
        types.put("list", List.class);
    }

    /**
     * Translates a data type from a schema to the corresponding Java data type.
     * If no match is found, {@code null} is returned.
     *
     * @param type  The name (string) of the data type as specified in the schema
     * @return      A Java data type that corresponds to data type mentioned in the schema.
     *              If no matching type is found, {@code null} is returned.
     */
    public static Class getJavaClass(String type)
    {
        return types.getOrDefault(type, null);
    }

}
