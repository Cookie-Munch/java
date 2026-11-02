package net.cookiemunch.model;

import java.util.List;

/**
 * Body of {@code POST /v1/webhooks}. {@code events} are event names ({@code consent.recorded},
 * {@code scan.completed}, …), {@code "*"} for every event, or {@code "<family>.*"} such as
 * {@code dsar.*}; a wildcard also takes events added later. {@code cbid} is an optional property
 * filter ({@code null}/omitted = all properties). {@code secret} is your own signing secret
 * (32–256 printable characters), not echoed back; {@code null} has one generated and returned once.
 */
public record WebhookCreate(String url, List<String> events, String cbid, String secret) {

  public WebhookCreate(String url, List<String> events) {
    this(url, events, null, null);
  }

  public WebhookCreate(String url, List<String> events, String cbid) {
    this(url, events, cbid, null);
  }
}
