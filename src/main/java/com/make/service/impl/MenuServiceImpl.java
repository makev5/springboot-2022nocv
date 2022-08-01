package com.make.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.make.entity.Menu;
import com.make.mapper.MenuMapper;
import com.make.service.MenuService;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
}
