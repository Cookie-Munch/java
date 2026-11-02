package net.cookiemunch.model;

/**
 * Response of {@code POST /v1/webhooks/{id}/roll}: the new signing secret, shown ONCE, or
 * {@code rolled = true} (and no secret) when you chose it.
 */
public record WebhookSecret(String secret, Boolean rolled) {

  public WebhookSecret(String secret) {
    this(secret, null);
  }
}
