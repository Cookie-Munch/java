package net.cookiemunch.model;

/** A computed vendor-risk rating. {@code band} is {@code low | medium | high}. */
public record RiskScore(int score, String band) {}
