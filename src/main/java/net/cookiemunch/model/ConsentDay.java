package net.cookiemunch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * One day of aggregated consent stats. The wire uses capitalised field names (camelCase from API version
 * 2026-11-11), mapped here via {@link JsonProperty} and {@link JsonAlias} onto conventional Java component names.
 */
public record ConsentDay(
    @JsonProperty("Date") @JsonAlias("date") String date,
    @JsonProperty("OptIn") @JsonAlias("optIn") int optIn,
    @JsonProperty("OptOut") @JsonAlias("optOut") int optOut,
    @JsonProperty("OptInImplied") @JsonAlias("optInImplied") int optInImplied,
    @JsonProperty("OptInStrict") @JsonAlias("optInStrict") int optInStrict,
    @JsonProperty("TypeOptInPref") @JsonAlias("typeOptInPref") int typeOptInPref,
    @JsonProperty("TypeOptInStat") @JsonAlias("typeOptInStat") int typeOptInStat,
    @JsonProperty("TypeOptInMark") @JsonAlias("typeOptInMark") int typeOptInMark,
    @JsonProperty("Impressions") @JsonAlias("impressions") int impressions,
    @JsonProperty("Countries") @JsonAlias("countries") Map<String, Integer> countries) {}
