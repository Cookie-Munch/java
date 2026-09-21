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
}
