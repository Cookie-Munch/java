package net.cookiemunch.model;

import java.util.List;

/** Response of {@code POST /v1/dsar/{id}/export}: a subject's consent records on one site. */
public record DsarExportResult(List<Object> records, int count, DsarRequest request) {}
