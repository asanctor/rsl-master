package rsl.server;

import fi.iki.elonen.NanoHTTPD;
import rsl.core.ContentHoster;
import rsl.server.events.ContentHosterStartEvent;
import rsl.server.events.ContentHosterStopEvent;
import rsl.server.events.InterfaceStartEvent;
import rsl.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fi.iki.elonen.NanoHTTPD.MIME_PLAINTEXT;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;


public class HTTPStoredContentHoster {


    // to use the HTTP api:

    // Upload a File:
    // POST file to /upload with the file called "file" as key

    // Retrieve a File:
    // GET from /file?id=xxx with xxx being the file id to retrieve

    // Delete a File:
    // GET from /delete?id=xxx with xxx being the file id to delete


    public final static int port = 5365;
    private static StaticFileServer server = new StaticFileServer(port);

    public static void start()
    {
        try {
            server.start();
            RSLServer.pubSubBus.publish(new ContentHosterStartEvent(port));
        } catch (IOException e) {
            Log.error("Could not start ContentHoster HTTP Server", e);
        }
    }

    public static void stop()
    {
        server.closeAllConnections();
        server.stop();
        RSLServer.pubSubBus.publish(new ContentHosterStopEvent());
    }

    static NanoHTTPD.Response handleFileRequest(NanoHTTPD.IHTTPSession session)
    {

        String uri = session.getUri().toLowerCase();
        if(!(uri.equals("/file") || uri.equals("/delete")))
        {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid Request (No Path at " + uri + ")");
        }

        // extract the requested file's id is present in the URL
        Map<String, List<String>> params = session.getParameters();
        List<String> ids = params.getOrDefault("id", null);
        if(ids == null || ids.size() == 0)
        {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid Request (No ID Specified)");
        }
        String id = ids.get(0);

        // get the corresponding file's path
        String filePath = ContentHoster.getContentPath(id);
        if(filePath == null || filePath.length() == 0)
        {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, MIME_PLAINTEXT, String.format("No Content Found for ID: '%s'", id));
        }

        // doublecheck that the file still exists on the filesystem
        File f = new File(filePath);
        if(!f.exists())
        {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, String.format("Content Missing for ID: '%s'", id));
        }

        if(uri.equals("/file")) {

            // open a FileInputStream from the file
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, String.format("Content Missing for ID: '%s'", id));
            }

            // send back the file
            return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/octet-stream", fis, f.length());

        } else if (uri.equals("/delete")) {

           boolean success = ContentHoster.removeContent(id);
           if(success)
           {
               return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, MIME_PLAINTEXT, String.format("Content Removed with ID: '%s'", id));
           } else {
               return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, String.format("Could Not Remove Content with ID: '%s'", id));
           }

        } else { // not possible
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "");
        }

    }

    static NanoHTTPD.Response handleFileUpload(NanoHTTPD.IHTTPSession session)
    {

        String uri = session.getUri().toLowerCase();
        if(!uri.equals("/upload"))
        {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid Request (No Path at " + uri + ")");
        }

        HashMap<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);
        } catch (Exception e) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error Parsing POST Body");
        }

        String path = files.getOrDefault("file", "");
        if( path == null || path.equals(""))
        {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, MIME_PLAINTEXT, "Invalid Request (No File Uploaded)");
        }

        File f = new File(path);
        if(!f.exists())
        {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Received File Not Found on Filesystem");
        }

        String id = ContentHoster.storeContent(f);
        return newFixedLengthResponse(NanoHTTPD.Response.Status.OK, MIME_PLAINTEXT, id);
    }


    private static class StaticFileServer extends NanoHTTPD
    {

        StaticFileServer(int port) {
            super(port);
        }

        public void start() throws IOException {
            super.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        }

        public void stop() {
            super.stop();
        }

        @Override
        public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
            if (session.getMethod() == Method.POST) {
                return HTTPStoredContentHoster.handleFileUpload(session);
            } else if (session.getMethod() == Method.GET) {
                return HTTPStoredContentHoster.handleFileRequest(session);
            } else {
                return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "Invalid Method");
            }
        }

    }


}
