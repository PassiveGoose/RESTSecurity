package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.ArrayList;
import java.util.Collections;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(@ModelAttribute("user") User user) {
        return "auth/register";
    }

    @PostMapping(value = "/register", params = "action=cancel")
    public String cancelRegister() {
        return "redirect:login";
    }

    @PostMapping(value = "/register", params = "action=create")
    public String register(@ModelAttribute("user") User user) {
        if (userService.saveUser(user, new ArrayList<>(Collections.singletonList("USER")))) {
            return "redirect:register?error";
        }
        return "redirect:login";
    }
}
