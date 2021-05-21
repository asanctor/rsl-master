package unittests.rsl.core;

import org.junit.*;
import rsl.core.ClassGenerationResult;
import rsl.core.RSL;
import rsl.util.ClassPath;

public class LoadSchemasToMemoryTests {

    @Test
    public void countLoadedModelsAndClasses()
    {
        ClassGenerationResult result = RSL.loadSchemaClasses("testschemas/");
        Assert.assertEquals(4, result.getModelCount());
        Assert.assertEquals(24, result.getClassCount());
    }

    @Test
    public void loadSchemasToMemoryTest()
    {
        boolean error = false;
        try {
            RSL.loadSchemaClasses("testschemas/");
        } catch(Error | Exception e)
        {
            e.printStackTrace();
            error = true;
        }
        Assert.assertEquals(false, error);
    }



    @Test
    public void loadSchemasToMemoryTwiceTest()
    {
        boolean error = false;
        try {
            RSL.loadSchemaClasses("testschemas/");
            RSL.loadSchemaClasses("testschemas/");
        } catch(Error | Exception e) {
            error = true;
        }
        Assert.assertEquals(false, error);
    }



}
