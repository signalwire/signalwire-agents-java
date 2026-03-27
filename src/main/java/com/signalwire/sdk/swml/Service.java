/*
 * Copyright (c) 2025 SignalWire
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */
package com.signalwire.sdk.swml;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.signalwire.sdk.logging.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Base SWML service with an embedded HTTP server, basic auth, security headers,
 * and explicit methods for all 38 schema-driven verbs.
 * <p>
 * Uses JDK built-in com.sun.net.httpserver.HttpServer with virtual threads.
 */
public class Service {

    private static final Logger log = Logger.getLogger(Service.class);
    private static final int MAX_REQUEST_BODY_SIZE = 1_048_576; // 1 MB
    private static final Gson gson = new Gson();

    protected final String name;
    protected String route;
    protected String host;
    protected int port;
    protected final Document document;

    // Auth
    private String authUser;
    private String authPassword;

    // HTTP server
    private HttpServer httpServer;

    public Service(String name) {
        this(name, "/", "0.0.0.0", resolvePort(), null, null);
    }

    public Service(String name, String route) {
        this(name, route, "0.0.0.0", resolvePort(), null, null);
    }

    public Service(String name, String route, String host, int port,
                   String authUser, String authPassword) {
        this.name = name;
        this.route = route.endsWith("/") && route.length() > 1
                ? route.substring(0, route.length() - 1) : route;
        this.host = host;
        this.port = port;
        this.document = new Document();

        // Auth setup
        if (authUser != null && authPassword != null) {
            this.authUser = authUser;
            this.authPassword = authPassword;
        } else {
            String envUser = System.getenv("SWML_BASIC_AUTH_USER");
            String envPass = System.getenv("SWML_BASIC_AUTH_PASSWORD");
            this.authUser = (envUser != null && !envUser.isEmpty()) ? envUser : name;
            this.authPassword = (envPass != null && !envPass.isEmpty()) ? envPass : generatePassword();
        }

        log.info("Service '%s' initialized with auth user: %s", name, this.authUser);
    }

    private static int resolvePort() {
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                return Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {
            }
        }
        return 3000;
    }

    private static String generatePassword() {
        var random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // -------- Auth --------

    public String getAuthUser() {
        return authUser;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    /**
     * Timing-safe basic auth validation using MessageDigest.isEqual.
     */
    protected boolean validateAuth(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }

        String encoded = authHeader.substring(6);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(encoded);
        } catch (IllegalArgumentException e) {
            return false;
        }

        String credentials = new String(decoded, StandardCharsets.UTF_8);
        int colonIdx = credentials.indexOf(':');
        if (colonIdx < 0) {
            return false;
        }

        String user = credentials.substring(0, colonIdx);
        String pass = credentials.substring(colonIdx + 1);

        // Timing-safe comparison for both user and password
        boolean userMatch = MessageDigest.isEqual(
                user.getBytes(StandardCharsets.UTF_8),
                authUser.getBytes(StandardCharsets.UTF_8));
        boolean passMatch = MessageDigest.isEqual(
                pass.getBytes(StandardCharsets.UTF_8),
                authPassword.getBytes(StandardCharsets.UTF_8));

        return userMatch && passMatch;
    }

    /**
     * Add security headers to every authenticated response.
     */
    protected void addSecurityHeaders(HttpExchange exchange) {
        var headers = exchange.getResponseHeaders();
        headers.set("X-Content-Type-Options", "nosniff");
        headers.set("X-Frame-Options", "DENY");
        headers.set("Cache-Control", "no-store");
    }

    // -------- Document access --------

    public Document getDocument() {
        return document;
    }

    // -------- 38 Schema-Driven Verb Methods --------
    // Each method adds the verb to the document's main section.
    // Java has no method_missing, so all are explicit.

    public Service answer(Map<String, Object> params) {
        document.addVerb("answer", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service ai(Map<String, Object> params) {
        document.addVerb("ai", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service amazonBedrock(Map<String, Object> params) {
        document.addVerb("amazon_bedrock", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service cond(List<Map<String, Object>> conditions) {
        document.addVerb("cond", conditions);
        return this;
    }

    public Service connect(Map<String, Object> params) {
        document.addVerb("connect", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service denoise(Map<String, Object> params) {
        document.addVerb("denoise", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service detectMachine(Map<String, Object> params) {
        document.addVerb("detect_machine", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service enterQueue(Map<String, Object> params) {
        document.addVerb("enter_queue", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service execute(Map<String, Object> params) {
        document.addVerb("execute", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service gotoLabel(Map<String, Object> params) {
        document.addVerb("goto", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service hangup(Map<String, Object> params) {
        document.addVerb("hangup", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service hangup() {
        document.addVerb("hangup", new LinkedHashMap<>());
        return this;
    }

    public Service joinConference(Map<String, Object> params) {
        document.addVerb("join_conference", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service joinRoom(Map<String, Object> params) {
        document.addVerb("join_room", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service label(Map<String, Object> params) {
        document.addVerb("label", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service liveTranscribe(Map<String, Object> params) {
        document.addVerb("live_transcribe", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service liveTranslate(Map<String, Object> params) {
        document.addVerb("live_translate", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service pay(Map<String, Object> params) {
        document.addVerb("pay", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service play(Map<String, Object> params) {
        document.addVerb("play", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service prompt(Map<String, Object> params) {
        document.addVerb("prompt", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service receiveFax(Map<String, Object> params) {
        document.addVerb("receive_fax", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service record(Map<String, Object> params) {
        document.addVerb("record", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service recordCall(Map<String, Object> params) {
        document.addVerb("record_call", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service request(Map<String, Object> params) {
        document.addVerb("request", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service returnVerb(Map<String, Object> params) {
        document.addVerb("return", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service sipRefer(Map<String, Object> params) {
        document.addVerb("sip_refer", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service sendDigits(Map<String, Object> params) {
        document.addVerb("send_digits", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service sendFax(Map<String, Object> params) {
        document.addVerb("send_fax", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service sendSms(Map<String, Object> params) {
        document.addVerb("send_sms", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service set(Map<String, Object> params) {
        document.addVerb("set", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    /**
     * Sleep takes an integer (milliseconds), not a map.
     */
    public Service sleep(int milliseconds) {
        document.addVerb("sleep", milliseconds);
        return this;
    }

    public Service stopDenoise(Map<String, Object> params) {
        document.addVerb("stop_denoise", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service stopRecordCall(Map<String, Object> params) {
        document.addVerb("stop_record_call", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service stopTap(Map<String, Object> params) {
        document.addVerb("stop_tap", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service switchVerb(Map<String, Object> params) {
        document.addVerb("switch", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service tap(Map<String, Object> params) {
        document.addVerb("tap", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service transfer(Map<String, Object> params) {
        document.addVerb("transfer", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service unset(Map<String, Object> params) {
        document.addVerb("unset", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    public Service userEvent(Map<String, Object> params) {
        document.addVerb("user_event", params != null ? params : new LinkedHashMap<>());
        return this;
    }

    // -------- HTTP Server --------

    /**
     * Read request body with size limit.
     */
    protected String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            byte[] buf = new byte[MAX_REQUEST_BODY_SIZE + 1];
            int total = 0;
            int n;
            while ((n = is.read(buf, total, buf.length - total)) > 0) {
                total += n;
                if (total > MAX_REQUEST_BODY_SIZE) {
                    throw new IOException("Request body exceeds maximum size");
                }
            }
            return new String(buf, 0, total, StandardCharsets.UTF_8);
        }
    }

    /**
     * Send a JSON response.
     */
    protected void sendJson(HttpExchange exchange, int status, Object body) throws IOException {
        String json = gson.toJson(body);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Send a 401 Unauthorized response.
     */
    protected void sendUnauthorized(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=\"SWML Service\"");
        exchange.sendResponseHeaders(401, -1);
        exchange.close();
    }

    /**
     * Send a 413 Payload Too Large response.
     */
    protected void sendPayloadTooLarge(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(413, -1);
        exchange.close();
    }

    /**
     * Start the HTTP server with health, ready, and main SWML endpoint.
     */
    public void serve() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(host, port), 0);
        httpServer.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        // Health endpoint (no auth)
        httpServer.createContext("/health", exchange -> {
            try {
                sendJson(exchange, 200, Map.of("status", "healthy"));
            } catch (Exception e) {
                log.error("Health handler error", e);
            }
        });

        // Ready endpoint (no auth)
        httpServer.createContext("/ready", exchange -> {
            try {
                sendJson(exchange, 200, Map.of("status", "ready"));
            } catch (Exception e) {
                log.error("Ready handler error", e);
            }
        });

        // Main SWML endpoint (with auth)
        String swmlPath = route.isEmpty() || route.equals("/") ? "/" : route;
        httpServer.createContext(swmlPath, exchange -> {
            try {
                if (!validateAuth(exchange)) {
                    sendUnauthorized(exchange);
                    return;
                }
                addSecurityHeaders(exchange);
                sendJson(exchange, 200, document.toMap());
            } catch (Exception e) {
                log.error("SWML handler error", e);
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        });

        httpServer.start();
        log.info("Service '%s' listening on %s:%d%s", name, host, port, swmlPath);
    }

    /**
     * Stop the HTTP server.
     */
    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            log.info("Service '%s' stopped", name);
        }
    }
}
