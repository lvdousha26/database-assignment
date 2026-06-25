package com.mingbo.service;

import com.mingbo.pojo.PasswordDTO;
import com.mingbo.pojo.User;

public interface UserService {

    User getByUsernameAndPassword(String username, String password);

    User selectByName(String username);

    User getById(Long id);

    User getUserPublicInfo(long id);

    void addUser(User user);

    void updateUserAvatar(User user);

    void updateUsername(String oldUsername, String newUsername);

    void updatePasswordByUsername(PasswordDTO passwordDTO);

    void deleteByIds(Long[] ids);

    void deleteById(Long id);
}
