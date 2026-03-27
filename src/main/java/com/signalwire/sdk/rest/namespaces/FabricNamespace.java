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
 * REST namespace for SignalWire Fabric resources.
 */
public class FabricNamespace {

    private final CrudResource subscribers;
    private final CrudResource addresses;
    private final CrudResource resources;

    public FabricNamespace(HttpClient httpClient) {
        this.subscribers = new CrudResource(httpClient, "/fabric/subscribers");
        this.addresses = new CrudResource(httpClient, "/fabric/addresses");
        this.resources = new CrudResource(httpClient, "/fabric/resources");
    }

    public CrudResource subscribers() { return subscribers; }
    public CrudResource addresses() { return addresses; }
    public CrudResource resources() { return resources; }
}
