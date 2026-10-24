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

  /** Sites with these tags — {@code GET /v1/sites?metadata[k]=v}. */
  public List<Site> list(Map<String, String> metadata) {
    return client.getList("/v1/sites" + Query.metadata(metadata), Site.class);
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

  /**
   * Rename or move a site — {@code PATCH /v1/sites/{cbid}} with any of {@code domain},
   * {@code name} (a null value clears it) and {@code aliases} (an empty list clears them). The cbid
   * and the consent ledger stay; a domain move clears verification. Nulls in {@code patch} are sent.
   * The result carries {@code site}, {@code diff} and {@code effects}.
   */
  public Map<String, Object> update(String cbid, Map<String, Object> patch) {
    return client.requestMapKeepingNulls("PATCH", base(cbid), new java.util.LinkedHashMap<>(patch));
  }

  /** As {@link #update(String, Map)} with {@code ?dryRun=1}: the diff and effects, nothing stored. */
  public Map<String, Object> dryRunUpdate(String cbid, Map<String, Object> patch) {
    return client.requestMapKeepingNulls("PATCH", base(cbid) + "?dryRun=1", new java.util.LinkedHashMap<>(patch));
  }

  /** Delete a site — {@code DELETE /v1/sites/{cbid}}. */
  public void delete(String cbid) {
    client.requestVoid("DELETE", base(cbid), null);
  }

  /**
   * Purge a SANDBOX site — {@code DELETE /v1/sites/{cbid}?hard=1}: its consent ledger, declarations and
   * test rights requests too. Anything not created as a sandbox is 403 {@code not_a_sandbox}.
   */
  public Map<String, Object> deleteSandbox(String cbid) {
    return client.requestMap("DELETE", base(cbid) + "?hard=1", null);
  }

  /** Partner links and the partner catalogue — {@code GET /v1/sites/{cbid}/partners}. */
  public Map<String, Object> listPartners(String cbid) {
    return client.getMap(base(cbid) + "/partners");
  }

  /**
   * Grant a partner platform ({@code forumulate}, {@code atlas}) the site's consent events —
   * {@code POST /v1/sites/{cbid}/partners}. No secret is created; a repeat is a no-op. {@code label}
   * may be null.
   */
  public Map<String, Object> linkPartner(String cbid, String partner, String externalId, String label) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("partner", partner);
    body.put("externalId", externalId);
    if (label != null) body.put("label", label);
    return client.requestMap("POST", base(cbid) + "/partners", body);
  }

  /** Revoke a partner link; deliveries stop at once — {@code DELETE /v1/sites/{cbid}/partners/{linkId}}. */
  public Map<String, Object> unlinkPartner(String cbid, String linkId) {
    return client.requestMap("DELETE", base(cbid) + "/partners/" + Query.pathSegment(linkId), null);
  }

  /** Get a site's config — {@code GET /v1/sites/{cbid}/config}. */
  public Map<String, Object> getConfig(String cbid) {
    return client.getMap(base(cbid) + "/config");
  }

  /**
   * Replace a site's config — {@code PUT /v1/sites/{cbid}/config}. A top-level field the config
   * omits reverts to its default; a top-level field sent as null reverts to its default too.
   */
  public Map<String, Object> putConfig(String cbid, Map<String, Object> config) {
    return putConfig(cbid, config, false);
  }

  /**
   * As {@link #putConfig(String, Map)}; with {@code keepUnspecified}
   * ({@code ?keep=unspecified}) a top-level field the config omits keeps its stored value.
   */
  public Map<String, Object> putConfig(String cbid, Map<String, Object> config, boolean keepUnspecified) {
    return client.requestMapKeepingNulls(
        "PUT", base(cbid) + "/config" + (keepUnspecified ? "?keep=unspecified" : ""), new java.util.LinkedHashMap<>(config));
  }

  /**
   * Change part of a site's config — {@code PATCH /v1/sites/{cbid}/config}, a JSON Merge Patch
   * (RFC 7386): omitted fields keep their stored value, and a field set to null is removed (it
   * takes its default). Nulls are sent as JSON null. {@link #putConfig} replaces the whole document.
   */
  public Map<String, Object> patchConfig(String cbid, Map<String, Object> config) {
    return client.requestMapKeepingNulls("PATCH", base(cbid) + "/config", new java.util.LinkedHashMap<>(config));
  }

  /**
   * Preview a config write without saving it — {@code PATCH} (or, with {@code replace}, {@code PUT})
   * {@code /v1/sites/{cbid}/config?dryRun=1}. Returns {@code config} (as it would be saved),
   * {@code diff} (every leaf that would change) and any {@code warnings}.
   */
  public Map<String, Object> dryRunConfig(String cbid, Map<String, Object> config, boolean replace) {
    return dryRunConfig(cbid, config, replace, false);
  }

  /** As {@link #dryRunConfig(String, Map, boolean)}; {@code keepUnspecified} (with replace) adds {@code &keep=unspecified}. */
  public Map<String, Object> dryRunConfig(String cbid, Map<String, Object> config, boolean replace, boolean keepUnspecified) {
    String keep = replace && keepUnspecified ? "&keep=unspecified" : "";
    return client.requestMapKeepingNulls(
        replace ? "PUT" : "PATCH", base(cbid) + "/config?dryRun=1" + keep, new java.util.LinkedHashMap<>(config));
  }

  /**
   * Check a proposed config for consent-button parity without saving it — {@code POST
   * /v1/sites/{cbid}/config/lint}: is "Accept all" more prominent than "Reject all" anywhere
   * (colour, size, placement, copy, custom CSS)? Returns {@code designWarnings}, {@code score}
   * (0-100) and {@code suggestedReject}. It checks exactly what {@code patchConfig} would
   * store (a null removes a field, as in PATCH). Needs only {@code sites:read}.
   */
  public Map<String, Object> lintConfig(String cbid, Map<String, Object> config) {
    return lintConfig(cbid, config, false);
  }

  /** As {@link #lintConfig(String, Map)}; {@code replace} checks what {@code putConfig} would store instead ({@code ?replace=1}). */
  public Map<String, Object> lintConfig(String cbid, Map<String, Object> config, boolean replace) {
    return client.requestMapKeepingNulls(
        "POST", base(cbid) + "/config/lint" + (replace ? "?replace=1" : ""), new java.util.LinkedHashMap<>(config));
  }

  /** Latest categorized cookie declaration — {@code GET /v1/sites/{cbid}/cookies}. */
  public CookieDeclaration cookies(String cbid) {
    return client.get(base(cbid) + "/cookies", CookieDeclaration.class);
  }

  /**
   * Start a cookie crawl — {@code POST /v1/sites/{cbid}/scan}. Answers at once with
   * {@code scanId} and {@code state: queued}.
   */
  public ScanStatus scan(String cbid) {
    return client.request("POST", base(cbid) + "/scan", null, ScanStatus.class);
  }

  /** {@link #scan(String)}, holding the request until it is done or failed, or {@code wait} (≤ 60 s) runs out. */
  public ScanStatus scan(String cbid, java.time.Duration wait) {
    return client.request("POST", base(cbid) + "/scan" + waitQuery(wait), null, ScanStatus.class);
  }

  /** The last scan: state, timings, cookie count, pages, error — {@code GET /v1/sites/{cbid}/scan}. */
  public ScanStatus scanStatus(String cbid) {
    return client.get(base(cbid) + "/scan", ScanStatus.class);
  }

  /** {@link #scanStatus(String)}, holding the request until a running scan settles, or {@code wait} (≤ 60 s) runs out. */
  public ScanStatus scanStatus(String cbid, java.time.Duration wait) {
    return client.get(base(cbid) + "/scan" + waitQuery(wait), ScanStatus.class);
  }

  /**
   * Return once the site's scan is done or failed (read {@code state} and {@code error}),
   * long-polling the server; at {@code timeout} it returns the still-running state. Each
   * request asks the server to hold at most 25 s, inside the default transport's 30 s.
   */
  public ScanStatus waitForScan(String cbid, java.time.Duration timeout) {
    return waitForScan(cbid, timeout, java.time.Duration.ofSeconds(25));
  }

  /** {@link #waitForScan(String, java.time.Duration)}, asking the server to hold each request at most {@code poll}. */
  public ScanStatus waitForScan(String cbid, java.time.Duration timeout, java.time.Duration poll) {
    long until = System.nanoTime() + timeout.toNanos();
    while (true) {
      long left = until - System.nanoTime();
      long asked = Math.max(0, Math.min(Math.min(60_000_000_000L, left), poll.toNanos()));
      long started = System.nanoTime();
      ScanStatus r = scanStatus(cbid, java.time.Duration.ofNanos(asked));
      if (!"scanning".equals(r.status()) || until - System.nanoTime() <= 0) return r;
      // A server that answered sooner than asked is not hammered.
      long early = Math.min(1_000_000_000L, until - System.nanoTime()) - (System.nanoTime() - started);
      if (early > 0) {
        try {
          Thread.sleep(early / 1_000_000L, (int) (early % 1_000_000L));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return r;
        }
      }
    }
  }

  /** {@code ?wait=30}, {@code ?wait=1.5}. */
  private static String waitQuery(java.time.Duration wait) {
    double s = Math.round(wait.toMillis()) / 1000.0;
    return "?wait=" + (s == Math.floor(s) ? String.valueOf((long) s) : String.valueOf(s));
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

  /**
   * Mint a short-lived signed token to preview the banner as a visitor in {@code region}
   * speaking {@code lang} sees it (headless browsers included; nothing is recorded) —
   * {@code POST /v1/sites/{cbid}/preview-token}. Any argument may be null. The result carries
   * {@code token}, {@code expiresAt} and {@code previewUrl}.
   */
  public Map<String, Object> previewToken(String cbid, String region, String lang, String url, Integer ttlSeconds) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    if (region != null) body.put("region", region);
    if (lang != null) body.put("lang", lang);
    if (url != null) body.put("url", url);
    if (ttlSeconds != null) body.put("ttlSeconds", ttlSeconds);
    return client.requestMap("POST", base(cbid) + "/preview-token", body);
  }

  /**
   * The brand.json last imported for the site — {@code GET /v1/sites/{cbid}/brand}: each role, the
   * theme token it sets, its {@code source} ({@code brand.json} or {@code site}), {@code ignored}
   * and {@code warnings}.
   */
  public Map<String, Object> getBrand(String cbid) {
    return client.requestMap("GET", base(cbid) + "/brand", null);
  }

  /**
   * Import a DTCG brand.json into the banner theme and the rights page — {@code PUT
   * /v1/sites/{cbid}/brand}. {@code input} is {@code {"url": "https://…"}} or the document. Values the
   * site set itself are kept (brand.json &lt; site banner theme &lt; per-banner variant).
   * {@code dryRun} saves nothing.
   */
  public Map<String, Object> setBrand(String cbid, Map<String, Object> input, boolean dryRun) {
    return client.requestMap("PUT", base(cbid) + "/brand" + (dryRun ? "?dryRun=1" : ""), new java.util.LinkedHashMap<>(input));
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

  /** Which banner design the site uses — {@code GET /v1/sites/{cbid}/banner}. {@code bannerId} is null when none is assigned. */
  public Map<String, Object> banner(String cbid) {
    return client.getMap(base(cbid) + "/banner");
  }

  /** The site's privacy and cookie policy, as Markdown — {@code GET /v1/sites/{cbid}/policy}. {@code opts} may be null. */
  public String policy(String cbid, net.cookiemunch.model.PolicyOptions opts) {
    Query q = new Query();
    if (opts != null) {
      q.add("contactEmail", opts.contactEmail()).add("effectiveDate", opts.effectiveDate());
      if (opts.jurisdictions() != null) q.add("jurisdictions", String.join(",", opts.jurisdictions()));
    }
    return client.requestRaw("GET", base(cbid) + "/policy" + q, null);
  }

  /**
   * Which trackers fired after opt-out in a captured session, and what personal data left
   * the page — {@code POST /v1/sites/{cbid}/sentry}.
   */
  public Map<String, Object> analyzeSession(String cbid, net.cookiemunch.model.SessionAnalysisInput input) {
    return client.requestMap("POST", base(cbid) + "/sentry", input);
  }

  /** Exactly what to publish to prove control of the domain, per method — {@code GET /v1/sites/{cbid}/verify/challenge}. */
  public Map<String, Object> verifyChallenge(String cbid) {
    return client.getMap(base(cbid) + "/verify/challenge");
  }

  /** Create up to 100 sites — {@code POST /v1/sites/bulk}. Partial success: each result reports {@code ok} or its own error. */
  public Map<String, Object> createBulk(List<net.cookiemunch.model.BulkSite> sites) {
    return client.requestMap("POST", "/v1/sites/bulk", Map.of("sites", sites));
  }

  /**
   * Pages where the embed could not load its banner renderer — {@code GET
   * /v1/sites/{cbid}/blocked}. The host page's Content Security Policy or Trusted Types
   * policy refused it, so nobody there can be asked for consent. Empty is healthy.
   */
  public Map<String, Object> blocked(String cbid) {
    return client.requestMap("GET", base(cbid) + "/blocked", null);
  }

  /**
   * Read a cookie declaration exported from another CMP and translate its categories into
   * ours — {@code POST /v1/sites/{cbid}/import}. Nothing is applied; the result comes back
   * for review, with the rows whose category could not be placed listed separately.
   */
  public Map<String, Object> importDeclaration(String cbid, String data) {
    return client.requestMap("POST", base(cbid) + "/import", Map.of("data", data));
  }

  /**
   * Import a declaration exported from another CMP AND declare it — {@code POST
   * /v1/sites/{cbid}/import?apply=1}. Every row whose category mapped to ours and that has
   * a purpose is declared; the rest come back in {@code skipped}. Cookies the site already
   * declared are kept unless {@code overwrite}.
   */
  public Map<String, Object> applyImport(String cbid, String data, boolean overwrite) {
    String query = "?apply=1" + (overwrite ? "&overwrite=1" : "");
    return client.requestMap("POST", base(cbid) + "/import" + query, Map.of("data", data));
  }

  /**
   * Declare what one cookie is — {@code PUT /v1/sites/{cbid}/cookies/{name}}. Overrides
   * scanned, observed and database classification everywhere (API, preference modal,
   * {@code data-cc="declaration"}). A name ending in {@code *} declares a prefix wildcard.
   *
   * @param declaration {@code category} (a standard category or a custom category id) and
   *     {@code purpose} are required; {@code purposes} (the purpose in other languages, a map
   *     of BCP-47 tag to text, e.g. {@code {"es": "…"}}; {@code purpose} is the fallback),
   *     {@code provider}, {@code expiry}, {@code domain}, {@code pattern} are optional.
   * @return {@code { cookie, warnings? }}
   */
  public Map<String, Object> declareCookie(String cbid, String name, Map<String, Object> declaration) {
    return declareCookie(cbid, name, declaration, false);
  }

  /**
   * {@link #declareCookie(String, String, Map)}; with {@code dryRun} nothing is stored and the
   * answer adds {@code dryRun}, {@code declared} (the would-be list) and a {@code diff}.
   */
  public Map<String, Object> declareCookie(String cbid, String name, Map<String, Object> declaration, boolean dryRun) {
    return client.requestMap("PUT", base(cbid) + "/cookies/" + Query.pathSegment(name) + (dryRun ? "?dryRun=1" : ""), declaration);
  }

  /**
   * Declare many cookies at once — {@code PUT /v1/sites/{cbid}/cookies}. Each entry carries
   * {@code name} plus the fields of {@link #declareCookie}. All-or-nothing; {@code replace}
   * also removes declarations the list does not name.
   *
   * @return {@code { declared, saved, removed, warnings? }}
   */
  public Map<String, Object> declareCookies(String cbid, List<Map<String, Object>> cookies, boolean replace) {
    return declareCookies(cbid, cookies, replace, false);
  }

  /**
   * {@link #declareCookies(String, List, boolean)}; with {@code dryRun} nothing is stored and
   * the answer says what would be, plus a {@code diff}.
   */
  public Map<String, Object> declareCookies(String cbid, List<Map<String, Object>> cookies, boolean replace, boolean dryRun) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("cookies", cookies);
    if (replace) body.put("replace", true);
    return client.requestMap("PUT", base(cbid) + "/cookies" + (dryRun ? "?dryRun=1" : ""), body);
  }

  /**
   * Retire a cookie the site renamed or stopped setting — {@code POST
   * /v1/sites/{cbid}/cookies/{name}/retire}. It leaves the banner's cookie list,
   * {@code data-cc="declaration"} and the public feed, and stays listed for the owner with
   * {@code retired}. A name ending in {@code *} retires every cookie with that prefix. If a
   * crawl or a visit sees it again afterwards it is listed again, flagged
   * {@code retired_but_seen}.
   *
   * @param renamedTo the new name, or {@code null}
   * @param note a note, or {@code null}
   * @return {@code { cookie, matched }}
   */
  public Map<String, Object> retireCookie(String cbid, String name, String renamedTo, String note) {
    return retireCookie(cbid, name, renamedTo, note, false);
  }

  /** {@link #retireCookie(String, String, String, String)}; with {@code dryRun} nothing is stored and the answer adds the would-be list ({@code retired}). */
  public Map<String, Object> retireCookie(String cbid, String name, String renamedTo, String note, boolean dryRun) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    if (renamedTo != null) body.put("renamedTo", renamedTo);
    if (note != null) body.put("note", note);
    return client.requestMap("POST", base(cbid) + "/cookies/" + Query.pathSegment(name) + "/retire" + (dryRun ? "?dryRun=1" : ""), body);
  }

  /** Un-retire a cookie: it is listed again everywhere — {@code DELETE /v1/sites/{cbid}/cookies/{name}/retire}. */
  public void unretireCookie(String cbid, String name) {
    client.requestVoid("DELETE", base(cbid) + "/cookies/" + Query.pathSegment(name) + "/retire", null);
  }

  /** What un-retiring would leave ({@code retired}), with nothing removed. {@code dryRun} false un-retires and returns an empty map. */
  public Map<String, Object> unretireCookie(String cbid, String name, boolean dryRun) {
    if (!dryRun) {
      unretireCookie(cbid, name);
      return Map.of();
    }
    return client.requestMap("DELETE", base(cbid) + "/cookies/" + Query.pathSegment(name) + "/retire?dryRun=1", null);
  }

  /**
   * Every declaration preset (atlas, forumulate, chatly, cookiemunch, referalli, firstpromoter, partnerstack, tapfiliate, impact): parameters, what each
   * declared on this site, and whether the vendor's manifest exists yet —
   * {@code GET /v1/sites/{cbid}/cookies/presets}.
   */
  public Map<String, Object> cookiePresets(String cbid) {
    return client.requestMap("GET", base(cbid) + "/cookies/presets", null);
  }

  /**
   * Apply (or re-sync) a vendor preset: declares what that platform writes, marked
   * {@code vendor} — {@code PUT /v1/sites/{cbid}/cookies/presets/{vendor}}. Atlas takes
   * {@code frontendApiHost}/{@code crossSubdomainCookies}; Chatly {@code publicId}/{@code dragging}/{@code category};
   * referral presets {@code cookieDomain}/{@code cookieDays}/{@code category} (referalli {@code legacyName}, impact {@code campaignId}).
   */
  public Map<String, Object> applyCookiePreset(String cbid, String vendor, Map<String, Object> params) {
    return applyCookiePreset(cbid, vendor, params, false);
  }

  /** {@link #applyCookiePreset(String, String, Map)}; with {@code dryRun} nothing is stored. */
  public Map<String, Object> applyCookiePreset(String cbid, String vendor, Map<String, Object> params, boolean dryRun) {
    return client.requestMap("PUT", base(cbid) + "/cookies/presets/" + Query.pathSegment(vendor) + (dryRun ? "?dryRun=1" : ""), params == null ? Map.of() : params);
  }

  /**
   * Declare any vendor's cookies and storage from its embed manifest —
   * {@code PUT /v1/sites/{cbid}/cookies/presets/custom} with {@code url} or {@code manifest} (and
   * {@code params}). The vendor id is the manifest's {@code product}.
   */
  public Map<String, Object> importCookieManifest(String cbid, Map<String, Object> input, boolean dryRun) {
    return client.requestMap("PUT", base(cbid) + "/cookies/presets/custom" + (dryRun ? "?dryRun=1" : ""), input == null ? Map.of() : input);
  }

  /** What to declare — {@code GET /v1/sites/{cbid}/cookies/suggestions}: detected vendors and one suggestion per undeclared row. */
  public Map<String, Object> cookieSuggestions(String cbid) {
    return client.requestMap("GET", base(cbid) + "/cookies/suggestions", null);
  }

  /**
   * Declare the suggestions — {@code POST /v1/sites/{cbid}/cookies/suggestions/declare} with optional
   * {@code names} and {@code presets}. Run with {@code dryRun} first.
   */
  public Map<String, Object> declareCookieSuggestions(String cbid, Map<String, Object> input, boolean dryRun) {
    return client.requestMap("POST", base(cbid) + "/cookies/suggestions/declare" + (dryRun ? "?dryRun=1" : ""), input == null ? Map.of() : input);
  }

  /** Remove every declaration a vendor preset made — {@code DELETE /v1/sites/{cbid}/cookies/presets/{vendor}}. */
  public Map<String, Object> removeCookiePreset(String cbid, String vendor, boolean dryRun) {
    return client.requestMap("DELETE", base(cbid) + "/cookies/presets/" + Query.pathSegment(vendor) + (dryRun ? "?dryRun=1" : ""), null);
  }

  /** Remove every stored (scanned or observed) row of this name; a declaration stays — {@code DELETE /v1/sites/{cbid}/observed-cookies/{name}}. */
  public void deleteObservedCookie(String cbid, String name) {
    client.requestVoid("DELETE", base(cbid) + "/observed-cookies/" + Query.pathSegment(name), null);
  }

  /** What deleting it would remove, with nothing removed. {@code dryRun} false deletes and returns an empty map. */
  public Map<String, Object> deleteObservedCookie(String cbid, String name, boolean dryRun) {
    if (!dryRun) {
      deleteObservedCookie(cbid, name);
      return Map.of();
    }
    return client.requestMap("DELETE", base(cbid) + "/observed-cookies/" + Query.pathSegment(name) + "?dryRun=1", null);
  }

  /**
   * Purge observed since T: remove every row a real visit first reported at or after
   * {@code since} (and before {@code until}, or {@code null}) — ISO-8601 or epoch ms. Crawled
   * rows and declarations stay — {@code POST /v1/sites/{cbid}/observed-cookies/purge}.
   */
  public Map<String, Object> purgeObservedCookies(String cbid, String since, String until, boolean dryRun) {
    Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("since", since);
    if (until != null) body.put("until", until);
    return client.requestMap("POST", base(cbid) + "/observed-cookies/purge" + (dryRun ? "?dryRun=1" : ""), body);
  }

  /**
   * Remove a declaration — {@code DELETE /v1/sites/{cbid}/cookies/{name}}. The scanned
   * classification applies again.
   */
  public void undeclareCookie(String cbid, String name) {
    client.requestVoid("DELETE", base(cbid) + "/cookies/" + Query.pathSegment(name), null);
  }

  /**
   * What removing a declaration would leave — {@code declared} and a {@code diff} — with
   * nothing removed ({@code DELETE …?dryRun=1}). {@code dryRun} false removes it and
   * returns an empty map.
   */
  public Map<String, Object> undeclareCookie(String cbid, String name, boolean dryRun) {
    if (!dryRun) {
      undeclareCookie(cbid, name);
      return Map.of();
    }
    return client.requestMap("DELETE", base(cbid) + "/cookies/" + Query.pathSegment(name) + "?dryRun=1", null);
  }
}
