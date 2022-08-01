package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.Menu;
import com.make.entity.User;
import com.make.service.MenuService;
import com.make.service.RoleService;
import com.make.utils.TreeNode;
import com.make.utils.TreeNodeBuilder;
import com.make.vo.DataView;
import com.make.vo.MenuVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;

    @RequestMapping("/toMenu")
    public String toMenu() {
        return "menu/menu";
    }


    @RequestMapping("/loadAllMenu")
    @ResponseBody
    public DataView loadAllMenu(MenuVo menuVo) {
        IPage<Menu> page = new Page<>(menuVo.getPage(), menuVo.getLimit());
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(menuVo.getTitle()), "title", menuVo.getTitle());
        queryWrapper.orderByDesc("ordernum");
        menuService.page(page, queryWrapper);
        return new DataView(page.getTotal(), page.getRecords());
    }

    /**
     * 加载下拉菜单
     */
    @RequestMapping("/loadMenuManagerLeftTreeJson")
    @ResponseBody
    public DataView loadMenuManagerLeftTreeJson() {
        List<Menu> list = menuService.list();
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Menu menu : list) {
            Boolean open = menu.getOpen() == 1 ? true : false;
            treeNodes.add(new TreeNode(menu.getId(), menu.getPid(), menu.getTitle(), open));
        }
        return new DataView(treeNodes);
    }

    /**
     * 赋值最大的排序值+1
     */
    @RequestMapping("/loadMenuMaxOrderNum")
    @ResponseBody
    public Map<String, Object> loadMenuMaxOrderNum() {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("ordernum");
        queryWrapper.last("limit 1");
        List<Menu> list = menuService.list(queryWrapper);
        map.put("value", list.get(0).getOrdernum() + 1);
        return map;
    }

    /**
     * 新增菜单
     *
     * @return
     */
    @RequestMapping("/addMenu")
    @ResponseBody
    public DataView addMenu(Menu menu) {
        DataView dataView = new DataView();
        menu.setType("menu");
        boolean save = menuService.save(menu);
        if (!save) {
            dataView.setCode(100);
            dataView.setMsg("数据插入失败");
            return dataView;
        }
        dataView.setCode(200);
        dataView.setMsg("菜单插入成功");
        return dataView;
    }

    /**
     * 更新菜单
     *
     * @return
     */
    @RequestMapping("/updateMenu")
    @ResponseBody
    public DataView updateMenu(Menu menu) {
        menuService.updateById(menu);
        DataView dataView = new DataView();
        dataView.setCode(200);
        dataView.setMsg("更新菜单成功");
        return dataView;
    }

    /**
     * 判断是否有子菜单
     */
    @RequestMapping("/checkMenuHasChildrenNode")
    @ResponseBody
    public Map<String, Object> checkChildren(Menu menu) {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", menu.getPid());
        List<Menu> list = menuService.list(queryWrapper);
        if (list.size() > 0) {
            map.put("value", true);
        } else {
            map.put("value", false);
        }
        return map;
    }

    /**
     * 删除菜单
     *
     * @param menu
     * @return
     */
    @RequestMapping("/deleteMenu")
    @ResponseBody
    public DataView deleteMenu(Menu menu) {
        DataView dataView = new DataView();
        menuService.removeById(menu.getId());
        dataView.setCode(200);
        dataView.setMsg("删除成功");
        return dataView;
    }

    /**
     * 加载主页面菜单栏,带有层级关系
     */
    @RequestMapping("/loadIndexLeftMenuJson")
    @ResponseBody
    public DataView loadIndexLeftMenuJson(Menu menu, HttpSession session) {


        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        List<Menu> list = null;

        // 1.取出session中的用户ID
        User user = (User) session.getAttribute("user");
        Integer userId = user.getId();
        // 管理员查询所有
        if (user.getUsername().equals("admin") || StringUtils.equals(user.getUsername(), "admin")){
            list = menuService.list();
        } else {
            // 2.根据用户ID查询角色ID
            List<Integer> currentUserRoleIds = roleService.queryUserRoleById(userId);
            // 3去重
            Set<Integer> mids = new HashSet<>();
            for (Integer rid : currentUserRoleIds) {
                // 3.1.根据角色ID查询菜单ID
                List<Integer> permissonIds = roleService.queryAllPermissionByRid(rid);
                // 3.2.菜单栏ID和角色ID去重
                mids.addAll(permissonIds);
            }
            // 4.根据角色ID查询菜单ID
            if (mids.size() > 0) {
                queryWrapper.in("id", mids);
                list = menuService.list(queryWrapper);
            }
        }


        // 5.构造层级关系
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Menu m : list) {
            Integer id = m.getId();
            Integer pid = m.getPid();
            String title = m.getTitle();
            String icon = m.getIcon();
            String href = m.getHref();
            Boolean open = m.getOpen().equals(1) ? true : false;
            treeNodes.add(new TreeNode(id, pid, title, icon, href, open));
        }
        // 层级关系
        List<TreeNode> nodeList = TreeNodeBuilder.build(treeNodes, 0);
        return new DataView(nodeList);
    }

}
