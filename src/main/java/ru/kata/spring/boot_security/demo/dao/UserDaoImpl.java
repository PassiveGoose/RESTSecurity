package ru.kata.spring.boot_security.demo.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public void removeUserById(int id) {
        entityManager.remove(entityManager.find(User.class, id));
    }

    @Override
    public List<User> getAllUsers() {
        CriteriaQuery<User> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(User.class);
        criteriaQuery.from(User.class);
        List<User> userList = entityManager.createQuery(criteriaQuery).getResultList();
        userList.forEach(user -> Hibernate.initialize(user.getRoles()));
        return userList;
    }

    @Override
    public void updateUser(User user) {
        entityManager.merge(user);
    }

    @Override
    public User getUserById(int id) {
        User user = entityManager.find(User.class, id);
        Hibernate.initialize(user.getRoles());
        return user;
    }

    @Override
    public Optional<Role> getRoleByName(String roleName) {
        CriteriaQuery<Role> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Role.class);
        criteriaQuery.from(Role.class);
        List<Role> roleList = entityManager.createQuery(criteriaQuery).getResultList();
        return roleList.stream().filter(role -> role.getName().equals(roleName)).findFirst();
    }
}
