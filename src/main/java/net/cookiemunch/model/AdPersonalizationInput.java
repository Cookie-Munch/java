package net.cookiemunch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Body of {@code POST /v1/sites/{cbid}/elements/ad-personalization}. All fields optional;
 * {@code enabled} defaults to true server-side. {@code defaultValue} is serialised as the
 * wire field {@code "default"} (a Java reserved word cannot be a record component name) —
 * it is the personalization state granted by "Allow all" and the toggle default.
 */
public record AdPersonalizationInput(
    Boolean enabled, @JsonProperty("default") Boolean defaultValue, String label) {}
