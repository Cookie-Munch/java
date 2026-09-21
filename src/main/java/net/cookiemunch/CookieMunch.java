package net.cookiemunch;

import com.fasterxml.jackson.databind.JsonNode;
import net.cookiemunch.model.ConsentIngest;
import net.cookiemunch.model.Identity;
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
  private final HttpTransport transport;
  private final Json json = new Json();

  private final SitesResource sites;
  private final ConsentResource consent;
  private final DsarResource dsar;
  private final VendorsResource vendors;
  private final RopaResource ropa;
  private final BrandKitsResource brandKits;
  private final PreferencesResource preferences;
  private final MembersResource members;
  private final KeysResource keys;
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
  private final OrgResource org;
  private final AssetsResource assets;

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
    this.transport = b.transport != null ? b.transport : new JdkHttpTransport();

    this.sites = new SitesResource(this);
    this.consent = new ConsentResource(this);
    this.dsar = new DsarResource(this);
    this.vendors = new VendorsResource(this);
    this.ropa = new RopaResource(this);
    this.brandKits = new BrandKitsResource(this);
    this.preferences = new PreferencesResource(this);
    this.members = new MembersResource(this);
    this.keys = new KeysResource(this);
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
    this.org = new OrgResource(this);
    this.assets = new AssetsResource(this);
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

  /** Current resource usage for the org — {@code GET /v1/usage}. */
  public Usage usage() {
    return get("/v1/usage", Usage.class);
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

  public PreferencesResource preferences() {
    return preferences;
  }

  public MembersResource members() {
    return members;
  }

  public KeysResource keys() {
    return keys;
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

  <T> T request(String method, String path, Object body, Class<T> type) {
    return json.read(exchange(method, path, body), type);
  }

  Map<String, Object> requestMap(String method, String path, Object body) {
    return json.readMap(exchange(method, path, body));
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
    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("Authorization", "Bearer " + apiKey);
    headers.put("X-API-Key", apiKey);
    headers.put("Accept", "application/json");
    if (userAgent != null && !userAgent.isEmpty()) {
      headers.put("User-Agent", userAgent);
    }

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
