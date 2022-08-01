package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.BanJi;
import com.make.service.BanJiService;
import com.make.vo.BanJiVo;
import com.make.vo.DataView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/banji")
public class BanJiController {

    @Autowired
    private BanJiService banJiService;

    @RequestMapping("/toBanJi")
    public String toBanJi(){
        return "banji/banji";
    }

    /**
     *
     * @return
     */
    @RequestMapping("/listBanJi")
    @ResponseBody
    public DataView listNews(BanJiVo banJiVo){
        QueryWrapper<BanJi> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(banJiVo.getName()), "name", banJiVo.getName());
        IPage<BanJi> page = new Page<>(banJiVo.getPage(), banJiVo.getLimit());
        banJiService.page(page, queryWrapper);
        return new DataView(page.getTotal(), page.getRecords());
    }

    /**
     * 新增或修改
     */
    @RequestMapping("/addOrUpdateBanJi")
    @ResponseBody
    public DataView addOrUpdate(BanJi banJi){
        banJiService.saveOrUpdate(banJi);
        DataView dataView = new DataView();
        dataView.setCode(200);
        dataView.setMsg("新增或修改成功");
        return dataView;
    }

    /**
     * 删除班级
     * @param id
     * @return
     */
    @RequestMapping("/deleteById")
    @ResponseBody
    public DataView deleteBanJi(Integer id){
        banJiService.removeById(id);
        DataView dataView = new DataView();
        dataView.setMsg("删除新闻成功");
        dataView.setCode(200);
        return dataView;
    }
}
