package net.cookiemunch.model;

/** A reusable, org-level banner theme. {@code theme}/{@code content} are open shapes. */
public record BrandKit(
    String id,
    String orgId,
    String name,
    Object theme,
    Object content,
    String logoUrl,
    String customCss) {}
