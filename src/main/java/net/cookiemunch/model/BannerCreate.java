package net.cookiemunch.model;

import java.util.Map;

/** Body of {@code POST /v1/banners}. */
public record BannerCreate(String name, Map<String, Object> json) {}
