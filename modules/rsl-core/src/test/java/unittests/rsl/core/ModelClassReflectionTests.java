package unittests.rsl.core;

import org.junit.*;
import rsl.core.ClassGenerationResult;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslUser;

public class ModelClassReflectionTests {



    @BeforeClass
    public static void setUp()
    {
        ClassGenerationResult result = RSL.loadSchemaClasses("testschemas/");
    }


    @Test
    public void CreateSharedClassByNameTest()
    {

        RslEntity img =  EntityFactory.createResource("shared","ImageResource");
        Assert.assertNotEquals(img, null);
    }

    @Test
    public void CreateModelClassByNameTest()
    {
        RslEntity slide =  EntityFactory.createResource("MindXpres","SlideResource");
        Assert.assertNotEquals(slide, null);
    }

    @Test
    public void AccessClassField()
    {
        RslEntity slide =  EntityFactory.createResource("MindXpres","SlideResource");
        slide.invokeMethod("setTheme", "test");
        String theme = slide.invokeMethod("getTheme");
        Assert.assertEquals("test", theme);

        //tests if user and his name can be retrieved using the slide class
        RslUser creator = new RslUser();
        creator.setUsername("Audrey");
        slide.invokeMethod("setCreator", creator);
        RslUser slideCreator = slide.invokeMethod("getCreator");
        String creatorName = slideCreator.getUsername();
        Assert.assertEquals("Audrey", creatorName);
    }

    @Test
    public void GetSharedClassByNameTest()
    {
        Class<?> c = EntityFactory.getResourceClass("shared","ImageResource");
        Assert.assertNotEquals(c, null);
    }

    @Test
    public void GetModelClassByNameTest()
    {
        Class<?> c = EntityFactory.getResourceClass("MindXpres","SlideResource");
        Assert.assertNotEquals(c, null);
    }




}
