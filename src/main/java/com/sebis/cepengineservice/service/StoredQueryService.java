package com.sebis.cepengineservice.service;

import com.sebis.cepengineservice.dto.StoredQueryDto;
import com.sebis.cepengineservice.entity.StoredQuery;
import com.sebis.cepengineservice.repository.StoredQueryRepository;
import com.sebis.cepengineservice.service.exception.ValidationException;
import com.sebis.cepengineservice.service.mapper.StoredQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Service
public class StoredQueryService {
    private StoredQueryRepository storedQueryRepository;

    @Autowired
    public StoredQueryService(StoredQueryRepository storedQueryRepository) {
        this.storedQueryRepository = storedQueryRepository;
    }

    public StoredQueryDto findOne(String id) {
        return StoredQueryMapper.map(storedQueryRepository.findOne(id));
    }

    public Collection<StoredQueryDto> findAll() {
        return StoredQueryMapper.map(storedQueryRepository.findAll());
    }

    @Transactional
    public String save(StoredQueryDto dto) {
        StoredQuery entity = StoredQuery.builder()
                .id(UUID.randomUUID().toString())
                .name(dto.getName())
                .query(dto.getQuery())
                .build();
        storedQueryRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void delete(String id) {
        if (!storedQueryRepository.exists(id)) {
            throw new ValidationException("StoredQuery with the given id was not found");
        }
        storedQueryRepository.delete(id);
    }
}
