package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {


    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public String getUser(Principal principal, Model model) {
        User user = userService.findUserByEmail(principal.getName());
        model.addAttribute("user", user);
        String roles = user.showRoles();
        model.addAttribute("listRole", roles);
        return "user";
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("listUser", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/new")
    public String newUser(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("setRoles", roleService.getAllRolesWithoutFirst());
        return "new";
    }

    @PostMapping("/new")
    public String saveUser(@ModelAttribute("user") User user) {
        setRoles(user);
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable(value = "id") Integer id, Model model) {
        model.addAttribute("user", userService.getUser(id));
        model.addAttribute("setRoles", roleService.getAllRolesWithoutFirst());
        return "edit";
    }

    @PatchMapping("/users/{id}")
    public String editUser(@ModelAttribute("user") User user) {
        setRoles(user);
        userService.editUser(user);
        return "redirect:/admin/users";
    }

    private void setRoles(@ModelAttribute("user") User user) {
        Set<Role> roles = roleService.getDefaultRole(1L);
        Set<String> roleNames = user.getRoles().stream().map(r -> r.getRole()).collect(Collectors.toSet());
        Set<Role> additionalRoles = roleService.getRoleByName(roleNames);
        roles.addAll(additionalRoles);
        user.setRoles(roles);
    }

    @DeleteMapping("/users/{id}")
    public String removeUserById(@PathVariable(value = "id") Integer id) {
        userService.removeUserById(id);
        return "redirect:/admin/users";
    }
}