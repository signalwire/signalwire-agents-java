/*
 * Copyright (c) 2025 SignalWire
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */
package com.signalwire.agents.rest.namespaces;

import com.signalwire.agents.rest.HttpClient;

import java.util.Map;

/**
 * REST namespace for project management resources.
 */
public class ProjectNamespace {

    private final HttpClient httpClient;

    public ProjectNamespace(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /** Get project info. */
    public Map<String, Object> get() {
        return httpClient.get("/project");
    }

    /** Update project settings. */
    public Map<String, Object> update(Map<String, Object> body) {
        return httpClient.put("/project", body);
    }

    /** List project tokens. */
    public Map<String, Object> listTokens() {
        return httpClient.get("/project/tokens");
    }

    /** Create a project token. */
    public Map<String, Object> createToken(Map<String, Object> body) {
        return httpClient.post("/project/tokens", body);
    }
}
