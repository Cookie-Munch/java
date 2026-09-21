package net.cookiemunch;

import net.cookiemunch.model.SubscriptionTopic;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The {@code /v1/subscriptions} endpoints: marketing preferences as topics x channels. */
public final class SubscriptionsResource {

  private final CookieMunch client;

  SubscriptionsResource(CookieMunch client) {
    this.client = client;
  }

  public Map<String, Object> topics() {
    return client.getMap("/v1/subscriptions/topics");
  }

  /** Replace the catalog. It is authored whole; anything omitted is removed. */
  public Map<String, Object> setTopics(List<SubscriptionTopic> topics) {
    return client.requestMap("PUT", "/v1/subscriptions/topics", Map.of("topics", topics));
  }

  public Map<String, Object> get(String subjectId) {
    return client.getMap("/v1/subscriptions/" + Query.pathSegment(subjectId));
  }

  public Map<String, Object> set(String subjectId, String topic, String channel, boolean optedIn) {
    return client.requestMap("PUT", "/v1/subscriptions/" + Query.pathSegment(subjectId),
        Map.of("topic", topic, "channel", channel, "optedIn", optedIn));
  }

  public Map<String, Object> unsubscribeAll(String subjectId) {
    return client.requestMap("POST", "/v1/subscriptions/" + Query.pathSegment(subjectId) + "/unsubscribe-all", null);
  }

  /** Lift a global unsubscribe, restoring the per-topic choices from before it. */
  public Map<String, Object> resubscribe(String subjectId) {
    return client.requestMap("POST", "/v1/subscriptions/" + Query.pathSegment(subjectId) + "/resubscribe", null);
  }

  public Map<String, Object> activation(String subjectId, List<SubscriptionTopic> topics) {
    return client.requestMap("POST", "/v1/subscriptions/" + Query.pathSegment(subjectId) + "/activation", Map.of("topics", topics));
  }
}
