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
 * REST namespace for compatibility (CXML/Twilio-compatible) resources.
 */
public class CompatNamespace {

    private final CrudResource calls;
    private final CrudResource messages;
    private final CrudResource accounts;
    private final CrudResource recordings;
    private final CrudResource queues;
    private final CrudResource conferences;
    private final CrudResource transcriptions;
    private final CrudResource applications;
    private final CrudResource sipDomains;
    private final CrudResource sipCredentialLists;
    private final CrudResource sipIpAccessControlLists;

    public CompatNamespace(HttpClient httpClient, String accountSid) {
        String base = "/compat/2010-04-01/Accounts/" + accountSid;
        this.calls = new CrudResource(httpClient, base + "/Calls");
        this.messages = new CrudResource(httpClient, base + "/Messages");
        this.accounts = new CrudResource(httpClient, base);
        this.recordings = new CrudResource(httpClient, base + "/Recordings");
        this.queues = new CrudResource(httpClient, base + "/Queues");
        this.conferences = new CrudResource(httpClient, base + "/Conferences");
        this.transcriptions = new CrudResource(httpClient, base + "/Transcriptions");
        this.applications = new CrudResource(httpClient, base + "/Applications");
        this.sipDomains = new CrudResource(httpClient, base + "/SIP/Domains");
        this.sipCredentialLists = new CrudResource(httpClient, base + "/SIP/CredentialLists");
        this.sipIpAccessControlLists = new CrudResource(httpClient, base + "/SIP/IpAccessControlLists");
    }

    public CrudResource calls() { return calls; }
    public CrudResource messages() { return messages; }
    public CrudResource accounts() { return accounts; }
    public CrudResource recordings() { return recordings; }
    public CrudResource queues() { return queues; }
    public CrudResource conferences() { return conferences; }
    public CrudResource transcriptions() { return transcriptions; }
    public CrudResource applications() { return applications; }
    public CrudResource sipDomains() { return sipDomains; }
    public CrudResource sipCredentialLists() { return sipCredentialLists; }
    public CrudResource sipIpAccessControlLists() { return sipIpAccessControlLists; }
}
