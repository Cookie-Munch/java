package net.cookiemunch;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A site's rights-page captcha: your own Turnstile, hCaptcha or reCAPTCHA keys (recommended), or off.
 * A site without its own setting uses the organisation default ({@link OrgResource#rightsCaptcha()}),
 * else off. Cookie Munch's own captcha is never used on rights pages. Needs sites:read / sites:write.
 */
public final class RightsCaptchaResource {

  private final CookieMunch client;

  RightsCaptchaResource(CookieMunch client) {
    this.client = client;
  }

  /** {@code GET /v1/sites/{cbid}/rights-captcha}: the setting in force ({@code source}: site, org or default). Never the secret. */
  public Map<String, Object> get(String cbid) {
    return client.requestMap("GET", path(cbid), null);
  }

  /**
   * {@code PUT /v1/sites/{cbid}/rights-captcha}. Keys: {@code mode} ({@code own} or {@code off}); for own,
   * {@code provider} (turnstile, hcaptcha, recaptcha_v2, recaptcha_v3), {@code siteKey} and {@code secret}
   * (write-only; omit to keep the stored one) — the provider must accept the secret before it is saved;
   * {@code minScore} for recaptcha_v3 (0.1-0.9). Off removes spam protection beyond the spam-trap field,
   * the rate limits and the email code.
   */
  public Map<String, Object> set(String cbid, Map<String, Object> input) {
    return client.requestMap("PUT", path(cbid), new LinkedHashMap<>(input));
  }

  /** {@code DELETE /v1/sites/{cbid}/rights-captcha}: the site inherits the organisation default again. Returns the setting now in force. */
  public Map<String, Object> remove(String cbid) {
    return client.requestMap("DELETE", path(cbid), null);
  }

  private static String path(String cbid) {
    return "/v1/sites/" + Query.pathSegment(cbid) + "/rights-captcha";
  }
}
