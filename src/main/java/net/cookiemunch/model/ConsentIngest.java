package net.cookiemunch.model;

import java.util.Map;

/**
 * Payload of the public {@code POST /api/v1/consent} endpoint (the consent-log write the
 * embed makes). No auth header is required — the {@code cbid} must be a registered site.
 * Optional fields serialise only when set. Use {@link #builder(String)} for ergonomics.
 */
public record ConsentIngest(
    String cbid,
    String stamp,
    ConsentChoices choices,
    String method,
    Integer ver,
    Long utc,
    String url,
    String tcString,
    String gppString,
    Map<String, Boolean> purposes,
    String subjectPolicyHash,
    Map<String, String> purposeRopa,
    String variant,
    String subjectId) {

  public static Builder builder(String cbid) {
    return new Builder(cbid);
  }

  /** Fluent builder for {@link ConsentIngest}. */
  public static final class Builder {
    private final String cbid;
    private String stamp;
    private ConsentChoices choices;
    private String method;
    private Integer ver;
    private Long utc;
    private String url;
    private String tcString;
    private String gppString;
    private Map<String, Boolean> purposes;
    private String subjectPolicyHash;
    private Map<String, String> purposeRopa;
    private String variant;
    private String subjectId;

    Builder(String cbid) {
      this.cbid = cbid;
    }

    public Builder stamp(String stamp) {
      this.stamp = stamp;
      return this;
    }

    public Builder choices(ConsentChoices choices) {
      this.choices = choices;
      return this;
    }

    public Builder choices(boolean preferences, boolean statistics, boolean marketing) {
      this.choices = new ConsentChoices(preferences, statistics, marketing);
      return this;
    }

    public Builder method(String method) {
      this.method = method;
      return this;
    }

    public Builder ver(int ver) {
      this.ver = ver;
      return this;
    }

    public Builder utc(long utc) {
      this.utc = utc;
      return this;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder tcString(String tcString) {
      this.tcString = tcString;
      return this;
    }

    public Builder gppString(String gppString) {
      this.gppString = gppString;
      return this;
    }

    public Builder purposes(Map<String, Boolean> purposes) {
      this.purposes = purposes;
      return this;
    }

    public Builder subjectPolicyHash(String subjectPolicyHash) {
      this.subjectPolicyHash = subjectPolicyHash;
      return this;
    }

    public Builder purposeRopa(Map<String, String> purposeRopa) {
      this.purposeRopa = purposeRopa;
      return this;
    }

    public Builder variant(String variant) {
      this.variant = variant;
      return this;
    }

    /**
     * Optional, app-supplied STABLE cross-surface subject id (e.g. a logged-in account id).
     * Lets an org correlate one subject's consent across all its sites/surfaces. Opaque —
     * stored and hashed server-side, never interpreted. Serialises only when set.
     */
    public Builder subjectId(String subjectId) {
      this.subjectId = subjectId;
      return this;
    }

    public ConsentIngest build() {
      return new ConsentIngest(
          cbid, stamp, choices, method, ver, utc, url, tcString, gppString, purposes,
          subjectPolicyHash, purposeRopa, variant, subjectId);
    }
  }
}
