package net.cookiemunch;

import java.util.LinkedHashMap;
import java.util.Map;

/** The hosted preference centre, opened for a visitor your server has signed in. */
public final class PreferenceCenterResource {

  private final CookieMunch client;

  PreferenceCenterResource(CookieMunch client) {
    this.client = client;
  }

  /**
   * {@code POST /v1/preference-center/tokens}: mint a preference-centre token for the page to pass to
   * {@code CookieMunch.setSubjectToken}. Give exactly one of {@code email} (resolved as the emailed
   * preference link resolves it) or {@code subjectId}; the other is null. {@code ttlSeconds} (1..86400,
   * default 3600) may be null. The token is a credential: never put it in a shared URL or a log. Needs
   * subscriptions:write and profile:write, plus identity:read by email. The result carries
   * {@code token} and {@code expiresAt}.
   */
  public Map<String, Object> mintToken(String cbid, String email, String subjectId, Integer ttlSeconds) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("cbid", cbid);
    if (email != null) body.put("email", email);
    if (subjectId != null) body.put("subjectId", subjectId);
    if (ttlSeconds != null) body.put("ttlSeconds", ttlSeconds);
    return client.requestMap("POST", "/v1/preference-center/tokens", body);
  }
}
