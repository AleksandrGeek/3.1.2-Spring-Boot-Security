package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;


@Service
@Transactional(readOnly = true)
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("✅ UserDetailServiceImpl created!");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ОТЛАДОЧНЫЙ КОД - должен появиться в логах!
        System.out.println("========================");
        System.out.println("=== LOADING USER: " + username + " ===");

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("!!! USER NOT FOUND: " + username);
                    return new UsernameNotFoundException(
                            String.format("User '%s' not found", username));
                });

        System.out.println("✅ User found: " + user.getUsername());
        System.out.println("🔑 Password hash: " + user.getPassword());
        System.out.println("🔑 Password starts with $2a$: " + user.getPassword().startsWith("$2a$"));
        System.out.println("📋 Roles (" + user.getRoles().size() + "):");
        user.getRoles().forEach(role -> System.out.println("   - " + role.getName()));
        System.out.println("========================");

        return user;
    }
}