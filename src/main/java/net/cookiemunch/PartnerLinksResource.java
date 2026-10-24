package net.cookiemunch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Suite-standard partner links — {@code /v1/partner_links}. */
public final class PartnerLinksResource {

  private final CookieMunch client;

  PartnerLinksResource(CookieMunch client) {
    this.client = client;
  }

  /** Every partner link of the organisation — {@code GET /v1/partner_links} (null metadata: all). */
  public List<Map<String, Object>> list(Map<String, String> metadata) {
    return client.getListOfMaps("/v1/partner_links" + Query.metadata(metadata));
  }

  /**
   * Make a partner link — {@code POST /v1/partner_links}. {@code resource} is {@code site:<cbid>} or
   * {@code org:<id>}; {@code opts} may carry {@code expires_at}, {@code externalId}, {@code label}.
   */
  public Map<String, Object> create(String partner, String resource, List<String> scopes, Map<String, Object> opts) {
    Map<String, Object> body = new LinkedHashMap<>(opts == null ? Map.of() : opts);
    body.put("partner", partner);
    body.put("resource", resource);
    body.put("scopes", scopes);
    return client.requestMap("POST", "/v1/partner_links", body);
  }

  /** Revoke a partner link at once — {@code DELETE /v1/partner_links/{id}}. */
  public Map<String, Object> revoke(String id) {
    return client.requestMap("DELETE", "/v1/partner_links/" + Query.pathSegment(id), null);
  }
}
