package unittests.rsl.server;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import rsl.core.ContentHoster;
import rsl.server.HTTPStoredContentHoster;
import rsl.server.RSLServer;
import rsl.util.Log;
import rsl.util.Random;
import rsl.util.TempDir;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("Duplicates")
public class HTTPContentHosterTests {

    private static String endPoint = "http://localhost";

    @BeforeClass
    public static void setEndPoint()
    {
        int port = HTTPStoredContentHoster.port;
        Assert.assertNotEquals(port, 0 );
        endPoint = "http://localhost:" + port;
    }

    @BeforeClass
    public static void startServer()
    {
        RSLServer.disableEventLoggerOutput();
        RSLServer.start();
    }

    @AfterClass
    public static void stopServer()
    {
        RSLServer.stop();
        RSLServer.enableEventLoggerOutput();
    }

    @Test
    public void InvalidGetRequest1()
    {
        try {
            String target = endPoint;
            Response fluidRes = Request.Get(target).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(400, res.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void InvalidGetRequest2()
    {
        try {
            String target = endPoint + "/file";
            Response fluidRes = Request.Get(target).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(400, res.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void NonExistingFileRequest()
    {
        try {
            String target = endPoint + "/file?id=1";
            Response fluidRes = Request.Get(target).execute();
            HttpResponse res = fluidRes.returnResponse();
            Assert.assertEquals(404, res.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void UploadFileTest()
    {
        try {

            String tmpPath = TempDir.makeTempFile(Random.randomString(12, true), "");
            String rndString = Random.randomString(100);
            FileUtils.writeStringToFile(new File(tmpPath), rndString, "UTF-8");

            File file = new File(tmpPath);
            FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("file", fileBody);
            HttpEntity entity = builder.build();

            String target = endPoint + "/upload";
            Response fluidRes = Request.Post(target).body(entity).execute();
            HttpResponse res = fluidRes.returnResponse();

            // check that the file was uploaded successfully
            String storedFileID = EntityUtils.toString(res.getEntity());
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
            Assert.assertNotNull(storedFileID);
            Assert.assertNotEquals("", storedFileID);

            // retrieve the file again, and check that the contents match
            target = endPoint + "/file?id=" + storedFileID;
            fluidRes = Request.Get(target).execute();
            res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());
            String fileContent = EntityUtils.toString(res.getEntity());
            Assert.assertEquals(fileContent, rndString);

            // check that the internal hoster knows about the file
            String path = ContentHoster.getContentPath(storedFileID);
            Assert.assertNotNull(path);
            Assert.assertTrue(new File(path).exists());

            // delete the file via HTTP api
            target = endPoint + "/delete?id=" + storedFileID;
            fluidRes = Request.Get(target).execute();
            res = fluidRes.returnResponse();
            Assert.assertEquals(200, res.getStatusLine().getStatusCode());

            // check that the file is really gone
            Assert.assertFalse(new File(path).exists());
            path = ContentHoster.getContentPath(storedFileID);
            Assert.assertNull(path);


        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}
