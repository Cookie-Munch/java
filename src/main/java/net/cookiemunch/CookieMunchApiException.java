package net.cookiemunch;

/**
 * Thrown for any non-2xx HTTP response from the Cookie Munch API. Carries the HTTP
 * status code and the raw response body, plus the server's parsed {@code error}
 * message and optional {@code code} field when the body is JSON.
 */
public class CookieMunchApiException extends CookieMunchException {

  private final int statusCode;
  private final String body;
  private final String code;

  public CookieMunchApiException(int statusCode, String message, String body, String code) {
    super(message);
    this.statusCode = statusCode;
    this.body = body;
    this.code = code;
  }

  /** The HTTP status code of the failed response. */
  public int getStatusCode() {
    return statusCode;
  }

  /** The raw, undecoded response body (may be empty, never {@code null}). */
  public String getBody() {
    return body;
  }

  /** The server's optional {@code code} field (e.g. {@code "banner_in_use"}); may be {@code null}. */
  public String getCode() {
    return code;
  }
}
