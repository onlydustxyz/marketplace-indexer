package com.onlydust.marketplace.indexer.domain.model.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawCheckRuns extends JsonDocument {

    @JsonProperty("check_runs")
    List<CheckRun> checkRuns;

    @Value
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor(force = true)
    public static class CheckRun extends JsonDocument {
        Long id;
    }
}
