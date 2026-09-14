package net.cookiemunch.model;

import java.util.Map;

/** Response of {@code POST /v1/vendors}: the stored vendor plus its computed risk. */
public record VendorCreateResult(Map<String, Object> vendor, RiskScore risk) {}
