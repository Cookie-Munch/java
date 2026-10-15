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

  /** Systems connected to run part of a request themselves. Never includes credentials. */
  public List<Map<String, Object>> executors() {
    return client.getListOfMaps("/v1/dsar/executors");
  }

  /**
   * Connect one. {@code profile} describes that system's API — paths, the words it uses for
   * export and erase, its status vocabulary, how it signs webhooks — so connecting a new
   * platform needs no code. The secret is stored encrypted and never returned; the response
   * carries the webhook URL to configure in that system. {@code webhookSecret} and
   * {@code auto} may be null. A {@code whsec_<base64>} webhook secret verifies deliveries signed
   * with its base64-decoded bytes or with the literal string; any other secret only with its
   * UTF-8 bytes.
   */
  public Map<String, Object> connectExecutor(
      String system, String baseUrl, String secretKey, Map<String, Object> profile, String webhookSecret, Boolean auto) {
    return client.requestMap("POST", "/v1/dsar/executors", executorBody(system, baseUrl, secretKey, profile, webhookSecret, auto));
  }

  /**
   * What {@link #connectExecutor} would create, validated in full, with nothing stored —
   * {@code POST /v1/dsar/executors?dryRun=1}. Returns {@code dryRun}, {@code executor}, {@code diff}.
   */
  public Map<String, Object> dryRunConnectExecutor(
      String system, String baseUrl, String secretKey, Map<String, Object> profile, String webhookSecret, Boolean auto) {
    return client.requestMap("POST", "/v1/dsar/executors?dryRun=1", executorBody(system, baseUrl, secretKey, profile, webhookSecret, auto));
  }

  private static Map<String, Object> executorBody(
      String system, String baseUrl, String secretKey, Map<String, Object> profile, String webhookSecret, Boolean auto) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("system", system);
    body.put("baseUrl", baseUrl);
    body.put("secretKey", secretKey);
    body.put("profile", profile);
    if (webhookSecret != null) body.put("webhookSecret", webhookSecret);
    if (auto != null) body.put("auto", auto);
    return body;
  }

  /**
   * Change a connection in place — {@code PATCH /v1/dsar/executors/{id}}. {@code patch} is sent
   * as-is: only the keys present change, the credential is kept unless {@code secretKey} is
   * given, and {@code "webhookSecret" -> null} removes it (nulls are kept for this call).
   */
  public Map<String, Object> updateExecutor(String id, Map<String, Object> patch) {
    return client.requestMapKeepingNulls("PATCH", "/v1/dsar/executors/" + Query.pathSegment(id), new LinkedHashMap<>(patch));
  }

  /**
   * The connection as {@link #updateExecutor} would leave it, and a diff, changing nothing —
   * {@code PATCH /v1/dsar/executors/{id}?dryRun=1}.
   */
  public Map<String, Object> dryRunUpdateExecutor(String id, Map<String, Object> patch) {
    return client.requestMapKeepingNulls(
        "PATCH", "/v1/dsar/executors/" + Query.pathSegment(id) + "?dryRun=1", new LinkedHashMap<>(patch));
  }

  /**
   * Would this webhook delivery verify? — {@code POST /v1/dsar/executors/{id}/webhook/test}.
   * Runs the receiver's exact check on {@code headers} and the raw {@code body} string (the
   * bytes that were signed) and acts on nothing. Returns {@code verified}, {@code stage}, {@code reason}.
   */
  public Map<String, Object> testExecutorWebhook(String id, Map<String, String> headers, String body) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("headers", headers == null ? Map.of() : headers);
    payload.put("body", body);
    return client.requestMap("POST", "/v1/dsar/executors/" + Query.pathSegment(id) + "/webhook/test", payload);
  }

  /**
   * The last 50 deliveries to a connection's webhook, newest first, each with its reason —
   * {@code GET /v1/dsar/executors/{id}/webhook/deliveries}.
   */
  public List<Map<String, Object>> executorWebhookDeliveries(String id) {
    return client.getListOfMaps("/v1/dsar/executors/" + Query.pathSegment(id) + "/webhook/deliveries");
  }

  /**
   * Try a connection against one email without filing a request —
   * {@code POST /v1/dsar/executors/{id}/test}. Runs the lookup and returns the raw (redacted)
   * upstream answers. {@code operation} (locate | export | erase) and {@code open} may be null;
   * {@code open} on an export opens and polls a real export once. An erasure is never sent.
   */
  public Map<String, Object> testExecutor(String id, String email, String operation, Boolean open) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("email", email);
    if (operation != null) body.put("operation", operation);
    if (open != null) body.put("open", open);
    return client.requestMap("POST", "/v1/dsar/executors/" + Query.pathSegment(id) + "/test", body);
  }

  /**
   * One task — {@code GET /v1/dsar/{id}/tasks/{taskId}}: its status history ({@code events}),
   * the requests made to a connected system on its behalf ({@code log}, redacted, 30 days) and
   * whether that system's version changed mid-request.
   */
  public Map<String, Object> task(String requestId, String taskId) {
    return client.getMap("/v1/dsar/" + Query.pathSegment(requestId) + "/tasks/" + Query.pathSegment(taskId));
  }

  /** Disconnect a system; its open sub-tasks stop being driven. */
  public void disconnectExecutor(String id) {
    client.requestVoid("DELETE", "/v1/dsar/executors/" + Query.pathSegment(id), null);
  }

  /** The export bundle a connected system produced, fetched from it on demand. */
  public Map<String, Object> taskExport(String requestId, String taskId) {
    return client.getMap(
        "/v1/dsar/" + Query.pathSegment(requestId) + "/tasks/" + Query.pathSegment(taskId) + "/export");
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
