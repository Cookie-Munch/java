package net.cookiemunch.model;

/** A flow validation/lint finding (e.g. {@code trap_view}, {@code orphan_view}). */
public record FlowIssue(String code, String message, String path) {}
