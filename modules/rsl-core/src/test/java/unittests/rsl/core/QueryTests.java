package unittests.rsl.core;

import org.junit.*;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.coremodel.RslEntity;
import rsl.persistence.PersistenceQuery;
import rsl.persistence.RslPersistence;
import rsl.util.Log;

import java.util.List;

// todo: allow tests to used custom db location so that it can be removed later

public class QueryTests {

    private static String xmlPath = "debug/presentations.xml";
    private static RslPersistence db;

    @BeforeClass
    public static void setUp()
    {
        RSL.setDBPath("debug/db/db.objectdb");
        RSL.loadSchemaClasses("testschemas/");
        //try {
        //    Thread.currentThread().sleep(1000);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
         //rsl.util.ClassPath.logLoadedClasses("rsl");

    }

    @Before
    public void clearDatabase()
    {
        RSL.getDB().emptyDatabase();
    }

    @AfterClass
    public static void tearDown()
    {
        RSL.getDB().emptyDatabase();
    }


    @Test
    public void CustomQuery()
    {
        RslEntity slide =  EntityFactory.createResource("MindXpres","SlideResource");
        slide.save();
        PersistenceQuery q = new PersistenceQuery(String.format("SELECT o FROM SlideResource o WHERE o.id = %dL", slide.getID()));
        List results = RSL.getDB().executeQuery(q);
        Assert.assertNotEquals(results.size(), 0);

        RslEntity slide2 = (RslEntity) results.get(0);
        Assert.assertEquals("SlideResource", slide2.getTypeName());
    }

    @Test
    public void GetByDatabaseID()
    {
        RslEntity slide =  EntityFactory.createResource("MindXpres","SlideResource");
        slide.save();
        RslEntity slide2 = RSL.getDB().getByDatabaseID(slide.getClass(), slide.getDatabaseID());
        Assert.assertNotNull(slide2);
    }


    @Test
    public void GetByID()
    {
        RslEntity slide =  EntityFactory.createResource("MindXpres","SlideResource");
        slide.save();
        RslEntity slide2 = RSL.getDB().getByID(RslEntity.class, slide.getID());
        Assert.assertNotNull(slide2);
    }


}

