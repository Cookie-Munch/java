package net.cookiemunch.model;

/**
 * Body of {@code POST /v1/dsar}. {@code test} marks a test request: left out of SLA metrics
 * and the deadline board, labelled as a test, and deletable with {@code dsar().delete(id)}.
 * Null fields are not sent.
 */
public record DsarCreate(String type, String subjectEmail, String regulation, String note, Boolean test, String messageToSubject) {

  /** {@code note} is internal; {@code messageToSubject} is the line the subject reads in the notice. */
  public DsarCreate(String type, String subjectEmail, String regulation, String note, Boolean test) {
    this(type, subjectEmail, regulation, note, test, null);
  }

  public DsarCreate(String type, String subjectEmail, String regulation, String note) {
    this(type, subjectEmail, regulation, note, null, null);
  }

  public DsarCreate(String type, String subjectEmail, String regulation) {
    this(type, subjectEmail, regulation, null, null, null);
  }
}
