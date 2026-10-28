package com.example.postapi;

import jakarta.validation.constraints.NotBlank;

public class Post {

    private Integer id;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    private String platform;

    public Post() {
    }

    public Post(Integer id, String content, String platform) {
        this.id = id;
        this.content = content;
        this.platform = platform;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}