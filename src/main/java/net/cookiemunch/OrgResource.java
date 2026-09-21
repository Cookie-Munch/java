package net.cookiemunch;

import net.cookiemunch.model.Org;

import java.util.LinkedHashMap;
import java.util.Map;

/** The {@code /v1/org} endpoints. Requires an unscoped key that is not property-locked. */
public final class OrgResource {

  private final CookieMunch client;

  OrgResource(CookieMunch client) {
    this.client = client;
  }

  /** The key's organisation — {@code GET /v1/org}. */
  public Org get() {
    return client.get("/v1/org", Org.class);
  }

  /**
   * Rename the org or set its logo — {@code PATCH /v1/org}. {@code patch} is sent as-is, so a
   * {@code "logoUrl"} entry mapped to {@code null} removes the logo, and omitting the key
   * leaves it unchanged (nulls are kept for this call). Deleting the org is not available
   * through the API.
   */
  public Map<String, Object> update(Map<String, Object> patch) {
    return client.requestMapKeepingNulls("PATCH", "/v1/org", new LinkedHashMap<>(patch));
  }
}
