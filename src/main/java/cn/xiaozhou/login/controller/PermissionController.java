package cn.xiaozhou.login.controller;

import cn.xiaozhou.login.annotation.LogAnnotation;
import cn.xiaozhou.login.dao.PermissionDao;
import cn.xiaozhou.login.dto.LoginUser;
import cn.xiaozhou.login.model.Permission;
import cn.xiaozhou.login.service.PermissionService;
import cn.xiaozhou.login.utils.UserUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Api(tags = "权限")
@RestController
@RequestMapping("/permissions")
public class PermissionController {
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "当前登录用户拥有的权限")
    @GetMapping("/current")
    public List<Permission> permissionsCurrent() {
        LoginUser loginUser = UserUtil.getLoginUser();
        List<Permission> list = loginUser.getPermissions();
        final List<Permission> permissions = list.stream().filter(l -> l.getType().equals(1))
                .collect(Collectors.toList());
        List<Permission> firstLevel = permissions.stream().filter(p -> p.getParentId().equals(0L))
                .collect(Collectors.toList());
        firstLevel.parallelStream().forEach(p -> {
            setChild(p, permissions);
        });
        return firstLevel;
    }

    private void setChild(Permission p, List<Permission> permissions) {
        List<Permission> child = permissions.parallelStream().filter(a -> a.getParentId().equals(p.getId()))
                .collect(Collectors.toList());
        p.setChild(child);
        if (!CollectionUtils.isEmpty(child)) {
            child.parallelStream().forEach(c -> {
                setChild(c, permissions);
            });
        }
    }

    @GetMapping
    @ApiOperation(value = "菜单列表")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public List<Permission> permissionList() {
        List<Permission> permissions = permissionDao.listAll();
        List<Permission> list = Lists.newArrayList();
        setPermissionList(0L, permissions, list);
        return list;
    }

    private void setPermissionList(Long pId, List<Permission> permissions, List<Permission> list) {
        for (Permission per : permissions) {
            if (per.getParentId().equals(pId)) {
                list.add(per);
                if (permissions.stream().filter(p -> p.getParentId().equals(per.getId())).findAny() != null) {
                    setPermissionList(per.getId(), permissions, list);
                }
            }
        }
    }

    @GetMapping("/all")
    @ApiOperation(value = "所有菜单")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public JSONArray permissionAll() {
        List<Permission> permissionAll = permissionDao.listAll();
        JSONArray jsonArray = new JSONArray();
        setPermissionTree(0L, permissionAll, jsonArray);
        return jsonArray;
    }

    private void setPermissionTree(Long pId, List<Permission> permissionAll, JSONArray jsonArray) {
        for (Permission per : permissionAll) {
            if (per.getParentId().equals(pId)) {
                String str = JSONObject.toJSONString(per);
                JSONObject parent = (JSONObject) JSONObject.parse(str);
                jsonArray.add(parent);
                if (permissionAll.stream().filter(p -> p.getParentId().equals(per.getId())).findAny() != null) {
                    JSONArray child = new JSONArray();
                    parent.put("child", child);
                    setPermissionTree(per.getId(), permissionAll, child);
                }
            }
        }
    }

    @GetMapping("/parents")
    @ApiOperation(value = "一级菜单")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public List<Permission> parentMenu() {
        List<Permission> parents = permissionDao.listParents();
        return parents;
    }

    @GetMapping(params = "roleId")
    @ApiOperation(value = "根据角色id获取权限")
    @PreAuthorize("hasAnyAuthority('sys:menu:query','sys:role:query')")
    public List<Permission> listByRoleId(Long roleId) {
        return permissionDao.listByRoleId(roleId);
    }

    @LogAnnotation
    @PostMapping
    @ApiOperation(value = "保存菜单")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    public void save(@RequestBody Permission permission) {
        permissionDao.save(permission);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据菜单id获取菜单")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    public Permission get(@PathVariable Long id) {
        return permissionDao.getById(id);
    }

    @LogAnnotation
    @PutMapping
    @ApiOperation(value = "修改菜单")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    public void update(@RequestBody Permission permission) {
        permissionService.update(permission);
    }

    @GetMapping("/owns")
    @ApiOperation(value = "校验当前用户的权限")
    public Set<String> ownsPermission() {
        List<Permission> permissionList = UserUtil.getLoginUser().getPermissions();
        if (CollectionUtils.isEmpty(permissionList)) {
            return Collections.emptySet();
        }
        return permissionList.parallelStream().filter(p -> !StringUtils.isEmpty(p.getPermission()))
                .map(Permission::getPermission).collect(Collectors.toSet());
    }

    @LogAnnotation
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除菜单")
    @PreAuthorize("hasAuthority('sys:menu:del')")
    public void delte(@PathVariable Long id) {
        permissionService.delete(id);
    }
}
