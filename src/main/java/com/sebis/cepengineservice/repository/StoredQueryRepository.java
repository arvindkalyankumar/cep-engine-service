package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.entity.StoredQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredQueryRepository extends JpaRepository<StoredQuery, String> {
}
