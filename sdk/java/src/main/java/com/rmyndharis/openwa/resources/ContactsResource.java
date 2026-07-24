package com.rmyndharis.idawhats.resources;

import static com.rmyndharis.idawhats.http.Http.encodeSegment;

import com.rmyndharis.idawhats.IdaWhatsClient;
import com.rmyndharis.idawhats.http.HttpMethod;
import com.rmyndharis.idawhats.model.CheckNumberResponse;
import com.rmyndharis.idawhats.model.ContactPhoneResponse;
import com.rmyndharis.idawhats.model.ContactRecord;
import com.rmyndharis.idawhats.model.ListContactsQuery;
import com.rmyndharis.idawhats.model.ProfilePictureResponse;
import com.rmyndharis.idawhats.model.SuccessResult;
import java.util.List;

/** Contacts resource — contact lookup and management. */
public final class ContactsResource {
    private final IdaWhatsClient client;

    public ContactsResource(IdaWhatsClient client) {
        this.client = client;
    }

    /** List contacts known to the session. */
    public List<ContactRecord> list(String sessionId, ListContactsQuery query) {
        return client.requestList(
            HttpMethod.GET,
            "/api/sessions/" + encodeSegment(sessionId) + "/contacts",
            query,
            null,
            ContactRecord.class);
    }

    /** Get details for a single contact by id (JID). */
    public ContactRecord get(String sessionId, String contactId) {
        return client.request(
            HttpMethod.GET,
            "/api/sessions/" + encodeSegment(sessionId) + "/contacts/" + encodeSegment(contactId),
            null,
            null,
            ContactRecord.class);
    }

    /** Check whether a phone number is registered on WhatsApp. */
    public CheckNumberResponse check(String sessionId, String number) {
        return client.request(
            HttpMethod.GET,
            "/api/sessions/" + encodeSegment(sessionId) + "/contacts/check/" + encodeSegment(number),
            null,
            null,
            CheckNumberResponse.class);
    }

    /** Get the contact's profile picture URL (or null). */
    public ProfilePictureResponse profilePicture(String sessionId, String contactId) {
        return client.request(
            HttpMethod.GET,
            "/api/sessions/" + encodeSegment(sessionId) + "/contacts/" + encodeSegment(contactId) + "/profile-picture",
            null,
            null,
            ProfilePictureResponse.class);
    }

    /** Resolve a contact id (e.g. a {@code @lid}) to a phone number. */
    public ContactPhoneResponse phone(String sessionId, String contactId) {
        return client.request(
            HttpMethod.GET,
            "/api/sessions/" + encodeSegment(sessionId) + "/contacts/" + encodeSegment(contactId) + "/phone",
            null,
            null,
            ContactPhoneResponse.class);
    }

    /** Block a contact. Requires an OPERATOR-level key. */
    public SuccessResult block(String sessionId, String contactId) {
        return client.request(
            HttpMethod.POST,
            "/api/sessions/" + encodeSegment(sessionId) + "/contacts/" + encodeSegment(contactId) + "/block",
            null,
            null,
            SuccessResult.class);
    }

    /** Unblock a contact. Requires an OPERATOR-level key. */
    public SuccessResult unblock(String sessionId, String contactId) {
        return client.request(
            HttpMethod.DELETE,
            "/api/sessions/" + encodeSegment(sessionId) + "/contacts/" + encodeSegment(contactId) + "/block",
            null,
            null,
            SuccessResult.class);
    }
}
