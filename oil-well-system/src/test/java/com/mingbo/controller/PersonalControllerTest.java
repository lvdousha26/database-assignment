package com.mingbo.controller;

import com.mingbo.pojo.PasswordDTO;
import com.mingbo.pojo.Result;
import com.mingbo.pojo.User;
import com.mingbo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PersonalControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PersonalController personalController;

    private MockMvc mockMvc;

    @TempDir
    Path tempDir;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(personalController).build();
        ReflectionTestUtils.setField(personalController, "uploadDir", tempDir.toString());

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
    @DisplayName("查询用户信息 - 成功")
    void selectByUsername_ShouldReturnUser_WhenUserExists() throws Exception {
        when(userService.selectByName("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/personal/userinfo")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.avatar").value("/assets/avatar.jpg"))
                .andExpect(jsonPath("$.data.background").value("/assets/bg.jpg"));

        verify(userService).selectByName("testuser");
    }

    @Test
    @DisplayName("查询用户信息 - 用户不存在")
    void selectByUsername_ShouldReturnError_WhenUserNotFound() throws Exception {
        when(userService.selectByName("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/personal/userinfo")
                        .param("username", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("-1"));

        verify(userService).selectByName("nonexistent");
    }

    @Test
    @DisplayName("更新用户信息 - 成功")
    void userUpdateInfo_ShouldSucceed() throws Exception {
        String json = """
                {
                    "username": "testuser",
                    "avatar": "/assets/new-avatar.jpg"
                }
                """;

        mockMvc.perform(put("/personal/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1"));

        verify(userService).updateUserAvatar(any(User.class));
    }

    @Test
    @DisplayName("上传头像 - 成功")
    void uploadAvatar_ShouldSucceed() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "fake-image-content".getBytes());

        mockMvc.perform(multipart("/personal/upload")
                        .file(file)
                        .param("type", "avatar")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").isString());

        verify(userService).updateUserAvatar(any(User.class));
    }

    @Test
    @DisplayName("上传背景图 - 成功")
    void uploadBackground_ShouldSucceed() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "bg.png", MediaType.IMAGE_PNG_VALUE, "fake-image-content".getBytes());

        mockMvc.perform(multipart("/personal/upload")
                        .file(file)
                        .param("type", "background")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1"))
                .andExpect(jsonPath("$.data").isString());

        verify(userService).updateUserAvatar(any(User.class));
    }

    @Test
    @DisplayName("上传文件 - 文件为空返回错误")
    void upload_ShouldReturnError_WhenFileEmpty() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "", MediaType.IMAGE_PNG_VALUE, new byte[0]);

        mockMvc.perform(multipart("/personal/upload")
                        .file(emptyFile)
                        .param("type", "avatar")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.msg").value("文件为空"));
    }

    @Test
    @DisplayName("修改用户名 - 成功")
    void updateUsername_ShouldSucceed() throws Exception {
        String json = """
                {
                    "oldUsername": "oldname",
                    "newUsername": "newname"
                }
                """;

        mockMvc.perform(post("/personal/updateUsername")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1"));

        verify(userService).updateUsername("oldname", "newname");
    }

    @Test
    @DisplayName("修改用户名 - 参数为空返回错误")
    void updateUsername_ShouldReturnError_WhenParamsInvalid() throws Exception {
        String json = """
                {
                    "oldUsername": "oldname",
                    "newUsername": ""
                }
                """;

        mockMvc.perform(post("/personal/updateUsername")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("-1"))
                .andExpect(jsonPath("$.msg").value("参数错误"));

        verify(userService, never()).updateUsername(anyString(), anyString());
    }

    @Test
    @DisplayName("修改密码 - 成功")
    void updatePassword_ShouldSucceed() throws Exception {
        when(userService.getByUsernameAndPassword("testuser", "123456")).thenReturn(testUser);

        String json = """
                {
                    "username": "testuser",
                    "old_pwd": "123456",
                    "new_pwd": "654321",
                    "re_pwd": "654321"
                }
                """;

        mockMvc.perform(post("/personal/updatepwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1"));

        verify(userService).updatePasswordByUsername(any(PasswordDTO.class));
    }

    @Test
    @DisplayName("修改密码 - 旧密码错误")
    void updatePassword_ShouldReturnError_WhenOldPasswordWrong() throws Exception {
        when(userService.getByUsernameAndPassword("testuser", "wrongpwd")).thenReturn(null);

        String json = """
                {
                    "username": "testuser",
                    "old_pwd": "wrongpwd",
                    "new_pwd": "654321",
                    "re_pwd": "654321"
                }
                """;

        mockMvc.perform(post("/personal/updatepwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("-1"));

        verify(userService, never()).updatePasswordByUsername(any());
    }
}
