package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.sequrity.UserDetailsImpl;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public String printUsers(ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        model.addAttribute("user", userDetails.getUser());
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user_list";
    }

    @PostMapping(value = "/add_user")
    public String addUser(@RequestParam("name") String name,
                          @RequestParam("surname") String surname,
                          @RequestParam("age") int age,
                          @RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam List<String> roles) {
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            return "redirect:/admin?error";
        }
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setAge(age);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        roles.forEach(role -> {
            if (role != null) {
                user.addRole(new Role(role));
            }
        });
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @PostMapping(value = "/delete_user", params = "id")
    public String deleteUser(@RequestParam int id) {
        userService.removeUserById(id);
        return "redirect:/admin";
    }

    @PostMapping(value = "/edit_user")
    public String editUser(@RequestParam int id,
                           @RequestParam("name") String name,
                           @RequestParam("surname") String surname,
                           @RequestParam("age") int age,
                           @RequestParam("username") String username,
                           @RequestParam List<String> roles) {
        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getId() != id) {
            return "redirect:/admin?error";
        }
        User user = userService.getUserById(id);
        user.setName(name);
        user.setSurname(surname);
        user.setAge(age);
        user.setUsername(username);
        List<Role> newRoles = new ArrayList<>();
        roles.forEach(role -> {
            if (role != null) {
                newRoles.add(new Role(role));
            }
        });
        user.setRoles(newRoles);
        userService.updateUser(user);
        return "redirect:/admin";
    }
}
