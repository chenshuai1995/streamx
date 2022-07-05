package com.streamxhub.streamx.console.core.metrics.flink;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * @author Jim Chen
 */
@Data
public class Exceptions implements Serializable {

    @JsonProperty("root-exception")
    private String rootException;

    private Long timestamp;

    @JsonIgnore
    @JsonProperty("all-exceptions")
    private String allExceptions;

    private Boolean truncated;

    @JsonIgnore
    private String exceptionHistory;
}
