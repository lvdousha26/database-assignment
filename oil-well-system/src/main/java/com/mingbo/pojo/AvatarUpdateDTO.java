package com.mingbo.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AvatarUpdateDTO {
    private MultipartFile file; // 上传的文件
    private String username; // 用户名


}
