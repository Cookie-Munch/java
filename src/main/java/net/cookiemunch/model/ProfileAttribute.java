package net.cookiemunch.model;

/** One attribute value with its provenance. {@code purpose} and {@code collectedAt} may be {@code null}. */
public record ProfileAttribute(String value, String purpose, Long collectedAt) {}
