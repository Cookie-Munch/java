package net.cookiemunch;

import net.cookiemunch.model.FulfillmentSystem;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** DSAR fulfilment: plan work per system, and the in-environment agent's protocol. */
public final class FulfillmentResource {

  private final CookieMunch client;

  FulfillmentResource(CookieMunch client) {
    this.client = client;
  }

  public Map<String, Object> sla() {
    return client.getMap("/v1/dsar/sla");
  }

  public Map<String, Object> plan(String requestId, List<FulfillmentSystem> systems, boolean includeHistorical) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("systems", systems);
    if (includeHistorical) body.put("includeHistorical", true);
    return client.requestMap("POST", "/v1/dsar/" + Query.pathSegment(requestId) + "/plan", body);
  }

  public Map<String, Object> status(String requestId) {
    return client.getMap("/v1/dsar/" + Query.pathSegment(requestId) + "/fulfillment");
  }

  /** For the in-environment agent: tasks to execute inside your network. {@code limit} may be null. */
  public Map<String, Object> pendingTasks(Integer limit) {
    return client.getMap("/v1/dsar/agent/tasks" + new Query().add("limit", limit));
  }

  /** For the in-environment agent: only the outcome crosses the boundary. {@code error} may be null. */
  public Map<String, Object> reportTask(String taskId, boolean ok, String error) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("ok", ok);
    if (error != null) body.put("error", error);
    return client.requestMap("POST", "/v1/dsar/agent/tasks/" + Query.pathSegment(taskId) + "/result", body);
  }
}
