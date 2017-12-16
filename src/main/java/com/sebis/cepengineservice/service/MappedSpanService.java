package com.sebis.cepengineservice.service;

import com.sebis.cepengineservice.dto.QueryDTO;
import com.sebis.cepengineservice.dto.QueryResult;
import com.sebis.cepengineservice.entity.MappedSpan;
import com.sebis.cepengineservice.repository.MappedSpanReadRepository;
import com.sebis.cepengineservice.service.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.sebis.cepengineservice.dto.AggregationType.DURATION_AVERAGE;

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
        validateAggregation(queryDTO);

        Collection<QueryResult> queryResult = readRepository.findByFilter(queryDTO);
        return queryResult.stream().map(singleResult -> {
            Map<String, Object> fieldMap = new HashMap<>();
            Iterator<String> columnIterator = queryDTO.getColumns().iterator();
            singleResult.getResults().forEach(result -> {
                fieldMap.put(columnIterator.next(), result);
            });
            return fieldMap;
        }).distinct().collect(Collectors.toList());
    }

    private void validateColumns(QueryDTO queryDTO) {
        List<String> fieldNames = Arrays.stream(MappedSpan.class.getFields())
                .map(Field::getName).collect(Collectors.toList());
        if (queryDTO.getColumns().stream().anyMatch(fieldNames::contains)) {
            throw new ValidationException("The column list is invalid");
        }
    }

    private void validateAggregation(QueryDTO queryDTO) {
        if (queryDTO.getAggregationType() != null &&
                queryDTO.getAggregationType().equals(DURATION_AVERAGE) &&
                queryDTO.getColumns().stream().noneMatch(column -> column.equalsIgnoreCase("duration"))) {
            throw new ValidationException("Duration column must be selected if duration_average aggregate type is chosen");
        }
    }
}
