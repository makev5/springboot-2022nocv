package com.make.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.make.entity.User;
import com.make.mapper.RoleMapper;
import com.make.mapper.UserMapper;
import com.make.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User login(String username, String password) {
        return userMapper.login(username, password);
    }

    @Override
    public void saveUserRole(Integer uid, Integer[] ids) {
        roleMapper.deleteRoleUserByUid(uid);
        if (ids != null && ids.length > 0){
            for (Integer rid : ids){
                roleMapper.saveUserRole(uid, rid);
            }
        }
    }
}
