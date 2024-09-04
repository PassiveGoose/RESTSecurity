package ru.kata.spring.boot_security.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RestUserController {

    private final UserService userService;

    @Autowired
    public RestUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping(value = "/users", params = "id")
    public ResponseEntity<User> getOneUser(@RequestParam int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping(value = "/users", params = {"name", "surname", "age", "username", "password"})
    public ResponseEntity<HttpStatus> addUser(@RequestBody User newUser) {
        if (!userService.saveUser(newUser.getName(), newUser.getSurname(), newUser.getAge(),
                newUser.getUsername(), newUser.getPassword(),
                newUser.getRoles().stream().map(Role::getName).collect(Collectors.toList()))) {
            return ResponseEntity.ok(HttpStatus.CREATED);
        }
        return ResponseEntity.badRequest().build();
    }

}
