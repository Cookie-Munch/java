package net.cookiemunch.model;

/**
 * Response of the cookie-scan endpoints. Per the authoritative OpenAPI schema the wire
 * shape is {@code { status, lastScannedAt }} — not the richer {@code ScanResult} shape
 * in the TypeScript SDK. {@code status} is {@code idle | scanning}; {@code lastScannedAt}
 * is epoch-ms of the last completed scan, or {@code null}.
 */
public record ScanStatus(String status, Long lastScannedAt) {}
