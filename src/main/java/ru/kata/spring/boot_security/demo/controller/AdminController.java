package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {


    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService,
                           RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String adminPage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    // CRUD операции - только для админа
    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin_new";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user,
                             @RequestParam(required = false) Set<Long> roleIds) {
        if (roleIds == null) roleIds = new HashSet<>();
        userService.createUser(user, roleIds);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin_edit";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute User user,
                             @RequestParam(required = false) Set<Long> roleIds) {
        if (roleIds == null) roleIds = new HashSet<>();
        userService.updateUser(user, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }
}