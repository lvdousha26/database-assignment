package com.mingbo.controller;


import com.mingbo.pojo.PasswordDTO;
import com.mingbo.pojo.Result;
import com.mingbo.pojo.User;
import com.mingbo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/personal")
@Slf4j
public class PersonalController {

    @Autowired
    private UserService userService;

    @Value("${upload.dir:}")
    private String uploadDir;

    private String getUploadDir() {
        if (!uploadDir.isEmpty()) return uploadDir;
        // 默认使用项目根目录下的 assets 文件夹
        String userDir = System.getProperty("user.dir");
        return userDir + File.separator + "assets";
    }

    /**
     * 根据用户名查找用户信息
     * @param username
     * @return
     */
    @RequestMapping("/userinfo")
    public Result selectByUsername(@RequestParam("username") String username) {
        User user = userService.selectByName(username);
        if (user != null) {
            return Result.success(user);
        }
        return Result.error("error");
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PutMapping("profile")
    public Result userUpdateInfo(@RequestBody User user) {
        log.info("更新用户头像信息, {}",user);
        userService.updateUserAvatar(user);
        return Result.success();
    }

    /**
     * 上传头像或背景图
     * @param file
     * @param type avatar 或 background
     * @param username
     * @return
     */
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("type") String type,
                             @RequestParam("username") String username) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        try {
            String dirPath = getUploadDir();
            // 确保 assets 目录存在
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String filename = type + "_" + username + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            File dest = new File(dir, filename);
            log.info("上传文件到: {}", dest.getAbsolutePath());
            file.transferTo(dest);

            String filePath = "/assets/" + filename;

            // 更新用户记录
            User user = new User();
            user.setUsername(username);
            if ("avatar".equals(type)) {
                user.setAvatar(filePath);
            } else if ("background".equals(type)) {
                user.setBackground(filePath);
            }
            userService.updateUserAvatar(user);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("上传失败");
        }
    }

    /**
     * 更新用户名
     * @param req
     * @return
     */
    @PostMapping("/updateUsername")
    public Result updateUsername(@RequestBody java.util.Map<String, String> req) {
        String oldUsername = req.get("oldUsername");
        String newUsername = req.get("newUsername");
        if (oldUsername == null || newUsername == null || newUsername.trim().isEmpty()) {
            return Result.error("参数错误");
        }
        userService.updateUsername(oldUsername, newUsername);
        return Result.success();
    }

    /**
     * 更新用户密码
     * @param passwordDTO
     * @return
     */
    @RequestMapping("/updatepwd")
    public Result updatePassword(@RequestBody PasswordDTO passwordDTO) {
        String old_pwd = passwordDTO.getOld_pwd();
        String username = passwordDTO.getUsername();
        String new_pwd = passwordDTO.getNew_pwd();
        String re_pwd = passwordDTO.getRe_pwd();
        User user = userService.getByUsernameAndPassword(username,old_pwd);
        if (user != null && new_pwd.equals(re_pwd)) {
            userService.updatePasswordByUsername(passwordDTO);
            return Result.success();
        }
        return Result.error("error");
    }

}


