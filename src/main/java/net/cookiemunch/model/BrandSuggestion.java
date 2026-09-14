package net.cookiemunch.model;

import java.util.List;

/** Theme tokens extracted from a site homepage ("Match my site"). */
public record BrandSuggestion(
    String background,
    String text,
    String highlight,
    String fontFamily,
    String fontUrl,
    List<String> palette) {}
