package net.cookiemunch.model;

/** A webhook delivery that failed every retry. Replay it by {@code id}. */
public record WebhookDeadLetter(
    String id,
    String orgId,
    String subscriptionId,
    String url,
    String eventType,
    String cbid,
    Object payload,
    int attempts,
    Integer lastStatus,
    String lastError,
    long failedAt) {}
