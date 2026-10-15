package net.cookiemunch;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A site's sign-in provider (Clerk, Atlas or any JWKS issuer): a visitor signed in to your site is
 * verified by their session, with no code mail. A site without its own setting uses the organisation
 * default ({@link OrgResource#rightsIdentity()}). Nothing here is secret. Needs sites:read / sites:write.
 */
public final class RightsIdentityResource {

  private final CookieMunch client;

  RightsIdentityResource(CookieMunch client) {
    this.client = client;
  }

  /** {@code GET /v1/sites/{cbid}/rights-identity}: the setting in force ({@code source}: site, org or default). */
  public Map<String, Object> get(String cbid) {
    return client.requestMap("GET", path(cbid), null);
  }

  /**
   * {@code PUT /v1/sites/{cbid}/rights-identity}. Keys: {@code type} (clerk, atlas or oidc_jwt) and
   * {@code issuer} (exactly the token's iss) are required; {@code jwksUrl} is required for oidc_jwt
   * (clerk/atlas default to {@code <issuer>/.well-known/jwks.json}); {@code authorizedParties} (your
   * own origins) is required for clerk and atlas; optional {@code audience}, {@code emailClaim}
   * (default email), {@code emailVerifiedClaim} (default email_verified) and {@code sessionCookieName}
   * (default __session), and {@code trustEmailClaim} (clerk/atlas only). oidc_jwt needs {@code audience}
   * or {@code authorizedParties}. The JWKS is fetched and checked before anything is saved. The token must
   * carry the email and a verified-email claim set to true, unless {@code trustEmailClaim} is true and the
   * claim is absent.
   */
  public Map<String, Object> set(String cbid, Map<String, Object> input) {
    return client.requestMap("PUT", path(cbid), new LinkedHashMap<>(input));
  }

  /** {@code DELETE /v1/sites/{cbid}/rights-identity}: the site inherits the organisation default again. Returns the setting now in force. */
  public Map<String, Object> remove(String cbid) {
    return client.requestMap("DELETE", path(cbid), null);
  }

  private static String path(String cbid) {
    return "/v1/sites/" + Query.pathSegment(cbid) + "/rights-identity";
  }
}
