package net.cookiemunch;

import net.cookiemunch.model.DsarCreate;
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
}
