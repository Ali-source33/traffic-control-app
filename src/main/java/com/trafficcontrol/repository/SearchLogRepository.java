package com.trafficcontrol.repository;

import com.trafficcontrol.entity.SearchLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public class SearchLogRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(SearchLog log) {
        entityManager.persist(log);
    }

    public List<SearchLog> findAll() {
        return entityManager.createQuery("SELECT s FROM SearchLog s", SearchLog.class).getResultList();
    }
}
