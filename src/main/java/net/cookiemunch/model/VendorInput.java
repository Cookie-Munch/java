package net.cookiemunch.model;

import java.util.List;

/** Body of {@code POST /v1/vendors}. */
public record VendorInput(
    String name,
    String category,
    List<String> dataShared,
    boolean dpaSigned,
    int subprocessors,
    List<String> certifications,
    String region) {}
