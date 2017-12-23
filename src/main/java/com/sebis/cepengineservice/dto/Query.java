package com.sebis.cepengineservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class Query {
    @NotNull
    private List<String> columns;
    private List<Rule> rules;
    private List<Aggregation> aggregations;
    @NotNull
    private Integer timeSlots;
    @NotNull
    private Integer timeSlotDuration;

    @Data
    @NoArgsConstructor
    public static class Rule {
        private String field;
        private String operator;
        private String value;
    }

    @Data
    @NoArgsConstructor
    public static class Aggregation {
        private String field;
        private AggregationType operation;

        public enum AggregationType {
            AVG, MAX, MIN, COUNT
        }
    }
}
