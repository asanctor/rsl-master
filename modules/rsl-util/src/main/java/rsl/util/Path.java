package rsl.util;

import java.io.File;

public class Path {

    public static String enforceTrailingSeperator(String path)
    {
        if(path.charAt(path.length()-1)!= File.separatorChar){
            path += File.separator;
        }
        return path;
    }

    public static String getWorkingDirectory()
    {
        return System.getProperty("user.dir");
    }

}
