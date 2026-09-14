package net.cookiemunch;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A tiny query-string builder. {@code null}/empty values are skipped. Produces a
 * leading {@code "?"} when at least one parameter is present, otherwise the empty
 * string, so it can be appended to a path unconditionally.
 */
final class Query {

  private final StringBuilder sb = new StringBuilder();

  Query add(String key, String value) {
    if (value != null && !value.isEmpty()) {
      sb.append(sb.length() == 0 ? '?' : '&')
          .append(encode(key))
          .append('=')
          .append(encode(value));
    }
    return this;
  }

  Query add(String key, Long value) {
    return value == null ? this : add(key, String.valueOf(value.longValue()));
  }

  Query add(String key, Integer value) {
    return value == null ? this : add(key, String.valueOf(value.intValue()));
  }

  @Override
  public String toString() {
    return sb.toString();
  }

  static String encode(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }

  /** Encode a single URL path segment (spaces as {@code %20}, slashes escaped). */
  static String pathSegment(String value) {
    return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
  }
}
