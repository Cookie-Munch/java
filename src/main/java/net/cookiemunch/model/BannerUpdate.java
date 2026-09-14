package net.cookiemunch.model;

import java.util.Map;

/** Body of {@code PUT /v1/banners/{id}}. Either field may be {@code null} to leave it unchanged. */
public record BannerUpdate(String name, Map<String, Object> json) {}
