package net.cookiemunch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Thin Jackson wrapper. Unknown JSON fields are ignored on read (forward-compatible
 * with server additions) and {@code null} fields are omitted on write (so optional
 * request-body fields disappear rather than serialise as {@code null}).
 */
final class Json {

  private final ObjectMapper mapper;

  Json() {
    this.mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  String write(Object value) {
    if (value instanceof Verbatim v) {
      return v.json();
    }
    try {
      return mapper.writeValueAsString(value);
    } catch (JsonProcessingException e) {
      throw new CookieMunchException("cookiemunch: failed to encode request body", e);
    }
  }

  /**
   * Encode a map keeping its null values. The shared mapper drops nulls everywhere —
   * right for optional record fields, wrong where null MEANS something, as in a PATCH that
   * clears a field. Without this, a Java caller could not clear a child org's DSAR routing
   * override at all: the null was silently removed and the request left it in place.
   */
  Verbatim keepingNulls(Map<String, Object> value) {
    try {
      return new Verbatim(mapper.copy().setSerializationInclusion(JsonInclude.Include.ALWAYS).writeValueAsString(value));
    } catch (JsonProcessingException e) {
      throw new CookieMunchException("cookiemunch: failed to encode request body", e);
    }
  }

  /** A body that is already JSON, sent exactly as-is. */
  record Verbatim(String json) {}

  <T> T read(String body, Class<T> type) {
    try {
      return mapper.readValue(body, type);
    } catch (JsonProcessingException e) {
      throw new CookieMunchException("cookiemunch: failed to decode response", e);
    }
  }

  <T> List<T> readList(String body, Class<T> element) {
    try {
      return mapper.readValue(body, mapper.getTypeFactory().constructCollectionType(List.class, element));
    } catch (JsonProcessingException e) {
      throw new CookieMunchException("cookiemunch: failed to decode response", e);
    }
  }

  List<Map<String, Object>> readListOfMaps(String body) {
    try {
      return mapper.readValue(
          body,
          mapper.getTypeFactory().constructCollectionType(
              List.class,
              mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)));
    } catch (JsonProcessingException e) {
      throw new CookieMunchException("cookiemunch: failed to decode response", e);
    }
  }

  Map<String, Object> readMap(String body) {
    try {
      return mapper.readValue(
          body, mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class));
    } catch (JsonProcessingException e) {
      throw new CookieMunchException("cookiemunch: failed to decode response", e);
    }
  }

  /** Parse to a tree, returning {@code null} on any failure (used for best-effort error bodies). */
  JsonNode readTreeQuiet(String body) {
    try {
      return mapper.readTree(body);
    } catch (Exception e) {
      return null;
    }
  }
}
