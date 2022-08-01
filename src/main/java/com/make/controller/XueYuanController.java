package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.XueYuan;
import com.make.service.XueYuanService;
import com.make.vo.DataView;
import com.make.vo.XueYuanVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/xueyuan")
public class XueYuanController {

    @Autowired
    private XueYuanService xueYuanService;

    @RequestMapping("/toXueYuan")
    public String toXueYuan(){
        return "xueyuan/xueyuan";
    }

    /**
     *
     * @return
     */
    @RequestMapping("/listXueYuan")
    @ResponseBody
    public DataView listNews(XueYuanVo xueYuanVo){
        QueryWrapper<XueYuan> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(xueYuanVo.getName()), "name", xueYuanVo.getName());
        IPage<XueYuan> page = new Page<>(xueYuanVo.getPage(), xueYuanVo.getLimit());
        xueYuanService.page(page, queryWrapper);
        return new DataView(page.getTotal(), page.getRecords());
    }

    /**
     * 新增或修改
     */
    @RequestMapping("/addOrUpdateXueYuan")
    @ResponseBody
    public DataView addOrUpdate(XueYuan xueYuan){
        xueYuanService.saveOrUpdate(xueYuan);
        DataView dataView = new DataView();
        dataView.setCode(200);
        dataView.setMsg("新增或修改成功");
        return dataView;
    }

    /**
     * 删除学院
     * @param id
     * @return
     */
    @RequestMapping("/deleteById")
    @ResponseBody
    public DataView deleteXueYuan(Integer id){
        xueYuanService.removeById(id);
        DataView dataView = new DataView();
        dataView.setMsg("删除新闻成功");
        dataView.setCode(200);
        return dataView;
    }
}
