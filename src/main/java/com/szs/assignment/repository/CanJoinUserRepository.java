package com.szs.assignment.repository;

import com.szs.assignment.model.user.CanJoinUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CanJoinUserRepository extends JpaRepository<CanJoinUser,Long>{

    Optional<CanJoinUser> findByNameAndRegNo(String name, String regNo);

}
