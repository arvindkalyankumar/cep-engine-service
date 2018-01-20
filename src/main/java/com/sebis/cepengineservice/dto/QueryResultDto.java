package com.sebis.cepengineservice.dto;

import lombok.Getter;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Getter
public class QueryResultDto {
    private List<Object> results;

    public QueryResultDto() {
    }

    public QueryResultDto(Object o1) {
        this.results = singletonList(o1);
    }

    public QueryResultDto(Object o1, Object o2) {
        this.results = asList(o1, o2);
    }

    public QueryResultDto(Object o1, Object o2, Object o3) {
        this.results = asList(o1, o2, o3);
    }

    public QueryResultDto(Object o1, Object o2, Object o3, Object o4) {
        this.results = asList(o1, o2, o3, o4);
    }

    public QueryResultDto(Object o1, Object o2, Object o3, Object o4, Object o5) {
        this.results = asList(o1, o2, o3, o4, o5);
    }

    public QueryResultDto(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        this.results = asList(o1, o2, o3, o4, o5, o6);
    }

    public QueryResultDto(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        this.results = asList(o1, o2, o3, o4, o5, o6, o7);
    }

    public QueryResultDto(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8) {
        this.results = asList(o1, o2, o3, o4, o5, o6, o7, o8);
    }
}
