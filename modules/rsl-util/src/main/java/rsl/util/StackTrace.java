package rsl.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTrace {

    public static void printStackTrace()
    {
        StringWriter sw = new StringWriter();
        new Throwable("").printStackTrace(new PrintWriter(sw));
        System.out.println(sw.toString());
    }
}
