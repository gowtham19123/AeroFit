package com.gym;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GymHandler implements HttpHandler {
    private final GymDAO dao = new GymDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        
        // Router for user-interface frontend static files
        if (path.equals("/") || path.equals("/index.html")) {
            serveFile(exchange, "web/index.html", "text/html");
        } else if (path.equals("/style.css")) {
            serveFile(exchange, "web/style.css", "text/css");
        } else if (path.equals("/script.js")) {
            serveFile(exchange, "web/script.js", "application/javascript");
            
        // Backend CRUD endpoints mapping logic
        } else if (path.equals("/addMember") && exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            Map<String,String> data = parseForm(readBody(exchange));
            try {
                dao.addMember(data.get("name"), data.get("plan"), Integer.parseInt(data.get("duration")));
                send(exchange, 200, "Member registered successfully!");
            } catch (Exception e) {
                send(exchange, 500, "Error: " + e.getMessage());
            }
        } else if (path.equals("/members") && exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            try { 
                send(exchange, 200, dao.getMembers(), "application/json"); 
            } catch (Exception e) { 
                send(exchange, 500, "Error: " + e.getMessage()); 
            }
        } else {
            send(exchange, 404, "Not found");
        }
    }

    private String readBody(HttpExchange ex) throws IOException {
        try (InputStream in = ex.getRequestBody()) { return new String(in.readAllBytes(), StandardCharsets.UTF_8); }
    }

    private Map<String,String> parseForm(String body) throws UnsupportedEncodingException {
        Map<String,String> map = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] p = pair.split("=", 2);
            if (p.length == 2) map.put(URLDecoder.decode(p[0], "UTF-8"), URLDecoder.decode(p[1], "UTF-8"));
        }
        return map;
    }

    private void serveFile(HttpExchange ex, String file, String type) throws IOException {
        File f = new File(file);
        if (!f.exists()) { send(ex, 404, "File not found"); return; }
        byte[] data = java.nio.file.Files.readAllBytes(f.toPath());
        ex.getResponseHeaders().set("Content-Type", type + "; charset=UTF-8");
        ex.sendResponseHeaders(200, data.length);
        try (OutputStream out = ex.getResponseBody()) { out.write(data); }
    }

    private void send(HttpExchange ex, int status, String text) throws IOException { 
        send(ex, status, text, "text/plain; charset=UTF-8"); 
    }

    private void send(HttpExchange ex, int status, String text, String type) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, data.length);
        try(OutputStream out = ex.getResponseBody()){ out.write(data); }
    }
}
