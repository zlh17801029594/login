package cn.xiaozhou.login.service;

import cn.xiaozhou.login.model.Permission;

public interface PermissionService {
    void save(Permission permission);

    void update(Permission permission);

    void delete(Long id);
}
