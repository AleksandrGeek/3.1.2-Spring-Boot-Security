package ru.kata.spring.boot_security.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.entities.Role;
import java.util.List;


@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleDao.getAllRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleDao.getRoleById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with id: {}", id);  // ✅ Только ошибка!
                    return new IllegalArgumentException("Role not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleDao.findByName(name)
                .orElseThrow(() -> {
                    log.error("Role not found with name: {}", name);  // ✅ Только ошибка!
                    return new IllegalArgumentException("Role not found with name: " + name);
                });
    }

    @Override
    @Transactional
    public Role createRole(String roleName) {
        // Проверяем, есть ли уже такая роль
        try {
            return findByName(roleName);  // Если есть - возвращаем
        } catch (IllegalArgumentException e) {
            // Создаем новую роль
            Role role = new Role(roleName);
            roleDao.save(role);
            log.info("Created new role: {}", roleName);  // ✅ Важно! Создание роли
            return role;
        }
    }

}