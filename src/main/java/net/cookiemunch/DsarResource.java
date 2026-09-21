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
