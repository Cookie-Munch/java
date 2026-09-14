package net.cookiemunch.model;

import java.util.Map;

/** A full account-level, reusable banner design. {@code json} is the open design payload. */
public record BannerRecord(
    String id,
    String orgId,
    String name,
    Map<String, Object> json,
    long createdAt,
    long updatedAt) {}
