package net.cookiemunch;

import net.cookiemunch.model.Member;

import java.util.List;
import java.util.Map;

/** The {@code /v1/members} endpoints. */
public final class MembersResource {

  private final CookieMunch client;

  MembersResource(CookieMunch client) {
    this.client = client;
  }

  /** List the org's members — {@code GET /v1/members}. */
  public List<Member> list() {
    return client.getList("/v1/members", Member.class);
  }

  /** Invite a member by email with a role — {@code POST /v1/members}. */
  public Member invite(String email, String role) {
    return client
        .request("POST", "/v1/members", Map.of("email", email, "role", role), MemberEnvelope.class)
        .member();
  }

  /** Change a member's role — {@code PATCH /v1/members/{userId}}. */
  public Member setRole(String userId, String role) {
    String path = "/v1/members/" + Query.pathSegment(userId);
    return client.request("PATCH", path, Map.of("role", role), MemberEnvelope.class).member();
  }

  /** Remove a member — {@code DELETE /v1/members/{userId}}. */
  public void remove(String userId) {
    client.requestVoid("DELETE", "/v1/members/" + Query.pathSegment(userId), null);
  }
}
