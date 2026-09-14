package net.cookiemunch.model;

/**
 * Query options for the install-snippet endpoint. {@code blockingMode} is one of
 * {@code auto | manual | checklist}; {@code culture} is a language override (e.g.
 * {@code "en"}). Either may be {@code null}.
 */
public record SnippetOptions(String blockingMode, String culture) {}
