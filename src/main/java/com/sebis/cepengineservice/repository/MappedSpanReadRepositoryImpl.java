package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.dto.QueryDTO;
import com.sebis.cepengineservice.dto.QueryResult;
import com.sebis.cepengineservice.entity.MappedSpan;
import com.sebis.cepengineservice.service.exception.ValidationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.sebis.cepengineservice.dto.AggregationType.DURATION_AVERAGE;

@Service
public class MappedSpanReadRepositoryImpl implements MappedSpanReadRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Collection<QueryResult> findByFilter(QueryDTO queryDTO) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<QueryResult> criteriaQuery = criteriaBuilder.createQuery(QueryResult.class);
        Root<MappedSpan> root = criteriaQuery.from(MappedSpan.class);

        if (queryDTO.getRules() != null) {
            List<Predicate> conditions = generateConditions(queryDTO, criteriaBuilder, root);
            criteriaQuery.where(conditions.toArray(new Predicate[conditions.size()]));
        }

        if (queryDTO.getAggregationType() != null && queryDTO.getAggregationType().equals(DURATION_AVERAGE)) {
            List<Selection> selections = generateAggregateSelections(queryDTO, criteriaBuilder, root);
            criteriaQuery.multiselect(selections.toArray(new Selection[selections.size()]));
            List<Expression> groupings = generateGroupings(queryDTO, root);
            criteriaQuery.groupBy(groupings.toArray(new Expression[groupings.size()]));
        } else {
            List<Selection> selections = generateSelections(queryDTO, root);
            criteriaQuery.multiselect(selections.toArray(new Selection[selections.size()]));
        }

        TypedQuery<QueryResult> q = em.createQuery(criteriaQuery);
        return q.getResultList();
    }

    private List<Selection> generateSelections(QueryDTO queryDTO, Root<MappedSpan> root) {
        return queryDTO.getColumns().stream()
                .map(root::get)
                .collect(Collectors.toList());
    }

    private List<Selection> generateAggregateSelections(QueryDTO queryDTO, CriteriaBuilder criteriaBuilder, Root<MappedSpan> root) {
        List<Selection> selections = new ArrayList<>();
        selections.addAll(queryDTO.getColumns().stream()
                .map(attributeName -> {
                    if ("duration".equals(attributeName)) {
                        return criteriaBuilder.avg(root.get("duration"));
                    } else {
                        return root.get(attributeName);
                    }
                })
                .collect(Collectors.toList()));
        return selections;
    }

    private List<Expression> generateGroupings(QueryDTO queryDTO, Root<MappedSpan> root) {
        return queryDTO.getColumns().stream()
                .filter(column -> !column.equalsIgnoreCase("duration"))
                .map(root::get)
                .collect(Collectors.toList());
    }

    private List<Predicate> generateConditions(QueryDTO queryDTO, CriteriaBuilder criteriaBuilder,
                                    Root<MappedSpan> root) {

        List<Predicate> conditions = queryDTO.getRules().stream().map(rule -> {
            switch (rule.getOperator()) {
                case "=": return criteriaBuilder.equal(root.get(rule.getField()), rule.getValue());
                case ">": {
                    try {
                        Long value = Long.parseLong(rule.getValue());
                        return criteriaBuilder.gt(root.get(rule.getField()), value);
                    } catch (NumberFormatException e) {
                        throw new ValidationException(String.format("%s is not a number", rule.getValue()));
                    }
                }
                case "<": {
                    try {
                        Long value = Long.parseLong(rule.getValue());
                        return criteriaBuilder.lt(root.get(rule.getField()), value);
                    } catch (NumberFormatException e) {
                        throw new ValidationException(String.format("%s is not a number", rule.getValue()));
                    }
                }
                default: throw new ValidationException(String.format("Operation \"%s\" is not supported", rule.getOperator()));
            }
        }).collect(Collectors.toList());
        if (queryDTO.getColumns().stream().anyMatch(column -> column.equalsIgnoreCase("activity"))) {
            conditions.add(criteriaBuilder.isNull(root.get("parentId")));
        }
        return conditions;
    }
}
