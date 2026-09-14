package net.cookiemunch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * One day of aggregated consent stats. The wire uses capitalised field names, mapped
 * here via {@link JsonProperty} onto conventional Java component names.
 */
public record ConsentDay(
    @JsonProperty("Date") String date,
    @JsonProperty("OptIn") int optIn,
    @JsonProperty("OptOut") int optOut,
    @JsonProperty("OptInImplied") int optInImplied,
    @JsonProperty("OptInStrict") int optInStrict,
    @JsonProperty("TypeOptInPref") int typeOptInPref,
    @JsonProperty("TypeOptInStat") int typeOptInStat,
    @JsonProperty("TypeOptInMark") int typeOptInMark,
    @JsonProperty("Impressions") int impressions,
    @JsonProperty("Countries") Map<String, Integer> countries) {}
