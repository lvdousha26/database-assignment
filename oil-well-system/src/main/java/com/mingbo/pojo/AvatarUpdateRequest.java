package com.mingbo.pojo;
import org.springframework.web.multipart.MultipartFile;
public class AvatarUpdateRequest {
    private MultipartFile file; // 上传的文件
    private String username; // 用户名

    // Getters 和 Setters
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
