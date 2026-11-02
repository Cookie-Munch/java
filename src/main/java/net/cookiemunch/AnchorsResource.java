package net.cookiemunch;

import java.util.Map;

/**
 * Audit anchoring — {@code /v1/anchors}: post the head of your own hash-chained log and keep the signed
 * receipt. {@code seq} must strictly increase per stream; the same seq and head again returns the
 * original receipt, a different head for an anchored seq is a 409 ({@code anchor_head_mismatch}).
 * {@code create} needs {@code anchors:write}; {@code list} and {@code get} need {@code anchors:read}.
 */
public final class AnchorsResource {

  private final CookieMunch client;

  AnchorsResource(CookieMunch client) {
    this.client = client;
  }

  /** Anchor {@code head} at {@code seq} on {@code stream} — {@code POST /v1/anchors}. Signed with the key in /v1/partner/jwks.json. */
  public Map<String, Object> create(String stream, String head, long seq) {
    return client.requestMap("POST", "/v1/anchors", Map.of("stream", stream, "head", head, "seq", seq));
  }

  /**
   * A stream's receipts, newest first — {@code GET /v1/anchors/{stream}}: {@code {stream, anchors, nextBefore}}.
   * Pass {@code nextBefore} as {@code before} until it is null; null arguments are left out.
   */
  public Map<String, Object> list(String stream, Integer limit, Long before) {
    return client.getMap("/v1/anchors/" + Query.encode(stream) + new Query().add("limit", limit).add("before", before));
  }

  /** One receipt — {@code GET /v1/anchors/{stream}/{id}}. */
  public Map<String, Object> get(String stream, String id) {
    return client.getMap("/v1/anchors/" + Query.encode(stream) + "/" + Query.encode(id));
  }
}
