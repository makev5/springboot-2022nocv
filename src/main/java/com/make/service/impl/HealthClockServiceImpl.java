package com.make.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.make.mapper.HealthClockMapper;
import com.make.entity.HealthClock;
import com.make.service.HealthClockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthClockServiceImpl extends ServiceImpl<HealthClockMapper, HealthClock> implements HealthClockService {

    @Autowired
    private HealthClockMapper healthClockMapper;
}
