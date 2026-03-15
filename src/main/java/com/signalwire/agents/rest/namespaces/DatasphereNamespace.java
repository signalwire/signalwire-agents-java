/*
 * Copyright (c) 2025 SignalWire
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */
package com.signalwire.agents.rest.namespaces;

import com.signalwire.agents.rest.CrudResource;
import com.signalwire.agents.rest.HttpClient;

import java.util.Map;

/**
 * REST namespace for DataSphere (knowledge base) resources.
 */
public class DatasphereNamespace {

    private final CrudResource documents;
    private final HttpClient httpClient;

    public DatasphereNamespace(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.documents = new CrudResource(httpClient, "/datasphere/documents");
    }

    public CrudResource documents() { return documents; }

    /** Search the knowledge base. */
    public Map<String, Object> search(Map<String, Object> body) {
        return httpClient.post("/datasphere/documents/search", body);
    }
}
