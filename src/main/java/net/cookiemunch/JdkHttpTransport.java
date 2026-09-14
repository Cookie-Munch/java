package net.cookiemunch;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * The default {@link HttpTransport}, backed by the JDK's {@code java.net.http.HttpClient}.
 * Thread-safe and reusable.
 */
public final class JdkHttpTransport implements HttpTransport {

  private final HttpClient client;
  private final Duration requestTimeout;

  /** Build a transport with a fresh {@link HttpClient} and sensible timeouts. */
  public JdkHttpTransport() {
    this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build(), Duration.ofSeconds(30));
  }

  /** Build a transport over a caller-supplied {@link HttpClient} (custom proxies, executors, …). */
  public JdkHttpTransport(HttpClient client) {
    this(client, Duration.ofSeconds(30));
  }

  public JdkHttpTransport(HttpClient client, Duration requestTimeout) {
    this.client = client;
    this.requestTimeout = requestTimeout;
  }

  @Override
  public HttpTransport.Response send(HttpTransport.Request request) throws IOException {
    String body = request.body();
    HttpRequest.BodyPublisher publisher =
        body == null
            ? HttpRequest.BodyPublishers.noBody()
            : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8);

    HttpRequest.Builder builder =
        HttpRequest.newBuilder()
            .uri(URI.create(request.url()))
            .timeout(requestTimeout)
            .method(request.method(), publisher);

    for (Map.Entry<String, String> header : request.headers().entrySet()) {
      builder.header(header.getKey(), header.getValue());
    }

    try {
      HttpResponse<String> response =
          client.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      String responseBody = response.body();
      return new HttpTransport.Response(response.statusCode(), responseBody == null ? "" : responseBody);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("cookiemunch: request interrupted", e);
    }
  }
}
