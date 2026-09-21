package net.cookiemunch;

import net.cookiemunch.model.PreferenceItem;

import java.util.List;
import java.util.Map;

/** The {@code /v1/preferences} endpoints. */
public final class PreferencesResource {

  private final CookieMunch client;

  PreferencesResource(CookieMunch client) {
    this.client = client;
  }

  /** List the org's preference-center records — {@code GET /v1/preferences}. */
  public List<PreferenceItem> list() {
    return client.getList("/v1/preferences", PreferenceItem.class);
  }

  /**
   * Record a subject's purpose choices — {@code POST /v1/preferences}. The response shape is
   * open; it is returned as a generic map.
   */
  public Map<String, Object> save(String subjectId, Map<String, Boolean> purposes) {
    Map<String, Object> body = Map.of("subjectId", subjectId, "purposes", purposes);
    return client.requestMap("POST", "/v1/preferences", body);
  }

  /**
   * One subject's preference record — {@code GET /v1/preferences/{subjectId}}. A subject with
   * none has empty {@code purposes}. Requires consent:read.
   */
  public Map<String, Object> get(String subjectId) {
    return client.getMap("/v1/preferences/" + Query.pathSegment(subjectId));
  }
}
