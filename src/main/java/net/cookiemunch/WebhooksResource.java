package net.cookiemunch;

import net.cookiemunch.model.WebhookCreate;
import net.cookiemunch.model.WebhookSecret;
import net.cookiemunch.model.WebhookSubscription;
import net.cookiemunch.model.WebhookTestResult;

import java.util.List;
import java.util.Map;

/** The {@code /v1/webhooks} endpoints. */
public final class WebhooksResource {

  private final CookieMunch client;

  WebhooksResource(CookieMunch client) {
    this.client = client;
  }

  /** List webhook subscriptions (secrets omitted) — {@code GET /v1/webhooks}. */
  public List<WebhookSubscription> list() {
    return client.getList("/v1/webhooks", WebhookSubscription.class);
  }

  /** Create a webhook subscription (the secret is returned ONCE) — {@code POST /v1/webhooks}. */
  public WebhookSubscription create(WebhookCreate input) {
    return client.request("POST", "/v1/webhooks", input, WebhookSubscription.class);
  }

  /** Delete a webhook subscription — {@code DELETE /v1/webhooks/{id}}. */
  public void delete(String id) {
    client.requestVoid("DELETE", "/v1/webhooks/" + Query.pathSegment(id), null);
  }

  /**
   * Change or pause a subscription — {@code PATCH /v1/webhooks/{id}}. {@code patch} is sent
   * as-is: {@code "active" -> false} pauses it, and a {@code "cbid"} entry mapped to
   * {@code null} widens it to every property in the org (nulls are kept for this call).
   */
  public java.util.Map<String, Object> update(String id, java.util.Map<String, Object> patch) {
    return client.requestMapKeepingNulls("PATCH", "/v1/webhooks/" + Query.pathSegment(id), new java.util.LinkedHashMap<>(patch));
  }

  /** Rotate the signing secret — {@code POST /v1/webhooks/{id}/roll}. The new secret is returned ONCE. */
  public WebhookSecret rollSecret(String id) {
    return client.request("POST", "/v1/webhooks/" + Query.pathSegment(id) + "/roll", null, WebhookSecret.class);
  }

  /** Send a signed test event now and report what the endpoint answered — {@code POST /v1/webhooks/{id}/test}. */
  public WebhookTestResult test(String id) {
    return client.request("POST", "/v1/webhooks/" + Query.pathSegment(id) + "/test", null, WebhookTestResult.class);
  }

  /** Deliveries that failed every retry, newest first — {@code GET /v1/webhooks/dead-letters}. */
  public Map<String, Object> deadLetters() {
    return client.getMap("/v1/webhooks/dead-letters");
  }

  /** Deliver a dead letter again, to the subscription as it is now — {@code POST /v1/webhooks/dead-letters/{id}/replay}. */
  public Map<String, Object> replayDeadLetter(String id) {
    return client.requestMap("POST", "/v1/webhooks/dead-letters/" + Query.pathSegment(id) + "/replay", null);
  }
}
