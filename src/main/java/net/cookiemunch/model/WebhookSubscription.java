package net.cookiemunch.model;

import java.util.List;

/**
 * A webhook subscription. {@code secret} is present only on the create response, and only when
 * Cookie Munch generated it. {@code events} are the patterns as given; {@code matches} the events
 * they take today. {@code cbid} is {@code null} when the subscription covers all properties in the org.
 */
public record WebhookSubscription(
    String id,
    String orgId,
    String url,
    String secret,
    List<String> events,
    String cbid,
    boolean active,
    long createdAt,
    List<String> matches) {}
