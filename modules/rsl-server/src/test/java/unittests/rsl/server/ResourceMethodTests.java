package unittests.rsl.server;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.*;
import rsl.core.ContentHoster;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.coremodel.RslLink;
import rsl.core.coremodel.RslResource;
import rsl.core.coremodel.RslSelector;
import rsl.server.RSLServer;
import rsl.server.interfaces.rest.RestInterface;
import rsl.util.Log;
import rsl.util.json.JSONObjectPath;
import rsl.util.json.JSONProcessor;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class ResourceMethodTests {

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
    public void setHostedContentTest()
    {
        try{

            RslResource resource = EntityFactory.createResource("MindXpres", "SlideResource").save();
            Assert.assertFalse(resource.hasHostedContent());

            String query = "{ 'command': 'setHostedContent', 'parameters': { 'id': '" + resource.getID() + "', 'hostedContentIndex': 'foobar' } }";

            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            resource.refresh();
            Assert.assertTrue(resource.hasHostedContent());
            Assert.assertEquals("foobar", resource.getHostedContentIndex());

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void detachHostedContentTest()
    {
        try{

            RslResource resource = EntityFactory.createResource("MindXpres", "SlideResource").save();
            Assert.assertFalse(resource.hasHostedContent());

            String contentIndex = resource.setHostedContent("foobar");
            Assert.assertTrue(resource.hasHostedContent());

            String query = "{ 'command': 'detachHostedContent', 'parameters': { 'id': '" + resource.getID() + "'} }";

            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            resource.refresh();

            Assert.assertFalse(resource.hasHostedContent());

            String contentPath = ContentHoster.getContentPath(contentIndex);
            Assert.assertNotNull(contentPath);


            ContentHoster.removeContent(resource.getHostedContentIndex());

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void deleteHostedContentTest()
    {
        try{

            RslResource resource = EntityFactory.createResource("MindXpres", "SlideResource").save();
            Assert.assertFalse(resource.hasHostedContent());

            String contentIndex = resource.setHostedContent("foobar");
            Assert.assertTrue(resource.hasHostedContent());

            String query = "{ 'command': 'deleteHostedContent', 'parameters': { 'id': '" + resource.getID() + "'} }";

            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            resource.refresh();
            Assert.assertFalse(resource.hasHostedContent());

            String contentPath = ContentHoster.getContentPath(contentIndex);
            Assert.assertNull(contentPath);

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void getSelectorsTest()
    {
        try{

            RslSelector sel1 = EntityFactory.createSelector("shared", "TextSelector").save();
            RslSelector sel2 = EntityFactory.createSelector("shared", "TextSelector").save();
            RslSelector sel3 = EntityFactory.createSelector("shared", "TextSelector").save();
            RslResource resource = EntityFactory.createResource("shared", "TextResource").save();

            sel1.setSelectorResource(resource);
            Assert.assertEquals(1, resource.getSelectors().size());

            String query = "{ 'command': 'getSelectors', 'parameters': { 'id': '" + resource.getID() + "'} }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            List resultList = JSONProcessor.extractArrayField(response, new JSONObjectPath("result"), List.class);
            Assert.assertEquals(1, resultList.size());

            sel2.setSelectorResource(resource);
            sel3.setSelectorResource(resource);
            Assert.assertEquals(3, resource.getSelectors().size());

            query = "{ 'command': 'getSelectors', 'parameters': { 'id': '" + resource.getID() + "'} }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            is = res.getEntity().getContent();
            response = IOUtils.toString(is, "UTF-8");
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            resultList = JSONProcessor.extractArrayField(response, new JSONObjectPath("result"), List.class);
            Assert.assertEquals(3, resultList.size());

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void addSelectorTest()
    {
        try{

            RslSelector sel1 = EntityFactory.createSelector("shared", "TextSelector").save();
            RslSelector sel2 = EntityFactory.createSelector("shared", "TextSelector").save();
            RslResource resource = EntityFactory.createResource("shared", "TextResource").save();

            String query = "{ 'command': 'addSelector', 'parameters': { 'id': '" + resource.getID() + "', 'selectorID': '" + sel1.getID() + "' } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            resource.refresh();
            Assert.assertEquals(1, resource.getSelectors().size());

            query = "{ 'command': 'addSelector', 'parameters': { 'id': '" + resource.getID() + "', 'selectorID': '" + sel2.getID() + "' } }";
            fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            res = fluidRes.returnResponse();
            is = res.getEntity().getContent();
            response = IOUtils.toString(is, "UTF-8");
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            resource.refresh();
            Assert.assertEquals(2, resource.getSelectors().size());

        }catch (Exception e){
            Assert.fail();
        }
    }

}
