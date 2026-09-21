package net.cookiemunch.model;

/** Tunes a warehouse enforcement plan. Every field may be {@code null}. */
public record EnforcementOptions(String permitsTable, String policyPrefix) {}
