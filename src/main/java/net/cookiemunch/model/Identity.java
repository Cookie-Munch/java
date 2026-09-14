package net.cookiemunch.model;

/** Response of {@code GET /v1/me}. */
public record Identity(String orgId, String plan, String keyPrefix) {}
