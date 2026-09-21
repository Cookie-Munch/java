package net.cookiemunch.model;

/**
 * One entry of {@code GET /v1/languages}: a language the banner already has copy for.
 *
 * <p>{@code source} is {@code "bundled"} (ships in consent.js, available the instant the
 * embed runs) or {@code "extended"} (ships in the renderer, fetched the first time a
 * banner is drawn). {@code rtl} languages flip the banner's layout.
 */
public record SupportedLanguage(String code, String name, String endonym, boolean rtl, String source) {}
