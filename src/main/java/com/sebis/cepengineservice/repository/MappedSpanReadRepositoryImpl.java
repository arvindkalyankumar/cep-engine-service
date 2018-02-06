package com.sebis.cepengineservice.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.stereotype.Repository;

import com.sebis.cepengineservice.dto.QueryDto;
import com.sebis.cepengineservice.dto.QueryResultDto;
import com.sebis.cepengineservice.entity.MappedSpan;
import com.sebis.cepengineservice.service.exception.ValidationException;

@Repository
public class MappedSpanReadRepositoryImpl implements MappedSpanReadRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Collection<QueryResultDto> findByFilter(QueryDto query, long fromTimestamp, long tillTimestamp) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<QueryResultDto> criteriaQuery = criteriaBuilder.createQuery(QueryResultDto.class);
        Root<MappedSpan> root = criteriaQuery.from(MappedSpan.class);

        List<Selection> selections = generateSelections(query, criteriaBuilder, root);
        criteriaQuery.multiselect(selections.toArray(new Selection[selections.size()]));

        List<Predicate> conditions = generateConditions(query, criteriaBuilder, root, fromTimestamp, tillTimestamp);
        criteriaQuery.where(conditions.toArray(new Predicate[conditions.size()]));

        if (query.getAggregations() != null) {
            List<Expression> groupings = generateGroupings(query, root);
            criteriaQuery.groupBy(groupings.toArray(new Expression[groupings.size()]));
        }

        TypedQuery<QueryResultDto> q = em.createQuery(criteriaQuery);
        return q.getResultList();
    }
    
    @Override
    public List<Map<String, Object>> findBySql(String query) {
    		List<Map<String, Object>> finalResult = new ArrayList<Map<String, Object>>();
    		String[] columns = getColumnNames(query);
        Query q = em.createNativeQuery(query);
        Map<String, Object> map = new HashMap<String, Object>();
        List resultList = q.getResultList();
        if (columns.length > 1) {
	        	for (Object result: resultList) {
	    			map = new HashMap<String, Object>();
            		for (int i= 0; i< columns.length; i++) {
            			map.put(columns[i].trim(), ((Object[])result)[i]);
            		}
            		finalResult.add(map);
	         }
        } else {
	        	for (Object result: resultList) {
	    			map = new HashMap<String, Object>();
        			map.put(columns[0], result);
            		finalResult.add(map);
	         }
        }
        
        return finalResult;
    }

    private String[] getColumnNames(String query) {
    		return query.toLowerCase().split("from")[0].split("select")[1].split(",");
	}

	private List<Selection> generateSelections(QueryDto query, CriteriaBuilder criteriaBuilder, Root<MappedSpan> root) {
        return query.getColumns()
                .stream()
                .map(column -> {
                    if (query.getAggregations() != null) {
                        Optional<QueryDto.Aggregation> maybeAggregation = query.getAggregations()
                                .stream()
                                .filter(aggr -> aggr.getField().equalsIgnoreCase(column))
                                .findAny();
                        if (maybeAggregation.isPresent()) {
                            QueryDto.Aggregation aggregation = maybeAggregation.get();
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

    private List<Predicate> generateConditions(QueryDto query, CriteriaBuilder criteriaBuilder, Root<MappedSpan> root,
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

    private List<Expression> generateGroupings(QueryDto query, Root<MappedSpan> root) {
        return query.getColumns().stream()
                .filter(column -> query.getAggregations()
                        .stream()
                        .noneMatch(aggregation -> aggregation.getField().equalsIgnoreCase(column)))
                .map(root::get)
                .collect(Collectors.toList());
    }
}
