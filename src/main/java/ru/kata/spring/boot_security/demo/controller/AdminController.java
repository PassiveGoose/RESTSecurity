package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public String printUsers(ModelMap model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", user);
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
        if (!userService.saveUser(name, surname, age, username, password, roles)) {
            return "redirect:/admin?error";
        }
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
        if (!userService.updateUser(id, name, surname, age, username, roles)) {
            return "redirect:/admin?error";
        }
        return "redirect:/admin";
    }
}
