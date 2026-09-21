package net.cookiemunch.model;

/**
 * Optional body of {@code POST /v1/keys}. Every field may be {@code null}.
 *
 * <p>Set {@code scopes} and/or {@code cbids} for a least-privilege key — leave both null
 * for full access to the whole org. A key locked with {@code cbids} works only on those
 * sites and on no org-wide endpoint. {@code expiresInDays} is 1–3650.
 */
public record ApiKeyIssueInput(String name, java.util.List<String> scopes, java.util.List<String> cbids, Integer expiresInDays) {
  /** A named, full-access key — the original one-argument form. */
  public ApiKeyIssueInput(String name) {
    this(name, null, null, null);
  }
}
