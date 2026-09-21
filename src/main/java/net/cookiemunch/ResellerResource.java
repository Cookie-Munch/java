package net.cookiemunch;

import net.cookiemunch.model.ResellerChildCreate;
import net.cookiemunch.model.ChildKeyInput;
import net.cookiemunch.model.ApiKeyPrefix;
import net.cookiemunch.model.ApiKeyIssued;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/reseller} endpoints: provision and manage child orgs. Needs the reseller:* scopes. */
public final class ResellerResource {

  private final CookieMunch client;

  ResellerResource(CookieMunch client) {
    this.client = client;
  }

  public Map<String, Object> list() {
    return client.getMap("/v1/reseller/customers");
  }

  public Map<String, Object> create(ResellerChildCreate input) {
    return client.requestMap("POST", "/v1/reseller/customers", input);
  }

  public Map<String, Object> get(String id) {
    return client.getMap("/v1/reseller/customers/" + Query.pathSegment(id));
  }

  /**
   * Update a child org. {@code patch} is sent as-is, so a {@code "dsarRouting"} entry
   * mapped to {@code null} clears that override, and omitting the key leaves it alone. The
   * client's JSON encoding drops nulls elsewhere, so this call keeps them deliberately.
   */
  public Map<String, Object> update(String id, Map<String, Object> patch) {
    return client.requestMapKeepingNulls("PATCH", "/v1/reseller/customers/" + Query.pathSegment(id), new LinkedHashMap<>(patch));
  }

  /** Suspend a child org (reversible). {@code purge} deletes it and its data instead — irreversibly. */
  public void deprovision(String id, boolean purge) {
    client.requestVoid("DELETE", "/v1/reseller/customers/" + Query.pathSegment(id) + new Query().add("purge", purge ? "true" : null), null);
  }

  public List<ApiKeyPrefix> listKeys(String id) {
    return client.getList("/v1/reseller/customers/" + Query.pathSegment(id) + "/keys", ApiKeyPrefix.class);
  }

  /** Mint an API key for a child org; the secret is returned once. {@code input} may be null. */
  public ApiKeyIssued mintKey(String id, ChildKeyInput input) {
    return client.request("POST", "/v1/reseller/customers/" + Query.pathSegment(id) + "/keys",
        input == null ? Map.of() : input, ApiKeyIssued.class);
  }

  public void revokeKey(String id, String prefix) {
    client.requestVoid("DELETE", "/v1/reseller/customers/" + Query.pathSegment(id) + "/keys/" + Query.pathSegment(prefix), null);
  }
}
