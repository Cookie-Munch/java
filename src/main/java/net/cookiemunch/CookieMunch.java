package net.cookiemunch;

import com.fasterxml.jackson.databind.JsonNode;
import net.cookiemunch.model.ConsentIngest;
import net.cookiemunch.model.Identity;
import net.cookiemunch.model.SupportedLanguage;
import net.cookiemunch.model.Usage;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small, typed Java client for the Cookie Munch Developer API (the {@code /v1}
 * surface) plus the public consent-ingest endpoint ({@code POST /api/v1/consent}).
 *
 * <p>Construct it with an API key; the organisation is derived server-side from the
 * key, so callers never pass an orgId:
 *
 * <pre>{@code
 * CookieMunch cm = new CookieMunch("fck_live_...");
 * List<Site> sites = cm.sites().list();
 * Identity me = cm.me();
 * }</pre>
 *
 * <p>The default base URL is {@code https://api.cookiemunch.net}. Both
 * {@code Authorization: Bearer <key>} and {@code X-API-Key: <key>} headers are sent on
 * every request. Instances are thread-safe and reusable.
 */
public final class CookieMunch {

  /** The production API origin. The {@code /v1} and {@code /api/v1} prefixes are added per-request. */
  public static final String DEFAULT_BASE_URL = "https://api.cookiemunch.net";

  private static final String DEFAULT_USER_AGENT = "cookiemunch-java/0.1.0";

  private final String apiKey;
  private final String baseUrl;
  private final String userAgent;
  private final boolean strictUnknownFields;
  private final String secretRecipient;
  private final Map<String, String> extraHeaders;
  private final HttpTransport transport;
  private final Json json = new Json();

  private final SitesResource sites;
  private final ConsentResource consent;
  private final DsarResource dsar;
  private final VendorsResource vendors;
  private final RopaResource ropa;
  private final BrandKitsResource brandKits;
  private final RightsPageResource rightsPage;
  private final PreferencesResource preferences;
  private final MembersResource members;
  private final KeysResource keys;
  private final SandboxResource sandbox;
  private final WebhooksResource webhooks;
  private final BannersResource banners;
  private final IdentityResource identity;
  private final VaultResource vault;
  private final ProfileResource profile;
  private final SubscriptionsResource subscriptions;
  private final AssessmentsResource assessments;
  private final DiscoveryResource discovery;
  private final AiResource ai;
  private final FulfillmentResource fulfillment;
  private final RegulatoryResource regulatory;
  private final ResellerResource reseller;
  private final SubjectsResource subjects;
  private final RightsLinksResource rightsLinks;
  private final RightsCaptchaResource rightsCaptcha;
  private final ConsentSyncSecretResource consentSyncSecret;
  private final PreferenceCenterResource preferenceCenter;
  private final RightsIdentityResource rightsIdentity;
  private final DsarSettingsResource dsarSettings;
  private final IntegrationsResource integrations;
  private final OrgResource org;
  private final AssetsResource assets;
  private final MetadataResource metadata;
  private final PartnerLinksResource partnerLinks;

  /** Create a client for the default base URL, authenticating with {@code apiKey}. */
  public CookieMunch(String apiKey) {
    this(builder(apiKey));
  }

  private CookieMunch(Builder b) {
    if (b.apiKey == null || b.apiKey.isEmpty()) {
      throw new IllegalArgumentException("apiKey is required");
    }
    this.apiKey = b.apiKey;
    this.baseUrl = trimTrailingSlash(b.baseUrl == null ? DEFAULT_BASE_URL : b.baseUrl);
    this.userAgent = b.userAgent;
    this.strictUnknownFields = b.strictUnknownFields;
    this.secretRecipient = b.secretRecipient;
    this.extraHeaders = new LinkedHashMap<>(b.headers);
    this.transport = b.transport != null ? b.transport : new JdkHttpTransport();

    this.sites = new SitesResource(this);
    this.consent = new ConsentResource(this);
    this.dsar = new DsarResource(this);
    this.vendors = new VendorsResource(this);
    this.ropa = new RopaResource(this);
    this.brandKits = new BrandKitsResource(this);
    this.rightsPage = new RightsPageResource(this);
    this.preferences = new PreferencesResource(this);
    this.members = new MembersResource(this);
    this.keys = new KeysResource(this);
    this.sandbox = new SandboxResource(this);
    this.webhooks = new WebhooksResource(this);
    this.banners = new BannersResource(this);
    this.identity = new IdentityResource(this);
    this.vault = new VaultResource(this);
    this.profile = new ProfileResource(this);
    this.subscriptions = new SubscriptionsResource(this);
    this.assessments = new AssessmentsResource(this);
    this.discovery = new DiscoveryResource(this);
    this.ai = new AiResource(this);
    this.fulfillment = new FulfillmentResource(this);
    this.regulatory = new RegulatoryResource(this);
    this.reseller = new ResellerResource(this);
    this.subjects = new SubjectsResource(this);
    this.rightsLinks = new RightsLinksResource(this);
    this.rightsCaptcha = new RightsCaptchaResource(this);
    this.consentSyncSecret = new ConsentSyncSecretResource(this);
    this.preferenceCenter = new PreferenceCenterResource(this);
    this.rightsIdentity = new RightsIdentityResource(this);
    this.dsarSettings = new DsarSettingsResource(this);
    this.integrations = new IntegrationsResource(this);
    this.org = new OrgResource(this);
    this.assets = new AssetsResource(this);
    this.metadata = new MetadataResource(this);
    this.partnerLinks = new PartnerLinksResource(this);
  }

  /** Tags on created resources — {@code /v1/metadata/{type}/{id}}. */
  public MetadataResource metadata() {
    return metadata;
  }

  /** Suite-standard partner links — {@code /v1/partner_links}. */
  public PartnerLinksResource partnerLinks() {
    return partnerLinks;
  }

  /** Start building a client, overriding base URL, transport, or user agent. */
  public static Builder builder(String apiKey) {
    return new Builder(apiKey);
  }

  // ---- top-level operations --------------------------------------------------

  /** Identity / echo for SDK bootstrapping — {@code GET /v1/me}. */
  public Identity me() {
    return get("/v1/me", Identity.class);
  }

  /**
   * Every organisation on this key's account — {@code GET /v1/orgs} — each with its effective
   * {@code plan}, {@code planSource} ({@code org} = its own subscription, {@code account} =
   * covered by a higher plan another org on the account pays for) and {@code subscribedPlan},
   * plus {@code orgLimit} and {@code upgradeUrl}. Needs an unscoped key that is not locked to
   * properties.
   */
  public Map<String, Object> listOrgs() {
    return requestMap("GET", "/v1/orgs", null);
  }

  /**
   * Create a sibling organisation owned by the same account — {@code POST /v1/orgs}.
   *
   * <p>For starting a separate business of your own. Needs an unscoped key and counts against
   * the account's plan org allowance (403 {@code org_limit} names the plan). Not reseller
   * provisioning, which is for organisations you run on behalf of YOUR customers.
   */
  public Map<String, Object> createOrg(String name) {
    return requestMap("POST", "/v1/orgs", Map.of("name", name));
  }

  /**
   * {@link #createOrg(String)} with options: {@code mintKey}, {@code keyScopes} (required for an
   * {@code orgs:create} key), {@code keyExpiresInDays} (default 365), {@code keyOwner},
   * {@code externalRef}, {@code metadata}.
   */
  public Map<String, Object> createOrg(String name, Map<String, Object> opts) {
    Map<String, Object> body = new LinkedHashMap<>(opts == null ? Map.of() : opts);
    body.put("name", name);
    return requestMap("POST", "/v1/orgs", body);
  }

  /** {@link #listOrgs()} keeping only organisations with these tags — {@code GET /v1/orgs?metadata[k]=v}. */
  public Map<String, Object> listOrgs(Map<String, String> metadata) {
    return requestMap("GET", "/v1/orgs" + Query.metadata(metadata), null);
  }

  /**
   * Remove a SANDBOX organisation of this account — {@code DELETE /v1/orgs/{id}?hard=1}. Anything not
   * created with {@code metadata.forgedeck_sandbox} is 403 {@code not_a_sandbox}.
   */
  public Map<String, Object> deleteSandboxOrg(String id) {
    return requestMap("DELETE", "/v1/orgs/" + Query.pathSegment(id) + "?hard=1", null);
  }

  /**
   * What {@code forgedeck env} writes for the product provisioned with this ref —
   * {@code GET /v1/provision/env}: names, non-secret values, and references for secrets.
   */
  public Map<String, Object> provisionEnv(String ref) {
    return getMap("/v1/provision/env" + new Query().add("ref", ref));
  }

  /** {@link #provision(Map, boolean)} resuming from a stable step id (version 2 body). */
  public Map<String, Object> provision(Map<String, Object> input, boolean dryRun, String fromStep) {
    return requestMap("POST", "/v1/provision" + new Query().add("dryRun", dryRun ? "1" : null).add("fromStep", fromStep), input);
  }

  /**
   * The languages the banner already has copy for — {@code GET /v1/languages}. Diff it
   * against your visitors' locales to find the ones you still have to write.
   */
  public List<SupportedLanguage> languages() {
    return getList("/v1/languages", SupportedLanguage.class);
  }

  /** Current resource usage for the org — {@code GET /v1/usage}. */
  public Usage usage() {
    return get("/v1/usage", Usage.class);
  }

  /**
   * What the org may use, from its plan — {@code GET /v1/entitlements}: every feature as
   * {@code {key, enabled}} and every limit as {@code limits.<name>} with {@code limit} (null =
   * unlimited) and {@code used} where metered. Any key may read it.
   */
  public Map<String, Object> entitlements() {
    return getMap("/v1/entitlements");
  }

  /**
   * The dated API versions, what each changed, and the one this key is pinned to ({@code yours}) —
   * {@code GET /v1/versions}. Re-pin with {@code keys().updateSelf(...)}.
   */
  public Map<String, Object> versions() {
    return getMap("/v1/versions");
  }

  /**
   * Every link a site's privacy wiring depends on, one row each with {@code ok} (null = could not
   * check) and a {@code fixHint} — {@code GET /v1/integration-health}. {@code cbid} may be null
   * when the org has one site.
   */
  public Map<String, Object> integrationHealth(String cbid) {
    return getMap("/v1/integration-health" + (cbid == null ? "" : "?cbid=" + Query.encode(cbid)));
  }

  /**
   * The site's embed manifest — {@code GET /v1/embed?cbid=}, the tenant variant of
   * {@code /.well-known/forgedeck-embed.json}: every origin by CSP directive and every browser key
   * the embed writes for this site's config. Public: no key is needed.
   */
  public Map<String, Object> embedManifest(String cbid) {
    return getMap("/v1/embed?cbid=" + Query.encode(cbid));
  }

  /**
   * Wire a product's privacy in one call (site, connected systems, Atlas, Forumulate) —
   * {@code POST /v1/provision}. Idempotent; {@code dryRun} writes nothing anywhere. Secrets in
   * {@code input} are used for this request only; none is returned.
   */
  public Map<String, Object> provision(Map<String, Object> input, boolean dryRun) {
    return requestMap("POST", "/v1/provision" + (dryRun ? "?dryRun=1" : ""), input);
  }

  /**
   * The org's audit log, newest first — {@code GET /v1/audit}. API actions appear as
   * {@code apikey:<prefix>}. Requires an unscoped key that is not property-locked.
   * {@code limit} (1-500) may be null for the server default.
   */
  public Map<String, Object> audit(Integer limit) {
    return getMap("/v1/audit" + new Query().add("limit", limit));
  }

  /**
   * One actor's audit rows — {@code GET /v1/audit?actor=}. {@code "self"} is the calling key's own
   * rows, the one form a scoped or property-locked key may read.
   */
  public Map<String, Object> audit(Integer limit, String actor) {
    return getMap("/v1/audit" + new Query().add("limit", limit).add("actor", actor));
  }

  /** Where the audit log is streamed as CloudEvents (never the secret) — {@code GET /v1/audit/sink}. */
  public Map<String, Object> getAuditSink() {
    return getMap("/v1/audit/sink");
  }

  /** Stream the audit log to an HTTPS endpoint — {@code PUT /v1/audit/sink {type: "webhook", url, secret?}}. */
  public Map<String, Object> setAuditSink(Map<String, Object> input) {
    return requestMap("PUT", "/v1/audit/sink", input);
  }

  /** Stop the audit-log export — {@code DELETE /v1/audit/sink}. */
  public void removeAuditSink() {
    requestMap("DELETE", "/v1/audit/sink", null);
  }

  /** The checks the org chose not to meet — {@code GET /v1/integration-health/waivers}. */
  public Map<String, Object> healthWaivers(String cbid) {
    return getMap("/v1/integration-health/waivers" + new Query().add("cbid", cbid));
  }

  /** Waive a check — {@code PUT /v1/integration-health/waivers {cbid?, check, subject?, reason, until?}}. Audited. */
  public Map<String, Object> setHealthWaiver(Map<String, Object> input) {
    return requestMap("PUT", "/v1/integration-health/waivers", input);
  }

  /** Remove a waiver — {@code DELETE /v1/integration-health/waivers}. cbid and subject may be null. */
  public void removeHealthWaiver(String check, String cbid, String subject) {
    requestMap("DELETE", "/v1/integration-health/waivers" + new Query().add("check", check).add("cbid", cbid).add("subject", subject), null);
  }

  /**
   * Record a consent decision via the PUBLIC {@code POST /api/v1/consent} endpoint —
   * the high-volume write the browser embed makes. The {@code cbid} must be a
   * registered site; a successful call returns HTTP 204 with no body. Note this
   * endpoint lives under {@code /api/v1}, not the {@code /v1} dev-API prefix.
   */
  public void logConsent(ConsentIngest payload) {
    requestVoid("POST", "/api/v1/consent", payload);
  }

  // ---- resource groups -------------------------------------------------------

  public SitesResource sites() {
    return sites;
  }

  public ConsentResource consent() {
    return consent;
  }

  public DsarResource dsar() {
    return dsar;
  }

  public VendorsResource vendors() {
    return vendors;
  }

  public RopaResource ropa() {
    return ropa;
  }

  public BrandKitsResource brandKits() {
    return brandKits;
  }

  /** The hosted rights page document. */
  public RightsPageResource rightsPage() {
    return rightsPage;
  }

  public PreferencesResource preferences() {
    return preferences;
  }

  public MembersResource members() {
    return members;
  }

  public KeysResource keys() {
    return keys;
  }

  /** The organisation's sandbox: a separate test organisation, never billed. */
  public SandboxResource sandbox() {
    return sandbox;
  }

  public WebhooksResource webhooks() {
    return webhooks;
  }

  public BannersResource banners() {
    return banners;
  }

  // ---- the privacy platform, and the reseller API --------------------------
  // Identity, vault and profile reads are POSTs so a person's identifiers never appear in a URL.

  public IdentityResource identity() {
    return identity;
  }

  public VaultResource vault() {
    return vault;
  }

  public ProfileResource profile() {
    return profile;
  }

  public SubscriptionsResource subscriptions() {
    return subscriptions;
  }

  public AssessmentsResource assessments() {
    return assessments;
  }

  public DiscoveryResource discovery() {
    return discovery;
  }

  public AiResource ai() {
    return ai;
  }

  public FulfillmentResource fulfillment() {
    return fulfillment;
  }

  public RegulatoryResource regulatory() {
    return regulatory;
  }

  public ResellerResource reseller() {
    return reseller;
  }

  public SubjectsResource subjects() {
    return subjects;
  }

  /** Account-linked rights requests (single-use links to the hosted rights page). */
  public RightsLinksResource rightsLinks() {
    return rightsLinks;
  }

  /** A site's consent-sync signing secret (hosted consent sync). */
  public ConsentSyncSecretResource consentSyncSecret() {
    return consentSyncSecret;
  }

  /** The hosted preference centre: mint a token for a visitor your server has signed in. */
  public PreferenceCenterResource preferenceCenter() {
    return preferenceCenter;
  }

  /** A site's rights-page captcha: your own keys (recommended), or off. */
  public RightsCaptchaResource rightsCaptcha() {
    return rightsCaptcha;
  }

  /** A site's sign-in provider: a visitor signed in to your site is verified by their session, with no code mail. */
  public RightsIdentityResource rightsIdentity() {
    return rightsIdentity;
  }

  /** A site's rights-request settings: graceDays (0-30; 7 for a new site, 0 until set for an existing one). Needs dsar:read / dsar:write. */
  public DsarSettingsResource dsarSettings() {
    return dsarSettings;
  }

  /** Cards for support, CRM and helpdesk sidebars. Needs dsar:read. */
  public IntegrationsResource integrations() {
    return integrations;
  }

  /** The key's organisation. Requires an unscoped key that is not property-locked. */
  public OrgResource org() {
    return org;
  }

  public AssetsResource assets() {
    return assets;
  }

  // ---- internal request plumbing (package-private; used by resource classes) --

  <T> T get(String path, Class<T> type) {
    return json.read(exchange("GET", path, null), type);
  }

  <T> List<T> getList(String path, Class<T> element) {
    return json.readList(exchange("GET", path, null), element);
  }

  Map<String, Object> getMap(String path) {
    return json.readMap(exchange("GET", path, null));
  }

  /** A bare JSON array of objects, for endpoints with no dedicated model. */
  List<Map<String, Object>> getListOfMaps(String path) {
    return json.readListOfMaps(exchange("GET", path, null));
  }

  <T> T request(String method, String path, Object body, Class<T> type) {
    return json.read(exchange(method, path, body), type);
  }

  Map<String, Object> requestMap(String method, String path, Object body) {
    return json.readMap(exchange(method, path, body));
  }

  /** As {@link #requestMap}, with extra request headers (e.g. Idempotency-Key). */
  Map<String, Object> requestMapWithHeaders(String method, String path, Object body, Map<String, String> extra) {
    return json.readMap(exchange(method, path, body, extra));
  }

  /** As {@link #requestMap}, but a null value in {@code body} is sent as JSON null. */
  Map<String, Object> requestMapKeepingNulls(String method, String path, Map<String, Object> body) {
    return json.readMap(exchange(method, path, json.keepingNulls(body)));
  }

  String requestRaw(String method, String path, Object body) {
    return exchange(method, path, body);
  }

  void requestVoid(String method, String path, Object body) {
    exchange(method, path, body);
  }

  /**
   * Execute a request against {@code baseUrl + path} and return the raw 2xx body
   * (empty string for a 204). Non-2xx responses raise {@link CookieMunchApiException}.
   */
  private String exchange(String method, String path, Object body) {
    return exchange(method, path, body, Map.of());
  }

  private String exchange(String method, String path, Object body, Map<String, String> extra) {
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("Authorization", "Bearer " + apiKey);
    headers.put("X-API-Key", apiKey);
    headers.put("Accept", "application/json");
    if (strictUnknownFields && !"GET".equals(method)) {
      headers.put("Prefer", "strict-unknown-fields");
    }
    if (secretRecipient != null && !"GET".equals(method)) {
      headers.put("X-Secret-Recipient", secretRecipient);
    }
    headers.putAll(extraHeaders);
    if (userAgent != null && !userAgent.isEmpty()) {
      headers.put("User-Agent", userAgent);
    }
    headers.putAll(extra);

    String payload = null;
    if (body != null) {
      payload = json.write(body);
      headers.put("Content-Type", "application/json");
    }

    HttpTransport.Response response;
    try {
      response = transport.send(new HttpTransport.Request(method, baseUrl + path, headers, payload));
    } catch (IOException e) {
      throw new CookieMunchException("cookiemunch: request failed: " + e.getMessage(), e);
    }

    int status = response.status();
    String responseBody = response.body() == null ? "" : response.body();
    if (status < 200 || status >= 300) {
      throw toApiError(status, responseBody);
    }
    return responseBody;
  }

  private CookieMunchApiException toApiError(int status, String body) {
    String message = "request failed with status " + status;
    String code = null;
    if (!body.isBlank()) {
      JsonNode node = json.readTreeQuiet(body);
      if (node != null) {
        JsonNode error = node.get("error");
        if (error != null && error.isTextual()) {
          message = error.asText();
        }
        JsonNode codeNode = node.get("code");
        if (codeNode != null && codeNode.isTextual()) {
          code = codeNode.asText();
        }
      }
    }
    return new CookieMunchApiException(status, message, body, code);
  }

  private static String trimTrailingSlash(String url) {
    int end = url.length();
    while (end > 0 && url.charAt(end - 1) == '/') {
      end--;
    }
    return url.substring(0, end);
  }

  /** Fluent builder for {@link CookieMunch}. */
  public static final class Builder {
    private String apiKey;
    private String baseUrl = DEFAULT_BASE_URL;
    private String userAgent = DEFAULT_USER_AGENT;
    private HttpTransport transport;
    private boolean strictUnknownFields;
    private String secretRecipient;
    private final Map<String, String> headers = new LinkedHashMap<>();

    Builder(String apiKey) {
      this.apiKey = apiKey;
    }

    public Builder apiKey(String apiKey) {
      this.apiKey = apiKey;
      return this;
    }

    /** Override the API origin (default {@link #DEFAULT_BASE_URL}). Trailing slashes are trimmed. */
    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /** Override the {@code User-Agent} sent on every request. */
    public Builder userAgent(String userAgent) {
      this.userAgent = userAgent;
      return this;
    }

    /**
     * Send {@code Prefer: strict-unknown-fields} on writes: a body field the API does not declare
     * is refused ({@code 400 unknown_field}) instead of ignored.
     */
    public Builder strictUnknownFields(boolean strict) {
      this.strictUnknownFields = strict;
      return this;
    }

    /**
     * Seal every once-shown secret to this X25519 public key ({@code x25519:<base64>}): answers carry
     * {@code sealed_secret} instead of the plaintext.
     */
    public Builder secretRecipient(String recipient) {
      this.secretRecipient = recipient;
      return this;
    }

    /** An extra header on every request, e.g. {@code header("Idempotency-Key", ref + ":" + run)}. */
    public Builder header(String name, String value) {
      this.headers.put(name, value);
      return this;
    }

    /** Inject a custom {@link HttpTransport} (for tests or non-standard runtimes). */
    public Builder transport(HttpTransport transport) {
      this.transport = transport;
      return this;
    }

    public CookieMunch build() {
      return new CookieMunch(this);
    }
  }
}
