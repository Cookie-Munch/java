package net.cookiemunch;

import net.cookiemunch.model.BrandKit;
import net.cookiemunch.model.BrandKitCreate;

import java.util.List;

/** The {@code /v1/brand-kits} endpoints. */
public final class BrandKitsResource {

  private final CookieMunch client;

  BrandKitsResource(CookieMunch client) {
    this.client = client;
  }

  /** List the org's reusable banner themes — {@code GET /v1/brand-kits}. */
  public List<BrandKit> list() {
    return client.getList("/v1/brand-kits", BrandKit.class);
  }

  /** Create a brand kit — {@code POST /v1/brand-kits}. */
  public BrandKit create(BrandKitCreate input) {
    return client.request("POST", "/v1/brand-kits", input, BrandKitEnvelope.class).kit();
  }

  /** Delete a brand kit — {@code DELETE /v1/brand-kits/{id}}. */
  public void delete(String id) {
    client.requestVoid("DELETE", "/v1/brand-kits/" + Query.pathSegment(id), null);
  }
}
