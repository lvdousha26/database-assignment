package com.mingbo.controller;


import com.mingbo.pojo.PasswordDTO;
import com.mingbo.pojo.Result;
import com.mingbo.pojo.User;
import com.mingbo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/personal")
@Slf4j
public class PersonalController {

    @Autowired
    private UserService userService;



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
        // 更新用户头像
        userService.updateUserAvatar(user);

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


