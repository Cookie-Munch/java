package net.cookiemunch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.cookiemunch.model.ApiKeyIssueInput;
import net.cookiemunch.model.Identifier;
import net.cookiemunch.model.PolicyOptions;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The shapes that matter on the platform surface: what is sent, and what comes back. */
class PlatformTest {

  private final List<HttpTransport.Request> calls = new ArrayList<>();

  private CookieMunch client(int status, String body) {
    calls.clear();
    HttpTransport transport = request -> {
      calls.add(request);
      return new HttpTransport.Response(status, body);
    };
    return CookieMunch.builder("fck_test").baseUrl("https://api.example.test").transport(transport).build();
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> body(HttpTransport.Request r) throws Exception {
    return r.body() == null ? null : new ObjectMapper().readValue(r.body(), Map.class);
  }

  /** Numestri F-01: per-language purposes, and dryRun on every declaration write. */
  @Test
  void declaredCookiesPurposesAndDryRun() throws Exception {
    CookieMunch c = client(200, "{\"dryRun\":true,\"declared\":[],\"diff\":{\"added\":[],\"removed\":[],\"changed\":[]}}");
    Map<String, Object> decl = new HashMap<>();
    decl.put("category", "necessary");
    decl.put("purpose", "Signed in.");
    decl.put("purposes", Map.of("es", "Sesión."));
    c.sites().declareCookie("cb", "sid", decl);
    c.sites().declareCookie("cb", "sid", decl, true);
    c.sites().declareCookies("cb", List.of(Map.of("name", "a", "category", "necessary", "purpose", "A")), true, true);
    Map<String, Object> out = c.sites().undeclareCookie("cb", "sid", true);
    List<String> seen = new ArrayList<>();
    for (HttpTransport.Request r : calls) seen.add(r.method() + " " + r.url());
    assertEquals(List.of(
        "PUT https://api.example.test/v1/sites/cb/cookies/sid",
        "PUT https://api.example.test/v1/sites/cb/cookies/sid?dryRun=1",
        "PUT https://api.example.test/v1/sites/cb/cookies?dryRun=1",
        "DELETE https://api.example.test/v1/sites/cb/cookies/sid?dryRun=1"), seen);
    assertEquals(Map.of("es", "Sesión."), body(calls.get(0)).get("purposes"));
    assertEquals(true, body(calls.get(2)).get("replace"));
    assertEquals(true, out.get("dryRun"));
  }

  @Test
  void orgMailProvider() throws Exception {
    CookieMunch c = client(200, "{}");
    c.org().mailProvider();
    Map<String, Object> input = new HashMap<>();
    input.put("provider", "sendgrid");
    input.put("fromEmail", "p@acme.com");
    input.put("secret", "SG.x");
    c.org().setMailProvider(input);
    c.org().testMailProvider("me@acme.com");
    c.org().testMailProvider(null);
    c.org().removeMailProvider();
    List<String> seen = new ArrayList<>();
    for (HttpTransport.Request r : calls) seen.add(r.method() + " " + r.url());
    assertEquals(List.of(
        "GET https://api.example.test/v1/org/mail-provider",
        "PUT https://api.example.test/v1/org/mail-provider",
        "POST https://api.example.test/v1/org/mail-provider/test",
        "POST https://api.example.test/v1/org/mail-provider/test",
        "DELETE https://api.example.test/v1/org/mail-provider"), seen);
    assertEquals(input, body(calls.get(1)));
    assertEquals(Map.of("to", "me@acme.com"), body(calls.get(2)));
    assertEquals(Map.of(), body(calls.get(3)));
  }

  @Test
  void rightsCaptcha() throws Exception {
    CookieMunch c = client(200, "{}");
    c.rightsCaptcha().get("s 1");
    Map<String, Object> own = new HashMap<>();
    own.put("mode", "own");
    own.put("provider", "hcaptcha");
    own.put("siteKey", "k-12345678");
    own.put("secret", "sec");
    c.rightsCaptcha().set("s1", own);
    c.rightsCaptcha().remove("s1");
    c.org().rightsCaptcha();
    c.org().setRightsCaptcha(Map.of("mode", "off"));
    c.org().removeRightsCaptcha();
    List<String> seen = new ArrayList<>();
    for (HttpTransport.Request r : calls) seen.add(r.method() + " " + r.url());
    assertEquals(List.of(
        "GET https://api.example.test/v1/sites/s%201/rights-captcha",
        "PUT https://api.example.test/v1/sites/s1/rights-captcha",
        "DELETE https://api.example.test/v1/sites/s1/rights-captcha",
        "GET https://api.example.test/v1/org/rights-captcha",
        "PUT https://api.example.test/v1/org/rights-captcha",
        "DELETE https://api.example.test/v1/org/rights-captcha"), seen);
    assertEquals(own, body(calls.get(1)));
    assertEquals(Map.of("mode", "off"), body(calls.get(4)));
  }

  /** The sign-in provider rights requests are verified with: the site's own setting and the organisation default. */
  @Test
  void rightsIdentity() throws Exception {
    CookieMunch c = client(200, "{}");
    c.rightsIdentity().get("s 1");
    Map<String, Object> clerk = new HashMap<>();
    clerk.put("type", "clerk");
    clerk.put("issuer", "https://clerk.acme.example");
    clerk.put("authorizedParties", List.of("https://www.acme.example"));
    c.rightsIdentity().set("s1", clerk);
    c.rightsIdentity().remove("s1");
    c.org().rightsIdentity();
    Map<String, Object> oidc = new HashMap<>();
    oidc.put("type", "oidc_jwt");
    oidc.put("issuer", "https://id.acme.example");
    oidc.put("jwksUrl", "https://id.acme.example/keys");
    oidc.put("sessionCookieName", "sid");
    c.org().setRightsIdentity(oidc);
    c.org().removeRightsIdentity();
    List<String> seen = new ArrayList<>();
    for (HttpTransport.Request r : calls) seen.add(r.method() + " " + r.url());
    assertEquals(List.of(
        "GET https://api.example.test/v1/sites/s%201/rights-identity",
        "PUT https://api.example.test/v1/sites/s1/rights-identity",
        "DELETE https://api.example.test/v1/sites/s1/rights-identity",
        "GET https://api.example.test/v1/org/rights-identity",
        "PUT https://api.example.test/v1/org/rights-identity",
        "DELETE https://api.example.test/v1/org/rights-identity"), seen);
    assertEquals(clerk, body(calls.get(1)));
    assertEquals(oidc, body(calls.get(4)));
  }

  @Test
  void deprovisionSuspendsByDefaultAndPurgesOnlyWhenAsked() {
    CookieMunch c = client(204, "");
    c.reseller().deprovision("c1", false);
    c.reseller().deprovision("c1", true);
    assertEquals("https://api.example.test/v1/reseller/customers/c1", calls.get(0).url());
    assertEquals("https://api.example.test/v1/reseller/customers/c1?purge=true", calls.get(1).url());
  }

  /** Jackson drops nulls from records, so clearing the override has to survive as a map entry. */
  @Test
  void clearingDsarRoutingSendsAnExplicitNull() throws Exception {
    CookieMunch c = client(200, "{}");
    Map<String, Object> patch = new HashMap<>();
    patch.put("dsarRouting", null);
    c.reseller().update("c1", patch);
    Map<String, Object> sent = body(calls.get(0));
    assertTrue(sent.containsKey("dsarRouting"));
    assertEquals(null, sent.get("dsarRouting"));
  }

  @Test
  void issueSendsLeastPrivilegeFields() throws Exception {
    CookieMunch c = client(200, "{\"key\":\"k\",\"prefix\":\"p\"}");
    c.keys().issue(new ApiKeyIssueInput("agency", List.of("consent:read"), List.of("cb_shop"), 30));
    assertEquals(Map.of("name", "agency", "scopes", List.of("consent:read"), "cbids", List.of("cb_shop"), "expiresInDays", 30), body(calls.get(0)));
  }

  @Test
  void theOneArgumentKeyInputStillMeansFullAccess() throws Exception {
    CookieMunch c = client(200, "{\"key\":\"k\",\"prefix\":\"p\"}");
    c.keys().issue(new ApiKeyIssueInput("ci"));
    assertEquals(Map.of("name", "ci"), body(calls.get(0)));
  }

  @Test
  void policyIsMarkdownWithOptionsInTheQuery() {
    CookieMunch c = client(200, "# Privacy policy");
    String md = c.sites().policy("s1", new PolicyOptions("dpo@x.com", null, List.of("gdpr", "ccpa")));
    assertEquals("# Privacy policy", md);
    assertEquals("https://api.example.test/v1/sites/s1/policy?contactEmail=dpo%40x.com&jurisdictions=gdpr%2Cccpa", calls.get(0).url());
  }

  @Test
  void ropaExportAndDsarNoticeAreText() {
    assertEquals("a,b\n", client(200, "a,b\n").ropa().exportCsv());
    assertEquals("Dear subject", client(200, "Dear subject").dsar().response("d1"));
  }

  @Test
  void identifiersTravelInTheBodyNeverTheUrl() throws Exception {
    CookieMunch c = client(200, "{}");
    List<Identifier> ids = List.of(new Identifier("email_sha256", "abc"));
    c.identity().resolve(ids);
    c.vault().current(ids);
    c.profile().activate(ids, "marketing");
    for (HttpTransport.Request r : calls) {
      assertEquals("POST", r.method());
      assertFalse(r.url().contains("abc"));
      assertEquals(List.of(Map.of("space", "email_sha256", "value", "abc")), body(r).get("identifiers"));
    }
  }

  @Test
  void rightsLinksCreateSendsTheSubjectAndOptions() throws Exception {
    CookieMunch c = client(201, "{\"url\":\"https://cookiemunch.net/r/s1?t=a.b\",\"expiresAt\":1}");
    Map<String, Object> out = c.rightsLinks().create("s1", "p@x.com", "u-1", List.of("access"), 600);
    assertEquals("https://cookiemunch.net/r/s1?t=a.b", out.get("url"));
    assertEquals("POST", calls.get(0).method());
    assertEquals("https://api.example.test/v1/sites/s1/rights-links", calls.get(0).url());
    assertEquals(Map.of("subject", Map.of("email", "p@x.com", "userId", "u-1"), "rights", List.of("access"), "ttlSeconds", 600), body(calls.get(0)));
    c.rightsLinks().create("s1", "p@x.com");
    assertEquals(Map.of("subject", Map.of("email", "p@x.com")), body(calls.get(1)));
  }

  @Test
  void rightsPageDomains() throws Exception {
    CookieMunch c = client(200, "{}");
    c.rightsPage().listDomains("s 1");
    c.rightsPage().addDomain("s", "privacy.acme.com");
    c.rightsPage().verifyDomain("s", "privacy.acme.com");
    c.rightsPage().removeDomain("s", "privacy.acme.com");
    String base = "https://api.example.test/v1/sites";
    assertEquals(List.of("GET", "POST", "POST", "DELETE"), calls.stream().map(HttpTransport.Request::method).toList());
    assertEquals(base + "/s%201/rights-page/domains", calls.get(0).url());
    assertEquals(Map.of("hostname", "privacy.acme.com"), body(calls.get(1)));
    assertEquals(base + "/s/rights-page/domains/privacy.acme.com/verify", calls.get(2).url());
    assertEquals(base + "/s/rights-page/domains/privacy.acme.com", calls.get(3).url());
  }

  @Test
  void executorWebhookCheckDeliveriesAndDryRuns() throws Exception {
    CookieMunch c = client(200, "{}");
    c.fulfillment().testExecutorWebhook("ex 1", Map.of("x-sig", "v1,abc"), "{\"a\":1}");
    c.fulfillment().dryRunConnectExecutor("crm", "https://crm.example", "sk_12345678", Map.of(), null, null);
    c.fulfillment().dryRunUpdateExecutor("ex 1", Map.of("auto", false));
    c.fulfillment().updateExecutor("ex 1", Map.of("auto", false));
    List<String> seen = new ArrayList<>();
    for (HttpTransport.Request r : calls) seen.add(r.method() + " " + r.url());
    assertEquals(List.of(
        "POST https://api.example.test/v1/dsar/executors/ex%201/webhook/test",
        "POST https://api.example.test/v1/dsar/executors?dryRun=1",
        "PATCH https://api.example.test/v1/dsar/executors/ex%201?dryRun=1",
        "PATCH https://api.example.test/v1/dsar/executors/ex%201"), seen);
    assertEquals(Map.of("headers", Map.of("x-sig", "v1,abc"), "body", "{\"a\":1}"), body(calls.get(0)));

    CookieMunch list = client(200, "[]");
    list.fulfillment().executorWebhookDeliveries("ex 1");
    assertEquals("GET https://api.example.test/v1/dsar/executors/ex%201/webhook/deliveries", calls.get(0).method() + " " + calls.get(0).url());
  }
}
