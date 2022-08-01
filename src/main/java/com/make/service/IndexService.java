package com.make.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.make.entity.LineTrend;
import com.make.entity.NocvData;

import java.util.List;

public interface IndexService extends IService<NocvData> {

    List<LineTrend> findSevenData();
}
