package net.cookiemunch;

import net.cookiemunch.model.AiInspectInput;
import net.cookiemunch.model.AiSystem;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/ai} endpoints: AI governance — policy, the inline gateway, inventory and lineage. */
public final class AiResource {

  private final CookieMunch client;

  AiResource(CookieMunch client) {
    this.client = client;
  }

  public Map<String, Object> getPolicy() {
    return client.getMap("/v1/ai/policy");
  }

  /** Replace the AI gateway policy. */
  public Map<String, Object> setPolicy(Map<String, Object> policy) {
    return client.requestMap("PUT", "/v1/ai/policy", Map.of("policy", policy));
  }

  /** Enforce consent and policy on a prompt or response. Needs the ai:inspect scope. */
  public Map<String, Object> inspect(AiInspectInput input) {
    return client.requestMap("POST", "/v1/ai/inspect", input);
  }

  public Map<String, Object> inventory() {
    return client.getMap("/v1/ai/inventory");
  }

  public Map<String, Object> lineage() {
    return client.getMap("/v1/ai/lineage");
  }

  public Map<String, Object> registerSystem(AiSystem system) {
    return client.requestMap("POST", "/v1/ai/systems", system);
  }

  public Map<String, Object> systems() {
    return client.getMap("/v1/ai/systems");
  }

  /** Recent gateway decisions. {@code limit} may be null for the server default. */
  public Map<String, Object> audit(Integer limit) {
    return client.getMap("/v1/ai/audit" + new Query().add("limit", limit));
  }
}
