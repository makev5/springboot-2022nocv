package com.make.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.make.entity.ChinaTotal;

public interface ChinaTotalService extends IService<ChinaTotal> {
    Integer maxID();

}
