package com.rmyndharis.idawhats.resources;

import static com.rmyndharis.idawhats.http.Http.encodeSegment;

import com.rmyndharis.idawhats.IdaWhatsClient;
import com.rmyndharis.idawhats.http.HttpMethod;
import com.rmyndharis.idawhats.model.SetProfileNameRequest;
import com.rmyndharis.idawhats.model.SetProfilePictureRequest;
import com.rmyndharis.idawhats.model.SetProfileStatusRequest;
import com.rmyndharis.idawhats.model.SuccessResult;

/** Profile resource — the linked account's display name, status text and picture. */
public final class ProfileResource {
    private final IdaWhatsClient client;

    public ProfileResource(IdaWhatsClient client) {
        this.client = client;
    }

    /** Set the account display name. */
    public SuccessResult setProfileName(String sessionId, String name) {
        return client.request(
            HttpMethod.PUT,
            "/api/sessions/" + encodeSegment(sessionId) + "/profile/name",
            null,
            new SetProfileNameRequest(name),
            SuccessResult.class);
    }

    /** Set the account about/status text (an empty string clears it). */
    public SuccessResult setProfileStatus(String sessionId, String status) {
        return client.request(
            HttpMethod.PUT,
            "/api/sessions/" + encodeSegment(sessionId) + "/profile/status",
            null,
            new SetProfileStatusRequest(status),
            SuccessResult.class);
    }

    /** Set the account profile picture from a {@code url} or a {@code base64} + {@code mimetype} pair. */
    public SuccessResult setProfilePicture(String sessionId, SetProfilePictureRequest body) {
        return client.request(
            HttpMethod.PUT,
            "/api/sessions/" + encodeSegment(sessionId) + "/profile/picture",
            null,
            body,
            SuccessResult.class);
    }
}
