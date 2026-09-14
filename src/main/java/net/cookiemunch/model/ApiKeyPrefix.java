package net.cookiemunch.model;

/** Display metadata for an issued API key (never the secret) — from {@code GET /v1/keys}. */
public record ApiKeyPrefix(String prefix, Long createdAt) {}
