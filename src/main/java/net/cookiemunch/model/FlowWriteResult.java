package net.cookiemunch.model;

import java.util.List;
import java.util.Map;

/**
 * Result of a flow edit/replace. On lint/validation failure {@code ok} is {@code false}
 * and {@code issues} explains why — the HTTP status is still 200, so always check
 * {@code ok()} before assuming success.
 */
public record FlowWriteResult(
    boolean ok, Map<String, Object> flow, Integer failedAt, Object op, List<FlowIssue> issues) {}
