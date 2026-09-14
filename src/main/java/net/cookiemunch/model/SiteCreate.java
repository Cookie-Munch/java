package net.cookiemunch.model;

/** Body of {@code POST /v1/sites}. {@code cbid} is optional (auto-generated when omitted). */
public record SiteCreate(String domain, String cbid) {

  /** Create a site request with the server auto-generating the cbid. */
  public SiteCreate(String domain) {
    this(domain, null);
  }
}
