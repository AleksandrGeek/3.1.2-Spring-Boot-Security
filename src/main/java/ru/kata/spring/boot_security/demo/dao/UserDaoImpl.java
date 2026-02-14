package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.entities.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


@Repository
public class UserDaoImpl implements UserDao {


    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createUser(User user) {
        entityManager.persist(user);
    }


    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }


    @Override
    public List<User> getAll() {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles", User.class);
        return query.getResultList();


    }

    @Override
    public void update(User user) {
        entityManager.merge(user);
    }

    @Override
    public void delete(Long id) {
        getById(id).ifPresent(user -> entityManager.remove(user));
    }


    @Override
    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username =:username",
                User.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst();
    }
}