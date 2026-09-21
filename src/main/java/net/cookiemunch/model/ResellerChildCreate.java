package net.cookiemunch.model;

/** Provisions a child org. With {@code mintKey}, its first API key is returned once as {@code apiKey}. Optional fields may be {@code null}. */
public record ResellerChildCreate(String name, String ownerEmail, java.util.Map<String, Object> controller, java.util.Map<String, Object> whiteLabel, Boolean delegatedAccess, Boolean mintKey, java.util.List<String> keyScopes) {}
