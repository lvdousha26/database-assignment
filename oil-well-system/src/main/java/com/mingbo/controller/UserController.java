package com.mingbo.controller;

import com.mingbo.anno.RateLimit;
import com.mingbo.pojo.Result;
import com.mingbo.pojo.User;
import com.mingbo.service.UserService;
import com.mingbo.util.JwtUtils;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @RateLimit(maxRequests = 5, windowSeconds = 60)
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

    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        String role = getRoleFromToken(request);
        if (!"管理员".equals(role)) {
            return Result.error("无权限");
        }
        List<User> users = userService.listAll();
        // 清除密码字段
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    @PutMapping("/{id}/role")
    public Result updateRole(@PathVariable Long id,
                             @RequestBody Map<String, String> body,
                             HttpServletRequest request) {
        String role = getRoleFromToken(request);
        if (!"管理员".equals(role)) {
            return Result.error("无权限");
        }
        userService.updateRole(id, body.get("role"));
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result updateStatus(@PathVariable Long id,
                               @RequestBody Map<String, Integer> body,
                               HttpServletRequest request) {
        String role = getRoleFromToken(request);
        if (!"管理员".equals(role)) {
            return Result.error("无权限");
        }
        userService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    private String getRoleFromToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            return null;
        }
        try {
            Map<String, Claim> claims = JwtUtils.parseJWT(token);
            return claims.get("role").asString();
        } catch (Exception e) {
            return null;
        }
    }
}
