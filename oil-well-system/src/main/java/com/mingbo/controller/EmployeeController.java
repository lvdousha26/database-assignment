package com.mingbo.controller;


import com.mingbo.pojo.*;
import com.mingbo.service.AuthorityService;
import com.mingbo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * 员工管理
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;

    /**
     * 新增员工
     * @param user
     * @return
     */
    @PostMapping
    public Result save(@RequestBody User user) {
        log.info("新增用户：{}", user);
        String username = user.getUsername();
        if (user.getStatus() == null){
            return Result.error("用户状态不能为空");
        }
        User userObjct = userService.selectByName(username);
        if (userObjct != null) {
            return Result.error("用户名已存在");
        }
        userService.addUser(user);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param userDTO
     * @return
     */
    @PostMapping("/page")
    public Result page(@RequestBody UserDTO userDTO) {
        log.info("用户分页查询，参数为：{}", userDTO);
        PageResult<?> pageResult = authorityService.getAuthorizedUserByPage(userDTO.getUsername(), userDTO.getPageSize(), userDTO.getCurrentPage());
        return Result.success(pageResult);
    }

    /**
     * 分页查询所有员工
     * @param userDTO
     * @return
     */
    @PostMapping("/list")
    public Result selectAll(@RequestBody UserDTO userDTO) {
        log.info("用户分页查询，参数为：{}", userDTO);
        PageResult<?> pageResult = authorityService.getAuthorizedUserByPage(userDTO.getUsername(), userDTO.getPageSize(), userDTO.getCurrentPage());
        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @PutMapping("/status/{status}/{id}")
    public Result startOrStop(@PathVariable Integer status,@PathVariable Long id) {
        log.info("启用禁用用户账号：{},{}", status, id);
        authorityService.setStatus(status, id);
        // userService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        log.info("根据id查询用户信息：{}", id);
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 编辑员工信息
     * @param user
     * @return
     */
    @PutMapping
    public Result update(@RequestBody User user) {
        log.info("编辑用户信息：{}", user);
        String username = user.getUsername();
        User userObjct = userService.selectByName(username);
        if (userObjct != null && !userObjct.getId().equals(user.getId())) {
            return Result.error("用户名已存在");
        }
        userService.updateUserAvatar(user);
        return Result.success();
    }

    /**
     * 批量删除员工信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result deleteByIds(@RequestBody Long[] ids) {
        log.info("删除用户信息：{}", ids);
        userService.deleteByIds(ids);
        return Result.success();
    }

    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Long id) {
        log.info("删除用户信息：{}", id);
        userService.deleteById(id);
        return Result.success();
    }
}