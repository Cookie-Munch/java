package net.cookiemunch.model;

/** An AI system to declare. {@code provider} and {@code purpose} may be {@code null}. */
public record AiSystem(String id, String name, String provider, String purpose) {}
