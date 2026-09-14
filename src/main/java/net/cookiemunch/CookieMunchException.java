package net.cookiemunch;

/**
 * Base unchecked exception for the SDK. Raised for transport failures and JSON
 * encode/decode errors. HTTP non-2xx responses raise the {@link CookieMunchApiException}
 * subclass, which additionally carries the status code and response body.
 */
public class CookieMunchException extends RuntimeException {

  public CookieMunchException(String message) {
    super(message);
  }

  public CookieMunchException(String message, Throwable cause) {
    super(message, cause);
  }
}
