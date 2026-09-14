package net.cookiemunch.model;

/** One anonymised consent record from {@code GET /v1/sites/{cbid}/consent/log}. */
public record ConsentLogRow(
    String stamp,
    long receivedAt,
    String region,
    String method,
    ConsentChoices choices,
    String anonIp,
    String url) {}
