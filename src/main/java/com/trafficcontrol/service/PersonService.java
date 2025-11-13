package com.trafficcontrol.service;

import com.trafficcontrol.entity.Person;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PersonService {

    @PersistenceContext
    private EntityManager em;

    public void createPerson(Person person) {
        em.persist(person);
    }

    public Person updatePersonByTc(Person person) {
        Person existing = getPersonByTc(person.getTcKimlik());
        if (existing == null) {
            throw new IllegalArgumentException("Person bulunamadı");
        }
        existing.setFirstName(person.getFirstName());
        existing.setLastName(person.getLastName());
        existing.setBirthDate(person.getBirthDate());
        existing.setAddress(person.getAddress());
        existing.setHasOutstandingFines(person.getHasOutstandingFines());
        existing.setIsWanted(person.getIsWanted());
        return em.merge(existing);
    }

    public void deletePerson(Person person) {
        em.remove(em.contains(person) ? person : em.merge(person));
    }

    public Person getPersonByTc(String tcKimlik) {
        return em.createQuery("SELECT p FROM Person p WHERE p.tcKimlik = :tc", Person.class)
                 .setParameter("tc", tcKimlik)
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
    }

    public List<Person> getAllPersons() {
        return em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
    }
}
