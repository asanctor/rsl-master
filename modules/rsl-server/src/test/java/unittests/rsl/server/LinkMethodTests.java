package unittests.rsl.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.*;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.RslQuery;
import rsl.core.coremodel.RslEntity;
import rsl.core.coremodel.RslLink;
import rsl.core.coremodel.RslResource;
import rsl.server.RSLServer;
import rsl.server.interfaces.rest.RestInterface;
import rsl.util.Log;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class LinkMethodTests {

    private static String endPoint = "http://localhost";

    @BeforeClass
    public static void setEndPoint()
    {
        Properties props = new RestInterface("",null).getDefaultProperties();
        String strPort = (String)props.getOrDefault("port", "0");
        int port = Integer.valueOf(strPort);
        Assert.assertNotEquals(port, 0 );
        endPoint = "http://localhost:" + port;
    }

    @BeforeClass
    public static void setUp()
    {
        RSL.setDBPath("debug/db/db.objectdb");
        RSL.loadSchemaClasses("testschemas/");
        RSLServer.disableEventLoggerOutput();
        RSLServer.start();
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown()
    {
        RSL.getDB().emptyDatabase();
        RSLServer.stop();
        RSLServer.enableEventLoggerOutput();
    }

    @Before
    public void clearDatabase()
    {
        RSL.getDB().emptyDatabase();
    }

    @Test
    public void addLinkSourcesTest()
    {
        try{

            RslResource en1 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslResource en2 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslResource en3 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslLink link = EntityFactory.createLink("MindXpres", "StructuralLink").save();

            String sources = "[" + en1.getID() + "]";
            String query = "{ 'command': 'addLinkSources', 'parameters': { 'id': '" + link.getID() + "', 'sources': " + sources + " } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            link.refresh();
            Assert.assertEquals(1, link.getSources().size());

            sources = "[" + en2.getID() + ", " + en3.getID() + "]";
            query = "{ 'command': 'addLinkSources', 'parameters': { 'id': '" + link.getID() + "', 'sources': " + sources + " } }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            link.refresh();
            Assert.assertEquals(3, link.getSources().size());

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void addLinkTargetsTest()
    {
        try{

            RslResource en1 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslResource en2 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslResource en3 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslLink link = EntityFactory.createLink("MindXpres", "StructuralLink").save();

            String targets = "[" + en1.getID() + "]";
            String query = "{ 'command': 'addLinkTargets', 'parameters': { 'id': '" + link.getID() + "', 'targets': " + targets + " } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
            link.refresh();
            Assert.assertEquals(1, link.getTargets().size());

            targets = "[" + en2.getID() + ", " + en3.getID() + "]";
            query = "{ 'command': 'addLinkTargets', 'parameters': { 'id': '" + link.getID() + "', 'targets': " + targets + " } }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
            link.refresh();
            Assert.assertEquals(3, link.getTargets().size());

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void removeLinkSourcesTest()
    {
        try{

            RslResource en1 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslResource en2 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslResource en3 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslLink link = EntityFactory.createLink("MindXpres", "StructuralLink").save();

            link.addSources(Collections.singletonList(en1));

            String sources = "[" + en1.getID() + "]";
            String query = "{ 'command': 'removeLinkSources', 'parameters': { 'id': '" + link.getID() + "', 'sources': " + sources + " } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
            link.refresh();
            Assert.assertEquals(0, link.getSources().size());

            link.addSources(Arrays.asList(en2, en3));

            sources = "[" + en2.getID() + ", " + en3.getID() + "]";
            query = "{ 'command': 'removeLinkSources', 'parameters': { 'id': '" + link.getID() + "', 'sources': " + sources + " } }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
            link.refresh();
            Assert.assertEquals(0, link.getSources().size());

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void removeLinkTargetsTest()
    {
        try{
            RslResource en1 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslResource en2 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslResource en3 = EntityFactory.createResource("MindXpres", "SlideResource").save();
            RslLink link = EntityFactory.createLink("MindXpres", "StructuralLink").save();

            link.addTargets(Collections.singletonList(en1));

            String targets = "[" + en1.getID() + "]";
            String query = "{ 'command': 'removeLinkTargets', 'parameters': { 'id': '" + link.getID() + "', 'targets': " + targets + " } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
            link.refresh();
            Assert.assertEquals(0, link.getSources().size());

            link.addTargets(Arrays.asList(en2, en3));

            targets = "[" + en2.getID() + ", " + en3.getID() + "]";
            query = "{ 'command': 'removeLinkTargets', 'parameters': { 'id': '" + link.getID() + "', 'targets': " + targets + " } }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
            link.refresh();
            Assert.assertEquals(0, link.getSources().size());

        }catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        }
    }




}
