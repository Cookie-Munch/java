package net.cookiemunch;

import net.cookiemunch.model.ApiKeyIssueInput;
import net.cookiemunch.model.ApiKeyIssued;
import net.cookiemunch.model.ApiKeyPrefix;

import java.util.List;
import java.util.Map;

/** The {@code /v1/keys} endpoints. */
public final class KeysResource {

  private final CookieMunch client;

  KeysResource(CookieMunch client) {
    this.client = client;
  }

  /** List API-key prefixes (display metadata; never the secret) — {@code GET /v1/keys}. */
  public List<ApiKeyPrefix> list() {
    return client.getList("/v1/keys", ApiKeyPrefix.class);
  }

  /** Issue a new API key. The key is shown ONCE — {@code POST /v1/keys}. */
  public ApiKeyIssued issue() {
    return client.request("POST", "/v1/keys", Map.of(), ApiKeyIssued.class);
  }

  /** Issue a new named API key — {@code POST /v1/keys}. */
  public ApiKeyIssued issue(ApiKeyIssueInput input) {
    Object body = input != null ? input : Map.of();
    return client.request("POST", "/v1/keys", body, ApiKeyIssued.class);
  }
}
