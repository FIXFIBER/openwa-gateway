package com.rmyndharis.idawhats.resources;

import static com.rmyndharis.idawhats.http.Http.encodeSegment;

import com.rmyndharis.idawhats.IdaWhatsClient;
import com.rmyndharis.idawhats.http.HttpMethod;
import com.rmyndharis.idawhats.model.SendImageStatusRequest;
import com.rmyndharis.idawhats.model.SendTextStatusRequest;
import com.rmyndharis.idawhats.model.SendVideoStatusRequest;
import com.rmyndharis.idawhats.model.StatusListResult;
import com.rmyndharis.idawhats.model.StatusResult;

/**
 * Status (Stories) resource — WhatsApp status updates.
 *
 * <p>NOTE: this is WhatsApp "Status/Stories", distinct from session lifecycle status.
 */
public final class StatusResource {
    private final IdaWhatsClient client;

    public StatusResource(IdaWhatsClient client) {
        this.client = client;
    }

    /** Get all status updates. */
    public StatusListResult list(String sessionId) {
        return client.request(
            HttpMethod.GET,
            "/api/sessions/" + encodeSegment(sessionId) + "/status",
            null,
            null,
            StatusListResult.class);
    }

    /** Get status updates from a specific contact. */
    public StatusListResult fromContact(String sessionId, String contactId) {
        return client.request(
            HttpMethod.GET,
            "/api/sessions/" + encodeSegment(sessionId) + "/status/" + encodeSegment(contactId),
            null,
            null,
            StatusListResult.class);
    }

    /** Post a text status update. */
    public StatusResult sendText(String sessionId, SendTextStatusRequest body) {
        return client.request(
            HttpMethod.POST,
            "/api/sessions/" + encodeSegment(sessionId) + "/status/send-text",
            null,
            body,
            StatusResult.class);
    }

    /** Post an image status update. */
    public StatusResult sendImage(String sessionId, SendImageStatusRequest body) {
        return client.request(
            HttpMethod.POST,
            "/api/sessions/" + encodeSegment(sessionId) + "/status/send-image",
            null,
            body,
            StatusResult.class);
    }

    /** Post a video status update. */
    public StatusResult sendVideo(String sessionId, SendVideoStatusRequest body) {
        return client.request(
            HttpMethod.POST,
            "/api/sessions/" + encodeSegment(sessionId) + "/status/send-video",
            null,
            body,
            StatusResult.class);
    }

    /** Delete a status update by id. */
    public void delete(String sessionId, String statusId) {
        client.requestVoid(
            HttpMethod.DELETE,
            "/api/sessions/" + encodeSegment(sessionId) + "/status/" + encodeSegment(statusId),
            null,
            null);
    }
}
