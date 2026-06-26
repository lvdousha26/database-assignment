package com.mingbo.controller;

import com.mingbo.pojo.GeneralRequestDTO;
import com.mingbo.pojo.ReferenceResource;
import com.mingbo.pojo.ReferenceUploadRequest;
import com.mingbo.pojo.Result;
import com.mingbo.service.MetaDataService;
import com.mingbo.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * 文件上传Controller类
 */
@RestController
@RequestMapping("/src")
public class ResourceController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private MetaDataService metaDataService;

    /**
     * 上传资源
     * @param request 请求上传信息，包括上传人id，文件类型，文件描述
     * @param file 上传文件
     * @return 执行情况信息
     */
    @PostMapping(value = "/reference-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result uploadReference(
            @RequestPart("metaData") ReferenceUploadRequest request,
            @RequestPart("file") MultipartFile file) {

        String error = CommonController.validateFile(file);
        if (error != null) {
            return Result.error(error);
        }

        // 保存文件到专用存储
        String refPath = null;
        try {
            refPath = storageService.saveUserFile(file, request.getId());
        } catch (IOException e) {
            Result.error(e.getMessage());
        }

        // 记录资源元数据
        ReferenceResource ref = new ReferenceResource();
        ref.setAdminId(request.getId());
        ref.setOriginalName(HtmlUtils.htmlEscape(file.getOriginalFilename()));
        ref.setStoragePath(refPath);
        ref.setResourceType(request.getResourceType());
        ref.setDescription(request.getDescription());
        ref.setUploadTime(new Timestamp(System.currentTimeMillis()));
        ref.setStatus((byte) 1);
        metaDataService.saveMetaData(ref);

        return Result.success(ref.getId());
    }

    /**
     * 查询保存的文件
     * @param referenceDTO 查询管理员id，页面号和页面项数
     * @return 查询结果
     */
    @GetMapping
    public Result getReference(GeneralRequestDTO referenceDTO) {
        try {
            return Result.success(metaDataService.getMetaDataPage(referenceDTO));
        } catch (DataAccessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param id 要删除的文件在记录中的id
     * @return 执行情况及错误信息
     */
    @Transactional
    @DeleteMapping
    public Result deleteReference(@RequestParam int id) {
        try {
            String url = metaDataService.deleteMetaData(id);
            storageService.deleteReferenceFile(url);
            return Result.success("成功执行删除");
        } catch (DataAccessException | IOException e) {
            return Result.error(e.getMessage());
        }
    }
}
