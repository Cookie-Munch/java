package net.cookiemunch.model;

import java.util.List;

/**
 * Body of {@code PATCH /v1/keys/{prefix}}. Rename a key, or replace its scopes or the
 * properties it is locked to — only the fields sent change; every field may be {@code null}
 * to leave it as-is (no null-clears-it semantics here, unlike {@code PATCH /v1/org}).
 */
public record ApiKeyUpdate(String name, List<String> scopes, List<String> cbids) {}
