package net.cookiemunch.model;

/**
 * One classified cookie in a scan snapshot. {@code category} is one of
 * {@code necessary | preferences | statistics | marketing | unclassified}.
 */
public record CategorizedCookie(
    String name, String domain, String category, String purpose, String provider, String expiry) {}
