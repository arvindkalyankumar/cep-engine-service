package com.sebis.cepengineservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredQueryDto {
    private String id;
    @NotNull
    private String name;
    @NotNull
    private String query;
}
