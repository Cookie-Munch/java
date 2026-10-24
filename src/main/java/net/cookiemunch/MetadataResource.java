package net.cookiemunch;

import java.util.LinkedHashMap;
import java.util.Map;

/** Tags on created resources — {@code /v1/metadata/{type}/{id}} (type: org, site, webhook, executor, dsar, partner_link). */
public final class MetadataResource {

  private final CookieMunch client;

  MetadataResource(CookieMunch client) {
    this.client = client;
  }

  /** A resource's tags, externalRef and sandbox flag — {@code GET /v1/metadata/{type}/{id}}. */
  public Map<String, Object> get(String type, String id) {
    return client.getMap("/v1/metadata/" + Query.pathSegment(type) + "/" + Query.pathSegment(id));
  }

  /**
   * Merge tags (a null value removes one) — {@code PATCH /v1/metadata/{type}/{id}}. Nulls are kept on
   * the wire. {@code forgedeck_sandbox} cannot be set here.
   */
  public Map<String, Object> update(String type, String id, Map<String, Object> metadata, boolean dryRun) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("metadata", new LinkedHashMap<>(metadata));
    return client.requestMapKeepingNulls("PATCH", "/v1/metadata/" + Query.pathSegment(type) + "/" + Query.pathSegment(id) + (dryRun ? "?dryRun=1" : ""), body);
  }
}
