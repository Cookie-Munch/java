package net.cookiemunch;

import net.cookiemunch.model.ScoredVendor;
import net.cookiemunch.model.VendorCreateResult;
import net.cookiemunch.model.VendorInput;

import java.util.List;

/** The {@code /v1/vendors} endpoints. */
public final class VendorsResource {

  private final CookieMunch client;

  VendorsResource(CookieMunch client) {
    this.client = client;
  }

  /** List vendors with their risk scores — {@code GET /v1/vendors}. */
  public List<ScoredVendor> list() {
    return client.getList("/v1/vendors", ScoredVendor.class);
  }

  /** Create a vendor and return it with a computed risk score — {@code POST /v1/vendors}. */
  public VendorCreateResult create(VendorInput input) {
    return client.request("POST", "/v1/vendors", input, VendorCreateResult.class);
  }
}
