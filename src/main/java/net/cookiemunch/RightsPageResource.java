package net.cookiemunch;

import java.util.Map;

/** The {@code /v1/sites/{cbid}/rights-page} endpoints — the hosted rights page document. */
public final class RightsPageResource {

  private final CookieMunch client;

  RightsPageResource(CookieMunch client) {
    this.client = client;
  }

  private static String base(String cbid) {
    return "/v1/sites/" + Query.pathSegment(cbid) + "/rights-page";
  }

  /** The draft (the default document until first saved), with publish warnings. */
  public Map<String, Object> get(String cbid) {
    return client.getMap(base(cbid));
  }

  /**
   * Replace the draft. Nothing is live until {@link #publish}. Add {@code baseUpdatedAt} (the draft's
   * {@code updatedAt} as last read; null if it had none) to refuse overwriting a newer save: a
   * mismatch is a 409 {@code draft_conflict}.
   */
  public Map<String, Object> put(String cbid, Map<String, Object> page) {
    return client.requestMapKeepingNulls("PUT", base(cbid), new java.util.LinkedHashMap<>(page));
  }

  /** Merge into the draft: objects merge, lists replace, null removes. */
  public Map<String, Object> patch(String cbid, Map<String, Object> page) {
    return client.requestMapKeepingNulls("PATCH", base(cbid), new java.util.LinkedHashMap<>(page));
  }

  /**
   * Validate a write without saving it ({@code ?dryRun=1}). A null value is sent as JSON null,
   * so a preview of a patch that removes a field shows the removal.
   */
  public Map<String, Object> dryRun(String cbid, Map<String, Object> page, boolean replace) {
    return client.requestMapKeepingNulls(replace ? "PUT" : "PATCH", base(cbid) + "?dryRun=1", new java.util.LinkedHashMap<>(page));
  }

  public Map<String, Object> getPublished(String cbid) {
    return client.getMap(base(cbid) + "/published");
  }

  /** Publish the draft; it goes live at once. Prefer {@link #publish(String, Object)} with the base you read. */
  public Map<String, Object> publish(String cbid) {
    return client.requestMap("POST", base(cbid) + "/publish", Map.of());
  }

  /**
   * Publish the draft only if it is still the one you read: {@code baseUpdatedAt} is the draft's
   * {@code updatedAt} (epoch ms or an ISO string; null if it had none). A mismatch is a 409
   * {@code draft_conflict} and nothing goes live.
   */
  public Map<String, Object> publish(String cbid, Object baseUpdatedAt) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("baseUpdatedAt", baseUpdatedAt);
    return client.requestMapKeepingNulls("POST", base(cbid) + "/publish", body);
  }

  public Map<String, Object> versions(String cbid) {
    return client.getMap(base(cbid) + "/versions");
  }

  /**
   * Publish an earlier version again; it goes live at once and REPLACES the current draft
   * (unpublished edits are lost).
   */
  public Map<String, Object> rollback(String cbid, int version) {
    return client.requestMap("POST", base(cbid) + "/rollback", Map.of("version", version));
  }

  /** As {@link #rollback(String, int)}, refused with a 409 {@code draft_conflict} if the draft changed since {@code baseUpdatedAt}. */
  public Map<String, Object> rollback(String cbid, int version, Object baseUpdatedAt) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("version", version);
    body.put("baseUpdatedAt", baseUpdatedAt);
    return client.requestMapKeepingNulls("POST", base(cbid) + "/rollback", body);
  }

  private static String domains(String cbid) {
    return base(cbid) + "/domains";
  }

  /**
   * Custom hostnames serving the rights page, with the plan allowance —
   * {@code GET /v1/sites/{cbid}/rights-page/domains}. Keys: domains, limit (null = unlimited), used, target.
   */
  public Map<String, Object> listDomains(String cbid) {
    return client.requestMap("GET", domains(cbid), null);
  }

  /** Add a custom hostname (e.g. privacy.example.com); {@code dns} holds the records to create. */
  public Map<String, Object> addDomain(String cbid, String hostname) {
    return client.requestMap("POST", domains(cbid), Map.of("hostname", hostname));
  }

  /** Check it now: DNS while pending, the HTTPS answer once routed. */
  public Map<String, Object> verifyDomain(String cbid, String hostname) {
    return client.requestMap("POST", domains(cbid) + "/" + Query.pathSegment(hostname) + "/verify", null);
  }

  /** Remove it: it stops being served and its certificate is not renewed. */
  public void removeDomain(String cbid, String hostname) {
    client.requestVoid("DELETE", domains(cbid) + "/" + Query.pathSegment(hostname), null);
  }
}
