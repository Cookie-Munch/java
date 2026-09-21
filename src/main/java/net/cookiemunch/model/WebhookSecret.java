package net.cookiemunch.model;

/** Response of {@code POST /v1/webhooks/{id}/roll}: the new signing secret, shown ONCE. */
public record WebhookSecret(String secret) {}
