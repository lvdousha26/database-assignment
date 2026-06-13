package com.mingbo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 存储业务类
 */
public interface StorageService {

    /**
     * 保存用户上传文件到本地（按用户ID分目录）
     * @param file 上传的文件
     * @param userId 上传人id
     * @return 上传最终路径
     * @throws IOException 文件流出错抛出此异常，如文件路径创建失败
     */
    String saveUserFile(MultipartFile file, int userId) throws IOException;

    /**
     * 加载文件（返回输入流）
     * @param filePath 加载文件路径
     * @return 文件流
     * @throws IOException 文件流出错时抛出，如文件不存在
     */
    InputStream load(String filePath) throws IOException;

    /**
     * 删除参考资源
     * @param filePath 要删除的文件路径
     */
    void deleteReferenceFile(String filePath) throws IOException;
}
