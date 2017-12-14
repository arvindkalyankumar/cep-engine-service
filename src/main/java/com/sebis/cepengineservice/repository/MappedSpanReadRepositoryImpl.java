package com.sebis.cepengineservice.repository;

import com.sebis.cepengineservice.dto.QueryDTO;
import com.sebis.cepengineservice.entity.MappedSpan;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;

@Service
public class MappedSpanReadRepositoryImpl implements MappedSpanReadRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Collection<MappedSpan> findByFilter(QueryDTO queryDTO) {
        CriteriaBuilder qb = em.getCriteriaBuilder();

        CriteriaQuery<MappedSpan> c = qb.createQuery(MappedSpan.class);
        Root<MappedSpan> p = c.from(MappedSpan.class);

        Predicate condition = qb.equal(p.get("activity"), "List Providers1");
        c.where(condition);

        TypedQuery<MappedSpan> q = em.createQuery(c);
        return q.getResultList();
    }
}
