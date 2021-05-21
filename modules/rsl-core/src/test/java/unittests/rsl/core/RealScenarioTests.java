package unittests.rsl.core;

import org.apache.commons.io.FileUtils;
import org.junit.*;
import rsl.core.EntityFactory;
import rsl.core.GraphCrawler;
import rsl.core.RSL;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.core.coremodel.RslResource;
import rsl.core.coremodel.RslSelector;
import rsl.persistence.PersistenceQuery;
import rsl.persistence.RslPersistence;
import rsl.util.contentgenerator.XMLContentGenerator;
import unittests.rsl.core.xmlparser.PresentationsParser;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RealScenarioTests {

    private static String xmlPath = "debug/presentations.xml";
    private static RslPersistence db;

    @BeforeClass
    public static void setUp()
    {
        RSL.setDBPath("debug/db/db.objectdb");
        RSL.loadSchemaClasses("testschemas/");

        RSL.getDB().emptyDatabase();
        String xml = XMLContentGenerator.getPresentationsAsXML(50);

        RslEntity[] entities = null;
        try {
            FileUtils.writeStringToFile(new File(xmlPath), xml, "UTF-8");
            entities = PresentationsParser.parseXML(new File(xmlPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(entities != null)
        {
            RSL.getDB().startTransaction();
            for(RslEntity e: entities)
            {
                e.save();
            }
            RSL.getDB().endTransaction();
        }

    }

    @AfterClass
    public static void tearDown(){ RSL.getDB().emptyDatabase();}

    @Test
    public void GetPresentationsByAuthor()
    {
        String q = String.format("SELECT o FROM PresentationResource o WHERE author = '%s'", "Reinout Roels");
        List results = RSL.getDB().executeQuery(new PersistenceQuery(q));
        Assert.assertTrue( results.size() > 0);
    }

    @Test
    public void GetSlidesForPresentation()
    {
        String q = String.format("SELECT o FROM PresentationResource o WHERE author = '%s'", "Reinout Roels");
        List results = RSL.getDB().executeQuery(new PersistenceQuery(q));
        Assert.assertTrue( results.size() > 0);

        RslEntity pres = (RslEntity) results.get(0);
        Class<?> linkClass = EntityFactory.getLinkClass("MindXpres","StructuralLink");
        Assert.assertNotNull(linkClass);

        Set<RslLink> links = pres.getOutgoingLinksOfType(linkClass);
        Assert.assertEquals( 1, links.size());

        RslLink l = links.iterator().next();
        Assert.assertNotNull(l);

        Class<?> slideClass = EntityFactory.getResourceClass("MindXpres","SlideResource");
        Assert.assertNotNull(slideClass);

        Set<RslEntity> slides = l.getTargetsOfType(slideClass);
        Assert.assertTrue( slides.size() > 0);
    }

    @Test
    public void GetAllPresentationContent()
    {

        Set<Class<? extends RslResource>> resources = new HashSet<>();
        resources.add(EntityFactory.getResourceClass("MindXpres","PresentationResource"));
        resources.add(EntityFactory.getResourceClass("MindXpres","SlideResource"));
        resources.add(EntityFactory.getResourceClass("shared","ImageResource"));
        resources.add(EntityFactory.getResourceClass("shared","TextResource"));

        Set<Class<? extends RslSelector>> selectors = new HashSet<>();
        selectors.add(EntityFactory.getSelectorClass("shared","ImageSelector"));

        Set<Class<? extends RslLink>> links = new HashSet<>();
        links.add(EntityFactory.getLinkClass("MindXpres","StructuralLink"));


        String q = String.format("SELECT o FROM PresentationResource o WHERE author = '%s'", "Reinout Roels");
        List results = RSL.getDB().executeQuery(new PersistenceQuery(q));
        Assert.assertTrue( results.size() > 0);
        RslEntity pres = (RslEntity) results.get(0);

        GraphCrawler crawler = new GraphCrawler();
        crawler.setAllowedResources(resources);
        crawler.setAllowedSelectors(selectors);
        crawler.setAllowedLinks(links);
        Set<RslEntity> crawlResults = crawler.crawl(pres);

        Assert.assertTrue( crawlResults.size() > 10);
    }



}

