package com.sebis.cepengineservice.service;

import com.sebis.cepengineservice.dto.QueryDTO;
import com.sebis.cepengineservice.entity.MappedSpan;
import com.sebis.cepengineservice.repository.MappedSpanReadRepository;
import com.sebis.cepengineservice.service.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MappedSpanService {
    private MappedSpanReadRepository readRepository;

    @Autowired
    public MappedSpanService(MappedSpanReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public List<Map<String, Object>> query(QueryDTO queryDTO) {
        validateColumns(queryDTO);
        Collection<MappedSpan> mappedSpans = readRepository.findByFilter(queryDTO);
        return mappedSpans.stream()
                .map(mappedSpan -> spanToFieldMap(queryDTO, mappedSpan))
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateColumns(QueryDTO queryDTO) {
        List<String> fieldNames = Arrays.stream(MappedSpan.class.getFields())
                .map(Field::getName).collect(Collectors.toList());
        if (queryDTO.getColumns().stream().anyMatch(fieldNames::contains)) {
            throw new ValidationException("The column list is invalid");
        }
    }

    private Map<String, Object> spanToFieldMap(QueryDTO queryDTO, MappedSpan mappedSpan) {
        Map<String, Object> fieldMap = new HashMap<>();
        queryDTO.getColumns().forEach(column -> {
            try {
                Field field = mappedSpan.getClass().getDeclaredField(column);
                field.setAccessible(true);
                fieldMap.put(column, field.get(mappedSpan));
            } catch (Exception e) {
                log.error("Failed to generate the result map", e);
            }
        });
        if (!fieldMap.containsKey("spanId")) {
            fieldMap.put("spanId", mappedSpan.getSpanId());
        }
        return fieldMap;
    }
}
