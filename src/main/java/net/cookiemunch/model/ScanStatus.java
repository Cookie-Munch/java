package net.cookiemunch.model;

/**
 * Response of the cookie-scan endpoints ({@code GET/POST /v1/sites/{cbid}/scan}).
 *
 * @param status {@code idle | scanning} — kept for older clients; read {@code state}
 * @param lastScannedAt epoch-ms of the last completed scan, or {@code null}
 * @param state {@code queued | scanning | done | failed}; {@code null} when never scanned
 * @param scanId the last scan's id
 * @param startedAt epoch-ms it was requested
 * @param finishedAt epoch-ms it finished (done or failed)
 * @param durationMs {@code finishedAt - startedAt}
 * @param cookieCount cookies this crawl saw (the stored list also keeps {@code keptFromEarlier})
 * @param keptFromEarlier cookies kept from earlier crawls and visits this crawl did not see
 * @param pagesVisited pages the crawl visited
 * @param error why it failed
 */
public record ScanStatus(
    String status,
    Long lastScannedAt,
    String state,
    String scanId,
    Long startedAt,
    Long finishedAt,
    Long durationMs,
    Integer cookieCount,
    Integer keptFromEarlier,
    Integer pagesVisited,
    String error) {

  /** The two fields every server has always sent. */
  public ScanStatus(String status, Long lastScannedAt) {
    this(status, lastScannedAt, null, null, null, null, null, null, null, null, null);
  }
}
