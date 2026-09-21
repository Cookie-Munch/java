package net.cookiemunch;

import net.cookiemunch.model.AssetUpload;
import net.cookiemunch.model.AssetUploadResult;

/** The {@code /v1/assets} endpoints. */
public final class AssetsResource {

  private final CookieMunch client;

  AssetsResource(CookieMunch client) {
    this.client = client;
  }

  /** Upload a banner image (up to 1,000,000 bytes) and get its public URL — {@code POST /v1/assets}. Requires sites:write. */
  public AssetUploadResult upload(AssetUpload input) {
    return client.request("POST", "/v1/assets", input, AssetUploadResult.class);
  }
}
