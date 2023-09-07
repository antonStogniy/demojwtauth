package com.example.demojwtauth.controllers;

import com.example.demojwtauth.payload.response.UploadFileResponse;
import com.example.demojwtauth.services.FileStorageService;
import com.example.demojwtauth.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/upload")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UserService userService;



    @GetMapping("/downloadmultifile")
    public ResponseEntity<?> getPhotos(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        List<String> photoURIs = userService.getUserPhotos().stream()
                .map(File::toURI)
                .map(uri -> uri.toString())
                .map(path -> path.substring(path.lastIndexOf("/") ))
                .map(path -> "http://localhost:8080/upload/downloadFile" + path)
                .collect(Collectors.toList());

        // Вернуть URI фотографий в виде HTTP ответа
        return new ResponseEntity<>(photoURIs, HttpStatus.OK);
    }

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
        // Получаем информацию о текущем аутентифицированном пользователе
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName(); // Имя текущего пользователя

        // Создаем папку для текущего пользователя, если она не существует
        String userUploadDirectory = "/downloadFiles/" + currentUserName;// Путь к папке пользователя
        String contentType = file.getContentType();
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
//        File userDirectory = new File(userUploadDirectory);
//        if (!userDirectory.exists()) {
//            userDirectory.mkdirs(); // Создаем папку, если она не существует
//        }

        // Сохраняем файл в папке текущего пользователя
        String fileName = fileStorageService.storeFile(file, currentUserName);

        // Создаем URL для скачивания файла
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(currentUserName + "/") // Путь к папке пользователя
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,contentType, file.getSize());
    }
    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName, currentUserName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }



//    @PostMapping("/uploadfile")
//    public String uploadFile(@RequestParam("file") MultipartFile file) {
//        // Получаем информацию о текущем аутентифицированном пользователе
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserName = authentication.getName(); // Имя текущего пользователя
//
//        // Создаем папку для текущего пользователя, если она не существует
//        String userUploadDirectory = "/downloadFile/" + currentUserName; // Путь к папке пользователя
//        File userDirectory = new File(userUploadDirectory);
//        if (!userDirectory.exists()) {
//            userDirectory.mkdirs(); // Создаем папку, если она не существует
//        }
//
//        // Сохраняем файл в папке текущего пользователя
//        String fileName = fileStorageService.storeFile(file, userUploadDirectory);
//
//        // Создаем URL для скачивания файла
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/downloadFile/")
//                .path(currentUserName + "/") // Путь к папке пользователя
//                .path(fileName)
//                .toUriString();
//
//        return "ok";
//        return new UploadFileResponse(fileName, fileDownloadUri,
//                file.getContentType(), file.getSize());
//    }
}
