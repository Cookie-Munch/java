package net.cookiemunch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.cookiemunch.model.ApiKeyIssued;
import net.cookiemunch.model.ApiKeyUpdate;
import net.cookiemunch.model.AssetUpload;
import net.cookiemunch.model.ConsentIngest;
import net.cookiemunch.model.DsarEraseResult;
import net.cookiemunch.model.DsarExportResult;
import net.cookiemunch.model.Identity;
import net.cookiemunch.model.Org;
import net.cookiemunch.model.Site;
import net.cookiemunch.model.SiteCreate;
import net.cookiemunch.model.WebhookSecret;
import net.cookiemunch.model.WebhookTestResult;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Unit tests over an injected {@link HttpTransport} — no network, deterministic under CI. */
class CookieMunchTest {

  /** A fake transport that records the last request and returns a configured response. */
  static final class FakeTransport implements HttpTransport {
    HttpTransport.Request last;
    int status = 200;
    String body = "";

    void reply(int status, String body) {
      this.status = status;
      this.body = body;
    }

    @Override
    public HttpTransport.Response send(HttpTransport.Request request) {
      this.last = request;
      return new HttpTransport.Response(status, body);
    }
  }

  private static CookieMunch clientWith(FakeTransport transport) {
    return CookieMunch.builder("fck_test_key")
        .baseUrl("https://example.test/")
        .transport(transport)
        .build();
  }

  @Test
  void sendsBearerAndApiKeyHeadersOnGet() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"orgId\":\"org_1\",\"plan\":\"pro\",\"keyPrefix\":\"fck_test\"}");
    CookieMunch client = clientWith(transport);

    Identity identity = client.me();

    assertEquals("GET", transport.last.method());
    // Trailing slash on the base URL is trimmed, then "/v1/me" appended.
    assertEquals("https://example.test/v1/me", transport.last.url());
    assertEquals("Bearer fck_test_key", transport.last.headers().get("Authorization"));
    assertEquals("fck_test_key", transport.last.headers().get("X-API-Key"));
    assertEquals("application/json", transport.last.headers().get("Accept"));
    assertEquals("org_1", identity.orgId());
    assertEquals("pro", identity.plan());
    assertEquals("fck_test", identity.keyPrefix());
  }

  @Test
  void decodesSitesListOnGet() {
    FakeTransport transport = new FakeTransport();
    transport.reply(
        200,
        "[{\"cbid\":\"cb_1\",\"orgId\":\"org_1\",\"domain\":\"example.com\",\"verified\":true,"
            + "\"verifyToken\":\"tok\"}]");
    CookieMunch client = clientWith(transport);

    List<Site> sites = client.sites().list();

    assertEquals("GET", transport.last.method());
    assertEquals("https://example.test/v1/sites", transport.last.url());
    assertEquals(1, sites.size());
    assertEquals("cb_1", sites.get(0).cbid());
    assertTrue(sites.get(0).verified());
  }

  @Test
  void postsConsentIngestToPublicEndpoint() {
    FakeTransport transport = new FakeTransport();
    transport.reply(204, ""); // the real endpoint returns 204 with no body
    CookieMunch client = clientWith(transport);

    client.logConsent(
        ConsentIngest.builder("cb_1")
            .stamp("stamp-abc")
            .choices(true, false, true)
            .method("explicit")
            .ver(1)
            .utc(1700000000000L)
            .url("https://example.com/")
            .build());

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/api/v1/consent", transport.last.url());
    assertEquals("application/json", transport.last.headers().get("Content-Type"));
    String sentBody = transport.last.body();
    assertNotNull(sentBody);
    assertTrue(sentBody.contains("\"cbid\":\"cb_1\""), sentBody);
    assertTrue(sentBody.contains("\"stamp\":\"stamp-abc\""), sentBody);
    assertTrue(sentBody.contains("\"marketing\":true"), sentBody);
    assertTrue(sentBody.contains("\"method\":\"explicit\""), sentBody);
    // NON_NULL inclusion: unset optional fields must not be serialised.
    assertFalse(sentBody.contains("tcString"), sentBody);
    assertFalse(sentBody.contains("gppString"), sentBody);
    assertFalse(sentBody.contains("subjectId"), sentBody);
  }

  @Test
  void consentIngestIncludesSubjectIdWhenSet() {
    FakeTransport transport = new FakeTransport();
    transport.reply(204, "");
    CookieMunch client = clientWith(transport);

    client.logConsent(
        ConsentIngest.builder("cb_1")
            .stamp("stamp-abc")
            .choices(true, false, true)
            .method("explicit")
            .ver(1)
            .utc(1700000000000L)
            .url("https://example.com/")
            .subjectId("user-42")
            .build());

    assertTrue(transport.last.body().contains("\"subjectId\":\"user-42\""), transport.last.body());
  }

  @Test
  void buildsConsentStatsQueryString() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "[{\"Date\":\"2024-01-01\",\"OptIn\":5}]");
    CookieMunch client = clientWith(transport);

    var days = client.consent().stats("cb_1", 100L, 200L);

    assertEquals(
        "https://example.test/v1/sites/cb_1/consent/stats?from=100&to=200", transport.last.url());
    assertEquals(1, days.size());
    assertEquals("2024-01-01", days.get(0).date());
    assertEquals(5, days.get(0).optIn());
  }

  @Test
  void exportReturnsRawStringBody() {
    String csv = "stamp,method\nabc,explicit\n";
    FakeTransport transport = new FakeTransport();
    transport.reply(200, csv);
    CookieMunch client = clientWith(transport);

    String got = client.consent().export("cb_1");

    assertEquals("GET", transport.last.method());
    assertEquals(csv, got);
  }

  @Test
  void deleteSendsNoBodyAndTolerates204() {
    FakeTransport transport = new FakeTransport();
    transport.reply(204, "");
    CookieMunch client = clientWith(transport);

    client.sites().delete("cb_1");

    assertEquals("DELETE", transport.last.method());
    assertEquals("https://example.test/v1/sites/cb_1", transport.last.url());
  }

  @Test
  void mapsJsonErrorBodyToApiException() {
    FakeTransport transport = new FakeTransport();
    transport.reply(409, "{\"error\":\"cbid already claimed\",\"code\":\"cbid_taken\"}");
    CookieMunch client = clientWith(transport);

    CookieMunchApiException ex =
        assertThrows(
            CookieMunchApiException.class, () -> client.sites().create(new SiteCreate("dup.com")));

    assertEquals(409, ex.getStatusCode());
    assertEquals("cbid already claimed", ex.getMessage());
    assertEquals("cbid_taken", ex.getCode());
    assertTrue(ex.getBody().contains("cbid already claimed"));
  }

  @Test
  void mapsNonJsonErrorBodyWithFallbackMessage() {
    FakeTransport transport = new FakeTransport();
    transport.reply(502, "upstream boom");
    CookieMunch client = clientWith(transport);

    CookieMunchApiException ex =
        assertThrows(CookieMunchApiException.class, () -> client.me());

    assertEquals(502, ex.getStatusCode());
    assertEquals("request failed with status 502", ex.getMessage());
    assertEquals("upstream boom", ex.getBody());
  }

  @Test
  void editFlowSerializesOperations() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"ok\":true}");
    CookieMunch client = clientWith(transport);

    var result =
        client
            .sites()
            .editFlow(
                "cb_1",
                List.of(java.util.Map.<String, Object>of("op", "addView", "id", "v2", "surface", "bar")));

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/v1/sites/cb_1/flow/ops", transport.last.url());
    assertTrue(transport.last.body().contains("\"operations\""), transport.last.body());
    assertTrue(transport.last.body().contains("\"op\":\"addView\""), transport.last.body());
    assertTrue(result.ok());
  }

  @Test
  void getsOrg() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"id\":\"org_1\",\"name\":\"Acme\",\"plan\":\"pro\",\"logoUrl\":\"https://example.com/logo.png\"}");
    CookieMunch client = clientWith(transport);

    Org org = client.org().get();

    assertEquals("GET", transport.last.method());
    assertEquals("https://example.test/v1/org", transport.last.url());
    assertEquals("org_1", org.id());
    assertEquals("Acme", org.name());
    assertEquals("pro", org.plan());
    assertEquals("https://example.com/logo.png", org.logoUrl());
  }

  @Test
  void orgUpdateOmitsLogoUrlWhenNotSet() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"id\":\"org_1\",\"name\":\"New Name\",\"plan\":\"pro\",\"logoUrl\":null}");
    CookieMunch client = clientWith(transport);

    client.org().update(Map.of("name", "New Name"));

    assertEquals("PATCH", transport.last.method());
    assertEquals("https://example.test/v1/org", transport.last.url());
    String sentBody = transport.last.body();
    assertTrue(sentBody.contains("\"name\":\"New Name\""), sentBody);
    // An omitted field must not appear on the wire at all — not even as null.
    assertFalse(sentBody.contains("logoUrl"), sentBody);
  }

  @Test
  void orgUpdateSendsExplicitNullToClearLogoUrl() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"id\":\"org_1\",\"name\":\"Acme\",\"plan\":\"pro\",\"logoUrl\":null}");
    CookieMunch client = clientWith(transport);

    Map<String, Object> patch = new HashMap<>();
    patch.put("logoUrl", null);
    client.org().update(patch);

    // The known trap: Jackson's shared mapper is configured NON_NULL, which would silently
    // drop a null field. requestMapKeepingNulls must send it on the wire as literal JSON null.
    assertEquals("{\"logoUrl\":null}", transport.last.body());
  }

  @Test
  void audit() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"entries\":[{\"id\":\"aud_1\",\"action\":\"key.created\"}]}");
    CookieMunch client = clientWith(transport);

    Map<String, Object> result = client.audit(50);

    assertEquals("GET", transport.last.method());
    assertEquals("https://example.test/v1/audit?limit=50", transport.last.url());
    assertEquals(1, ((List<?>) result.get("entries")).size());
  }

  @Test
  void auditWithoutLimitOmitsQueryString() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"entries\":[]}");
    CookieMunch client = clientWith(transport);

    client.audit(null);

    assertEquals("https://example.test/v1/audit", transport.last.url());
  }

  @Test
  void uploadsAsset() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"url\":\"https://cdn.example.com/a.png\"}");
    CookieMunch client = clientWith(transport);

    var result = client.assets().upload(new AssetUpload("aGVsbG8=", "image/png"));

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/v1/assets", transport.last.url());
    assertTrue(transport.last.body().contains("\"data\":\"aGVsbG8=\""), transport.last.body());
    assertTrue(transport.last.body().contains("\"contentType\":\"image/png\""), transport.last.body());
    assertEquals("https://cdn.example.com/a.png", result.url());
  }

  @Test
  void rollsApiKey() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"key\":\"fck_live_new\",\"prefix\":\"fck_live\"}");
    CookieMunch client = clientWith(transport);

    ApiKeyIssued issued = client.keys().roll("fck_live");

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/v1/keys/fck_live/roll", transport.last.url());
    assertEquals("fck_live_new", issued.key());
  }

  @Test
  void updatesApiKey() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"ok\":true}");
    CookieMunch client = clientWith(transport);

    client.keys().update("fck_live", new ApiKeyUpdate("renamed", List.of("sites:read"), null));

    assertEquals("PATCH", transport.last.method());
    assertEquals("https://example.test/v1/keys/fck_live", transport.last.url());
    String sentBody = transport.last.body();
    assertTrue(sentBody.contains("\"name\":\"renamed\""), sentBody);
    assertTrue(sentBody.contains("\"scopes\":[\"sites:read\"]"), sentBody);
    // ApiKeyUpdate has no null-clears-it semantics: an unset field is simply omitted.
    assertFalse(sentBody.contains("cbids"), sentBody);
  }

  @Test
  void rollsWebhookSecret() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"secret\":\"whsec_new\"}");
    CookieMunch client = clientWith(transport);

    WebhookSecret secret = client.webhooks().rollSecret("wh_1");

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/v1/webhooks/wh_1/roll", transport.last.url());
    assertEquals("whsec_new", secret.secret());
  }

  @Test
  void testsWebhook() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"ok\":true,\"status\":200}");
    CookieMunch client = clientWith(transport);

    WebhookTestResult result = client.webhooks().test("wh_1");

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/v1/webhooks/wh_1/test", transport.last.url());
    assertTrue(result.ok());
    assertEquals(200, result.status());
  }

  @Test
  void listsWebhookDeadLetters() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"deadLetters\":[{\"id\":\"dl_1\"}]}");
    CookieMunch client = clientWith(transport);

    Map<String, Object> result = client.webhooks().deadLetters();

    assertEquals("GET", transport.last.method());
    assertEquals("https://example.test/v1/webhooks/dead-letters", transport.last.url());
    assertEquals(1, ((List<?>) result.get("deadLetters")).size());
  }

  @Test
  void replaysWebhookDeadLetter() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"ok\":true}");
    CookieMunch client = clientWith(transport);

    client.webhooks().replayDeadLetter("dl_1");

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/v1/webhooks/dead-letters/dl_1/replay", transport.last.url());
  }

  @Test
  void getsPreferenceRecord() {
    FakeTransport transport = new FakeTransport();
    transport.reply(200, "{\"subjectId\":\"sub_1\",\"purposes\":{\"marketing\":true}}");
    CookieMunch client = clientWith(transport);

    Map<String, Object> record = client.preferences().get("sub_1");

    assertEquals("GET", transport.last.method());
    assertEquals("https://example.test/v1/preferences/sub_1", transport.last.url());
    assertEquals("sub_1", record.get("subjectId"));
  }

  @Test
  void erasesDsarSubject() {
    FakeTransport transport = new FakeTransport();
    transport.reply(
        200,
        "{\"erased\":3,\"encryptionEnabled\":true,\"request\":{\"id\":\"dsar_1\",\"type\":\"deletion\","
            + "\"subjectEmail\":\"a@b.com\",\"regulation\":\"gdpr\",\"status\":\"in_progress\","
            + "\"createdAt\":1,\"dueAt\":2,\"note\":null}}");
    CookieMunch client = clientWith(transport);

    DsarEraseResult result = client.dsar().erase("dsar_1", "cb_1", "stamp-abc");

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/v1/dsar/dsar_1/erase", transport.last.url());
    String sentBody = transport.last.body();
    assertTrue(sentBody.contains("\"cbid\":\"cb_1\""), sentBody);
    assertTrue(sentBody.contains("\"stamp\":\"stamp-abc\""), sentBody);
    assertEquals(3, result.erased());
    assertTrue(result.encryptionEnabled());
    assertEquals("dsar_1", result.request().id());
  }

  @Test
  void exportsDsarSubject() {
    FakeTransport transport = new FakeTransport();
    transport.reply(
        200,
        "{\"records\":[{\"a\":1}],\"count\":1,\"request\":{\"id\":\"dsar_1\",\"type\":\"access\","
            + "\"subjectEmail\":\"a@b.com\",\"regulation\":\"gdpr\",\"status\":\"in_progress\","
            + "\"createdAt\":1,\"dueAt\":2,\"note\":null}}");
    CookieMunch client = clientWith(transport);

    DsarExportResult result = client.dsar().export("dsar_1", "cb_1", "stamp-abc");

    assertEquals("POST", transport.last.method());
    assertEquals("https://example.test/v1/dsar/dsar_1/export", transport.last.url());
    String sentBody = transport.last.body();
    assertTrue(sentBody.contains("\"cbid\":\"cb_1\""), sentBody);
    assertTrue(sentBody.contains("\"stamp\":\"stamp-abc\""), sentBody);
    assertEquals(1, result.count());
    assertEquals("dsar_1", result.request().id());
  }
}
