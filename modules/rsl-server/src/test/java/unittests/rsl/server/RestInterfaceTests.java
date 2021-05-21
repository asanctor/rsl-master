package unittests.rsl.server;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.junit.*;

import org.apache.http.*;
import rsl.server.RSLServer;
import rsl.server.interfaces.rest.RestInterface;
import rsl.util.Log;

import java.util.Properties;

@SuppressWarnings("Duplicates")
public class RestInterfaceTests {

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
    public static void startServer()
    {
        RSLServer.disableEventLoggerOutput();
        RSLServer.start();
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void stopServer()
    {
        RSLServer.stop();
        RSLServer.enableEventLoggerOutput();
    }

    @Test
    public void invalidMethodTest()
    {
        try {
             Response fluidRes = Request.Get(endPoint).execute();
             HttpResponse res = fluidRes.returnResponse();
             Assert.assertEquals(405, res.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void invalidPostRequestTest ()
    {
        try {
            Response fluidRes = Request.Post(endPoint).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(400, res.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getServerVersionTest()
    {
        try {
            String query = "{ 'command': 'getServerVersion', 'parameters': { } }";
            Response fluidRes = Request.Post(endPoint).bodyForm(Form.form().add("query", query).build()).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }



}

