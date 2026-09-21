package net.cookiemunch;

import net.cookiemunch.model.Identifier;
import net.cookiemunch.model.ProfileAttribute;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/profile} endpoints: attributes, with consent enforced when they are used. */
public final class ProfileResource {

  private final CookieMunch client;

  ProfileResource(CookieMunch client) {
    this.client = client;
  }

  public Map<String, Object> get(List<Identifier> identifiers) {
    return client.requestMap("POST", "/v1/profile/get", Map.of("identifiers", identifiers));
  }

  public Map<String, Object> setAttributes(List<Identifier> identifiers, Map<String, ProfileAttribute> attributes) {
    return client.requestMap("POST", "/v1/profile/attributes", Map.of("identifiers", identifiers, "attributes", attributes));
  }

  /** Attribute values usable for {@code purpose} — empty when the person has not consented to it. */
  public Map<String, Object> activate(List<Identifier> identifiers, String purpose) {
    return client.requestMap("POST", "/v1/profile/activate", Map.of("identifiers", identifiers, "purpose", purpose));
  }
}
