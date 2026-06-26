package com.mingbo.controller;

import com.mingbo.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@RestController
@RequestMapping("/uploads")
@Slf4j
public class CommonController {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"
    );

    @Value("${server.base-url:http://localhost:8080}")
    private String baseUrl;

    @PostMapping("/avatar")
    public Result upload(MultipartFile file) {
        log.info("文件上传：{}", file.getOriginalFilename());

        String error = validateFile(file);
        if (error != null) {
            return Result.error(error);
        }

        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("创建uploads文件夹失败", e);
            }
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败", e);
        }

        return Result.success(baseUrl + "/" + uploadDir + fileName);
    }

    public static String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空";
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return "文件大小不能超过 10MB";
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            return "不支持的文件类型，仅允许上传图片文件";
        }
        String originalName = file.getOriginalFilename();
        if (originalName != null) {
            String ext = originalName.toLowerCase();
            boolean validExt = ALLOWED_EXTENSIONS.stream().anyMatch(ext::endsWith);
            if (!validExt) {
                return "不支持的文件扩展名，仅允许上传图片文件";
            }
        }
        return null;
    }
}
