package net.cookiemunch;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/regulatory} endpoints: the curated privacy-law dataset. */
public final class RegulatoryResource {

  private final CookieMunch client;

  RegulatoryResource(CookieMunch client) {
    this.client = client;
  }

  /** The dataset, optionally narrowed to {@code jurisdictions} (may be null). */
  public Map<String, Object> feed(List<String> jurisdictions) {
    return client.getMap("/v1/regulatory/feed"
        + new Query().add("jurisdictions", jurisdictions == null ? null : String.join(",", jurisdictions)));
  }

  /** What takes effect within {@code days} (may be null for the server default). */
  public Map<String, Object> upcoming(Integer days) {
    return client.getMap("/v1/regulatory/upcoming" + new Query().add("days", days));
  }
}
