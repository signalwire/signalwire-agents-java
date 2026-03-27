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
 * REST namespace for recording resources.
 */
public class RecordingNamespace {

    private final CrudResource recordings;

    public RecordingNamespace(HttpClient httpClient) {
        this.recordings = new CrudResource(httpClient, "/calling/recordings");
    }

    public CrudResource recordings() { return recordings; }
}
