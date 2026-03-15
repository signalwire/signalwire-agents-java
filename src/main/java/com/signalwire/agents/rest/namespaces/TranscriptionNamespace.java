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
 * REST namespace for transcription resources.
 */
public class TranscriptionNamespace {

    private final CrudResource transcriptions;

    public TranscriptionNamespace(HttpClient httpClient) {
        this.transcriptions = new CrudResource(httpClient, "/calling/transcriptions");
    }

    public CrudResource transcriptions() { return transcriptions; }
}
