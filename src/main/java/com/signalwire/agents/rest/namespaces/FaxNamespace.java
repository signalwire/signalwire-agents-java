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
 * REST namespace for fax resources.
 */
public class FaxNamespace {

    private final CrudResource faxes;

    public FaxNamespace(HttpClient httpClient) {
        this.faxes = new CrudResource(httpClient, "/fax/faxes");
    }

    public CrudResource faxes() { return faxes; }
}
