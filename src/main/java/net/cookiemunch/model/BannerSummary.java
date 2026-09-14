package net.cookiemunch.model;

import java.util.List;

/** Lightweight listing view of an account-level banner design — from {@code GET /v1/banners}. */
public record BannerSummary(
    String id, String name, long updatedAt, List<String> assignedCbids) {}
