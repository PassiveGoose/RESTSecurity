package ru.kata.spring.boot_security.demo.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class ApplicationStart implements ApplicationRunner {

    @PersistenceContext
    private EntityManager entityManager;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationStart(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        entityManager.persist(new Role("USER"));
        entityManager.persist(new Role("ADMIN"));

        User adminUser = new User();
        adminUser.setName("admin");
        adminUser.setSurname("admin");
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));

        Role userRole = entityManager.find(Role.class, 1);
        Role adminRole = entityManager.find(Role.class, 2);

        adminUser.setRoles(new ArrayList<>(List.of(userRole, adminRole)));
        entityManager.persist(adminUser);

        User regularUser = new User();
        regularUser.setName("user");
        regularUser.setSurname("user");
        regularUser.setUsername("user");
        regularUser.setPassword(passwordEncoder.encode("user"));

        userRole = entityManager.find(Role.class, 1);

        regularUser.setRoles(new ArrayList<>(List.of(userRole)));
        entityManager.persist(regularUser);
    }
}