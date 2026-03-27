package com.signalwire.sdk.rest;

import com.signalwire.sdk.rest.namespaces.CallingNamespace;
import com.signalwire.sdk.rest.namespaces.ConferenceNamespace;
import com.signalwire.sdk.rest.namespaces.QueueNamespace;
import com.signalwire.sdk.rest.namespaces.RecordingNamespace;
import com.signalwire.sdk.rest.namespaces.TranscriptionNamespace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for REST calling-related namespaces.
 */
class CallingTest {

    private final HttpClient httpClient = new HttpClient("test.signalwire.com", "proj", "tok");

    @Test
    void testCallingNamespaceCallsPath() {
        var ns = new CallingNamespace(httpClient);
        assertEquals("/calling/calls", ns.calls().getBasePath());
    }

    @Test
    void testCallingNamespaceNotNull() {
        var ns = new CallingNamespace(httpClient);
        assertNotNull(ns.calls());
    }

    @Test
    void testConferenceNamespacePaths() {
        var ns = new ConferenceNamespace(httpClient);
        assertEquals("/calling/conferences", ns.conferences().getBasePath());
        assertEquals("/calling/conferences/participants", ns.participants().getBasePath());
    }

    @Test
    void testQueueNamespacePath() {
        var ns = new QueueNamespace(httpClient);
        assertEquals("/calling/queues", ns.queues().getBasePath());
    }

    @Test
    void testRecordingNamespacePath() {
        var ns = new RecordingNamespace(httpClient);
        assertEquals("/calling/recordings", ns.recordings().getBasePath());
    }

    @Test
    void testTranscriptionNamespacePath() {
        var ns = new TranscriptionNamespace(httpClient);
        assertEquals("/calling/transcriptions", ns.transcriptions().getBasePath());
    }

    @Test
    void testCallingFromClient() {
        var client = RestClient.builder()
                .project("proj").token("tok").space("test.signalwire.com")
                .build();
        assertNotNull(client.calling());
        assertNotNull(client.conferences());
        assertNotNull(client.queues());
        assertNotNull(client.recordings());
        assertNotNull(client.transcriptions());
    }
}
