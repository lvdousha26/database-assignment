package com.oilwell.service.impl;

import com.oilwell.mapper.UserMapper;
import com.oilwell.pojo.User;
import com.oilwell.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getByUsernameAndPassword(String username, String password) {
        return userMapper.selectByUsernameAndPassword(username, password);
    }

    @Override
    public void addUser(User user) {
        userMapper.insert(user);
    }
}
