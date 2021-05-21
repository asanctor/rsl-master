package unittests.rsl.server;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.junit.*;
import rsl.core.EntityFactory;
import rsl.core.RSL;
import rsl.core.coremodel.RslResource;
import rsl.core.coremodel.RslSelector;
import rsl.server.RSLServer;
import rsl.server.interfaces.rest.RestInterface;
import rsl.util.json.JSONObjectPath;
import rsl.util.json.JSONProcessor;

import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("Duplicates")
public class SelectorMethodTests {

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
    public void setSelectorResourceTest()
    {
        try{

            RslSelector sel1 = EntityFactory.createSelector("shared", "TextSelector").save();
            RslResource resource = EntityFactory.createResource("shared", "TextResource").save();

            String query = "{ 'command': 'setSelectorResource', 'parameters': { 'selectorID': '" + sel1.getID() + "', 'resourceID': '" + resource.getID() + "' } }";

            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            sel1.refresh();
            Assert.assertNotNull(sel1.getSelectorResource());

        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void getSelectorResourceTest()
    {
        try{

            RslSelector sel1 = EntityFactory.createSelector("shared", "TextSelector").save();
            RslResource resource = EntityFactory.createResource("shared", "TextResource").save();

            sel1.setSelectorResource(resource);

            String query = "{ 'command': 'getSelectorResource', 'parameters': { 'id': '" + sel1.getID() + "'} }";

            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            InputStream is = res.getEntity().getContent();
            String response = IOUtils.toString(is, "UTF-8");
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            String idString = JSONProcessor.extractStringField(response, new JSONObjectPath("result", "id"));
            Assert.assertNotNull(idString);
            long id = Long.parseLong(idString);

            Assert.assertEquals(resource.getID(), id);

        }catch (Exception e){
            Assert.fail();
        }
    }

}
