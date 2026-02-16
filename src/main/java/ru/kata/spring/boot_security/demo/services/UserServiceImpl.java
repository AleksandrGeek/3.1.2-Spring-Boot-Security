package ru.kata.spring.boot_security.demo.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;



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
        log.info("Создание пользователя: {}", user.getUsername());

        // 1. Проверка на существование
        if (findByUsername(user.getUsername()) != null) {
            log.error("Пользователь {} уже существует", user.getUsername());
            throw new IllegalArgumentException("Пользователь с таким именем уже существует: " + user.getUsername());
        }

        // 2. Шифруем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 3. Получаем роли
        Set<Role> roles = roleService.getRolesByIds(roleIds);
        user.setRoles(roles);

        // 4. Сохраняем
        userDao.createUser(user);
        log.info("Пользователь {} успешно создан с ролями: {}",
                user.getUsername(), roles.stream().map(Role::getName).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        log.debug("Поиск пользователя по id: {}", id);

        return userDao.getById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с id {} не найден", id);
                    return new IllegalArgumentException("Пользователь не найден с id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        return userDao.getAll();
    }

    @Override
    @Transactional
    public void updateUser(User user, Set<Long> roleIds) {
        log.info("Обновление пользователя с id: {}", user.getId());

        // 1. Проверяем, что пользователь существует
        User existingUser = getUserById(user.getId());

        // 2. Обновляем основные поля
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        // 3. Обновляем пароль только если ввели новый
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            log.debug("Пароль обновлен для пользователя: {}", user.getUsername());
        }

        // 4. Обновляем роли
        Set<Role> newRoles = roleService.getRolesByIds(roleIds);
        existingUser.setRoles(newRoles);

        // 5. Сохраняем
        userDao.update(existingUser);
        log.info("Пользователь {} успешно обновлен", user.getUsername());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id: {}", id);

        // 1. Проверяем, что пользователь существует
        User user = getUserById(id);

        // 2. Удаляем
        userDao.delete(id);
        log.info("Пользователь {} (id: {}) успешно удален", user.getUsername(), id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.debug("Поиск пользователя по имени: {}", username);
        return userDao.findByUsername(username).orElse(null);
    }
}