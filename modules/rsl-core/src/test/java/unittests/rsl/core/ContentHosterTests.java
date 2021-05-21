package unittests.rsl.core;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import rsl.core.ContentHoster;
import rsl.util.Log;
import rsl.util.Path;
import rsl.util.Random;
import rsl.util.TempDir;

import java.io.File;
import java.io.IOException;

public class ContentHosterTests {

    @Test
    public void deleteFileTest()
    {
        String rndString = Random.randomString(100);
        String index = ContentHoster.storeContent(rndString);
        String path = ContentHoster.getContentPath(index);
        Assert.assertTrue(fileExists(path));
        boolean success = ContentHoster.removeContent(index);
        Assert.assertTrue(success);
        Assert.assertFalse(fileExists(path));
    }


    @Test
    public void hostStringTest()
    {
        String rndString = Random.randomString(100);
        String index = ContentHoster.storeContent(rndString);
        String path = ContentHoster.getContentPath(index);
        Assert.assertNotNull(path);
        Assert.assertNotEquals(path, "");

        String result = "";
        try {
            result = FileUtils.readFileToString(new File(path), "UTF-8");
        } catch (IOException e) {
            Assert.fail("Could not read file");
        }

        Assert.assertEquals(result, rndString);

        boolean cleaned = FileUtils.deleteQuietly(new File(path));
        Assert.assertTrue(cleaned);

    }

    @Test
    public void hostByteArrayTest()
    {
        String rndString = Random.randomString(100);
        byte[] content = rndString.getBytes();
        String index = ContentHoster.storeContent(content);
        String path = ContentHoster.getContentPath(index);
        Assert.assertNotNull(path);
        Assert.assertNotEquals(path, "");
        String result = "";
        try {
            result = FileUtils.readFileToString(new File(path), "UTF-8");
        } catch (IOException e) {
            Assert.fail("Could not read file");
        }
        Assert.assertEquals(result, rndString);

        boolean cleaned = FileUtils.deleteQuietly(new File(path));
        Assert.assertTrue(cleaned);
    }

    @Test
    public void hostFileTest()
    {
        String tmpPath = TempDir.makeTempFile(Random.randomString(12, true), "");
        String rndString = Random.randomString(100);
        try {
            FileUtils.writeStringToFile(new File(tmpPath), rndString, "UTF-8");
        } catch (IOException e) {
            Assert.fail("Could not write file");
        }
        String index = ContentHoster.storeContent(new File(tmpPath));
        String path = ContentHoster.getContentPath(index);
        Assert.assertNotNull(path);
        Assert.assertNotEquals(path, "");
        String result = "";
        try {
            result = FileUtils.readFileToString(new File(path), "UTF-8");
        } catch (IOException e) {
            Assert.fail("Could not read file");
        }
        Assert.assertEquals(result, rndString);

        boolean cleaned = FileUtils.deleteQuietly(new File(path));
        Assert.assertTrue(cleaned);
    }

    @Test
    public void duplicateFileTest()
    {
        String tmpPath = TempDir.makeTempFile(Random.randomString(12, true), "");
        String rndString = Random.randomString(100);
        try {
            FileUtils.writeStringToFile(new File(tmpPath), rndString, "UTF-8");
        } catch (IOException e) {
            Assert.fail("Could not write file");
        }

        String index = ContentHoster.storeContent(new File(tmpPath));
        String path = ContentHoster.getContentPath(index);
        Assert.assertNotNull(path);
        Assert.assertNotEquals(path, "");

        index = ContentHoster.storeContent(new File(tmpPath));
        path = ContentHoster.getContentPath(index);
        Assert.assertNotNull(path);
        Assert.assertNotEquals(path, "");

        boolean cleaned = FileUtils.deleteQuietly(new File(path));
        Assert.assertTrue(cleaned);
    }


    private boolean fileExists(String path)
    {
        File f = new File(path);
        return f.exists();
    }
}
