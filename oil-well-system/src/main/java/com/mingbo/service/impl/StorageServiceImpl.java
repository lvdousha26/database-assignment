package com.mingbo.service.impl;

import com.mingbo.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件存储业务类
 */
@Service
public class StorageServiceImpl implements StorageService {

    // 本地存储路径配置
    private final Path referenceRoot;
    private final Path userRoot;

    public StorageServiceImpl() {
        // 默认存储路径（可改为通过@Value注入）
        this.referenceRoot = Paths.get("./uploads/reference");
        this.userRoot = Paths.get("./uploads/user");

        // 确保目录存在
        try {
            Files.createDirectories(referenceRoot);
            Files.createDirectories(userRoot);
        } catch (IOException e) {
            throw new RuntimeException("无法创建存储目录", e);
        }
    }

    @Override
    public String saveUserFile(MultipartFile file, int userId) throws IOException {
        // 按用户ID创建子目录
        Path userDir = userRoot.resolve(String.valueOf(userId));
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }
        return saveToLocal(file, userDir);
    }

    @Override
    public InputStream load(String filePath) throws IOException {
        return Files.newInputStream(Paths.get(filePath));
    }

    @Override
    public void deleteReferenceFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    // ============ 私有工具方法 ============
    private String saveToLocal(MultipartFile file, Path basePath) throws IOException {
        // 生成唯一文件名（保留扩展名）
        String originalFilename = file.getOriginalFilename();
        String extension = null;
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID() + extension;

        // 保存文件
        Path destination = basePath.resolve(newFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        // 返回文件存储绝对路径
        return destination.toAbsolutePath().toString();
    }
}
