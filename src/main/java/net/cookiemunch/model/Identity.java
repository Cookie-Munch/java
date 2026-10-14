package net.cookiemunch.model;

import java.util.List;

/**
 * Response of {@code GET /v1/me}. {@code plan} is the EFFECTIVE plan; {@code planSource} is
 * {@code org} (its own subscription, free, or a reseller child plan) or {@code account} (a
 * higher plan another org with the same owner pays for covers it); {@code subscribedPlan} is
 * what the org pays for itself; {@code features} is what the effective plan unlocks.
 */
public record Identity(
    String orgId,
    String orgName,
    String plan,
    String planSource,
    String subscribedPlan,
    List<String> features,
    String keyPrefix) {}
