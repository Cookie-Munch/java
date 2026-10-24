package net.cookiemunch;

import java.util.Map;

/**
 * A site's rights-request settings: {@code graceDays} (0-30; 7 for a new site, 0 until set for an existing one), how long an erasure or account
 * closure waits before any system acts. Needs dsar:read / dsar:write.
 */
public final class DsarSettingsResource {

  private final CookieMunch client;

  DsarSettingsResource(CookieMunch client) {
    this.client = client;
  }

  /** {@code GET /v1/sites/{cbid}/dsar-settings}. */
  public Map<String, Object> get(String cbid) {
    return client.getMap(path(cbid));
  }

  /** {@code PUT /v1/sites/{cbid}/dsar-settings}. Applies to cases opened afterwards. */
  public Map<String, Object> set(String cbid, int graceDays) {
    return client.requestMap("PUT", path(cbid), Map.of("graceDays", graceDays));
  }

  private static String path(String cbid) {
    return "/v1/sites/" + Query.pathSegment(cbid) + "/dsar-settings";
  }
}
