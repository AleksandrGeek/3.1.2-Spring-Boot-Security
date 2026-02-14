package ru.kata.spring.boot_security.demo.dao;


import ru.kata.spring.boot_security.demo.entities.User;

import java.util.List;
import java.util.Optional;


public interface UserDao {

    void createUser(User user);

    Optional<User> getById(Long id);

    List<User> getAll();

    void update(User user);

    void delete(Long id);

    Optional<User> findByUsername(String username);


}
