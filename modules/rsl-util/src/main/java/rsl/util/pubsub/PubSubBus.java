package rsl.util.pubsub;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import java.util.concurrent.Executors;

public class PubSubBus {

    private EventBus eventBus;
    private String busName;

    public PubSubBus(String busName, boolean async, PubSubEventLogger logger)
    {
        this(busName, async);
        subscribe(logger);
    }

    public PubSubBus(String busName, boolean async)
    {
        this.busName = busName;
        if(async)
        {
            eventBus = new AsyncEventBus(Executors.newCachedThreadPool());
        }else{
            eventBus = new EventBus();
        }
    }

    public void publish(PubSubEvent event)
    {
        event.busName = busName;
        eventBus.post(event);
    }

    public void subscribe(Object obj)
    {
        eventBus.register(obj);
    }

}
