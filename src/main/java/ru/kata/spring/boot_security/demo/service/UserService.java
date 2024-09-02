package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean saveUser(User user, List<String> roleNames) ;

    boolean saveUser(String name, String surname, int age,
                     String username, String password, List<String> roles);

    void removeUserById(int id);

    List<User> getAllUsers();

    boolean updateUser(int id, String name, String surname, int age,
                       String username, List<String> roles);

    boolean updateUser(User user, List<String> roleNames);

    User getUserById(int id);

    Optional<User> getUserByUsername(String username);

    Optional<Role> getRoleByName(String roleName);
}
