package net.cookiemunch.model;

import java.util.List;

/** A vendor with its computed risk flattened in (list response). */
public record ScoredVendor(
    String id,
    String name,
    String category,
    List<String> dataShared,
    boolean dpaSigned,
    int subprocessors,
    List<String> certifications,
    String region,
    RiskScore risk) {}
