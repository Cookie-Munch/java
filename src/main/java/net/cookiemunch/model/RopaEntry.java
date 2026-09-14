package net.cookiemunch.model;

import java.util.List;

/** A stored RoPA (Record of Processing Activities) entry. */
public record RopaEntry(
    String id,
    String name,
    String purpose,
    String legalBasis,
    List<String> dataCategories,
    List<String> recipients,
    int retentionDays,
    boolean crossBorderTransfer) {}
