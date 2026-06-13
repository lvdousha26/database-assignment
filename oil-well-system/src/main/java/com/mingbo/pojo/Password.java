package com.mingbo.pojo;

public class Password {

    private String old_pwd;

    private String new_pwd;

    private String re_pwd;

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOld_pwd() {
        return old_pwd;
    }

    public void setOld_pwd(String old_pwd) {
        this.old_pwd = old_pwd;
    }

    public String getNew_pwd() {
        return new_pwd;
    }

    public void setNew_pwd(String new_pwd) {
        this.new_pwd = new_pwd;
    }

    public String getRe_pwd() {
        return re_pwd;
    }

    public void setRe_pwd(String re_pwd) {
        this.re_pwd = re_pwd;
    }

    @Override
    public String toString() {
        return "Password{" +
                "old_pwd='" + old_pwd + '\'' +
                ", new_pwd='" + new_pwd + '\'' +
                ", re_pwd='" + re_pwd + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
