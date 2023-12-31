package com.example.demojwtauth.controllers;

import com.example.demojwtauth.entity.Post;
import com.example.demojwtauth.entity.User;
import com.example.demojwtauth.entity.UserProfile;
import com.example.demojwtauth.repository.PostRepository;
import com.example.demojwtauth.repository.UserProfileRepository;
import com.example.demojwtauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserProfileRepository userProfileRepository;
    @PostMapping("/create")
    public Post createPost(@RequestBody Post post){
        System.out.println(getPostsByAuthUserProfile());
        post.setUserProfile(getUserAuthProfile());
        return postRepository.save(post);
    }
    @GetMapping("/authuserprofile")
    public List<Post> getPostsByAuthUserProfile(){
        UserProfile userProfile = getUserAuthProfile();

        return postRepository.findPostsByUserProfile(userProfile);
    }
    @PutMapping("/update/{postId}")
    public Post updatePost(@PathVariable Long postId,
                           @Valid @RequestBody Post updatePost){
        return postRepository.findById(postId).map(post -> {
            post.setTitle(updatePost.getTitle());
            post.setContent(updatePost.getContent());
            post.setDescription(updatePost.getDescription());
            return postRepository.save(post);
        }).orElseThrow(()-> new NoSuchElementException());
    }
    private UserProfile getUserAuthProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername()).get();
        UserProfile userProfile = userProfileRepository.findUserProfileByUser(user);
        return userProfile;
    }
}
