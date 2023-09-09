package com.example.demojwtauth.services;

import com.example.demojwtauth.entity.FileStorageProperties;
import com.example.demojwtauth.entity.User;
import com.example.demojwtauth.exceptions.FileStorageException;
import com.example.demojwtauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class SortByCreationDateFileService {
    @Autowired
    UserRepository userRepository;
    private final Path fileStorageLocation;

    @Autowired
    public SortByCreationDateFileService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
//            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        // Ваша логика для извлечения текущего пользователя (может зависеть от используемой аутентификации)
        // Например, если вы используете Spring Security, то текущий пользователь может быть UserDetails

        // Пример:
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername()).get();
            return user;
        }
        return null;
    }
}
