package com.make.controller;

import com.make.entity.NocvData;
import com.make.service.IndexService;
import com.make.vo.DataView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/china")
public class ChinaController {

    @Autowired
    private IndexService indexService;

    @RequestMapping("/deleteById")
    @ResponseBody
    public DataView deleteById(Integer id){
        indexService.removeById(id);
        DataView dataView = new DataView();
        dataView.setCode(200);
        dataView.setMsg("删除成功");
        return dataView;
    }

    @RequestMapping("/addOrUpdateChina")
    @ResponseBody
    public DataView addOrUpdateChina(NocvData nocvData){
        boolean save = indexService.saveOrUpdate(nocvData);
        DataView dataView = new DataView();
        if (save) {
            dataView.setCode(200);
            dataView.setMsg("新增成功");
            return dataView;
        }
        dataView.setCode(500);
        dataView.setMsg("新增失败");
        return dataView;
    }
}
