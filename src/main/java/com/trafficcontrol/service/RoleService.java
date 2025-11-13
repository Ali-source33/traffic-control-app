package com.trafficcontrol.service;

import com.trafficcontrol.entity.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Primary
@Qualifier("roleService")
public class RoleService {

    @PersistenceContext
    private EntityManager em;

    public Role getRoleByName(String roleName) {
        return em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                 .setParameter("name", roleName.toUpperCase())
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
    }

    public void createRole(Role role) {
        em.persist(role);
    }
}
