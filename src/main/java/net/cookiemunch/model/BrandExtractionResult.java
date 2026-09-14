package net.cookiemunch.model;

/** Response of {@code POST /v1/sites/{cbid}/brand} (the suggestion wrapped in an object). */
public record BrandExtractionResult(BrandSuggestion suggestion) {}
