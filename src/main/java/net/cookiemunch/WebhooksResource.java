package net.cookiemunch;

import net.cookiemunch.model.WebhookCreate;
import net.cookiemunch.model.WebhookSubscription;

import java.util.List;

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
}
