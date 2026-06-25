package com.mingbo.service.impl;

import com.mingbo.mapper.UserMapper;
import com.mingbo.pojo.PasswordDTO;
import com.mingbo.pojo.User;
import com.mingbo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectByName(String username) {
        return userMapper.selectByName(username);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getUserPublicInfo(long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User getByUsernameAndPassword(String username, String password) {
        return userMapper.selectByUsernameAndPassword(username, password);
    }

    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public void updateUserAvatar(User user) {
        userMapper.updateUserAvatarByUsername(user);
    }

    @Override
    public void updateUsername(String oldUsername, String newUsername) {
        userMapper.updateUsername(oldUsername, newUsername);
    }

    @Override
    public void updatePasswordByUsername(PasswordDTO passwordDTO) {
        userMapper.updatePasswordByUsername(passwordDTO);
    }

    @Override
    public void deleteByIds(Long[] ids) {
        userMapper.deleteBatchIds(ids);
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public List<User> listAll() {
        return userMapper.listAll();
    }

    @Override
    public void updateRole(Long id, String role) {
        userMapper.updateRole(id, role);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }
}
