package net.cookiemunch;

import net.cookiemunch.model.RopaEntry;
import net.cookiemunch.model.RopaInput;

import java.util.List;

/** The {@code /v1/ropa} endpoints. */
public final class RopaResource {

  private final CookieMunch client;

  RopaResource(CookieMunch client) {
    this.client = client;
  }

  /** List all RoPA entries — {@code GET /v1/ropa}. */
  public List<RopaEntry> list() {
    return client.getList("/v1/ropa", RopaEntry.class);
  }

  /** Create a RoPA entry — {@code POST /v1/ropa}. */
  public RopaEntry create(RopaInput input) {
    return client.request("POST", "/v1/ropa", input, RopaEnvelope.class).entry();
  }
}
