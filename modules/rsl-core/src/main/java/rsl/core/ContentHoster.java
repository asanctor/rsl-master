package rsl.core;

import org.apache.commons.io.FileUtils;
import rsl.util.Log;
import rsl.util.Random;
import rsl.util.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ContentHoster {

    public static String getContentPath(String id)
    {
        if(id == null || id.length() == 0)
        {
            return null;
        }
        String firstChar = id.substring(0, 1);
        String storePath = Config.hostedValueDir + firstChar + File.separator + id;
        File f = new File(storePath);
        if(!f.exists()){
            return null;
        }
        return storePath;
    }

    public static boolean removeContent(String id)
    {
        if(id == null || id.length() == 0)
        {
            return false;
        }
        String firstChar = id.substring(0, 1);
        String storePath = Config.hostedValueDir + firstChar + File.separator + id;
        boolean success = FileUtils.deleteQuietly(new File(storePath));

        if(!success){
            Log.warning(String.format("Could not delete hosted content '%s'", id));
            return false;
        }else{
            return true;
        }
    }


    public static String storeContent(String content)
    {
        InputStream is = new ByteArrayInputStream(content.getBytes());
        return storeContent(is);
    }

    public static String storeContent(File content)
    {
        InputStream is = null;
        try {
            is = FileUtils.openInputStream(content);
        } catch (IOException e) {
            Log.error("Error while storing file", e);
            return null;
        }
        return storeContent(is);
    }

    public static String storeContent(byte[] content)
    {
        InputStream is = new ByteArrayInputStream(content);
        return storeContent(is);
    }

    public static String storeContent(InputStream content)
    {
        // first, write the content to a file in the temp dir
        String tmpPath = TempDir.makeTempFile(Random.randomString(24, true), ".rsl");

        // while writing the content to the file, calculate the MD5 hash
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try(DigestInputStream dis = new DigestInputStream(content, md))
            {
                File srcFile = new File(tmpPath);
                FileUtils.copyInputStreamToFile(dis, srcFile);
                String md5 = md5DigestToString(md.digest()).toLowerCase();
                String firstChar = md5.substring(0, 1);

                // check if file already exists
                String existsPath = getContentPath(md5);
                if(existsPath!=null) // it exists
                {
                        if(srcFile.length() == new File(existsPath).length()) // file is the same, no need to store again
                        {
                            FileUtils.forceDelete(srcFile);
                            return md5;
                        }else{ // file is different, but we have a hash collision, this should not happen, but if it does, return null
                            FileUtils.forceDelete(srcFile);
                            return null;
                        }
                }

                // if we would store all files in the same dir, we would run into performance issues (especially Windows)
                // for this reason we create a subdir for each starting letter
                // e.g. if the MD5 is debe2294ecd0e0f08eab7690d2a6ee69, we store the file in {hostedValueDir}/d/debe2294ecd0e0f08eab7690d2a6ee69
                String storePath = Config.hostedValueDir + firstChar + File.separator + md5;
                FileUtils.moveFile(srcFile, new File(storePath));
                return md5;
            } catch (Exception e) {
                Log.error("Error while writing inputstream to file", e);
                return null;
            }
        } catch (NoSuchAlgorithmException e) {
            Log.error("Error while calculating MD5 of inputstream", e);
            return null;
        }
    }

    private static String md5DigestToString(byte[] digest){
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

}
