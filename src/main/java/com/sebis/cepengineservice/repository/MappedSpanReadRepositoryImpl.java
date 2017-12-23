package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.dto.Query;
import com.sebis.cepengineservice.dto.QueryResult;
import com.sebis.cepengineservice.entity.MappedSpan;
import com.sebis.cepengineservice.service.exception.ValidationException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MappedSpanReadRepositoryImpl implements MappedSpanReadRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Collection<QueryResult> findByFilter(Query query, long fromTimestamp, long tillTimestamp) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<QueryResult> criteriaQuery = criteriaBuilder.createQuery(QueryResult.class);
        Root<MappedSpan> root = criteriaQuery.from(MappedSpan.class);

        List<Selection> selections = generateSelections(query, criteriaBuilder, root);
        criteriaQuery.multiselect(selections.toArray(new Selection[selections.size()]));

        List<Predicate> conditions = generateConditions(query, criteriaBuilder, root, fromTimestamp, tillTimestamp);
        criteriaQuery.where(conditions.toArray(new Predicate[conditions.size()]));

        if (query.getAggregations() != null) {
            List<Expression> groupings = generateGroupings(query, root);
            criteriaQuery.groupBy(groupings.toArray(new Expression[groupings.size()]));
        }

        TypedQuery<QueryResult> q = em.createQuery(criteriaQuery);
        return q.getResultList();
    }

    private List<Selection> generateSelections(Query query, CriteriaBuilder criteriaBuilder, Root<MappedSpan> root) {
        return query.getColumns()
                .stream()
                .map(column -> {
                    if (query.getAggregations() != null) {
                        Optional<Query.Aggregation> maybeAggregation = query.getAggregations()
                                .stream()
                                .filter(aggr -> aggr.getField().equalsIgnoreCase(column))
                                .findAny();
                        if (maybeAggregation.isPresent()) {
                            Query.Aggregation aggregation = maybeAggregation.get();
                            switch (maybeAggregation.get().getOperation()) {
                                case AVG:
                                    return criteriaBuilder.avg(root.get(maybeAggregation.get().getField()));
                                case MAX:
                                    return criteriaBuilder.max(root.get(maybeAggregation.get().getField()));
                                case MIN:
                                    return criteriaBuilder.min(root.get(maybeAggregation.get().getField()));
                                case COUNT:
                                    return criteriaBuilder.countDistinct(root.get(maybeAggregation.get().getField()));
                                default: {
                                    throw new ValidationException(String.format("Aggregation \"%s\" is not supported",
                                            aggregation.getOperation()));
                                }
                            }
                        } else {
                            return root.get(column);
                        }
                    } else {
                        return root.get(column);
                    }
                }).collect(Collectors.toList());
    }

    private List<Predicate> generateConditions(Query query, CriteriaBuilder criteriaBuilder, Root<MappedSpan> root,
                                               long fromTimestamp, long tillTimestamp) {
        List<Predicate> conditions = new ArrayList<>();
        if (query.getRules() != null) {
            conditions.addAll(
                    query.getRules().stream().map(rule -> {
                        switch (rule.getOperator()) {
                            case "=":
                                return criteriaBuilder.equal(root.get(rule.getField()), rule.getValue());
                            case "!=":
                                return criteriaBuilder.notEqual(root.get(rule.getField()), rule.getValue());
                            case "contains":
                                return criteriaBuilder.like(root.get(rule.getField()), "%".concat(rule.getValue()).concat("%"));
                            default:
                                throw new ValidationException(String.format("Operation \"%s\" is not supported",
                                        rule.getOperator()));
                        }
                    }).collect(Collectors.toList())
            );
        }
        conditions.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), fromTimestamp));
        conditions.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), tillTimestamp));
        return conditions;
    }

    private List<Expression> generateGroupings(Query query, Root<MappedSpan> root) {
        return query.getColumns().stream()
                .filter(column -> query.getAggregations()
                        .stream()
                        .noneMatch(aggregation -> aggregation.getField().equalsIgnoreCase(column)))
                .map(root::get)
                .collect(Collectors.toList());
    }
}
