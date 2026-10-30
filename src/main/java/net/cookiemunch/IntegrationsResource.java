package net.cookiemunch;

import java.util.LinkedHashMap;
import java.util.Map;

/** Cards for support, CRM and helpdesk sidebars (dsar:read), and identity-provider connect (dsar:write). */
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

  /**
   * {@code POST /v1/integrations/{provider}/connect}: connect an identity provider's privacy orchestrator. Mints a
   * dsar:write key locked to {@code cbid} and hands it to the provider, authorised with {@code providerKey}; the
   * minted key is never returned, only its prefix. {@code connector} ({@code url, method, body}) only for a provider
   * with no built-in connector, else null. Needs dsar:write (no keys:write); {@code dryRun} mints and sends nothing.
   */
  public Map<String, Object> connect(String provider, String cbid, String providerKey, Map<String, Object> connector, boolean dryRun) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("cbid", cbid);
    body.put("providerKey", providerKey);
    if (connector != null) {
      body.put("connector", connector);
    }
    return client.requestMap("POST", "/v1/integrations/" + Query.pathSegment(provider) + "/connect" + (dryRun ? "?dryRun=1" : ""), body);
  }
}
