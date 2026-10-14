package net.cookiemunch.model;

/**
 * Body of {@code POST /v1/dsar}. {@code test} marks a test request: left out of SLA metrics
 * and the deadline board, labelled as a test, and deletable with {@code dsar().delete(id)}.
 * Null fields are not sent.
 */
public record DsarCreate(String type, String subjectEmail, String regulation, String note, Boolean test) {

  public DsarCreate(String type, String subjectEmail, String regulation, String note) {
    this(type, subjectEmail, regulation, note, null);
  }

  public DsarCreate(String type, String subjectEmail, String regulation) {
    this(type, subjectEmail, regulation, null, null);
  }
}
