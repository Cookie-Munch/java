package net.cookiemunch.model;

/** Tunes a generated policy. Every field may be {@code null}. */
public record PolicyOptions(String contactEmail, String effectiveDate, java.util.List<String> jurisdictions) {}
