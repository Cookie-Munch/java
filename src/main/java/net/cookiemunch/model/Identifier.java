package net.cookiemunch.model;

/** One of the ways a person is known, e.g. {@code new Identifier("email_sha256", "<hex>")}. */
public record Identifier(String space, String value) {}
