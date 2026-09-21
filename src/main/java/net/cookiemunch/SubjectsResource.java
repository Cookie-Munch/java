package net.cookiemunch;

import java.util.Map;

/** One person's consent across every site in the org, by the subject id your apps attach. */
public final class SubjectsResource {

  private final CookieMunch client;

  SubjectsResource(CookieMunch client) {
    this.client = client;
  }

  /** {@code GET /v1/subjects/{id}/consent}. Needs consent:read; not available to property-locked keys. */
  public Map<String, Object> consent(String subjectId) {
    return client.getMap("/v1/subjects/" + Query.pathSegment(subjectId) + "/consent");
  }
}
