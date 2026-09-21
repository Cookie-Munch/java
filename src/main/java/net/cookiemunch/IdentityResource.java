package net.cookiemunch;

import net.cookiemunch.model.Identifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/identity} endpoints: a person as a cluster of identifiers. Reads are POSTs so identifiers never appear in a URL. */
public final class IdentityResource {

  private final CookieMunch client;

  IdentityResource(CookieMunch client) {
    this.client = client;
  }

  /** The subject id for these identifiers, or a null {@code subjectId} if unknown. */
  public Map<String, Object> resolve(List<Identifier> identifiers) {
    return client.requestMap("POST", "/v1/identity/resolve", Map.of("identifiers", identifiers));
  }

  /** Stitch identifiers into one subject. A durable merge. */
  public Map<String, Object> link(List<Identifier> identifiers) {
    return client.requestMap("POST", "/v1/identity/link", Map.of("identifiers", identifiers));
  }

  /** Every identifier stitched to a subject. */
  public Map<String, Object> cluster(String subjectId) {
    return client.getMap("/v1/identity/" + Query.pathSegment(subjectId));
  }
}
