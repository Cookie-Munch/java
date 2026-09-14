package net.cookiemunch;

import net.cookiemunch.model.BrandKit;
import net.cookiemunch.model.DsarRequest;
import net.cookiemunch.model.Member;
import net.cookiemunch.model.RopaEntry;

/*
 * Internal single-key response envelopes. Several create/mutate endpoints wrap their
 * result in a one-field object ({ request }, { entry }, { kit }, { member }); the SDK
 * unwraps these and returns the inner value directly for ergonomics.
 */

record DsarEnvelope(DsarRequest request) {}

record RopaEnvelope(RopaEntry entry) {}

record BrandKitEnvelope(BrandKit kit) {}

record MemberEnvelope(Member member) {}
