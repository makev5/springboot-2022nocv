package com.make.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.make.entity.NocvNews;
import com.make.mapper.NocvNewsMapper;
import com.make.service.NocvNewsService;
import org.springframework.stereotype.Service;

@Service
public class NocvNewsServiceImpl extends ServiceImpl<NocvNewsMapper, NocvNews> implements NocvNewsService {
}
