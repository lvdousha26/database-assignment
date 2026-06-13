package com.oilwell.controller;

import com.oilwell.pojo.Result;
import com.oilwell.pojo.User;
import com.oilwell.service.UserService;
import com.oilwell.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestParam String username,
                        @RequestParam String password) {
        User user = userService.getByUsernameAndPassword(username, password);
        if (user != null) {
            if (user.getStatus() != null && user.getStatus() == 0) {
                return Result.error("账户被禁用");
            }
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("id", user.getId());
            claims.put("role", user.getRole());
            String jwt = JwtUtils.generateJwt(claims);
            return Result.success(jwt);
        }
        return Result.error("账号或者密码错误");
    }

    @PostMapping("/register")
    public Result register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam(defaultValue = "普通用户") String role) {
        User user = userService.getByUsernameAndPassword(username, password);
        if (user != null) {
            return Result.error("用户已存在");
        }
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setRole(role);
        newUser.setStatus(1);
        userService.addUser(newUser);
        return Result.success();
    }
}
