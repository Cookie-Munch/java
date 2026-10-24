package net.cookiemunch;

import java.util.Map;

/** Cards for support, CRM and helpdesk sidebars. Needs dsar:read. */
public final class IntegrationsResource {

  private final CookieMunch client;

  IntegrationsResource(CookieMunch client) {
    this.client = client;
  }

  /**
   * {@code GET /v1/integrations/{app}/card}: a person's open rights requests, by {@code iss} and {@code sub}
   * (preferred) or {@code email}; nulls are left out. Never matched across identities on a shared email.
   */
  public Map<String, Object> subjectCard(String app, String iss, String sub, String email) {
    return client.getMap("/v1/integrations/" + Query.pathSegment(app) + "/card"
        + new Query().add("iss", iss).add("sub", sub).add("email", email));
  }
}
