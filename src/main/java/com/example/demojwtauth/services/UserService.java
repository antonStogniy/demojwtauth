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
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
    private final Path fileStorageLocation;
    @Autowired
    public UserService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        try {
//            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    public User getCurrentUser(){
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
//        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
//        User user = userRepository.findByUsername(userPrincipal.getUsername()).get();
//        return user;
    }

    public List<File> getUserPhotos() {
        User currentUser = getCurrentUser();
        List<File> userPhotos = new ArrayList<>();

        if (currentUser != null && StringUtils.hasText(currentUser.getUsername())) {

            Path currentPath = this.fileStorageLocation.resolve(currentUser.getUsername());
            File userPhotoDirectory = new File(currentPath.toUri());

            if (userPhotoDirectory.exists() && userPhotoDirectory.isDirectory()) {
                File[] photoFiles = userPhotoDirectory.listFiles();

                if (photoFiles != null) {
                    for (File photoFile : photoFiles) {
                        userPhotos.add(photoFile);
                    }
                }
            }
        }

        return userPhotos;
    }
}
