package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.Menu;
import com.make.entity.Role;
import com.make.service.MenuService;
import com.make.service.RoleService;
import com.make.utils.TreeNode;
import com.make.vo.DataView;
import com.make.vo.RoleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @RequestMapping("/toRole")
    public String toRole() {
        return "role/role";
    }

    @RequestMapping("/loadAllRole")
    @ResponseBody
    public DataView loadAllRole(RoleVo roleVo) {
        IPage<Role> page = new Page<>(roleVo.getPage(), roleVo.getLimit());
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(roleVo.getName()), "name", roleVo.getName());
        queryWrapper.like(StringUtils.isNotBlank(roleVo.getRemark()), "remark", roleVo.getRemark());
        roleService.page(page, queryWrapper);
        return new DataView(page.getTotal(), page.getRecords());
    }

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    @RequestMapping("/addRole")
    @ResponseBody
    public DataView addRole(Role role) {
        roleService.save(role);
        DataView dataView = new DataView();
        dataView.setMsg("添加角色成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 修改角色
     *
     * @param role
     * @return
     */
    @RequestMapping("/updateRole")
    @ResponseBody
    public DataView updateRole(Role role) {
        roleService.updateById(role);
        DataView dataView = new DataView();
        dataView.setMsg("修改角色成功");
        dataView.setCode(200);
        return dataView;
    }


    /**
     * 删除角色成功
     *
     * @param role
     * @return
     */
    @RequestMapping("/deleteRole")
    @ResponseBody
    public DataView deleteRole(Role role) {
        roleService.removeById(role.getId());
        DataView dataView = new DataView();
        dataView.setMsg("删除角色成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 初始化下拉列表具有权限
     */
    @RequestMapping("/initPermissionByRoleId")
    @ResponseBody
    public DataView initPermissionByRoleId(Integer roleId) {
        // 1.把所有的菜单权限查出来
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        List<Menu> allPermissions = menuService.list();
        // 2.根据角色rid查询所有的菜单mid
        List<Integer> currentRolePermissions = roleService.queryAllPermissionByRid(roleId);
        // 3.rid mid 所有的ID 去查询菜单和角色的数据
        List<Menu> menus = null;
        // 4.查询到 mid rid
        if (currentRolePermissions.size() > 0) {
            queryWrapper.in("id", currentRolePermissions);
            menus = menuService.list(queryWrapper);
        } else {
            menus = new ArrayList<>();
        }
        // 5.返回前台的格式，带有层级关系
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Menu allPermission : allPermissions) {
            String checkArr = "0";
            for (Menu currentRolePermission : menus) {
                if (allPermission.getId().equals(currentRolePermission.getId())) {
                    checkArr = "1";
                    break;
                }
            }
            Boolean spread = (allPermission.getOpen() == null || allPermission.getOpen() == 1) ? true : false;
            treeNodes.add(new TreeNode(allPermission.getId(), allPermission.getPid(), allPermission.getTitle(), spread, checkArr));
        }
        return new DataView(treeNodes);
    }

    @RequestMapping("/saveRolePermission")
    @ResponseBody
    public DataView saveRolePermission(Integer rid, Integer[] mids) {
        // 1. 删除之前所有的mid权限
        roleService.deleteRoleByRid(rid);
        // 2.保存权限
        if (mids != null && mids.length > 0) {
            for (Integer mid : mids){
                roleService.saveRoleMenu(rid, mid);
            }
        }
        DataView dataView = new DataView();
        dataView.setCode(200);
        dataView.setMsg("菜单权限分配成功");
        return dataView;
    }
}
