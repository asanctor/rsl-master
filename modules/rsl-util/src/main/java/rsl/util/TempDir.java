package rsl.util;

import java.io.File;
import java.io.IOException;

public class TempDir {

    public static String getTempDir()
    {
        return Path.enforceTrailingSeperator(System.getProperty("java.io.tmpdir"));
    }

    public static String makeTempFile(String filename, String suffix)
    {
        try {
            File tmp = File.createTempFile(Random.randomString(12, true), suffix);
            return tmp.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


}