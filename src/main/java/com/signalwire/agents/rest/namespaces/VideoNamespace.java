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
 * REST namespace for video room resources.
 */
public class VideoNamespace {

    private final CrudResource rooms;
    private final CrudResource roomSessions;
    private final CrudResource recordings;

    public VideoNamespace(HttpClient httpClient) {
        this.rooms = new CrudResource(httpClient, "/video/rooms");
        this.roomSessions = new CrudResource(httpClient, "/video/room_sessions");
        this.recordings = new CrudResource(httpClient, "/video/recordings");
    }

    public CrudResource rooms() { return rooms; }
    public CrudResource roomSessions() { return roomSessions; }
    public CrudResource recordings() { return recordings; }
}
