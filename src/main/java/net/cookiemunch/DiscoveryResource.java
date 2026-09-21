package net.cookiemunch;

import net.cookiemunch.model.EnforcementOptions;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/discovery} endpoints: the data map from an in-environment scan (metadata only). */
public final class DiscoveryResource {

  private final CookieMunch client;

  DiscoveryResource(CookieMunch client) {
    this.client = client;
  }

  public Map<String, Object> ingestMap(Map<String, Object> map) {
    return client.requestMap("POST", "/v1/discovery/map", Map.of("map", map));
  }

  public Map<String, Object> getMap() {
    return client.getMap("/v1/discovery/map");
  }

  public Map<String, Object> ropaDrafts() {
    return client.getMap("/v1/discovery/ropa-drafts");
  }

  public Map<String, Object> evidence() {
    return client.getMap("/v1/discovery/evidence");
  }

  /** What changed since the last scan, and where the RoPA disagrees with reality. */
  public Map<String, Object> drift() {
    return client.getMap("/v1/discovery/drift");
  }

  /** Plan masking / row-access policy for postgres, mysql or snowflake. Applies nothing. {@code opts} may be null. */
  public Map<String, Object> planEnforcement(String dialect, List<Map<String, Object>> rules, EnforcementOptions opts) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("dialect", dialect);
    body.put("rules", rules);
    if (opts != null && opts.permitsTable() != null) body.put("permitsTable", opts.permitsTable());
    if (opts != null && opts.policyPrefix() != null) body.put("policyPrefix", opts.policyPrefix());
    return client.requestMap("POST", "/v1/discovery/enforcement", body);
  }
}
