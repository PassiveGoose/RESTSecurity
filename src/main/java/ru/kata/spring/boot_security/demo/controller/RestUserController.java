package ru.kata.spring.boot_security.demo.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/users")
    public ResponseEntity<HttpStatus> addUser(@RequestBody Map<String, Object> body) {
        User newUserData = parseUser(body.get("user"));
        List<String> roles = parseRoles(body.get("roles"));

        if (!userService.saveUser(newUserData.getName(), newUserData.getSurname(), newUserData.getAge(),
                newUserData.getUsername(), newUserData.getPassword(), roles)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    @PutMapping(value = "/users", params = "id")
    public ResponseEntity<HttpStatus> editUser(@RequestBody Map<String, Object> body, @RequestParam int id) {
        User updateUser = parseUser(body.get("user"));
        List<String> roles = parseRoles(body.get("roles"));

        if (!userService.updateUser(id, updateUser.getName(), updateUser.getSurname(),
                updateUser.getAge(), updateUser.getUsername(), roles)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping(value = "/users", params = "id")
    public ResponseEntity<HttpStatus> deleteUser(@RequestParam int id) {
        userService.removeUserById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private User parseUser(Object json) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(json), User.class);
    }

    private List<String> parseRoles(Object json) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(json), new TypeToken<List<String>>() {}.getType());
    }

}
