package net.cookiemunch.model;

/**
 * A data-subject access request. {@code type} is
 * {@code access | deletion | rectification | portability | opt-out}; {@code regulation}
 * is {@code gdpr | ccpa}; {@code status} is
 * {@code received | verifying | in_progress | completed | rejected}.
 */
public record DsarRequest(
    String id,
    String type,
    String subjectEmail,
    String regulation,
    String status,
    long createdAt,
    long dueAt,
    String note) {}
