package net.cookiemunch.model;

/** One entry in the org's topic catalog. {@code name} and {@code downstream} may be {@code null}. */
public record SubscriptionTopic(String code, String name, java.util.List<String> channels, java.util.Map<String, String> downstream) {}
