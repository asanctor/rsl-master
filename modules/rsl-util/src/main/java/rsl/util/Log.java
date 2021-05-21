package rsl.util;

import rsl.util.Logger;
import java.util.EnumSet;

public class Log {

    private static Logger log = new Logger();
    static {
        log.setLogLevels(EnumSet.of(Logger.LogLevel.DEBUG, Logger.LogLevel.ERROR, Logger.LogLevel.INFO, Logger.LogLevel.WARNING));
        log.setLogMethods(EnumSet.of(Logger.LogMethod.CONSOLE));
    }

    public static <T> void log(T msg){log.log(msg);}
    public static <T> void debug(T msg){log.debug(msg);}
    public static <T> void info(T msg){log.info(msg);}
    public static <T> void warning(T msg){log.warning(msg);}
    public static <T> void error(T msg){log.error(msg);}
    public static <T> void error(T msg, Exception e){log.error(msg, e);}


}
