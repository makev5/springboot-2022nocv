package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.HeSuan;
import com.make.entity.Vaccine;
import com.make.service.VaccineService;
import com.make.vo.DataView;
import com.make.vo.HeSuanVo;
import com.make.vo.VaccineVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/vaccine")
public class VaccineController {

    @Autowired
    private VaccineService vaccineService;

    @RequestMapping("/toVaccine")
    public String toVaccine(){
        return "vaccine/vaccine";
    }

    @RequestMapping("/loadAllVaccine")
    @ResponseBody
    public DataView loadAllHeSuan(VaccineVo vaccineVo){
        QueryWrapper<Vaccine> queryWrapper = new QueryWrapper<>();
        IPage<Vaccine> page = new Page<>(vaccineVo.getPage(), vaccineVo.getLimit());
        queryWrapper.like(StringUtils.isNotBlank(vaccineVo.getName()), "name", vaccineVo.getName());
        vaccineService.page(page, queryWrapper);
        return new DataView(page.getTotal(), page.getRecords());
    }

    /**
     * 添加疫苗
     * @param vaccine
     * @return
     */
    @RequestMapping("/addVaccine")
    @ResponseBody
    public DataView addVaccine(Vaccine vaccine){
        vaccineService.save(vaccine);
        DataView dataView = new DataView();
        dataView.setMsg("添加成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 更新疫苗
     * @param vaccine
     * @return
     */
    @RequestMapping("/updateVaccine")
    @ResponseBody
    public DataView updateVaccine(Vaccine vaccine){
        vaccineService.updateById(vaccine);
        DataView dataView = new DataView();
        dataView.setMsg("更新成功");
        dataView.setCode(200);
        return dataView;
    }

    /**
     * 删除疫苗
     * @param id
     * @return
     */
    @RequestMapping("/deleteVaccine")
    @ResponseBody
    public DataView deleteVaccine(Integer id){
        vaccineService.removeById(id);
        DataView dataView = new DataView();
        dataView.setMsg("删除成功");
        dataView.setCode(200);
        return dataView;
    }
}
