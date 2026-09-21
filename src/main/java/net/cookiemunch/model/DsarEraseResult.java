package net.cookiemunch.model;

/**
 * Response of {@code POST /v1/dsar/{id}/erase}. {@code warning} is present when no KEK is
 * configured, so nothing was cryptographically erased.
 */
public record DsarEraseResult(int erased, boolean encryptionEnabled, String warning, DsarRequest request) {}
