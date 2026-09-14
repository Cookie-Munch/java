package net.cookiemunch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import net.cookiemunch.model.ConsentIngest;
import net.cookiemunch.model.Identity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * End-to-end test over the real {@link JdkHttpTransport} + {@code java.net.http.HttpClient},
 * driven by the JDK's built-in {@code com.sun.net.httpserver.HttpServer} (no external deps).
 * Proves the default transport actually speaks HTTP correctly.
 */
class IntegrationHttpServerTest {

  private HttpServer server;
  private CookieMunch client;

  private volatile String lastMethod;
  private volatile String lastPath;
  private volatile String lastBody;
  private final Map<String, String> lastHeaders = new ConcurrentHashMap<>();

  @BeforeEach
  void setUp() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/",
        exchange -> {
          lastMethod = exchange.getRequestMethod();
          lastPath = exchange.getRequestURI().getPath();
          lastBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
          String auth = exchange.getRequestHeaders().getFirst("Authorization");
          String apiKey = exchange.getRequestHeaders().getFirst("X-API-Key");
          if (auth != null) {
            lastHeaders.put("Authorization", auth);
          }
          if (apiKey != null) {
            lastHeaders.put("X-API-Key", apiKey);
          }

          if ("/v1/me".equals(lastPath)) {
            byte[] payload =
                "{\"orgId\":\"org_1\",\"plan\":\"pro\",\"keyPrefix\":\"fck_test\"}"
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
              os.write(payload);
            }
          } else {
            exchange.sendResponseHeaders(204, -1); // no body
          }
          exchange.close();
        });
    server.start();

    int port = server.getAddress().getPort();
    // Exercise the default JdkHttpTransport by not injecting a transport.
    client = CookieMunch.builder("fck_test_key").baseUrl("http://127.0.0.1:" + port).build();
  }

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop(0);
    }
  }

  @Test
  void getMeRoundTripsThroughRealTransport() {
    Identity identity = client.me();

    assertEquals("GET", lastMethod);
    assertEquals("/v1/me", lastPath);
    assertEquals("Bearer fck_test_key", lastHeaders.get("Authorization"));
    assertEquals("fck_test_key", lastHeaders.get("X-API-Key"));
    assertEquals("org_1", identity.orgId());
    assertEquals("pro", identity.plan());
    assertEquals("fck_test", identity.keyPrefix());
  }

  @Test
  void postConsentRoundTripsAndAccepts204() {
    client.logConsent(
        ConsentIngest.builder("cb_1")
            .choices(true, false, true)
            .method("explicit")
            .ver(1)
            .utc(1700000000000L)
            .url("https://example.com/")
            .build());

    assertEquals("POST", lastMethod);
    assertEquals("/api/v1/consent", lastPath);
    assertTrue(lastBody.contains("\"cbid\":\"cb_1\""), lastBody);
    assertTrue(lastBody.contains("\"method\":\"explicit\""), lastBody);
  }
}
