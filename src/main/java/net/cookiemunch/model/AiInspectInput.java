package net.cookiemunch.model;

/** A prompt or response to check against consent and policy. Optional fields may be {@code null}. */
public record AiInspectInput(String prompt, String purpose, String model, String actor, String direction, java.util.Map<String, Boolean> consent) {}
