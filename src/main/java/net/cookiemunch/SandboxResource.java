package net.cookiemunch;

import java.util.Map;

/**
 * The organisation's sandbox — {@code /v1/sandbox}: a separate test organisation with the same people
 * and plan features, never billed. Its keys are test keys and its rights requests are all tests.
 * {@code create} and {@code delete} need a full-access key or {@code orgs:create}.
 */
public final class SandboxResource {

  private final CookieMunch client;

  SandboxResource(CookieMunch client) {
    this.client = client;
  }

  /** The sandbox, or null — {@code GET /v1/sandbox}. From a sandbox key, the sandbox itself. */
  @SuppressWarnings("unchecked")
  public Map<String, Object> get() {
    Map<String, Object> r = client.getMap("/v1/sandbox");
    Object s = r == null ? null : r.get("sandbox");
    return s instanceof Map ? (Map<String, Object>) s : null;
  }

  /** Make the sandbox (or reuse it, {@code created: false}) and a new test key for it, shown once — {@code POST /v1/sandbox}. */
  public Map<String, Object> create(String keyName) {
    return client.requestMap("POST", "/v1/sandbox", keyName == null ? Map.of() : Map.of("keyName", keyName));
  }

  /** Erase the sandbox and everything in it — {@code DELETE /v1/sandbox}. Cannot be undone; the live organisation is not touched. */
  public Map<String, Object> delete() {
    return client.requestMap("DELETE", "/v1/sandbox", null);
  }
}
