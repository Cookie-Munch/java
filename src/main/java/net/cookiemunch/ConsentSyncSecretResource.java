package net.cookiemunch;

import java.util.Map;

/**
 * A site's consent-sync signing secret. Your server signs consent-sync tokens with it so a signed-in
 * visitor's consent decision follows them across browsers (hosted consent sync). Reading needs
 * sites:read; rotate and remove need sites:write and consent:write.
 */
public final class ConsentSyncSecretResource {

  private final CookieMunch client;

  ConsentSyncSecretResource(CookieMunch client) {
    this.client = client;
  }

  /** {@code GET /v1/sites/{cbid}/consent-sync-secret}: {@code configured}, {@code secretLast4}, {@code createdAt}. Never the secret. */
  public Map<String, Object> get(String cbid) {
    return client.requestMap("GET", path(cbid), null);
  }

  /**
   * {@code POST /v1/sites/{cbid}/consent-sync-secret}: mint the secret, or replace it (the old one stops at
   * once). The answer's {@code secret} is shown this once: keep it on your server, never in a page.
   */
  public Map<String, Object> rotate(String cbid) {
    return client.requestMap("POST", path(cbid), null);
  }

  /** {@code DELETE /v1/sites/{cbid}/consent-sync-secret}: hosted consent sync stops for the site. */
  public Map<String, Object> remove(String cbid) {
    return client.requestMap("DELETE", path(cbid), null);
  }

  private static String path(String cbid) {
    return "/v1/sites/" + Query.pathSegment(cbid) + "/consent-sync-secret";
  }
}
