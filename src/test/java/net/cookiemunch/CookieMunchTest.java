package net.cookiemunch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.cookiemunch.model.ConsentIngest;
import net.cookiemunch.model.Identity;
import net.cookiemunch.model.Site;
import net.cookiemunch.model.SiteCreate;

import org.junit.jupiter.api.Test;

import java.util.List;

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
}
