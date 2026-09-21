package net.cookiemunch.model;

/** The key's organisation — {@code GET /v1/org}. {@code logoUrl} is {@code null} when unset. */
public record Org(String id, String name, String plan, String logoUrl) {}
