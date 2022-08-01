package com.make.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.make.mapper.ChinaTotalMapper;
import com.make.entity.ChinaTotal;
import com.make.service.ChinaTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChinaTotalServiceImpl extends ServiceImpl<ChinaTotalMapper, ChinaTotal> implements ChinaTotalService {

    @Autowired
    private ChinaTotalMapper chinaTotalMapper;

    @Override
    public Integer maxID() {
        return chinaTotalMapper.maxID();
    }
}
