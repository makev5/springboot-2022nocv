package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.NocvNews;
import com.make.service.NocvNewsService;
import com.make.vo.DataView;
import com.make.vo.NocvNewsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/news")
public class NocvNewsController {

    @Autowired
    private NocvNewsService nocvNewsService;

    @RequestMapping("/toNews")
    public String toNews(){
        return "news/news";
    }

    /**
     *
     * @return
     */
    @RequestMapping("/listNews")
    @ResponseBody
    public DataView listNews(NocvNewsVo nocvNewsVo){
        QueryWrapper<NocvNews> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(nocvNewsVo.getTitle()), "title", nocvNewsVo.getTitle());
        IPage<NocvNews> page = new Page<>(nocvNewsVo.getPage(), nocvNewsVo.getLimit());
        nocvNewsService.page(page, queryWrapper);
        return new DataView(page.getTotal(), page.getRecords());
    }

    /**
     * 新增或修改
     */
    @RequestMapping("/addOrUpdateNews")
    @ResponseBody
    public DataView addOrUpdate(NocvNews nocvNews){
        nocvNewsService.saveOrUpdate(nocvNews);
        DataView dataView = new DataView();
        dataView.setCode(200);
        dataView.setMsg("新增或修改成功");
        return dataView;
    }

    /**
     * 删除新闻
     * @param id
     * @return
     */
    @RequestMapping("/deleteById")
    @ResponseBody
    public DataView deleteNews(Integer id){
        nocvNewsService.removeById(id);
        DataView dataView = new DataView();
        dataView.setMsg("删除新闻成功");
        dataView.setCode(200);
        return dataView;
    }
}
