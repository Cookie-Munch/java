package net.cookiemunch.model;

/** Response of {@code GET /v1/sites/{cbid}/snippet}. */
public record InstallSnippet(
    String snippet, String src, String api, String cbid, String blockingMode) {}
