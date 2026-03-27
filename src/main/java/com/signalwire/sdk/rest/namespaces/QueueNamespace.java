/*
 * Copyright (c) 2025 SignalWire
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */
package com.signalwire.sdk.rest.namespaces;

import com.signalwire.sdk.rest.CrudResource;
import com.signalwire.sdk.rest.HttpClient;

/**
 * REST namespace for queue resources.
 */
public class QueueNamespace {

    private final CrudResource queues;

    public QueueNamespace(HttpClient httpClient) {
        this.queues = new CrudResource(httpClient, "/calling/queues");
    }

    public CrudResource queues() { return queues; }
}
