package com.sebis.cepengineservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "spans_mapped")
@Data
@NoArgsConstructor
public class MappedSpan {
    @Id
    private Long id;
    private Long traceIdHigh;
    private Long traceId;
    private String name;
    private Long spanId;
    private Long parentId;
    private Long timestamp;
    private Long duration;
    private String annotationKey;
    private String annotationValue;
    private String endpoint;
    private Integer port;
    private String service;
    private String activity;
}

