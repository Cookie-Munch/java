package net.cookiemunch.model;

/** Mints a key for a child org. Every field may be {@code null}. */
public record ChildKeyInput(String name, java.util.List<String> scopes, java.util.List<String> cbids) {}
