package net.cookiemunch.model;

/** Body of {@code POST /v1/dsar}. */
public record DsarCreate(String type, String subjectEmail, String regulation, String note) {

  public DsarCreate(String type, String subjectEmail, String regulation) {
    this(type, subjectEmail, regulation, null);
  }
}
