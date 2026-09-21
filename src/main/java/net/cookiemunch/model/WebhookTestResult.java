package net.cookiemunch.model;

/** Response of {@code POST /v1/webhooks/{id}/test}: what the endpoint answered a signed test delivery. */
public record WebhookTestResult(boolean ok, Integer status, String error) {}
