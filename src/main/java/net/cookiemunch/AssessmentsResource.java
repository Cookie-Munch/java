package net.cookiemunch;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/assessments} endpoints: DPIA, PIA, LIA, TIA, AI-impact and vendor assessments. */
public final class AssessmentsResource {

  private final CookieMunch client;

  AssessmentsResource(CookieMunch client) {
    this.client = client;
  }

  public Map<String, Object> templates() {
    return client.getMap("/v1/assessments/templates");
  }

  public Map<String, Object> list() {
    return client.getMap("/v1/assessments");
  }

  public Map<String, Object> start(String template, String subject) {
    return client.requestMap("POST", "/v1/assessments", Map.of("template", template, "subject", subject));
  }

  public Map<String, Object> get(String id) {
    return client.getMap("/v1/assessments/" + Query.pathSegment(id));
  }

  public Map<String, Object> answer(String id, String questionId, Object value) {
    return client.requestMap("POST", "/v1/assessments/" + Query.pathSegment(id) + "/answer", Map.of("questionId", questionId, "value", value));
  }

  /** Fill factual answers from the latest data map. Never overwrites a human answer. */
  public Map<String, Object> autoPopulateFromMap(String id) {
    return client.requestMap("POST", "/v1/assessments/" + Query.pathSegment(id) + "/autopopulate-from-map", null);
  }

  /** Fill from evidence you supply (questionId → answer), stamped with {@code source}, which may be null. */
  public Map<String, Object> autoPopulate(String id, Map<String, Object> evidence, String source) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("evidence", evidence);
    if (source != null) body.put("source", source);
    return client.requestMap("POST", "/v1/assessments/" + Query.pathSegment(id) + "/autopopulate", body);
  }

  public Map<String, Object> submit(String id) {
    return client.requestMap("POST", "/v1/assessments/" + Query.pathSegment(id) + "/submit", null);
  }

  /** Record approval. {@code by} becomes the approval record — pass the person who approved. */
  public Map<String, Object> approve(String id, String by) {
    return client.requestMap("POST", "/v1/assessments/" + Query.pathSegment(id) + "/approve", Map.of("by", by));
  }

  public Map<String, Object> reject(String id, String by, String reason) {
    return client.requestMap("POST", "/v1/assessments/" + Query.pathSegment(id) + "/reject", Map.of("by", by, "reason", reason));
  }
}
