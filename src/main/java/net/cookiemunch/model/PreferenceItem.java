package net.cookiemunch.model;

/** A configurable consent preference/purpose record. */
public record PreferenceItem(
    String id, String orgId, String cbid, String label, String description, String category) {}
