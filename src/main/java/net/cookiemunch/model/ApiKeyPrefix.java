package net.cookiemunch.model;

/** Display metadata for an issued API key (never the secret) — from {@code GET /v1/keys}. */
public record ApiKeyPrefix(
    String prefix,
    Long createdAt,
    /** Epoch ms the key stops working; null = never. */
    Long expiresAt,
    String name,
    /** null = full access. */
    java.util.List<String> scopes,
    java.util.List<String> cbids,
    /** Epoch ms it last authenticated a request (recorded at most hourly); null = not since recording began. */
    Long lastUsedAt) {
  public ApiKeyPrefix(String prefix, Long createdAt) {
    this(prefix, createdAt, null, null, null, null, null);
  }
}
