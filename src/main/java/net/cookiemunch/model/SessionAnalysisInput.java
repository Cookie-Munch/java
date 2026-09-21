package net.cookiemunch.model;

/** A captured browsing session. Supply {@code har} or {@code requests}; the rest may be {@code null}. */
public record SessionAnalysisInput(Object har, java.util.List<Object> requests, java.util.Map<String, Boolean> consent, Boolean gpc) {}
