package com.nammaarena.auth.repository;

import com.nammaarena.auth.entity.User;
import com.nammaarena.auth.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
    List<User> findByRoleIn(List<Role> roles);
}