package com.make.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.make.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<Integer> queryAllPermissionByRid(Integer roleId);

    void deleteRoleByRid(Integer rid);

    void saveRoleMenu(Integer rid, Integer mid);

    List<Integer> queryUserRoleById(Integer id);
}
