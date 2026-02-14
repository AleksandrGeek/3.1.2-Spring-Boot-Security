package ru.kata.spring.boot_security.demo.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserDao userDao,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createUser(User user, Set<Long> roleIds) {
        // Проверка на существование
        if (findByUsername(user.getUsername()) != null) {
            log.error("User with name {} already exists", user.getUsername());
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }

        // Шифруем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // ✅ Получаем роли через getRoleById в цикле
        Set<Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            Role role = roleService.getRoleById(roleId);  // ← используем существующий метод!
            roles.add(role);
            log.debug("Added role: {} to user", role.getName());
        }

        user.setRoles(roles);
        userDao.createUser(user);
        log.info("User created: {}", user.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userDao.getById(id)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", id);
                    return new IllegalArgumentException("User not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    @Override
    @Transactional
    public void updateUser(User user, Set<Long> roleIds) {
        User existingUser = getUserById(user.getId());

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // ✅ Тот же подход для обновления
        Set<Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            Role role = roleService.getRoleById(roleId);  // ← используем существующий метод!
            roles.add(role);
        }

        existingUser.setRoles(roles);
        userDao.update(existingUser);
        log.info("User updated: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userDao.delete(id);
        log.info("User deleted: {} (id: {})", user.getUsername(), id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userDao.findByUsername(username).orElse(null);
    }
}
