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
import rsl.server.RSLServer;
import rsl.server.interfaces.rest.RestInterface;
import rsl.util.Log;
import rsl.util.json.JSONProcessor;
import rsl.util.json.JSONObjectPath;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;


@SuppressWarnings("Duplicates")
public class EntityMethodTests {

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
    public void createEntityTest()
    {
        try {
            String query = "{ 'command': 'createEntity', 'parameters': { 'model': 'shared', 'entity': 'TextResource' } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();

            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");

            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            String idString =  JSONProcessor.extractStringField(response, new JSONObjectPath("result", "id"));
            Assert.assertNotNull(idString);

            Long id = Long.parseLong(idString);
            RslEntity entity = RslQuery.getEntityByID(id);
            Assert.assertNotNull(entity);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void deleteEntityTest()
    {
        try {
            RslEntity en = EntityFactory.createResource("shared", "TextResource");
            en.save();
            long id = en.getID();

            String query = "{ 'command': 'deleteEntity', 'parameters': { 'id': '" + id + "' } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            //InputStream is = res.getEntity().getContent();
            //String response = IOUtils.toString(is, "UTF-8");

            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            RslEntity entity = RslQuery.getEntityByID(id);
            Assert.assertNull(entity);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


    @Test
    public void setEntityFields()
    {
        RslEntity pres = EntityFactory.createResource("MindXpres", "PresentationResource");
        pres.save();

        try{

            String query = "{ 'command': 'setEntityFields', 'parameters': { 'id': '" + pres.getID() + "', 'fields': {'title': 'test', 'author': 'rroels', 'theme': 'vub', 'importance': 1} } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();

            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");

            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            // since the HTTP request uses a threadlocal entitymanager, we need to refresh the object in this thread to see the change
            pres.refresh();

            RslEntity pres2 = RslQuery.getEntityByID(pres.getID());
            Assert.assertNotNull(pres2);

            String title = pres2.getField("title");
            String author = pres2.getField("author");
            String theme = pres2.getField("theme");
            long importance = pres2.getField("importance");

            Assert.assertEquals("test", title);
            Assert.assertEquals("rroels", author);
            Assert.assertEquals("vub", theme);
            Assert.assertEquals(1, importance);

        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void getEntityTest()
    {
        RslEntity pres = EntityFactory.createResource("MindXpres", "PresentationResource");
        pres.setField("title", "test");
        pres.save();

        try{
            String query = "{ 'command': 'getEntity', 'parameters': { 'id': '" + pres.getID() + "' } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();

            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");

            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            String title =  JSONProcessor.extractStringField(response, new JSONObjectPath("result", "title"));
            Assert.assertEquals("test", title);

        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void getEntitiesTest()
    {
        RslEntity pres1 = EntityFactory.createResource("MindXpres", "PresentationResource").save();
        RslEntity pres2 = EntityFactory.createResource("MindXpres", "PresentationResource").save();
        RslEntity pres3 = EntityFactory.createResource("MindXpres", "PresentationResource").save();

        try{
            String query = "{ 'command': 'getEntities', 'parameters': { 'entities': [" + pres1.getID() + "] } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            List results = JSONProcessor.extractArrayField(response, new JSONObjectPath("result"), Object.class);
            Assert.assertEquals(1, results.size());

            String entities = String.format("[%s, %s, %s]", pres1.getID(), pres2.getID(), pres3.getID());
            query = "{ 'command': 'getEntities', 'parameters': { 'entities': " + entities + " } }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            is = res.getEntity().getContent();
            response = IOUtils.toString(is, "UTF-8");
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            results = JSONProcessor.extractArrayField(response, new JSONObjectPath("result"), Object.class);
            Assert.assertEquals(3, results.size());



        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }

    }


    @Test
    public void entityMethodChainTest()
    {
        RslEntity pres = EntityFactory.createResource("MindXpres", "PresentationResource");
        pres.setField("title", "test");
        pres.save();

        try{

            String query = "{ 'command': 'getEntity', 'parameters': { 'id': '" + pres.getID() + "' } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            query = "{ 'command': 'setEntityFields', 'parameters': { 'id': '" + pres.getID() + "', 'fields': {'title': 'test', 'author': 'rroels', 'theme': 'vub'} } }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            query = "{ 'command': 'getEntity', 'parameters': { 'id': '" + pres.getID() + "' } }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");

            String title =  JSONProcessor.extractStringField(response, new JSONObjectPath("result", "title"));
            String author =  JSONProcessor.extractStringField(response, new JSONObjectPath("result", "author"));
            String theme =  JSONProcessor.extractStringField(response, new JSONObjectPath("result", "theme"));

            Assert.assertEquals("test", title);
            Assert.assertEquals("rroels", author);
            Assert.assertEquals("vub", theme);

        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }

    }

    @Test
    public void getLinkedEntity()
    {
        RslEntity pres = EntityFactory.createResource("MindXpres", "PresentationResource");
        pres.setField("title", "test");
        pres.save();

        RslLink link1 = EntityFactory.createLink("MindXpres", "StructuralLink");
        RslLink link2 = EntityFactory.createLink("MindXpres", "StructuralLink");
        RslLink link3 = EntityFactory.createLink("MindXpres", "StructuralLink");

        link1.addTarget(pres);
        link2.addSource(pres);
        link3.addSource(pres);

        try{
            String query = "{ 'command': 'getEntity', 'parameters': { 'id': '" + pres.getID() + "' } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();

            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");

            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            String title =  JSONProcessor.extractStringField(response, new JSONObjectPath("result", "title"));
            Assert.assertEquals("test", title);

            List<Long> outgoingLinks = JSONProcessor.extractArrayField(response, new JSONObjectPath("result", "outgoingLinks"), Long.class);
            Assert.assertNotNull(outgoingLinks);
            Assert.assertEquals(2, outgoingLinks.size());

        }catch(Exception e){
            e.printStackTrace();
            Assert.fail();
        }



    }





}

