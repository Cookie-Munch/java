package net.cookiemunch;

import net.cookiemunch.model.ConsentDay;
import net.cookiemunch.model.ConsentLogRow;
import net.cookiemunch.model.EraseResult;
import net.cookiemunch.model.SubjectExport;

import java.util.List;
import java.util.Map;

/**
 * The consent endpoints: the authenticated per-site stats/log/export/receipt reads plus
 * the subject erase/export operations. The high-volume public ingest write lives on the
 * client itself ({@link CookieMunch#logConsent}).
 */
public final class ConsentResource {

  private final CookieMunch client;

  ConsentResource(CookieMunch client) {
    this.client = client;
  }

  private static String base(String cbid) {
    return "/v1/sites/" + Query.pathSegment(cbid);
  }

  /**
   * Verify the consent log's tamper-evident hash chain —
   * {@code GET /v1/sites/{cbid}/consent/verify}. Each record carries the hash of the one
   * before it, so an edited, reordered or removed record answers false.
   */
  public boolean verify(String cbid) {
    Object valid = client.requestMap("GET", base(cbid) + "/consent/verify", null).get("valid");
    return Boolean.TRUE.equals(valid);
  }

  /** Aggregated per-day consent stats — {@code GET /v1/sites/{cbid}/consent/stats}. */
  public List<ConsentDay> stats(String cbid) {
    return stats(cbid, null, null);
  }

  /** Aggregated per-day consent stats over an epoch-ms window (either bound may be null). */
  public List<ConsentDay> stats(String cbid, Long from, Long to) {
    String query = new Query().add("from", from).add("to", to).toString();
    return client.getList(base(cbid) + "/consent/stats" + query, ConsentDay.class);
  }

  /** Recent anonymised consent records — {@code GET /v1/sites/{cbid}/consent/log}. */
  public List<ConsentLogRow> log(String cbid) {
    return log(cbid, null, null, null);
  }

  /** Recent anonymised consent records with an epoch-ms window and row limit (any may be null). */
  public List<ConsentLogRow> log(String cbid, Long from, Long to, Integer limit) {
    String query = new Query().add("from", from).add("to", to).add("limit", limit).toString();
    return client.getList(base(cbid) + "/consent/log" + query, ConsentLogRow.class);
  }

  /** CSV audit export returned as a raw string — {@code GET /v1/sites/{cbid}/consent/export}. */
  public String export(String cbid) {
    return export(cbid, null, null);
  }

  /** CSV audit export over an epoch-ms window (either bound may be null). */
  public String export(String cbid, Long from, Long to) {
    String query = new Query().add("from", from).add("to", to).toString();
    return client.requestRaw("GET", base(cbid) + "/consent/export" + query, null);
  }

  /**
   * Signed ISO-27560 consent receipt (JSON) — {@code GET /v1/sites/{cbid}/receipt/{stamp}}.
   * The receipt shape is open; it is returned as a generic map.
   */
  public Map<String, Object> receipt(String cbid, String stamp) {
    return client.getMap(base(cbid) + "/receipt/" + Query.pathSegment(stamp));
  }

  /**
   * Crypto-erase a subject's consent records by their consent-receipt stamp. Irreversible —
   * {@code POST /v1/sites/{cbid}/erase-consent}.
   */
  public EraseResult eraseSubject(String cbid, String stamp) {
    return client.request(
        "POST", base(cbid) + "/erase-consent", Map.of("stamp", stamp), EraseResult.class);
  }

  /**
   * Export a data subject's consent records by their receipt stamp (GDPR access/portability)
   * — {@code GET /v1/sites/{cbid}/subject-export?stamp=...}.
   */
  public SubjectExport exportSubject(String cbid, String stamp) {
    String query = new Query().add("stamp", stamp).toString();
    return client.request("GET", base(cbid) + "/subject-export" + query, null, SubjectExport.class);
  }
}
