package com.finsight.user.repository;

import com.finsight.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existById(String id);

    boolean existByEmail(String email);

}
