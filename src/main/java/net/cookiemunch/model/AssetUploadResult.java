package net.cookiemunch.model;

/** Response of {@code POST /v1/assets}: the stored image's public URL. */
public record AssetUploadResult(String url) {}
