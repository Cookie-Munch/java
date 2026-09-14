package net.cookiemunch.model;

import java.util.List;

/** Reports which sites a publish compiled the design into. */
public record BannerPublishResult(List<String> publishedCbids) {}
