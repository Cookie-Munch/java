package net.cookiemunch.model;

import java.util.List;

/**
 * Body of {@code POST /v1/ropa}. {@code legalBasis} is one of
 * {@code consent | contract | legal-obligation | vital-interests | public-task | legitimate-interests}.
 */
public record RopaInput(
    String name,
    String purpose,
    String legalBasis,
    List<String> dataCategories,
    List<String> recipients,
    int retentionDays,
    boolean crossBorderTransfer) {}
