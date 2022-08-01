package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.BanJi;
import com.make.entity.Role;
import com.make.entity.User;
import com.make.entity.XueYuan;
import com.make.service.BanJiService;
import com.make.service.RoleService;
import com.make.service.UserService;
import com.make.service.XueYuanService;
import com.make.vo.DataView;
import com.make.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BanJiService banJiService;

    @Autowired
    private XueYuanService xueYuanService;

    @Autowired
    private RoleService roleService;

    @RequestMapping("/toUser")
    public String toUser() {
        return "user/user";
    }

    @RequestMapping("/toUserInfo")
    public String toUserInfo() {
        return "user/userInfo";
    }

    @RequestMapping("/toChangePassword")
    public String toChangePassword() {
        return "user/changepassword";
    }

    /**
     * 分页查询所有用户
     *
     * @param userVo
     * @return
     */
    @RequestMapping("/loadAllUser")
    @ResponseBody
    public DataView loadAllUser(UserVo userVo) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        IPage<User> page = new Page<>(userVo.getPage(), userVo.getLimit());
        queryWrapper.like(StringUtils.isNotBlank(userVo.getUsername()), "username", userVo.getUsername());
        queryWrapper.eq(StringUtils.isNotBlank(userVo.getPhone()), "phone", userVo.getPhone());

        IPage<User> iPage = userService.page(page, queryWrapper);

        for (User user : iPage.getRecords()) {
            // 为班级名称赋值
            if (user.getBanJiId() != null) {
                BanJi banji = banJiService.getById(user.getBanJiId());
                user.setBanJiName(banji.getName());
            }
            // 为学院名称赋值
            if (user.getXueYuanId() != null) {
                XueYuan xueYuan = xueYuanService.getById(user.getXueYuanId());
                user.setXueYuanName(xueYuan.getName());
            }
            // 为老师名称赋值
            if (user.getTeacherId() != null) {
                User teacher = userService.getById(user.getTeacherId());
                user.setTeacherName(teacher.getUsername());
            }
        }
        return new DataView(iPage.getTotal(), iPage.getRecords());
    }

    /**
     * 新增用户
     */
    @RequestMapping("/addUser")
    @ResponseBody
    public DataView addUser(User user){
        boolean save = userService.save(user);
        DataView dataView = new DataView();
        dataView.setMsg("添加用户成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 初始化拉下列表的数据 班级
     */
    @RequestMapping("/listAllBanJI")
    @ResponseBody
    public List<BanJi> listAllBanJI(){
        List<BanJi> list = banJiService.list();
        return list;
    }

    /**
     * 初始化拉下列表的数据 学院
     */
    @RequestMapping("/listAllXueYuan")
    @ResponseBody
    public List<XueYuan> listAllXueYuan(){
        List<XueYuan> list = xueYuanService.list();
        return list;
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @RequestMapping("/updateUser")
    @ResponseBody
    public DataView updateUser(User user){
        userService.updateById(user);
        DataView dataView = new DataView();
        dataView.setMsg("编辑用户成功");
        dataView.setCode(200);
        return dataView;
    }

    @RequestMapping("/deleteUser/{id}")
    @ResponseBody
    public DataView deleteUser(@PathVariable("id") Integer id){
        userService.removeById(id);
        DataView dataView = new DataView();
        dataView.setMsg("删除用户成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 重置密码成功
     * @param id
     * @return
     */
    @RequestMapping("/resetPwd/{id}")
    @ResponseBody
    public DataView resetPwd(@PathVariable("id") Integer id){
        User user = new User();
        user.setPassword("123456");
        user.setId(id);
        userService.updateById(user);
        DataView dataView = new DataView();
        dataView.setMsg("重置密码成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 初始化用户角色
     * @return
     */
    @RequestMapping("/initRoleByUserId")
    @ResponseBody
    public DataView initRoleByUserId(Integer id){
        // 1.查询所有角色
        List<Map<String, Object>> maps = roleService.listMaps();
        // 2.查询当前登录用户所拥有的角色
        List<Integer> currentUserRoleIds = roleService.queryUserRoleById(id);
        // 3.让前端变为选中
        for (Map<String,Object> map : maps){
            Boolean LAY_CHECKED = false;
            Integer roleId = (Integer) map.get("id");
            for (Integer rid : currentUserRoleIds){
                if (rid.equals(roleId)){
                    LAY_CHECKED= true;
                    break;
                }
            }
            map.put("LAY_CHECKED", LAY_CHECKED);

        }
        return new DataView(Long.valueOf(maps.size()), maps);
    }

    @RequestMapping("/saveUserRole")
    @ResponseBody
    public DataView saveUserRole(Integer uid, Integer[] ids){
        userService.saveUserRole(uid, ids);
        DataView dataView = new DataView();
        dataView.setMsg("角色分配成功");
        dataView.setCode(200);
        return dataView;
    }

}
