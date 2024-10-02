package com.example.jwt.repository;

import com.example.jwt.entity.UserEntity;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Table(name = "user")
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    public UserEntity findByUserId(String userId);
}
