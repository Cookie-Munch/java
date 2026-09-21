package net.cookiemunch.model;

/** One purpose decision recorded against a person. Optional fields may be {@code null}. */
public record ConsentDecision(String purpose, boolean allowed, String legalBasis, String jurisdiction, String provenance, Long collectedAt) {}
