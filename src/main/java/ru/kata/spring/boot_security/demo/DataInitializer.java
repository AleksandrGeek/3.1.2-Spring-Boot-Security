package ru.kata.spring.boot_security.demo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void init() {
        System.out.println("=== STARTING DATA INITIALIZATION ===");

        // 1. Создаем роли
        Optional<Role> adminRoleOpt = roleRepository.findByName("ROLE_ADMIN");
        Role adminRole;
        if (adminRoleOpt.isEmpty()) {
            adminRole = new Role("ROLE_ADMIN");
            roleRepository.save(adminRole);
            System.out.println("✅ Created role: ROLE_ADMIN");
        } else {
            adminRole = adminRoleOpt.get();
            System.out.println("✅ Role ROLE_ADMIN already exists");
        }

        Optional<Role> userRoleOpt = roleRepository.findByName("ROLE_USER");
        Role userRole;
        if (userRoleOpt.isEmpty()) {
            userRole = new Role("ROLE_USER");
            roleRepository.save(userRole);
            System.out.println("✅ Created role: ROLE_USER");
        } else {
            userRole = userRoleOpt.get();
            System.out.println("✅ Role ROLE_USER already exists");
        }

        // 2. Создаем пользователей если их нет
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");

            // Кодируем пароль
            String encodedPassword = passwordEncoder.encode("admin");
            admin.setPassword(encodedPassword);
            System.out.println("Admin password encoded: " + encodedPassword);

            admin.setEmail("admin@test.com");
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole, userRole)));

            userRepository.save(admin);
            System.out.println("✅ Created user: admin/admin (roles: ADMIN, USER)");
        } else {
            System.out.println("❌ User 'admin' already exists");
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");

            // Кодируем пароль
            String encodedPassword = passwordEncoder.encode("user");
            user.setPassword(encodedPassword);
            System.out.println("User password encoded: " + encodedPassword);

            user.setEmail("user@test.com");
            user.setRoles(new HashSet<>(Arrays.asList(userRole)));

            userRepository.save(user);
            System.out.println("✅ Created user: user/user (role: USER)");
        } else {
            System.out.println("❌ User 'user' already exists");
        }

        System.out.println("=== DATA INITIALIZATION COMPLETE ===");
    }
}