package com.sebis.cepengineservice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sebis.cepengineservice.dto.QueryDto;
import com.sebis.cepengineservice.dto.QueryResultDto;
import com.sebis.cepengineservice.entity.MappedSpan;
import com.sebis.cepengineservice.repository.MappedSpanReadRepository;
import com.sebis.cepengineservice.service.exception.ValidationException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MappedSpanService {
    private MappedSpanReadRepository readRepository;

    @Autowired
    public MappedSpanService(MappedSpanReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public List<Map<String, Object>> query(QueryDto query) {
        List<Map<String, Object>> finalResult = new ArrayList<>();
        for (int i = 0; i < query.getTimeSlots(); i++) {
            long fromTimestamp = (System.currentTimeMillis() - (i + 1) * query.getTimeSlotDuration() * 1000) * 1000;
            long tillTimestamp = (System.currentTimeMillis() - i * query.getTimeSlotDuration() * 1000) * 1000;
            Collection<QueryResultDto> queryResult;
            try {
                queryResult = readRepository.findByFilter(query, fromTimestamp, tillTimestamp);
            } catch (Exception e) {
                throw new ValidationException(e.getMessage(), e);
            }
            List<Map<String, Object>> timeSlotResult = map(queryResult, query);
            final int finalI = i;
            timeSlotResult.forEach(map -> map.put("timeSlot", finalI + 1));
            finalResult.addAll(timeSlotResult);
        }
        return finalResult;
    }
    
    public List<Map<String, Object>> sqlQuery(String query) {
         return readRepository.findBySql(query);
    }

    private List<Map<String, Object>> map(Collection<QueryResultDto> queryResult, QueryDto query) {
        return queryResult.stream().map(singleResult -> {
            Map<String, Object> fieldMap = new HashMap<>();
            Iterator<String> columnIterator = query.getColumns().iterator();
            singleResult.getResults().forEach(result -> fieldMap.put(columnIterator.next(), result));
            return fieldMap;
        }).distinct().collect(Collectors.toList());
    }
}
