package com.ecg.rl.API.fund.dao;


import com.ecg.rl.API.fund.entity.FundHistory;
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
public class FundHistoryDao implements IFundHistoryDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void saveOrUpdate(FundHistory fundHistory) {
        FundHistory target = getFundHistoryById(fundHistory.getId());
        if(target == null) {
            em.persist(fundHistory);
        } else {
            em.merge(fundHistory);
        }
    }

    @Override
    public List<FundHistory> getFundHistoryByCode(String code) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FundHistory> cq = cb.createQuery(FundHistory.class);
        Root<FundHistory> rootEntry = cq.from(FundHistory.class);
        Predicate predicate = cb.equal(rootEntry.get("fundCode"), code);
        CriteriaQuery<FundHistory> single = cq.select(rootEntry).where(predicate);
        cq.orderBy(cb.asc(rootEntry.get("date")));
        TypedQuery<FundHistory> query = em.createQuery(single);
        List<FundHistory> funds = query.getResultList();
        return funds;
    }

    @Override
    public FundHistory getFundHistoryById(String id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FundHistory> cq = cb.createQuery(FundHistory.class);
        Root<FundHistory> rootEntry = cq.from(FundHistory.class);
        Predicate predicate = cb.equal(rootEntry.get("id"), id);
        CriteriaQuery<FundHistory> single = cq.select(rootEntry).where(predicate);
        TypedQuery<FundHistory> query = em.createQuery(single);
        List<FundHistory> funds = query.getResultList();
        if (funds.isEmpty()) {
            return null;
        } else {
            return funds.get(0);
        }
    }


}
