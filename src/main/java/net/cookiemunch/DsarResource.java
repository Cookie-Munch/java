package net.cookiemunch;

import net.cookiemunch.model.DsarCreate;
import net.cookiemunch.model.DsarEraseResult;
import net.cookiemunch.model.DsarExportResult;
import net.cookiemunch.model.DsarRequest;

import java.util.List;
import java.util.Map;

/** The {@code /v1/dsar} endpoints. */
public final class DsarResource {

  private final CookieMunch client;

  DsarResource(CookieMunch client) {
    this.client = client;
  }

  /** List all DSARs — {@code GET /v1/dsar}. */
  public List<DsarRequest> list() {
    return client.getList("/v1/dsar", DsarRequest.class);
  }

  /** One request, in the shape {@code list} returns — {@code GET /v1/dsar/{id}}. */
  public DsarRequest get(String id) {
    return client.request("GET", "/v1/dsar/" + Query.pathSegment(id), null, DsarRequest.class);
  }

  /**
   * Delete a TEST request with its tasks and log — {@code DELETE /v1/dsar/{id}}. A real request
   * is refused with 409 {@code not_a_test_request}; the deletion is audited.
   */
  public void delete(String id) {
    client.requestVoid("DELETE", "/v1/dsar/" + Query.pathSegment(id), null);
  }

  /**
   * Mark a CLOSED request (completed or rejected) as a test, after the fact —
   * {@code POST /v1/dsar/{id}/mark-test}. An open one is refused with 409
   * {@code request_not_closed}. Audited; {@link #delete} then works on it.
   * Returns {@code { request, changed }}.
   */
  public Map<String, Object> markTest(String id) {
    return client.requestMap("POST", "/v1/dsar/" + Query.pathSegment(id) + "/mark-test", null);
  }

  /** File a new DSAR — {@code POST /v1/dsar}. */
  public DsarRequest create(DsarCreate input) {
    return client.request("POST", "/v1/dsar", input, DsarEnvelope.class).request();
  }

  /**
   * Advance a DSAR to a new status — {@code POST /v1/dsar/{id}/advance}. {@code toStatus} is
   * one of {@code received | verifying | in_progress | completed | rejected}.
   */
  public DsarRequest advance(String id, String toStatus) {
    String path = "/v1/dsar/" + Query.pathSegment(id) + "/advance";
    return client.request("POST", path, Map.of("toStatus", toStatus), DsarEnvelope.class).request();
  }

  /** {@link #advance(String, String)}, replacing the line the subject reads in the notice. */
  public DsarRequest advance(String id, String toStatus, String messageToSubject) {
    if (messageToSubject == null) return advance(id, toStatus);
    String path = "/v1/dsar/" + Query.pathSegment(id) + "/advance";
    return client.request("POST", path, Map.of("toStatus", toStatus, "messageToSubject", messageToSubject), DsarEnvelope.class).request();
  }

  /** The subject-facing response notice for a request, as plain text — {@code GET /v1/dsar/{id}/response}. */
  public String response(String id) {
    return client.requestRaw("GET", "/v1/dsar/" + Query.pathSegment(id) + "/response", null);
  }

  /**
   * Erase a subject's consent records on one site, for a deletion request past identity
   * verification — {@code POST /v1/dsar/{id}/erase}. Noted on the request. Requires
   * dsar:write and consent:write.
   */
  public DsarEraseResult erase(String id, String cbid, String stamp) {
    String path = "/v1/dsar/" + Query.pathSegment(id) + "/erase";
    return client.request("POST", path, Map.of("cbid", cbid, "stamp", stamp), DsarEraseResult.class);
  }

  /**
   * A subject's consent records on one site, for an access or portability request past
   * identity verification — {@code POST /v1/dsar/{id}/export}. Noted on the request. Requires
   * dsar:write and consent:read.
   */
  public DsarExportResult export(String id, String cbid, String stamp) {
    String path = "/v1/dsar/" + Query.pathSegment(id) + "/export";
    return client.request("POST", path, Map.of("cbid", cbid, "stamp", stamp), DsarExportResult.class);
  }
}
