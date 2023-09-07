package com.example.demojwtauth.repository;

import com.example.demojwtauth.entity.User;
import com.example.demojwtauth.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    UserProfile findUserProfileByUser(User user);
}
