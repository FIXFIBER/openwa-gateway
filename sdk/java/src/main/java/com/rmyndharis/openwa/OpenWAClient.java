package com.rmyndharis.idawhats;

import com.google.gson.Gson;
import com.rmyndharis.idawhats.errors.IdaWhatsApiError;
import com.rmyndharis.idawhats.errors.IdaWhatsError;
import com.rmyndharis.idawhats.http.DefaultHttpTransport;
import com.rmyndharis.idawhats.http.Http;
import com.rmyndharis.idawhats.http.HttpMethod;
import com.rmyndharis.idawhats.http.HttpRequestData;
import com.rmyndharis.idawhats.http.HttpResponseData;
import com.rmyndharis.idawhats.http.HttpTransport;
import com.rmyndharis.idawhats.model.AuthValidateResponse;
import com.rmyndharis.idawhats.resources.CallsResource;
import com.rmyndharis.idawhats.resources.CatalogResource;
import com.rmyndharis.idawhats.resources.ChannelsResource;
import com.rmyndharis.idawhats.resources.ChatsResource;
import com.rmyndharis.idawhats.resources.ContactsResource;
import com.rmyndharis.idawhats.resources.GroupsResource;
import com.rmyndharis.idawhats.resources.HealthResource;
import com.rmyndharis.idawhats.resources.LabelsResource;
import com.rmyndharis.idawhats.resources.MessagesResource;
import com.rmyndharis.idawhats.resources.ProfileResource;
import com.rmyndharis.idawhats.resources.SearchResource;
import com.rmyndharis.idawhats.resources.SessionsResource;
import com.rmyndharis.idawhats.resources.StatusResource;
import com.rmyndharis.idawhats.resources.TemplatesResource;
import com.rmyndharis.idawhats.resources.WebhooksResource;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * The single entry point to the IdaWhats SDK. Holds configuration and exposes
 * domain resources as fields:
 *
 * <pre>{@code
 * IdaWhatsClient client = new IdaWhatsClient("http://localhost:2785", "owa_k1_…");
 * client.sessions.start("my-session");
 * client.messages.sendText("my-session",
 *     SendTextRequest.builder().chatId("628123456789@c.us").text("Hello!").build());
 * }</pre>
 */
public final class IdaWhatsClient {
    private final Gson gson = new Gson();
    private final ClientConfig config;
    private final HttpTransport transport;

    // ── Resources ──────────────────────────────────────────────────────
    public final SessionsResource sessions = new SessionsResource(this);
    public final MessagesResource messages = new MessagesResource(this);
    public final SearchResource search = new SearchResource(this);
    public final ContactsResource contacts = new ContactsResource(this);
    public final GroupsResource groups = new GroupsResource(this);
    public final WebhooksResource webhooks = new WebhooksResource(this);
    public final ChatsResource chats = new ChatsResource(this);
    public final LabelsResource labels = new LabelsResource(this);
    public final ChannelsResource channels = new ChannelsResource(this);
    public final CatalogResource catalog = new CatalogResource(this);
    public final StatusResource status = new StatusResource(this);
    public final TemplatesResource templates = new TemplatesResource(this);
    public final HealthResource health = new HealthResource(this);
    public final ProfileResource profile = new ProfileResource(this);
    public final CallsResource calls = new CallsResource(this);

    public IdaWhatsClient(ClientConfig config) {
        // ClientConfig's constructor validates baseUrl/apiKey/timeout, so config is already sound here.
        this.config = config;
        this.transport = config.transport != null ? config.transport : new DefaultHttpTransport();
    }

    public IdaWhatsClient(String baseUrl, String apiKey) {
        this(ClientConfig.builder().baseUrl(baseUrl).apiKey(apiKey).build());
    }

    /** Validate the configured API key and resolve its role. */
    public AuthValidateResponse auth() {
        return request(HttpMethod.POST, "/api/auth/validate", null, null, AuthValidateResponse.class);
    }

    // ── Internal request API used by all resources ─────────────────────

    /** Issue a request and deserialize a single object (or {@code null} for 204/empty). */
    public <T> T request(HttpMethod method, String path, Object query, Object body, Class<T> type) {
        HttpResponseData res = execute(method, path, query, body);
        if (res.status() == 204 || res.body() == null || res.body().isEmpty()) {
            return null;
        }
        return gson.fromJson(res.body(), type);
    }

    /** Issue a request and deserialize a JSON array into a {@code List} (empty for 204/empty). */
    @SuppressWarnings("unchecked")
    public <T> List<T> requestList(HttpMethod method, String path, Object query, Object body, Class<T> elementType) {
        HttpResponseData res = execute(method, path, query, body);
        if (res.status() == 204 || res.body() == null || res.body().isEmpty()) {
            return List.of();
        }
        Class<T[]> arrayType = (Class<T[]>) Array.newInstance(elementType, 0).getClass();
        T[] arr = gson.fromJson(res.body(), arrayType);
        return arr == null ? List.of() : List.of(arr);
    }

    /** Issue a request that returns no body. */
    public void requestVoid(HttpMethod method, String path, Object query, Object body) {
        execute(method, path, query, body);
    }

    private HttpResponseData execute(HttpMethod method, String path, Object query, Object body) {
        String url = Http.buildUrl(config.baseUrl, path, query, gson);
        Map<String, String> headers = Http.mergeHeaders(config.defaultHeaders, null, config.apiKey);
        String bodyJson = body != null ? gson.toJson(body) : null;
        HttpRequestData reqData = new HttpRequestData(method, url, headers, bodyJson, config.timeout);
        HttpResponseData res;
        try {
            // A timeout surfaces as IdaWhatsTimeoutError (unchecked) and propagates.
            res = transport.send(reqData);
        } catch (IOException e) {
            throw new IdaWhatsError("Transport error — " + method + " " + path + ": " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IdaWhatsError("Transport interrupted — " + method + " " + path);
        } catch (IllegalArgumentException e) {
            // e.g. a JDK-restricted header name in defaultHeaders, or a URI-illegal char — keep it
            // inside the IdaWhatsError contract instead of leaking a raw JDK exception to the caller.
            throw new IdaWhatsError("Invalid request — " + method + " " + path + ": " + e.getMessage());
        }
        if (res.status() < 200 || res.status() >= 300) {
            throw IdaWhatsApiError.fromResponse(res.status(), "", res.body(), method + " " + path);
        }
        return res;
    }
}
