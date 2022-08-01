package com.make.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.make.entity.Vaccine;
import com.make.mapper.VaccineMapper;
import com.make.service.VaccineService;
import org.springframework.stereotype.Service;

@Service
public class VaccineServiceImpl extends ServiceImpl<VaccineMapper, Vaccine> implements VaccineService {

}
