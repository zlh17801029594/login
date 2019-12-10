package cn.xiaozhou.login.dto;

import cn.xiaozhou.login.model.SysUser;

import java.util.List;

public class UserDto extends SysUser {
    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
