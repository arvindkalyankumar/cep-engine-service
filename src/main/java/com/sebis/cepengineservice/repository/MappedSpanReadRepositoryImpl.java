package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.dto.QueryDTO;
import com.sebis.cepengineservice.entity.MappedSpan;
import com.sebis.cepengineservice.service.exception.ValidationException;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MappedSpanReadRepositoryImpl implements MappedSpanReadRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Collection<MappedSpan> findByFilter(QueryDTO queryDTO) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<MappedSpan> criteriaQuery = criteriaBuilder.createQuery(MappedSpan.class);

        if (queryDTO.getRules() != null) {
            List<Predicate> conditions = generateConditions(queryDTO, criteriaBuilder, criteriaQuery);
            criteriaQuery.where(conditions.toArray(new Predicate[conditions.size()]));
        }

        TypedQuery<MappedSpan> q = em.createQuery(criteriaQuery);
        return q.getResultList();
    }

    private List<Predicate> generateConditions(QueryDTO queryDTO, CriteriaBuilder criteriaBuilder,
                                    CriteriaQuery<MappedSpan> criteriaQuery) {
        Root<MappedSpan> root = criteriaQuery.from(MappedSpan.class);
        List<Predicate> conditions = queryDTO.getRules().stream().map(rule -> {
            switch (rule.getOperation()) {
                case "=": return criteriaBuilder.equal(root.get(rule.getKey()), rule.getValue());
                case ">": {
                    try {
                        Long value = Long.parseLong(rule.getValue());
                        return criteriaBuilder.gt(root.get(rule.getKey()), value);
                    } catch (NumberFormatException e) {
                        throw new ValidationException(String.format("%s is not a number", rule.getValue()));
                    }
                }
                case "<": {
                    try {
                        Long value = Long.parseLong(rule.getValue());
                        return criteriaBuilder.lt(root.get(rule.getKey()), value);
                    } catch (NumberFormatException e) {
                        throw new ValidationException(String.format("%s is not a number", rule.getValue()));
                    }
                }
                default: throw new ValidationException(String.format("Operation \"%s\" is not supported", rule.getOperation()));
            }
        }).collect(Collectors.toList());
        if (queryDTO.getColumns().stream().anyMatch(column -> column.equalsIgnoreCase("activity"))) {
            conditions.add(criteriaBuilder.isNull(root.get("parentId")));
        }
        return conditions;
    }
}
