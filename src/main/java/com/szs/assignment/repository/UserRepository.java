package com.szs.assignment.repository;

import com.szs.assignment.model.user.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo,Long>{
    Optional<UserInfo> findByUserId(String userId);

}
