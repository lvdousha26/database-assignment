package com.mingbo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasswordDTO {

    private String old_pwd;

    private String new_pwd;

    private String re_pwd;

    private String username;


}
