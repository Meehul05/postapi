package com.example.postapi;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final List<Post> posts = new ArrayList<>();
    private int nextId = 1;

    // GET - Read all posts
    @GetMapping
    public ApiResponse<List<Post>> getAllPosts() {
        return new ApiResponse<>(
                true,
                "Posts retrieved successfully",
                posts);
    }

    // GET - Read one post
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> getPost(@PathVariable int id) {

        for (Post post : posts) {
            if (post.getId() == id) {
                return ResponseEntity.ok(
                        new ApiResponse<>(
                                true,
                                "Post retrieved successfully",
                                post));
            }
        }

        return ResponseEntity.notFound().build();
    }

    // POST - Create post
    @PostMapping
    public ApiResponse<Post> createPost(@Valid @RequestBody Post post) {

        logger.info("Creating new post"); // logging

        post.setId(nextId++);
        posts.add(post);

        return new ApiResponse<>(
                true,
                "Post created successfully",
                post);
    }

    // PUT - Update post
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Post>> updatePost(
            @PathVariable int id,
            @Valid @RequestBody Post updatedPost) {

        for (Post post : posts) {
            if (post.getId() == id) {

                post.setContent(updatedPost.getContent());
                post.setPlatform(updatedPost.getPlatform());

                return ResponseEntity.ok(
                        new ApiResponse<>(
                                true,
                                "Post updated successfully",
                                post));
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/error")
    public String testError() {
        throw new RuntimeException("Test exception");
    }

    // DELETE - Delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable int id) {

        for (Post post : posts) {
            if (post.getId() == id) {

                posts.remove(post);

                return ResponseEntity.ok(
                        new ApiResponse<>(
                                true,
                                "Post deleted successfully",
                                null));
            }
        }

        return ResponseEntity.notFound().build();
    }
}