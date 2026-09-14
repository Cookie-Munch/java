package net.cookiemunch.model;

import java.util.List;

/** Response of {@code GET /v1/sites/{cbid}/cookies}: the latest categorized scan snapshot. */
public record CookieDeclaration(long updatedAt, List<CategorizedCookie> cookies) {}
