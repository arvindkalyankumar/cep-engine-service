package com.sebis.cepengineservice.service;

import com.sebis.cepengineservice.dto.QueryDTO;
import com.sebis.cepengineservice.entity.MappedSpan;
import com.sebis.cepengineservice.repository.MappedSpanReadRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class MappedSpanService {
    private MappedSpanReadRepository readRepository;

    public MappedSpanService(MappedSpanReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public List<Map<String, Object>> query(QueryDTO query) {
        Collection<MappedSpan> mappedSpans = readRepository.findByFilter(query);
        return null;
    }


}
