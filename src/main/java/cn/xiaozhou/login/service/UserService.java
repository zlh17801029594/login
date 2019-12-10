package cn.xiaozhou.login.service;

import cn.xiaozhou.login.dto.UserDto;
import cn.xiaozhou.login.model.SysUser;

public interface UserService {
    SysUser saveUser(UserDto userDto);

    SysUser updateUser(UserDto userDto);

    SysUser getUser(String username);

    void changePassword(String username, String oldPassword, String newPassword);
}
