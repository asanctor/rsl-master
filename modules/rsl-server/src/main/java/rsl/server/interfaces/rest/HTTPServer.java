package rsl.server.interfaces.rest;


import com.google.gson.ExclusionStrategy;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;
import fi.iki.elonen.NanoHTTPD;
import rsl.core.coremodel.RslEntity;
import rsl.server.ClientRequest;
import rsl.server.RSLServer;
import rsl.server.events.RequestReceivedEvent;
import rsl.server.interfaces.ClientCommunicationInterface;
import rsl.server.json.RequestListDeserializer;
import rsl.server.json.RequestMapDeserializer;
import rsl.server.json.RslJsonTypeAdapterFactory;
import rsl.server.json.RslObjectExclusionStrategy;
import rsl.util.Log;
import rsl.util.json.JSONDeserializeSettings;
import rsl.util.json.JSONProcessor;
import rsl.util.json.JSONSerializeSettings;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTPServer extends NanoHTTPD {

    private int port;
    private ClientCommunicationInterface clientInterface;

    public HTTPServer(ClientCommunicationInterface clientInterface, int port)
    {
        super(port);
        this.clientInterface = clientInterface;
        this.port = port;
    }

    public void start() throws IOException
    {
        super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);

    }

    public void stop()
    {
        //super.closeAllConnections();
        super.stop();
    }

    @Override
    public Response serve(IHTTPSession session) {

        if(session.getMethod() == Method.OPTIONS) { // needed for CORS support
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, "");
            return setHeaders(response);
        }else if(session.getMethod() == Method.POST) {

            String msg = "";
            try {
                session.parseBody(new HashMap<>());
                Map<String, java.util.List<String>> params = session.getParameters();
                msg = params.getOrDefault("query", Collections.singletonList("")).get(0);
            } catch (Exception e) {
                e.printStackTrace();
                Response response = newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Could Not Parse Request");
                return setHeaders(response);
            }

            ClientRequest req = parseCommand(msg);
            if(req == null) {
                Response response = newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Could Not Parse Request");
                return setHeaders(response);
            }

            ServerState state = new ServerState();
            RSLServer.pubSubBus.publish(new RequestReceivedEvent(clientInterface, state, req));

            try {
                state.awaitResult();
            } catch (InterruptedException e) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Thread Interupted Before Results Returned");
            }

            Response.Status status = NanoHTTPD.Response.Status.lookup(state.response.getStatusCode());
            if(status == null) { status = Response.Status.INTERNAL_ERROR;}

            List<TypeAdapterFactory> adapterFactories = Collections.singletonList(new RslJsonTypeAdapterFactory());
            List<ExclusionStrategy> exclusionStrategies = Collections.singletonList(new RslObjectExclusionStrategy());

            String responseJSON = JSONProcessor.toJSON(state.response, new JSONSerializeSettings(null, exclusionStrategies, adapterFactories));
            Response response = newFixedLengthResponse(status, MIME_PLAINTEXT, responseJSON);
            return setHeaders(response);

        } else {
            Response response = newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "Invalid Method");
            return setHeaders(response);
        }

    }

    private Response setHeaders(Response response)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Max-Age", "3628800");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "X-Requested-With");
        response.addHeader("Access-Control-Allow-Headers", "Authorization");
        return response;
    }

    private ClientRequest parseCommand(String message)
    {

        HashMap<Class, JsonDeserializer> deserializers = new HashMap<>();
        deserializers.put(List.class, new RequestListDeserializer());
        deserializers.put(Map.class, new RequestMapDeserializer());

        ClientRequest result = JSONProcessor.fromJSON(message, new JSONDeserializeSettings(deserializers), ClientRequest.class);
        if(result != null)
        {
            result.raw = message;
        }
        return result;
    }

}
