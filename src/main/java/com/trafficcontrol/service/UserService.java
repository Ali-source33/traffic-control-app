package com.trafficcontrol.service;

import com.trafficcontrol.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;

    public void createUser(User user) {
        em.persist(user);
    }

    public User updateUser(User user) {
        return em.merge(user);
    }

    public void deleteUser(User user) {
        em.remove(em.contains(user) ? user : em.merge(user));
    }

    public User getUserById(Long id) {
        return em.find(User.class, id);
    }

    public User getUserByUsername(String username) {
        return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                 .setParameter("username", username)
                 .getResultStream()
                 .findFirst()
                 .orElse(null);
    }

    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + username);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().getName()) // ROLE_ADMIN, ROLE_KOLLUK, ROLE_USER gibi
                .build();
    }
}
