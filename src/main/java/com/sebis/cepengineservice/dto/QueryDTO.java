package com.sebis.cepengineservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QueryDTO {
    private List<String> columns;
    private List<Rule> rules;
    private AggregationType aggregationType;

    @Data
    @NoArgsConstructor
    public static class Rule {
        private String key;
        private String operation;
        private String value;
    }
}
