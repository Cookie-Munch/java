package net.cookiemunch.model;

/**
 * A registered property. Per the server's OpenAPI schema the wire shape includes the
 * verification fields ({@code verified}, {@code verifyToken}, …), which the TypeScript
 * SDK's aspirational {@code Site} type omits.
 */
public record Site(
    String cbid,
    String orgId,
    String domain,
    Boolean verified,
    String verifyToken,
    String verifyMethod,
    Long verifiedAt) {}
