package net.cookiemunch.model;

import java.util.List;
import java.util.Map;

/**
 * The v2 banner flow returned by {@code GET /v1/sites/{cbid}/flow}. {@code flow} and
 * {@code categories} are open, deeply-nested objects owned by {@code @cookiemunch/core},
 * left as generic maps here.
 */
public record SiteFlow(
    int v,
    Map<String, Object> flow,
    Map<String, Object> categories,
    String customCss,
    List<FlowIssue> lint) {}
