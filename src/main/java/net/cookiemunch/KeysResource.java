package net.cookiemunch;

import net.cookiemunch.model.ApiKeyIssueInput;
import net.cookiemunch.model.ApiKeyIssued;
import net.cookiemunch.model.ApiKeyPrefix;
import net.cookiemunch.model.ApiKeyUpdate;

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

  /** Revoke a key by its prefix — {@code DELETE /v1/keys/{prefix}}. Immediate. */
  public void revoke(String prefix) {
    client.requestVoid("DELETE", "/v1/keys/" + Query.pathSegment(prefix), null);
  }

  /**
   * Rotate a key: a new secret, shown ONCE, with the same name, scopes, property lock and
   * expiry — {@code POST /v1/keys/{prefix}/roll}. The old secret stops working immediately.
   * Requires an unscoped key that is not property-locked.
   */
  public ApiKeyIssued roll(String prefix) {
    return client.request("POST", "/v1/keys/" + Query.pathSegment(prefix) + "/roll", null, ApiKeyIssued.class);
  }

  /**
   * Rename a key, or replace its scopes or property lock — {@code PATCH /v1/keys/{prefix}}.
   * Only the fields sent on {@code patch} change.
   */
  public Map<String, Object> update(String prefix, ApiKeyUpdate patch) {
    return client.requestMap("PATCH", "/v1/keys/" + Query.pathSegment(prefix), patch);
  }
}
