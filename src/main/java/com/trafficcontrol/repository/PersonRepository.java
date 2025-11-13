package com.trafficcontrol.repository;

import com.trafficcontrol.entity.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public class PersonRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Person person) {
        entityManager.persist(person);
    }

    public Person update(Person person) {
        return entityManager.merge(person);
    }

    public void delete(Person person) {
        entityManager.remove(entityManager.contains(person) ? person : entityManager.merge(person));
    }

    public Person findById(Long id) {
        return entityManager.find(Person.class, id);
    }

    public List<Person> findAll() {
        return entityManager.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }

    public Person findByTcKimlik(String tcKimlik) {
        try {
            return entityManager.createQuery("SELECT p FROM Person p WHERE p.tcKimlik = :tc", Person.class)
                    .setParameter("tc", tcKimlik)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
