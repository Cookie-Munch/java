package net.cookiemunch.model;

/**
 * An organisation member. Per the authoritative OpenAPI schema the wire identifier is
 * {@code userId} (not {@code id}); {@code role} is {@code owner | admin | member | viewer}.
 */
public record Member(String userId, String email, String role) {}
