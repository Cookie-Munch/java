package net.cookiemunch.model;

/**
 * Body of {@code POST /v1/assets}. {@code data} is the image, base64-encoded (a
 * {@code data:} URL is also accepted); {@code contentType} is one of
 * {@code image/png, image/jpeg, image/webp, image/gif, image/svg+xml}. Up to 1,000,000 bytes.
 */
public record AssetUpload(String data, String contentType) {}
