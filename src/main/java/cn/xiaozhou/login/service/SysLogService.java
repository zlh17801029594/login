package cn.xiaozhou.login.service;

import cn.xiaozhou.login.model.SysLogs;

public interface SysLogService {
    void save(SysLogs sysLogs);

    void save(Long userId, String module, Boolean flag, String remark);

    void deleteLogs();
}
