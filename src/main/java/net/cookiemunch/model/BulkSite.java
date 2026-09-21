package net.cookiemunch.model;

/** One site to create in a bulk call. {@code cbid} and {@code platform} may be {@code null}. */
public record BulkSite(String domain, String cbid, String platform) {}
