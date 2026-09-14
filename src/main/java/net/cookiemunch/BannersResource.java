package net.cookiemunch;

import net.cookiemunch.model.BannerAssignments;
import net.cookiemunch.model.BannerCreate;
import net.cookiemunch.model.BannerPublishResult;
import net.cookiemunch.model.BannerRecord;
import net.cookiemunch.model.BannerSummary;
import net.cookiemunch.model.BannerUpdate;

import java.util.List;
import java.util.Map;

/** The {@code /v1/banners} endpoints (account-level reusable banner designs). */
public final class BannersResource {

  private final CookieMunch client;

  BannersResource(CookieMunch client) {
    this.client = client;
  }

  private static String base(String id) {
    return "/v1/banners/" + Query.pathSegment(id);
  }

  /** List the org's reusable banner designs — {@code GET /v1/banners}. */
  public List<BannerSummary> list() {
    return client.getList("/v1/banners", BannerSummary.class);
  }

  /** Create a reusable banner design — {@code POST /v1/banners}. */
  public BannerRecord create(BannerCreate input) {
    return client.request("POST", "/v1/banners", input, BannerRecord.class);
  }

  /** Get one banner design — {@code GET /v1/banners/{id}}. */
  public BannerRecord get(String id) {
    return client.get(base(id), BannerRecord.class);
  }

  /** Update a banner design's name and/or json — {@code PUT /v1/banners/{id}}. */
  public BannerRecord update(String id, BannerUpdate patch) {
    return client.request("PUT", base(id), patch, BannerRecord.class);
  }

  /** Delete a banner design (fails while still assigned) — {@code DELETE /v1/banners/{id}}. */
  public void delete(String id) {
    client.requestVoid("DELETE", base(id), null);
  }

  /** List the site cbids a design is assigned to — {@code GET /v1/banners/{id}/assignments}. */
  public BannerAssignments assignments(String id) {
    return client.get(base(id) + "/assignments", BannerAssignments.class);
  }

  /** Set the sites a design is assigned to — {@code PUT /v1/banners/{id}/assignments}. */
  public BannerAssignments setAssignments(String id, List<String> cbids) {
    return client.request(
        "PUT", base(id) + "/assignments", Map.of("cbids", cbids), BannerAssignments.class);
  }

  /** Compile the design into every assigned site's config — {@code POST /v1/banners/{id}/publish}. */
  public BannerPublishResult publish(String id) {
    return client.request("POST", base(id) + "/publish", null, BannerPublishResult.class);
  }
}
