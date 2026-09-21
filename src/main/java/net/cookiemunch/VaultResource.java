package net.cookiemunch;

import net.cookiemunch.model.Identifier;
import net.cookiemunch.model.ConsentDecision;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/vault} endpoints: a resolved person's consent. */
public final class VaultResource {

  private final CookieMunch client;

  VaultResource(CookieMunch client) {
    this.client = client;
  }

  public Map<String, Object> record(List<Identifier> identifiers, List<ConsentDecision> decisions) {
    return client.requestMap("POST", "/v1/vault/record", Map.of("identifiers", identifiers, "decisions", decisions));
  }

  /** Allow/deny per purpose, across all of the person's identifiers. */
  public Map<String, Object> current(List<Identifier> identifiers) {
    return client.requestMap("POST", "/v1/vault/current", Map.of("identifiers", identifiers));
  }

  /** The same decisions in full: legal basis, jurisdiction, provenance, time. */
  public Map<String, Object> permits(List<Identifier> identifiers) {
    return client.requestMap("POST", "/v1/vault/permits", Map.of("identifiers", identifiers));
  }
}
