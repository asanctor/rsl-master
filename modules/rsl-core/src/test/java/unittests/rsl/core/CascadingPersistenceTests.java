package unittests.rsl.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.coremodel.*;

public class CascadingPersistenceTests {

    @BeforeClass
    public static void setUp()
    {
        RSL.loadSchemaClasses("testschemas/");
    }

    @Before
    public void clearDatabase()
    {
        RSL.getDB().emptyDatabase();
    }

    @Test
    public void cascadingPersistanceTest1()
    {

        RslResource slide1 =  EntityFactory.createResource("MindXpres","SlideResource");
        RslResource slide2 =  EntityFactory.createResource("MindXpres","SlideResource");
        RslResource slide3 =  EntityFactory.createResource("MindXpres","SlideResource");
        RslResource slide4 =  EntityFactory.createResource("MindXpres","SlideResource");

        // slide2 -> slide3
        RslLink nav1 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav1.addSource(slide2);
        nav1.addTarget(slide3);

        // slide3 -> slide4
        RslLink nav2 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav2.addSource(slide3);
        nav2.addTarget(slide4);

        // slide1 -> slide2
        RslLink nav3 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav3.addSource(slide1);
        nav3.addTarget(slide2);

        Class<?> slideClass = EntityFactory.getResourceClass("MindXpres", "SlideResource");
        Class<?> navClass = EntityFactory.getLinkClass("MindXpres", "NavigationPathLink");

        long slideCount = RSL.getDB().countByClass(slideClass);
        long navCount = RSL.getDB().countByClass(navClass);

        Assert.assertEquals(0, slideCount);
        Assert.assertEquals(0, navCount);
        Assert.assertEquals(0, RSL.getDB().countAll());

    }

    @Test
    public void cascadingPersistanceTest2()
    {

        RslResource slide1 =  EntityFactory.createResource("MindXpres","SlideResource");
        slide1.save();

        RslResource slide2 =  EntityFactory.createResource("MindXpres","SlideResource");
        RslResource slide3 =  EntityFactory.createResource("MindXpres","SlideResource");
        RslResource slide4 =  EntityFactory.createResource("MindXpres","SlideResource");

        // slide2 -> slide3
        RslLink nav1 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav1.addSource(slide2);
        nav1.addTarget(slide3);

        // slide3 -> slide4
        RslLink nav2 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav2.addSource(slide3);
        nav2.addTarget(slide4);

        // slide1 -> slide2
        RslLink nav3 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav3.addSource(slide1);
        nav3.addTarget(slide2);

        Class<?> slideClass = EntityFactory.getResourceClass("MindXpres", "SlideResource");
        Class<?> navClass = EntityFactory.getLinkClass("MindXpres", "NavigationPathLink");

        long slideCount = RSL.getDB().countByClass(slideClass);
        long navCount = RSL.getDB().countByClass(navClass);

        Assert.assertEquals(4, slideCount);
        Assert.assertEquals(3, navCount);
        Assert.assertEquals(7, RSL.getDB().countAll());

    }

    @Test
    public void cascadingPersistanceTest3()
    {

        RslResource slide1 =  EntityFactory.createResource("MindXpres","SlideResource");
        RslResource slide2 =  EntityFactory.createResource("MindXpres","SlideResource");
        RslResource slide3 =  EntityFactory.createResource("MindXpres","SlideResource");
        RslResource slide4 =  EntityFactory.createResource("MindXpres","SlideResource");
        slide3.save();

        // slide2 -> slide3
        RslLink nav1 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav1.addSource(slide2);
        nav1.addTarget(slide3);

        // slide3 -> slide4
        RslLink nav2 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav2.addSource(slide3);
        nav2.addTarget(slide4);

        // slide1 -> slide2
        RslLink nav3 =  EntityFactory.createLink("MindXpres","NavigationPathLink");
        nav3.addSource(slide1);
        nav3.addTarget(slide2);

        Class<?> slideClass = EntityFactory.getResourceClass("MindXpres", "SlideResource");
        Class<?> navClass = EntityFactory.getLinkClass("MindXpres", "NavigationPathLink");

        long slideCount = RSL.getDB().countByClass(slideClass);
        long navCount = RSL.getDB().countByClass(navClass);

        Assert.assertEquals(4, slideCount);
        Assert.assertEquals(3, navCount);
        Assert.assertEquals(7, RSL.getDB().countAll());

    }

    @Test
    public void cascadingPersistanceTest4(){

        RslUser audrey = new RslUser();
        audrey.setUsername("audrey");
        audrey.save();

        RslEntity entityX = new RslEntity();
        entityX.setCreator(audrey);
        entityX.setName("test");
        entityX.save();

        int entity = audrey.getCreatedEntities().size();
        RslUser p = entityX.getCreator();
        String creator = p.getUsername();

        Assert.assertEquals("audrey", creator);
        Assert.assertEquals(1 , entity);
    }

}
