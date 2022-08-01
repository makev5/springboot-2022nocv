package com.make.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.make.entity.Role;
import com.make.mapper.RoleMapper;
import com.make.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Integer> queryAllPermissionByRid(Integer roleId) {
        return roleMapper.queryAllPermissionByRid(roleId);
    }

    @Override
    public void deleteRoleByRid(Integer rid) {
        roleMapper.deleteRoleByRid(rid);
    }

    @Override
    public void saveRoleMenu(Integer rid, Integer mid) {
        roleMapper.saveRoleMenu(rid, mid);
    }

    @Override
    public List<Integer> queryUserRoleById(Integer id) {
        return roleMapper.queryUserRoleById(id);
    }
}
