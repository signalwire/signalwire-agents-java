/*
 * Copyright (c) 2025 SignalWire
 *
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 */
package com.signalwire.sdk.rest.namespaces;

import com.signalwire.sdk.rest.CrudResource;
import com.signalwire.sdk.rest.HttpClient;

import java.util.Map;

/**
 * REST namespace for phone number management.
 */
public class PhoneNumbersNamespace {

    private final CrudResource numbers;
    private final HttpClient httpClient;

    public PhoneNumbersNamespace(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.numbers = new CrudResource(httpClient, "/phone_numbers");
    }

    /** List all phone numbers. */
    public Map<String, Object> list() { return numbers.list(); }

    /** List phone numbers with query parameters. */
    public Map<String, Object> list(Map<String, String> queryParams) { return numbers.list(queryParams); }

    /** Get a single phone number. */
    public Map<String, Object> get(String id) { return numbers.get(id); }

    /** Purchase a phone number. */
    public Map<String, Object> create(Map<String, Object> body) { return numbers.create(body); }

    /** Update a phone number. */
    public Map<String, Object> update(String id, Map<String, Object> body) { return numbers.update(id, body); }

    /** Release a phone number. */
    public Map<String, Object> delete(String id) { return numbers.delete(id); }

    /** Search available phone numbers. */
    public Map<String, Object> search(Map<String, String> queryParams) {
        return httpClient.get("/phone_numbers/search", queryParams);
    }

    /** Get the underlying CRUD resource. */
    public CrudResource getResource() { return numbers; }
}
