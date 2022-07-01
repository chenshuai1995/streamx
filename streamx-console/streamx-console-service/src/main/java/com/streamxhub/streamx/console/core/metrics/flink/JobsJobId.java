package com.streamxhub.streamx.console.core.metrics.flink;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author Jim Chen
 * @date 2022-07-01
 */
@Data
public class JobsJobId implements Serializable {

    @JsonProperty("jid")
    private String jid;

    private String name;

    private String isStoppable;

    private String state;

    @JsonProperty("start-time")
    private Long startTime;

    @JsonProperty("end-time")
    private Long endTime;

    private Long duration;

    private Long maxParallelism;

    private Long now;

    private Timestamps timestamps;

    private List<Vertice> vertices;

    @JsonProperty("status-counts")
    private StatusConts statusConts;

    private Plan Plan;

    @Data
    private static class Timestamps implements Serializable {
        private Long INITIALIZING;
        private Long CANCELLING;
        private Long RECONCILING;
        private Long CREATED;
        private Long RUNNING;
        private Long FINISHED;
        private Long FAILING;
        private Long FAILED;
        private Long CANCELED;
        private Long RESTARTING;
        private Long SUSPENDED;
    }

    @Data
    public static class Vertice implements Serializable {
        private String id;
        private String name;
        private Long maxParallelism;
        private Long parallelism;
        private String status;

        @JsonProperty("start-time")
        private Long startTime;

        @JsonProperty("end-time")
        private Long endTime;

        private Long duration;

        private Task tasks;

        private Metric metrics;

    }

    @Data
    private static class Task implements Serializable {
        private Long CANCELED;
        private Long SCHEDULED;
        private Long DEPLOYING;
        private Long CANCELING;
        private Long CREATED;
        private Long FINISHED;
        private Long FAILED;
        private Long RUNNING;
        private Long RECONCILING;
        private Long INITIALIZING;
    }

    @Data
    private static class Metric implements Serializable {
        @JsonProperty("read-bytes")
        private Long readBytes;

        @JsonProperty("read-bytes-complete")
        private Boolean readBytesComplete;

        @JsonProperty("write-bytes")
        private Long writeBytes;

        @JsonProperty("write-bytes-complete")
        private Boolean writeBytesComplete;

        @JsonProperty("read-records")
        private Long readRecords;

        @JsonProperty("read-records-complete")
        private Boolean readRecordsComplete;

        @JsonProperty("write-records")
        private Long writeRecords;

        @JsonProperty("write-records-complete")
        private Boolean writeRecordsComplete;

    }


    @Data
    private static class StatusConts implements Serializable{
        private Long CANCELED;
        private Long SCHEDULED;
        private Long DEPLOYING;
        private Long CANCELING;
        private Long CREATED;
        private Long FINISHED;
        private Long FAILED;
        private Long RUNNING;
        private Long RECONCILING;
        private Long INITIALIZING;
    }

    @Data
    private static class Plan implements Serializable {
        private String jid;
        private String name;
        private List<Node> nodes;
    }

    @Data
    private static class Node implements Serializable {

        private String id;
        private Long parallelism;
        private String operator;
        @JsonProperty("operator_strategy")
        private String operatorStrategy;
        private String description;
        @JsonIgnore
        private String inputs;

        @JsonIgnore
        @JsonProperty("optimizer_properties")
        private String optimizerProperties;

    }


}
