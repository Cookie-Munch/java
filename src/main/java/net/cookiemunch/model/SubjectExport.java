package net.cookiemunch.model;

import java.util.List;

/** Response of the subject data-export endpoint (GDPR access/portability). */
public record SubjectExport(String cbid, String stamp, List<Object> records, int count) {}
