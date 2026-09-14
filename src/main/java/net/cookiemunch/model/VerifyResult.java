package net.cookiemunch.model;

/** Response of {@code POST /v1/sites/{cbid}/verify}. {@code method} is {@code dns | meta | file}. */
public record VerifyResult(boolean verified, String method, String reason) {}
