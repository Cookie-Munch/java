package net.cookiemunch.model;

/** Reports what {@code POST /v1/sites/{cbid}/elements/ad-personalization} did. */
public record AdPersonalizationResult(
    boolean ok, String form, boolean enabled, boolean injectedElement, String note) {}
