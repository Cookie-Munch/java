package net.cookiemunch.model;

/**
 * The org's resource-usage summary. Per the authoritative OpenAPI schema the wire fields
 * are {@code { domains, seats, monthlyEvents }} (not the plan/period shape in the
 * TypeScript SDK). Fields are boxed since the server may omit any of them.
 */
public record Usage(Integer domains, Integer seats, Integer monthlyEvents) {}
