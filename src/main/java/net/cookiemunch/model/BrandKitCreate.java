package net.cookiemunch.model;

/** Body of {@code POST /v1/brand-kits}. */
public record BrandKitCreate(
    String name, Object theme, Object content, String logoUrl, String customCss) {

  public BrandKitCreate(String name, Object theme) {
    this(name, theme, null, null, null);
  }
}
