package ru.kata.spring.boot_security.demo;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.annotation.PostConstruct;
import java.util.Set;

@Slf4j
@Component
public class DataInitializer {

    private final UserService userService;    // ✅ Только сервисы
    private final RoleService roleService;    // ✅ Только сервисы

    public DataInitializer(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    @Transactional
    public void init() {
        log.info("=== STARTING DATA INITIALIZATION ===");

        // 1. Создаем роли через RoleService
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN");
        Role userRole = createRoleIfNotExists("ROLE_USER");
        log.info("✅ Roles ready: ADMIN, USER");

        // 2. Создаем админа через UserService (пароль передаем сырой!)
        if (userService.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin");           // ✅ СЫРОЙ пароль!
            admin.setEmail("admin@test.com");

            userService.createUser(admin, Set.of(adminRole.getId(), userRole.getId()));
            log.info("Admin user created: {} (id: {})", admin.getUsername(), admin.getId());
        }

        // 3. Создаем пользователя через UserService
        if (userService.findByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setPassword("user");             // ✅ СЫРОЙ пароль!
            user.setEmail("user@test.com");

            userService.createUser(user, Set.of(userRole.getId()));
            log.info("Regular user created: {} (id: {})", user.getUsername(), user.getId());
        }

        System.out.println("=== DATA INITIALIZATION COMPLETE ===");
    }

    private Role createRoleIfNotExists(String roleName) {
        try {
            return roleService.findByName(roleName);
        } catch (Exception e) {
            // Добавьте метод createRole в RoleService
            return roleService.createRole(roleName);
        }
    }
}