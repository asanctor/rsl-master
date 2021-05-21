package rsl.util;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.EnumSet;

public class Logger {

    private EnumSet<LogLevel> logLevels = EnumSet.allOf(LogLevel.class);
    private EnumSet<LogMethod> logMethods = EnumSet.of(LogMethod.CONSOLE);
    private LogLevel defaultLogLevel = LogLevel.INFO;
    private String fileLogPath = "log.txt";
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private FileOutputStream fos;
    private PrintWriter fileWriter;

    public enum LogLevel {
        DEBUG, INFO, WARNING, ERROR
    }

    public enum LogMethod {
        CONSOLE, FILE
    }

    public void setLogLevels(EnumSet<LogLevel> levels) {
        this.logLevels = levels;
    }

    public void setLogMethods(EnumSet<LogMethod> methods) {
        this.logMethods = methods;
        if(methods.contains(LogMethod.FILE))
        {
            setFileLogPath(fileLogPath);
        }
    }

    public void setFileLogPath(String path)
    {
        this.fileLogPath = path;

        try {
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            fos = new FileOutputStream(new File(path));
            fileWriter = new PrintWriter(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void setDefaultLogLevel(LogLevel lvl)
    {
        this.defaultLogLevel = lvl;
    }


    public <T> void log(T msg)
    {
        switch (defaultLogLevel)
        {
            case INFO:
                info(msg);
                break;
            case DEBUG:
                debug(msg);
                break;
            case WARNING:
                warning(msg);
                break;
            case ERROR:
                error(msg);
                break;
        }
    }

    public <T> void debug(T msg)
    {
        if(logLevels.contains(LogLevel.DEBUG))
        {
            String m = makeLogLine(msg, LogLevel.DEBUG);
            writeConsoleLine(m);
            writeFileLogLine(m);
        }
    }

    public <T> void info(T msg)
    {
        if(logLevels.contains(LogLevel.INFO))
        {
            String m = makeLogLine(msg, LogLevel.INFO);
            writeConsoleLine(m);
            writeFileLogLine(m);
        }
    }

    public <T> void warning(T msg)
    {
        if(logLevels.contains(LogLevel.WARNING))
        {
            String m = makeLogLine(msg, LogLevel.WARNING);
            writeConsoleLine(m);
            writeFileLogLine(m);
        }
    }

    public <T> void error(T msg)
    {
        error(msg, null);
    }

    public <T>  void error(T msg, Exception e)
    {
        if(logLevels.contains(LogLevel.ERROR))
        {
            String m = makeLogLine(msg, LogLevel.ERROR);
            writeConsoleLine(m);
            if(e != null && logMethods.contains(LogMethod.CONSOLE))
            {
                e.printStackTrace();
            }

            writeFileLogLine(m);
            if(e != null && logMethods.contains(LogMethod.FILE))
            {
                e.printStackTrace(fileWriter);
            }
        }
    }


    private void writeConsoleLine(String msg)
    {
        if(logMethods.contains(LogMethod.CONSOLE))
        {
            System.out.println(msg);
        }
    }

    private void writeFileLogLine(String msg)
    {
        if(logMethods.contains(LogMethod.FILE))
        {
            fileWriter.println(msg);
        }
    }

    private <T> String makeLogLine(T msg, LogLevel lvl)
    {
        LocalDateTime date = LocalDateTime.now();
        String timestamp = "[" + date.format(timeFormatter) + "] ";

        // get longest string in loglevels
        int longestLevel = 0;
        for (LogLevel logLevel : LogLevel.values()) {
            if(logLevel.toString().length() > longestLevel)
            {
                longestLevel = logLevel.toString().length();
            }
        }

        // left pad current loglevel string
        String lvlString = String.format("%-" + longestLevel + "s", lvl.toString()) + " ";

        if(msg == null)
        {
            return timestamp + lvlString + "null";
        } else {
            return timestamp + lvlString + msg.toString();
        }
    }

}
