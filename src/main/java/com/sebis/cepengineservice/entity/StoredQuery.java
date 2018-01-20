package com.sebis.cepengineservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "stored_query")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredQuery {
    @Id
    private String id;
    private String name;
    private String query;
}
