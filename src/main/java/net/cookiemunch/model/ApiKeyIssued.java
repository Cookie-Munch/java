package net.cookiemunch.model;

/** Response of {@code POST /v1/keys}. The {@code key} is returned ONCE and never again. */
public record ApiKeyIssued(String key, String prefix) {}
