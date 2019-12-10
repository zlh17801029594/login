package cn.xiaozhou.login.service.impl;

import cn.xiaozhou.login.dao.UserDao;
import cn.xiaozhou.login.dto.UserDto;
import cn.xiaozhou.login.model.SysUser;
import cn.xiaozhou.login.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger("adminLogger");

    @Autowired
    private UserDao userDao;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    @Transactional
    public SysUser saveUser(UserDto userDto) {
        SysUser user = userDto;
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setStatus(SysUser.Status.VALID);
        userDao.save(user);
        saveUserRoles(user.getId(), userDto.getRoleIds());
        log.debug("新增用户{}", user.getUsername());
        return user;
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds != null) {
            userDao.deleteUserRole(userId);
            if (!CollectionUtils.isEmpty(roleIds)) {
                userDao.saveUserRoles(userId, roleIds);
            }
        }
    }

    @Override
    @Transactional
    public SysUser updateUser(UserDto userDto) {
        SysUser user = userDto;
        userDao.update(user);
        userDao.saveUserRoles(user.getId(), userDto.getRoleIds());
        return user;
    }

    @Override
    public SysUser getUser(String username) {
        return userDao.getUser(username);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        SysUser user = userDao.getUser(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("旧密码错误");
        }
        userDao.changePassword(user.getId(), bCryptPasswordEncoder.encode(newPassword));
        log.debug("修改{}的密码", username);
    }
}
