package ru.kata.spring.boot_security.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Component
public class ApplicationStart implements ApplicationRunner {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationStart(UserService userService,
                            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        User user = new User();
        user.setName("admin");
        user.setSurname("admin");
        user.setAge(1);
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.addRole(new Role());
        user.addRole(new Role("ADMIN"));
        userService.saveUser(user);

        user = new User();
        user.setName("user");
        user.setSurname("user");
        user.setAge(1);
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user"));
        user.addRole(new Role());
        userService.saveUser(user);
    }
}
