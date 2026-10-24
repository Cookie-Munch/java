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

  /**
   * Set a key's expiry (epoch ms) or, with {@code null}, remove it — {@code PATCH /v1/keys/{prefix}}
   * with {@code expiresAt}.
   */
  public Map<String, Object> setExpiry(String prefix, Long expiresAt) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("expiresAt", expiresAt);
    return client.requestMapKeepingNulls("PATCH", "/v1/keys/" + Query.pathSegment(prefix), body);
  }

  /**
   * Key hygiene — {@code GET /v1/keys/report}: keys unused for {@code unusedDays} (null = 90), keys
   * with broad access, and keys expiring within {@code expiringDays} (null = 14), each with a reason.
   */
  public Map<String, Object> report(Integer unusedDays, Integer expiringDays) {
    StringBuilder q = new StringBuilder();
    if (unusedDays != null) q.append(q.length() == 0 ? "?" : "&").append("unusedDays=").append(unusedDays);
    if (expiringDays != null) q.append(q.length() == 0 ? "?" : "&").append("expiringDays=").append(expiringDays);
    return client.getMap("/v1/keys/report" + q);
  }

  /** The suite-standard listing — {@code GET /v1/keys?shape=standard}. */
  public Map<String, Object> listStandard() {
    return client.getMap("/v1/keys?shape=standard");
  }

  /** The calling key itself, with any key — {@code GET /v1/keys/self}. */
  public Map<String, Object> self() {
    return client.getMap("/v1/keys/self");
  }

  /**
   * The calling key rotates itself — {@code POST /v1/keys/self/roll}. The old secret stops at once;
   * build the client with an {@code Idempotency-Key} header so a lost answer replays.
   */
  public Map<String, Object> rollSelf() {
    return client.requestMap("POST", "/v1/keys/self/roll", null);
  }

  /**
   * A short-lived key (at most 12 hours) within this key's own scopes and properties —
   * {@code POST /v1/keys/vend}. {@code opts} may carry {@code cbids} and {@code name}.
   */
  public Map<String, Object> vend(List<String> scopes, int ttlSeconds, Map<String, Object> opts) {
    Map<String, Object> body = new java.util.LinkedHashMap<>(opts == null ? Map.of() : opts);
    body.put("scopes", scopes);
    body.put("ttlSeconds", ttlSeconds);
    return client.requestMap("POST", "/v1/keys/vend", body);
  }

  /** Revoke a published secret — {@code POST /v1/keys/revoke_leaked}; always answers {@code received}. */
  public Map<String, Object> revokeLeaked(String key, String source) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("key", key);
    if (source != null) body.put("source", source);
    return client.requestMap("POST", "/v1/keys/revoke_leaked", body);
  }
}
