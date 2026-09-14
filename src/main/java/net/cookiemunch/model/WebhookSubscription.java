package net.cookiemunch.model;

import java.util.List;

/**
 * A webhook subscription. {@code secret} is present only on the create response.
 * {@code cbid} is {@code null} when the subscription covers all properties in the org.
 */
public record WebhookSubscription(
    String id,
    String orgId,
    String url,
    String secret,
    List<String> events,
    String cbid,
    boolean active,
    long createdAt) {}
