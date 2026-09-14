package net.cookiemunch;

import net.cookiemunch.model.AbResult;
import net.cookiemunch.model.AdPersonalizationInput;
import net.cookiemunch.model.AdPersonalizationResult;
import net.cookiemunch.model.BrandExtractionResult;
import net.cookiemunch.model.CookieDeclaration;
import net.cookiemunch.model.FlowWriteResult;
import net.cookiemunch.model.InstallSnippet;
import net.cookiemunch.model.ScanStatus;
import net.cookiemunch.model.Site;
import net.cookiemunch.model.SiteCreate;
import net.cookiemunch.model.SiteFlow;
import net.cookiemunch.model.SnippetOptions;
import net.cookiemunch.model.VerifyResult;

import java.util.List;
import java.util.Map;

/** The {@code /v1/sites} endpoints. */
public final class SitesResource {

  private final CookieMunch client;

  SitesResource(CookieMunch client) {
    this.client = client;
  }

  private static String base(String cbid) {
    return "/v1/sites/" + Query.pathSegment(cbid);
  }

  /** List all sites for the key's org — {@code GET /v1/sites}. */
  public List<Site> list() {
    return client.getList("/v1/sites", Site.class);
  }

  /** Register a new site — {@code POST /v1/sites}. */
  public Site create(SiteCreate input) {
    return client.request("POST", "/v1/sites", input, Site.class);
  }

  /** Register a new site by domain (cbid auto-generated) — {@code POST /v1/sites}. */
  public Site create(String domain) {
    return create(new SiteCreate(domain));
  }

  /** Get one site — {@code GET /v1/sites/{cbid}}. */
  public Site get(String cbid) {
    return client.get(base(cbid), Site.class);
  }

  /** Delete a site — {@code DELETE /v1/sites/{cbid}}. */
  public void delete(String cbid) {
    client.requestVoid("DELETE", base(cbid), null);
  }

  /** Get a site's config — {@code GET /v1/sites/{cbid}/config}. */
  public Map<String, Object> getConfig(String cbid) {
    return client.getMap(base(cbid) + "/config");
  }

  /** Upsert a site's config (merged over the stored config) — {@code PUT /v1/sites/{cbid}/config}. */
  public Map<String, Object> putConfig(String cbid, Map<String, Object> config) {
    return client.requestMap("PUT", base(cbid) + "/config", config);
  }

  /** Latest categorized cookie declaration — {@code GET /v1/sites/{cbid}/cookies}. */
  public CookieDeclaration cookies(String cbid) {
    return client.get(base(cbid) + "/cookies", CookieDeclaration.class);
  }

  /** Kick off an async cookie crawl — {@code POST /v1/sites/{cbid}/scan}. */
  public ScanStatus scan(String cbid) {
    return client.request("POST", base(cbid) + "/scan", null, ScanStatus.class);
  }

  /** Current cookie-scan status — {@code GET /v1/sites/{cbid}/scan}. */
  public ScanStatus scanStatus(String cbid) {
    return client.get(base(cbid) + "/scan", ScanStatus.class);
  }

  /** A/B experiment results — {@code GET /v1/sites/{cbid}/ab}. */
  public List<AbResult> ab(String cbid) {
    return client.getList(base(cbid) + "/ab", AbResult.class);
  }

  /** Install snippet with defaults — {@code GET /v1/sites/{cbid}/snippet}. */
  public InstallSnippet snippet(String cbid) {
    return snippet(cbid, null);
  }

  /** Install snippet with options — {@code GET /v1/sites/{cbid}/snippet}. */
  public InstallSnippet snippet(String cbid, SnippetOptions options) {
    Query query = new Query();
    if (options != null) {
      query.add("blockingmode", options.blockingMode()).add("culture", options.culture());
    }
    return client.get(base(cbid) + "/snippet" + query, InstallSnippet.class);
  }

  /** Verify the site's domain — {@code POST /v1/sites/{cbid}/verify}. {@code method} is dns|meta|file. */
  public VerifyResult verify(String cbid, String method) {
    return client.request("POST", base(cbid) + "/verify", Map.of("method", method), VerifyResult.class);
  }

  /** Suggest theme tokens from the site's homepage — {@code POST /v1/sites/{cbid}/brand}. */
  public BrandExtractionResult brand(String cbid) {
    return client.request("POST", base(cbid) + "/brand", Map.of(), BrandExtractionResult.class);
  }

  /** Read a site's v2 banner flow plus lint issues — {@code GET /v1/sites/{cbid}/flow}. */
  public SiteFlow getFlow(String cbid) {
    return client.get(base(cbid) + "/flow", SiteFlow.class);
  }

  /**
   * Apply an ordered batch of structured edit ops — {@code POST /v1/sites/{cbid}/flow/ops}.
   * Each operation is a map with an {@code "op"} key plus that op's args. Always check
   * {@link FlowWriteResult#ok()}.
   */
  public FlowWriteResult editFlow(String cbid, List<Map<String, Object>> operations) {
    return client.request(
        "POST", base(cbid) + "/flow/ops", Map.of("operations", operations), FlowWriteResult.class);
  }

  /**
   * Wholesale-replace the flow with a full v2 config — {@code PUT /v1/sites/{cbid}/flow}.
   * Always check {@link FlowWriteResult#ok()}.
   */
  public FlowWriteResult setFlow(String cbid, Map<String, Object> config) {
    return client.request("PUT", base(cbid) + "/flow", config, FlowWriteResult.class);
  }

  /** Enable the personalized-ads split — {@code POST /v1/sites/{cbid}/elements/ad-personalization}. */
  public AdPersonalizationResult enableAdPersonalization(String cbid) {
    return enableAdPersonalization(cbid, null);
  }

  /** Enable the personalized-ads split with options. */
  public AdPersonalizationResult enableAdPersonalization(String cbid, AdPersonalizationInput input) {
    Object body = input != null ? input : Map.of();
    return client.request(
        "POST", base(cbid) + "/elements/ad-personalization", body, AdPersonalizationResult.class);
  }
}
