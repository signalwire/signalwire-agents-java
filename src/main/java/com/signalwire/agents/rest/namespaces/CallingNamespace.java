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
 * REST namespace for calling-related resources.
 */
public class CallingNamespace {

    private final CrudResource calls;

    public CallingNamespace(HttpClient httpClient) {
        this.calls = new CrudResource(httpClient, "/calling/calls");
    }

    public CrudResource calls() { return calls; }
}
