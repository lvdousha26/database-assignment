package com.mingbo.service;

import com.mingbo.mapper.UserMapper;
import com.mingbo.pojo.PasswordDTO;
import com.mingbo.pojo.User;
import com.mingbo.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("123456")
                .role("user")
                .gender("男")
                .phone("13800138000")
                .avatar("/assets/avatar.jpg")
                .background("/assets/bg.jpg")
                .status(1)
                .build();
    }

    @Test
    @DisplayName("通过用户名查询用户 - 成功")
    void selectByName_ShouldReturnUser_WhenUserExists() {
        when(userMapper.selectByName("testuser")).thenReturn(testUser);

        User result = userService.selectByName("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("user", result.getRole());
        verify(userMapper).selectByName("testuser");
    }

    @Test
    @DisplayName("通过用户名查询用户 - 不存在返回null")
    void selectByName_ShouldReturnNull_WhenUserNotExists() {
        when(userMapper.selectByName("nonexistent")).thenReturn(null);

        User result = userService.selectByName("nonexistent");

        assertNull(result);
        verify(userMapper).selectByName("nonexistent");
    }

    @Test
    @DisplayName("通过ID查询用户 - 成功")
    void getById_ShouldReturnUser_WhenIdExists() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        User result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userMapper).selectById(1L);
    }

    @Test
    @DisplayName("通过用户名和密码查询 - 成功")
    void getByUsernameAndPassword_ShouldReturnUser_WhenCredentialsMatch() {
        when(userMapper.selectByUsernameAndPassword("testuser", "123456")).thenReturn(testUser);

        User result = userService.getByUsernameAndPassword("testuser", "123456");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper).selectByUsernameAndPassword("testuser", "123456");
    }

    @Test
    @DisplayName("通过用户名和密码查询 - 密码错误返回null")
    void getByUsernameAndPassword_ShouldReturnNull_WhenPasswordWrong() {
        when(userMapper.selectByUsernameAndPassword("testuser", "wrongpwd")).thenReturn(null);

        User result = userService.getByUsernameAndPassword("testuser", "wrongpwd");

        assertNull(result);
        verify(userMapper).selectByUsernameAndPassword("testuser", "wrongpwd");
    }

    @Test
    @DisplayName("新增用户")
    void addUser_ShouldCallMapperInsert() {
        userService.addUser(testUser);

        verify(userMapper).insert(testUser);
    }

    @Test
    @DisplayName("更新用户头像")
    void updateUserAvatar_ShouldUpdateAvatarFieldOnly() {
        User updateUser = new User();
        updateUser.setUsername("testuser");
        updateUser.setAvatar("/assets/new-avatar.jpg");

        userService.updateUserAvatar(updateUser);

        verify(userMapper).updateUserAvatarByUsername(updateUser);
    }

    @Test
    @DisplayName("更新用户背景图")
    void updateUserAvatar_ShouldUpdateBackgroundFieldOnly() {
        User updateUser = new User();
        updateUser.setUsername("testuser");
        updateUser.setBackground("/assets/new-bg.jpg");

        userService.updateUserAvatar(updateUser);

        verify(userMapper).updateUserAvatarByUsername(updateUser);
    }

    @Test
    @DisplayName("修改用户名 - 成功")
    void updateUsername_ShouldSucceed_WhenNewUsernameNotTaken() {
        doNothing().when(userMapper).updateUsername("oldname", "newname");

        userService.updateUsername("oldname", "newname");

        verify(userMapper).updateUsername("oldname", "newname");
    }

    @Test
    @DisplayName("修改密码")
    void updatePasswordByUsername_ShouldCallMapper() {
        PasswordDTO dto = new PasswordDTO();
        dto.setUsername("testuser");
        dto.setOld_pwd("123456");
        dto.setNew_pwd("654321");
        dto.setRe_pwd("654321");

        userService.updatePasswordByUsername(dto);

        verify(userMapper).updatePasswordByUsername(dto);
    }

    @Test
    @DisplayName("批量删除用户")
    void deleteByIds_ShouldCallMapperDeleteBatch() {
        Long[] ids = {1L, 2L, 3L};

        userService.deleteByIds(ids);

        verify(userMapper).deleteBatchIds(ids);
    }

    @Test
    @DisplayName("根据ID删除用户")
    void deleteById_ShouldCallMapperDelete() {
        userService.deleteById(1L);

        verify(userMapper).deleteById(1L);
    }

    @Test
    @DisplayName("获取用户公开信息 - 委托selectById")
    void getUserPublicInfo_ShouldDelegateToSelectById() {
        when(userMapper.selectById(1L)).thenReturn(testUser);

        User result = userService.getUserPublicInfo(1L);

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
        verify(userMapper).selectById(1L);
    }
}
