package unittests.rsl.core;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rsl.core.EntityFactory;
import rsl.core.GraphCrawler;
import rsl.core.RSL;
import rsl.core.coremodel.*;
import rsl.persistence.PersistenceQuery;
import rsl.persistence.RslPersistence;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static unittests.rsl.core.xmlparser.eSPACEParser.testEntities;


public class RealScenarioTests2 {

    //private static String xmlPath = "C:\\Users\\Audrey\\Desktop\\rsl-master\\modules\\rsl-core\\src\\test\\resources\\espace.xml";
    private static RslPersistence db;

    @BeforeClass
    public static void setUp()
    {
        RSL.setDBPath("debug/db/db.objectdb");
        RSL.loadSchemaClasses("testschemas/");

        RSL.getDB().emptyDatabase();

        //RslEntity[] entities = null;
        //entities = eSPACEParser.parseXML(new File(xmlPath));


        testEntities();



    }

    @AfterClass
    public static void tearDown(){ }//RSL.getDB().emptyDatabase();}

    @Test
    public void GetApplicationByCreator()
    {

        String q = String.format("SELECT o FROM ApplicationResource o WHERE name = '%s'", "Cat app");
        List results = RSL.getDB().executeQuery(new PersistenceQuery(q));
        System.out.println(results);
        Assert.assertTrue( results.size() > 0);
    }

    @Test
    public void GetFUIsForApp()
    {
        String q = String.format("SELECT o FROM ApplicationResource o WHERE name = '%s'", "Cat app");
        List results = RSL.getDB().executeQuery(new PersistenceQuery(q));
        Assert.assertTrue( results.size() > 0);

        RslEntity pres = (RslEntity) results.get(0);
        Class<?> linkClass = EntityFactory.getLinkClass("eSPACE","StructuralLink");
        Assert.assertNotNull(linkClass);

        Set<RslLink> links = pres.getOutgoingLinksOfType(linkClass);
        Assert.assertEquals( 1, links.size());

        RslLink l = links.iterator().next();
        Assert.assertNotNull(l);

        Class<?> FUIClass = EntityFactory.getResourceClass("eSPACE","FUIResource");
        Assert.assertNotNull(FUIClass);

        Set<RslEntity> fuis = l.getTargetsOfType(FUIClass);
        Assert.assertTrue( fuis.size() > 0);
        //TODO: continue with DComp, AC and UIe
    }

    @Test
    public void GetAllApplicationContent()
    {

        Set<Class<Preference>> preferences = new HashSet<>();
        preferences.add(Preference.class);

        Set<Class<RslUser>> users = new HashSet<>();
        users.add(RslUser.class);

        Set<Class<? extends RslResource>> resources = new HashSet<>();
        resources.add(EntityFactory.getResourceClass("eSPACE","ApplicationResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","FUIResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","DCompResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","UIeResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","ACResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","ParameterResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","LayoutResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","PhysicalObjectResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","DeviceResource"));
        resources.add(EntityFactory.getResourceClass("eSPACE","PropertySetResource"));
        resources.add(EntityFactory.getResourceClass("shared","ImageResource"));

        Set<Class<? extends RslSelector>> selectors = new HashSet<>();
        selectors.add(EntityFactory.getSelectorClass("shared","ImageSelector"));

        Set<Class<? extends RslLink>> links = new HashSet<>();
        links.add(EntityFactory.getLinkClass("eSPACE","StructuralLink"));
        links.add(EntityFactory.getLinkClass("eSPACE","NavigationalLink"));


        String q = String.format("SELECT o FROM ApplicationResource o WHERE name = '%s'", "Cat app");
        List results = RSL.getDB().executeQuery(new PersistenceQuery(q));
        Assert.assertTrue( results.size() > 0);
        RslEntity apps = (RslEntity) results.get(0);

        GraphCrawler crawler = new GraphCrawler();
        crawler.setAllowedPreferences(preferences);
        crawler.setAllowedUsers(users);
        crawler.setAllowedResources(resources);
        crawler.setAllowedSelectors(selectors);
        crawler.setAllowedLinks(links);
        Set<RslEntity> crawlResults = crawler.crawl(apps);

        Assert.assertTrue( crawlResults.size() > 10);
    }



}

