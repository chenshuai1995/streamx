package com.streamxhub.streamx.console.core.metrics.flink;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author Jim Chen
 */
@Data
public class Backpressure implements Serializable {

    private String status;

    @JsonProperty("backpressure-level")
    private String backpressureLevel;

    @JsonProperty("end-timestamp")
    private Long endTimestamp;

    private List<Subtask> subtasks;

    @Data
    public static class Subtask implements Serializable {

        private Integer subtask;

        @JsonProperty("backpressure-level")
        private String backpressureLevel;

        private Integer ratio;

        private Integer idleRatio;

        private String busyRatio;

    }
}
