package net.cookiemunch.model;

/**
 * One A/B banner-experiment variant's results. Per the authoritative OpenAPI schema the
 * wire fields are {@code { variant, impressions, optIns, optInRate }} (note {@code optIns},
 * not the {@code optIn}/{@code optOut} pair in the TypeScript SDK). {@code optInRate} is a
 * percentage.
 */
public record AbResult(String variant, int impressions, int optIns, double optInRate) {}
