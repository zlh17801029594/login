package cn.xiaozhou.login.service.impl;

import cn.xiaozhou.login.dao.SysLogsDao;
import cn.xiaozhou.login.model.SysLogs;
import cn.xiaozhou.login.model.SysUser;
import cn.xiaozhou.login.service.SysLogService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SysLogServiceImpl implements SysLogService {

    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private SysLogsDao sysLogsDao;

    @Async
    @Override
    public void save(SysLogs sysLogs) {
        if (sysLogs == null || sysLogs.getUser() == null || sysLogs.getUser().getId() == null) {
            return;
        }
        sysLogsDao.save(sysLogs);
    }

    @Async
    @Override
    public void save(Long userId, String module, Boolean flag, String remark) {
        SysLogs sysLogs = new SysLogs();
        sysLogs.setFlag(flag);
        sysLogs.setModule(module);
        sysLogs.setRemark(remark);
        SysUser user = new SysUser();
        user.setId(userId);
        sysLogs.setUser(user);
        sysLogsDao.save(sysLogs);
    }

    @Override
    public void deleteLogs() {
        Date date = DateUtils.addMonths(new Date(), -3);
        String time = DateFormatUtils.format(date, DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern());
        int n = sysLogsDao.deleteLogs(time);
        log.info("删除{}之前日志{}条", time, n);
    }
}
