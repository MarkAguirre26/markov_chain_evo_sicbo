package com.virtual.app.sicbo.module.repository;

import com.virtual.app.sicbo.module.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User findByUsernameAndIsActive(String username, int isActive);

   User findByEmailAndIsActiveAndLogged(String email, int isActive, int logged);
    User findByEmail(String email);
    User findUserByUsernameAndEmail(String username, String email);

}