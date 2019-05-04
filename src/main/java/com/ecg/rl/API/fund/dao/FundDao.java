package com.ecg.rl.API.fund.dao;


import com.ecg.rl.API.fund.entity.Fund;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;


@Transactional
@Repository
public class FundDao implements IFundDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public void saveOrUpdate(Fund fund) {
        Fund target = getFundByCode(fund.getCode());
        if(target == null) {
            em.persist(fund);
        } else {
            em.merge(fund);
        }

    }

    @Override
    public Fund getFundByCode(String code) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fund> cq = cb.createQuery(Fund.class);
        Root<Fund> rootEntry = cq.from(Fund.class);
        Predicate predicate = cb.equal(rootEntry.get("code"), code);
        CriteriaQuery<Fund> single = cq.select(rootEntry).where(predicate);
        TypedQuery<Fund> query = em.createQuery(single);
        List<Fund> funds = query.getResultList();
        if (funds.isEmpty()) {
            return null;
        } else {
            return funds.get(0);
        }
    }

    @Override
    public List<Fund> getFundsByRange(int offset, int step) {
        return null;
    }

    @Override
    public List<Fund> getAllFunds() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Fund> cq = cb.createQuery(Fund.class);
        Root<Fund> rootEntry = cq.from(Fund.class);
        CriteriaQuery<Fund> all = cq.select(rootEntry);
        TypedQuery<Fund> query = em.createQuery(all);
        List<Fund> funds = query.getResultList();
        return funds;
    }
}
