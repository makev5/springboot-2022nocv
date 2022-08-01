package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.HealthClock;
import com.make.service.HealthClockService;
import com.make.vo.DataView;
import com.make.vo.HealthClockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HealthClockController {

    @Autowired
    private HealthClockService healthClockService;

    @RequestMapping("/toHealthClock")
    public String HealthClock(){
        return "admin/healthclock";
    }

    @RequestMapping("/listHealthClock")
    @ResponseBody
    public DataView listHealthClock(HealthClockVo healthClockVo){
        IPage<HealthClock> page = new Page<>(healthClockVo.getPage(), healthClockVo.getLimit());
        QueryWrapper<HealthClock> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(healthClockVo.getUsername()!=null, "username", healthClockVo.getUsername());
        queryWrapper.eq(healthClockVo.getPhone()!=null, "phone", healthClockVo.getPhone());
        healthClockService.page(page, queryWrapper);

        return new DataView(page.getTotal(), page.getRecords());
    }

    /**
     * 添加或者修改健康打卡数据
     *
     */
    @RequestMapping("/addHealthClock")
    @ResponseBody
    public DataView addHealthClock(HealthClock healthClock){
        boolean b = healthClockService.saveOrUpdate(healthClock);
        DataView dataView = new DataView();
        if (b){
            dataView.setCode(200);
            dataView.setMsg("添加成功");
            return dataView;
        }
        dataView.setCode(500);
        dataView.setMsg("添加失败");
        return dataView;
    }

    /**
     * 删除健康打卡数据
     * @param id
     * @return
     */
    @RequestMapping("/deleteHealthClockById")
    @ResponseBody
    public DataView deleteHealthClockById(Integer id){
        healthClockService.removeById(id);
        DataView dataView = new DataView();
        dataView.setCode(200);
        dataView.setMsg("删除成功");
        return dataView;
    }
}
