package com.gms.demo.repository;

import com.gms.demo.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {
    User findByEmailOrUsername(String email, String username);

    User findByUsername(String username);

    User findByEmail(String value);

    User findByPhone(String value);

    User findByRole(String superAdmin);

    int countByRole(String customer);

    List<User> findByRole(String role,Sort sort);
    List<User> findByUsernameContainingIgnoreCaseAndRole(String search, String customer, Sort name);

    List<User> findAllByRole(String admin);

    boolean existsByEmail(String newEmail);
}
