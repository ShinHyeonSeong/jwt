package com.example.jwt.repository;

import com.example.jwt.entity.UserAuthEntity;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Table(name = "user_auth")
public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Integer> {
    public List<UserAuthEntity> findAllByUserNo_UserNo(int userNo);
    //findByUserNo_UserNo(int userNo);
}
