package rsl.util.pubsub;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PubSubEventLogger {

    private boolean outputEnabled = true;

    @Subscribe
    @AllowConcurrentEvents
    public void handleEvent(PubSubEvent event)
    {
        if(outputEnabled){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String time = sdf.format(new Date(event.getTimestamp()));

            String logMessage = getLogMessage(event);
            String logOutput = String.format("[%s] %s", time, logMessage);
            System.out.println(logOutput);
        }
    }

    public void enableOutput()
    {
        outputEnabled = true;
    }

    public void disableOutput()
    {
        outputEnabled = false;
    }

    private String getLogMessage(PubSubEvent event)
    {
        Method handler = getEventHandleMethod(event);
        try {
            return handler.invoke(this, event).toString();
        } catch (Exception e) {
            System.out.println("Error while invoking the logging method for event of type " + event.getClass().getSimpleName());
            System.out.println("Make sure that the class is defined as public and that your method can take this type as argument!");
            e.printStackTrace();
            return "";
        }
    }

    // use reflection to find a method to handle this specific type of event
    // if it's not found, use the generic method for PubSubEvent events
    private Method getEventHandleMethod(PubSubEvent event)
    {
        String methodName = event.getClass().getSimpleName();
        try {
            return this.getClass().getDeclaredMethod("handle" + methodName, event.getClass());
        } catch (NoSuchMethodException e) {
            try {
                return this.getClass().getDeclaredMethod("handlePubSubEvent", PubSubEvent.class);
            } catch (NoSuchMethodException e1) {
                System.out.println("handlePubSubEvent method not found in PubSubEventLogger");
                e1.printStackTrace();
                return null;
            }
        }
    }


    private String handlePubSubEvent(PubSubEvent event)
    {
        return event.getMessage();
    }

    private String handleErrorEvent(ErrorEvent event)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        event.getException().printStackTrace(pw);
        String stackTrace = sw.toString();
        String exceptionType = event.getException().getClass().getSimpleName();
        return String.format("ERROR (%s) %s \n %s \n %s", exceptionType, event.getMessage(), event.getException().getMessage(), stackTrace);
    }


}
