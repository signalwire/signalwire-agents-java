/*
 * Copyright (c) 2025 SignalWire
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */
package com.signalwire.agents.rest.namespaces;

import com.signalwire.agents.rest.CrudResource;
import com.signalwire.agents.rest.HttpClient;

/**
 * REST namespace for audio stream resources.
 */
public class StreamNamespace {

    private final CrudResource streams;

    public StreamNamespace(HttpClient httpClient) {
        this.streams = new CrudResource(httpClient, "/streams");
    }

    public CrudResource streams() { return streams; }
}
