package net.cookiemunch;

import java.io.IOException;
import java.util.Map;

/**
 * The pluggable HTTP layer used by {@link CookieMunch}. The default implementation
 * ({@link JdkHttpTransport}) wraps the JDK's {@code java.net.http.HttpClient}; tests
 * (or non-standard runtimes) can inject their own implementation via
 * {@link CookieMunch.Builder#transport(HttpTransport)}.
 */
public interface HttpTransport {

  /**
   * Perform a single HTTP request and return its status + body. Implementations must
   * not throw for non-2xx responses — those are surfaced to the caller as an
   * {@link HttpTransport.Response}; only genuine transport failures throw.
   *
   * @param request the request to send
   * @return the response status and (possibly empty) body
   * @throws IOException on a transport-level failure (connection refused, timeout, …)
   */
  Response send(Request request) throws IOException;

  /** An outbound HTTP request. {@code body} is {@code null} when there is no payload. */
  record Request(String method, String url, Map<String, String> headers, String body) {}

  /** An HTTP response. {@code body} is never {@code null} (empty string for no body). */
  record Response(int status, String body) {}
}
