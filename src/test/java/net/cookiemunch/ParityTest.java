package net.cookiemunch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Every operation the Developer API documents is reachable from this SDK.
 *
 * <p>The list lives in {@code sdks/operations.json}, generated from the server's OpenAPI
 * document and shared by all six server-side SDKs. Every public method of every resource
 * is called through a recording transport — arguments are built from their reflected
 * types, records through their canonical constructors — and what reached the wire is
 * compared in both directions.
 */
class ParityTest {

  private static final String PLACEHOLDER = "x1";

  /** ../operations.json in the product repo; ./operations.json in the published SDK repo. */
  private static Path operationsFile() {
    Path shared = Path.of("../operations.json");
    return Files.exists(shared) ? shared : Path.of("operations.json");
  }

  @Test
  void everyDocumentedOperationIsReachable() throws Exception {
    @SuppressWarnings("unchecked")
    List<String> operations = (List<String>) new ObjectMapper()
        .readValue(Files.readString(operationsFile()), Map.class).get("operations");

    TreeSet<String> seen = new TreeSet<>();
    HttpTransport transport = request -> {
      String url = request.url();
      if (!url.contains("/api/v1/")) { // the public consent ingest is not the Developer API
        String path = url.split("\\?", 2)[0].split("/v1", 2)[1];
        List<String> segs = new ArrayList<>();
        for (String s : path.split("/", -1)) segs.add(s.equals(PLACEHOLDER) ? "{}" : s);
        seen.add(request.method() + " /v1" + String.join("/", segs));
      }
      return new HttpTransport.Response(200, "{}");
    };
    CookieMunch client = CookieMunch.builder("fck_test").baseUrl("https://api.example.test").transport(transport).build();

    List<Object> targets = new ArrayList<>();
    targets.add(client);
    for (Method m : CookieMunch.class.getMethods()) {
      if (m.getParameterCount() == 0 && m.getReturnType().getSimpleName().endsWith("Resource")) {
        targets.add(m.invoke(client));
      }
    }
    for (Object target : targets) {
      for (Method m : target.getClass().getMethods()) {
        if (m.getDeclaringClass() != target.getClass() || Modifier.isStatic(m.getModifiers())) continue;
        Object[] args = new Object[m.getParameterCount()];
        for (int i = 0; i < args.length; i++) {
          args[i] = placeholder(m.getGenericParameterTypes()[i]);
        }
        try {
          m.invoke(target, args);
        } catch (Exception ignored) {
          // only what reached the wire matters here
        }
      }
    }

    List<String> missing = new ArrayList<>(operations);
    missing.removeAll(seen);
    assertEquals(List.of(), missing, missing.size() + " documented operations are unreachable");
    List<String> extra = new ArrayList<>(seen);
    extra.removeAll(operations);
    assertEquals(List.of(), extra, "calls the API does not document");
  }

  private static Object placeholder(Type type) throws Exception {
    Class<?> raw = type instanceof ParameterizedType p ? (Class<?>) p.getRawType() : (Class<?>) type;
    if (raw == String.class) return PLACEHOLDER;
    if (raw == boolean.class || raw == Boolean.class) return true;
    if (raw == int.class || raw == Integer.class) return 1;
    if (raw == long.class || raw == Long.class) return 1L;
    if (List.class.isAssignableFrom(raw)) {
      Type element = type instanceof ParameterizedType p ? p.getActualTypeArguments()[0] : String.class;
      return List.of(placeholder(element));
    }
    if (Map.class.isAssignableFrom(raw)) return Map.of("a", PLACEHOLDER);
    if (raw.isRecord()) {
      RecordComponent[] parts = raw.getRecordComponents();
      Class<?>[] types = new Class<?>[parts.length];
      Object[] values = new Object[parts.length];
      for (int i = 0; i < parts.length; i++) {
        types[i] = parts[i].getType();
        values[i] = placeholder(parts[i].getGenericType());
      }
      return raw.getDeclaredConstructor(types).newInstance(values);
    }
    return raw == Object.class ? Map.of("a", PLACEHOLDER) : null;
  }
}
