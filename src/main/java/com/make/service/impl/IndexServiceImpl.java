package com.make.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.make.mapper.IndexMapper;
import com.make.entity.LineTrend;
import com.make.entity.NocvData;
import com.make.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexServiceImpl extends ServiceImpl<IndexMapper, NocvData> implements IndexService {

    @Autowired
    private IndexMapper indexMapper;

    @Override
    public List<LineTrend> findSevenData() {
        List<LineTrend> list7 = indexMapper.findSevenData();
        return list7;
    }
}
