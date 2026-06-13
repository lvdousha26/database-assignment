package com.oilwell.service;

import com.oilwell.pojo.User;

public interface UserService {

    User getByUsernameAndPassword(String username, String password);

    void addUser(User user);
}
