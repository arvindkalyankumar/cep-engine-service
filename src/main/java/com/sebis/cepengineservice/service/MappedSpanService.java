package com.sebis.cepengineservice.service;

import com.sebis.cepengineservice.dto.Query;
import com.sebis.cepengineservice.dto.QueryResult;
import com.sebis.cepengineservice.repository.MappedSpanReadRepository;
import com.sebis.cepengineservice.service.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MappedSpanService {
    private MappedSpanReadRepository readRepository;

    @Autowired
    public MappedSpanService(MappedSpanReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public List<Map<String, Object>> query(Query query) {
        try {
            Collection<QueryResult> queryResult = readRepository.findByFilter(query);
            return map(queryResult, query);
        } catch (Exception e) {
            throw new ValidationException(e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> map(Collection<QueryResult> queryResult, Query query) {
        return queryResult.stream().map(singleResult -> {
            Map<String, Object> fieldMap = new HashMap<>();
            Iterator<String> columnIterator = query.getColumns().iterator();
            singleResult.getResults().forEach(result -> {
                fieldMap.put(columnIterator.next(), result);
            });
            return fieldMap;
        }).distinct().collect(Collectors.toList());
    }

}
