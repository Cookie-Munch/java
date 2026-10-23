package net.cookiemunch;

import net.cookiemunch.model.Org;

import java.util.LinkedHashMap;
import java.util.Map;

/** The {@code /v1/org} endpoints. Requires an unscoped key that is not property-locked. */
public final class OrgResource {

  private final CookieMunch client;

  OrgResource(CookieMunch client) {
    this.client = client;
  }

  /** The key's organisation — {@code GET /v1/org}. */
  public Org get() {
    return client.get("/v1/org", Org.class);
  }

  /**
   * Rename the org or set its logo — {@code PATCH /v1/org}. {@code patch} is sent as-is, so a
   * {@code "logoUrl"} entry mapped to {@code null} removes the logo, and omitting the key
   * leaves it unchanged (nulls are kept for this call). Deleting the org is not available
   * through the API.
   */
  public Map<String, Object> update(Map<String, Object> patch) {
    return client.requestMapKeepingNulls("PATCH", "/v1/org", new LinkedHashMap<>(patch));
  }

  /**
   * Your organisation's own data-controller identity — {@code GET /v1/org/controller}. Every
   * rights notice names it, before any site config {@code controller} block. {@code set} is
   * false when there is none.
   */
  public Map<String, Object> controller() {
    return client.requestMap("GET", "/v1/org/controller", null);
  }

  /**
   * Set the organisation's data-controller identity — {@code PUT /v1/org/controller}. Keys:
   * {@code legalName} (required), {@code contactEmail}, {@code address}, {@code dpoContact}.
   * Replaces the stored one: a key left out is removed. Needs an unscoped key or {@code rights:write}.
   */
  public Map<String, Object> setController(Map<String, Object> input) {
    return client.requestMap("PUT", "/v1/org/controller", new LinkedHashMap<>(input));
  }

  /** Remove the identity — {@code DELETE /v1/org/controller}. Notices then name the site config {@code controller} block. */
  public void removeController() {
    client.requestVoid("DELETE", "/v1/org/controller", null);
  }

  /**
   * Your own mail provider for rights-request mail — {@code GET /v1/org/mail-provider}. Never
   * returns the credential, only {@code secretLast4}; {@code sender} says whether verification
   * codes go through it now ({@code own}) or are not mailed at all ({@code none}) — there is no
   * shared sender.
   */
  public Map<String, Object> mailProvider() {
    return client.requestMap("GET", "/v1/org/mail-provider", null);
  }

  /**
   * Connect your own mail provider (recommended) — {@code PUT /v1/org/mail-provider}. Keys:
   * {@code provider} (brevo, sendgrid, postmark, resend, ses, smtp), {@code fromEmail},
   * {@code fromName}, {@code replyTo}, {@code secret} (write-only; omit to keep the stored one,
   * same provider only), {@code ses} ({@code region}, {@code accessKeyId}) or {@code smtp}
   * ({@code host}, {@code port}, {@code security}, {@code username}). Replaces the stored one;
   * it carries verification codes only after {@link #testMailProvider} succeeds.
   */
  public Map<String, Object> setMailProvider(Map<String, Object> input) {
    return client.requestMap("PUT", "/v1/org/mail-provider", new LinkedHashMap<>(input));
  }

  /**
   * Send a test through your provider — {@code POST /v1/org/mail-provider/test}. A null
   * {@code to} sends to the reply-to, else the from address. A refusal comes back as
   * {@code ok: false} with the provider's reason.
   */
  public Map<String, Object> testMailProvider(String to) {
    Map<String, Object> body = new LinkedHashMap<>();
    if (to != null) body.put("to", to);
    return client.requestMap("POST", "/v1/org/mail-provider/test", body);
  }

  /** Remove your provider — {@code DELETE /v1/org/mail-provider}. No verification codes are mailed after this. */
  public void removeMailProvider() {
    client.requestVoid("DELETE", "/v1/org/mail-provider", null);
  }

  /** The default captcha for your rights pages (sites without their own) — {@code GET /v1/org/rights-captcha}. Never the secret. */
  public Map<String, Object> rightsCaptcha() {
    return client.requestMap("GET", "/v1/org/rights-captcha", null);
  }

  /** Set the default — {@code PUT /v1/org/rights-captcha}: own keys (recommended) or off. Same keys as {@link RightsCaptchaResource#set}. */
  public Map<String, Object> setRightsCaptcha(Map<String, Object> input) {
    return client.requestMap("PUT", "/v1/org/rights-captcha", new LinkedHashMap<>(input));
  }

  /** Remove the default — {@code DELETE /v1/org/rights-captcha}: sites without their own setting have no captcha. Returns the setting now in force. */
  public Map<String, Object> removeRightsCaptcha() {
    return client.requestMap("DELETE", "/v1/org/rights-captcha", null);
  }

  /** The default sign-in provider rights requests are verified with (sites without their own) — {@code GET /v1/org/rights-identity}. */
  public Map<String, Object> rightsIdentity() {
    return client.requestMap("GET", "/v1/org/rights-identity", null);
  }

  /** Set the default — {@code PUT /v1/org/rights-identity}. Same keys as {@link RightsIdentityResource#set}. */
  public Map<String, Object> setRightsIdentity(Map<String, Object> input) {
    return client.requestMap("PUT", "/v1/org/rights-identity", new LinkedHashMap<>(input));
  }

  /** Remove the default — {@code DELETE /v1/org/rights-identity}. Returns the setting now in force. */
  public Map<String, Object> removeRightsIdentity() {
    return client.requestMap("DELETE", "/v1/org/rights-identity", null);
  }
}
