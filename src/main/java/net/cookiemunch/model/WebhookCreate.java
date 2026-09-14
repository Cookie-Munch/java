package net.cookiemunch.model;

import java.util.List;

/**
 * Body of {@code POST /v1/webhooks}. {@code events} are drawn from
 * {@code consent.recorded, scan.completed, scan.cookies_changed, dsar.created,
 * dsar.updated, banner.published}. {@code cbid} is an optional property filter
 * ({@code null}/omitted = all properties).
 */
public record WebhookCreate(String url, List<String> events, String cbid) {

  public WebhookCreate(String url, List<String> events) {
    this(url, events, null);
  }
}
