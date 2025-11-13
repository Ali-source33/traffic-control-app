package com.trafficcontrol.service;

import com.trafficcontrol.entity.SearchLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Primary
@Qualifier("searchLogService")
public class SearchLogService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void logSearch(SearchLog log) {
        em.persist(log);
    }

    @Transactional(readOnly = true)
    public List<SearchLog> getAllLogs() {
        return em.createQuery("SELECT s FROM SearchLog s", SearchLog.class).getResultList();
    }
}
