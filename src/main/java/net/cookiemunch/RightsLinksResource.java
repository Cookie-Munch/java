package net.cookiemunch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Account-linked rights requests: single-use links to the hosted rights page for a signed-in person. */
public final class RightsLinksResource {

  private final CookieMunch client;

  RightsLinksResource(CookieMunch client) {
    this.client = client;
  }

  /**
   * {@code POST /v1/sites/{cbid}/rights-links}. The page locks the person's email and skips the
   * code step; show the returned {@code url} only to them. {@code userId}, {@code rights} and
   * {@code ttlSeconds} (60..3600, default 900) may be null. Needs dsar:write. The result carries
   * {@code url} and {@code expiresAt}.
   */
  public Map<String, Object> create(String cbid, String email, String userId, List<String> rights, Integer ttlSeconds) {
    Map<String, Object> subject = new LinkedHashMap<>();
    subject.put("email", email);
    if (userId != null) subject.put("userId", userId);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("subject", subject);
    if (rights != null) body.put("rights", rights);
    if (ttlSeconds != null) body.put("ttlSeconds", ttlSeconds);
    return client.requestMap("POST", "/v1/sites/" + Query.pathSegment(cbid) + "/rights-links", body);
  }

  /** {@link #create(String, String, String, List, Integer)} with every option left at its default. */
  public Map<String, Object> create(String cbid, String email) {
    return create(cbid, email, null, null, null);
  }
}
