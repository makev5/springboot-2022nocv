package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.HeSuan;
import com.make.service.HeSuanService;
import com.make.vo.DataView;
import com.make.vo.HeSuanVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hesuan")
public class HeSuanController {

    @Autowired
    private HeSuanService heSuanService;

    @RequestMapping("/toHeSuan")
    public String toHeSuan(){
        return "hesuan/hesuan";
    }

    @RequestMapping("/loadAllHeSuan")
    @ResponseBody
    public DataView loadAllHeSuan(HeSuanVo heSuanVo){
        QueryWrapper<HeSuan> queryWrapper = new QueryWrapper<>();
        IPage<HeSuan> page = new Page<>(heSuanVo.getPage(), heSuanVo.getLimit());
        queryWrapper.like(StringUtils.isNotBlank(heSuanVo.getName()), "name", heSuanVo.getName());
        heSuanService.page(page, queryWrapper);
        return new DataView(page.getTotal(), page.getRecords());
    }

    /**
     * 添加核酸
     * @param heSuan
     * @return
     */
    @RequestMapping("/addHeSuan")
    @ResponseBody
    public DataView addHeSuan(HeSuan heSuan){
        heSuanService.save(heSuan);
        DataView dataView = new DataView();
        dataView.setMsg("添加成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 更新核酸
     * @param heSuan
     * @return
     */
    @RequestMapping("/updateHeSuan")
    @ResponseBody
    public DataView updateHeSuan(HeSuan heSuan){
        heSuanService.updateById(heSuan);
        DataView dataView = new DataView();
        dataView.setMsg("更新成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 删除核酸
     * @param id
     * @return
     */
    @RequestMapping("/deleteHeSuan")
    @ResponseBody
    public DataView deleteHeSuan(Integer id){
        heSuanService.removeById(id);
        DataView dataView = new DataView();
        dataView.setMsg("删除成功");
        dataView.setCode(200);
        return dataView;
    }
}
