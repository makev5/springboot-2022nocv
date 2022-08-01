package com.make.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.make.entity.ChinaTotal;
import com.make.entity.LineTrend;
import com.make.entity.NocvData;
import com.make.entity.NocvNews;
import com.make.service.ChinaTotalService;
import com.make.service.IndexService;
import com.make.service.NocvNewsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {

    @Autowired
    private IndexService indexService;

    @Autowired
    private ChinaTotalService chinaTotalService;

    @Autowired
    private NocvNewsService nocvNewsService;

    /**
     * 主控页面
     * @param model
     * @return
     */
    @RequestMapping
    public String index(Model model){
        // 1.找到ID最大的那一条数据
        Integer id = chinaTotalService.maxID();
        // 2.根据ID进行查询数据
        ChinaTotal chinaTotal = chinaTotalService.getById(id);
        model.addAttribute("chinaTotal", chinaTotal);
        return "index";
    }

    /**
     * china地图页面
     * @param model
     * @return
     */
    @RequestMapping("/toChina")
    public String toChina(Model model) throws ParseException {
        // 1.找到ID最大的那一条数据
        Integer id = chinaTotalService.maxID();
        // 2.根据ID进行查询数据
        ChinaTotal chinaTotal = chinaTotalService.getById(id);

        // 加入缓存
        Jedis jedis = new Jedis("localhost");

        // 拿到客户端
        if (jedis!=null){
            String confirm = jedis.get("confirm");
            String input = jedis.get("input");
            String heal = jedis.get("heal");
            String dead = jedis.get("dead");
            String updateTime = jedis.get("updateTime");
            if (StringUtils.isNotBlank(confirm)
                    && StringUtils.isNotBlank(input)
                    && StringUtils.isNotBlank(heal)
                    && StringUtils.isNotBlank(dead)
                    && StringUtils.isNotBlank(updateTime)) {

                ChinaTotal chinaTotalRedis = new ChinaTotal();
                chinaTotalRedis.setConfirm(Integer.parseInt(confirm));
                chinaTotalRedis.setInput(Integer.parseInt(input));
                chinaTotalRedis.setHeal(Integer.parseInt(heal));
                chinaTotalRedis.setDead(Integer.parseInt(dead));
                //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                chinaTotalRedis.setUpdateTime(new Date());
                model.addAttribute("chinaTotal", chinaTotalRedis);
                // 3.疫情播报新闻
                QueryWrapper<NocvNews> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderByDesc("create_time").last("limit 5");
                List<NocvNews> nocvNewsList = nocvNewsService.list(queryWrapper);
                model.addAttribute("nocvNewsList", nocvNewsList);
            } else {
                model.addAttribute("chinaTotal", chinaTotal);
                // 3.疫情播报新闻
                QueryWrapper<NocvNews> queryWrapper = new QueryWrapper<>();
                queryWrapper.orderByDesc("create_time").last("limit 5");
                List<NocvNews> nocvNewsList = nocvNewsService.list(queryWrapper);
                model.addAttribute("nocvNewsList", nocvNewsList);
                // 更新缓存
                jedis.set("confirm", String.valueOf(chinaTotal.getConfirm()));
                jedis.set("input", String.valueOf(chinaTotal.getInput()));
                jedis.set("heal", String.valueOf(chinaTotal.getHeal()));
                jedis.set("dead", String.valueOf(chinaTotal.getDead()));
                jedis.set("updateTime", String.valueOf(chinaTotal.getUpdateTime()));
            }

        }
        return "china";
    }

    @RequestMapping("/query")
    @ResponseBody
    public List<NocvData> queryData() {
        QueryWrapper<NocvData> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        queryWrapper.last("limit 34");

        //加入缓存
        Jedis jedis = new Jedis("localhost");
        if (jedis!=null){
            //1。有缓存，返回数据即可
            List<String> listRedis = jedis.lrange("nocvdata", 0, 33);
            List<NocvData> dataList = new ArrayList<>();
            if (listRedis.size() > 0){
                for (int i = 0; i < listRedis.size(); i++) {
                    System.out.println("列表项为：" + listRedis.get(i));
                    String s = listRedis.get(i);
                    JSONObject jsonObject = JSONObject.parseObject(s);
                    Object name = jsonObject.get("name");
                    Object value = jsonObject.get("value");
                    NocvData nocvData = new NocvData();
                    nocvData.setName(String.valueOf(name));
                    nocvData.setValue(Integer.parseInt(value.toString()));
                    dataList.add(nocvData);
                }
                return dataList;
            }else {
                //2.没缓存，查库，更新缓存
                List<NocvData> list = indexService.list(queryWrapper);
                for (NocvData nocvData : list) {
                    jedis.lpush("nocvdata", JSONObject.toJSONString(nocvData));
                }
                return list;
            }

        }

        // 没有连接redis
        List<NocvData> list = indexService.list(queryWrapper);
        return list;
    }

    @RequestMapping("/toPie")
    public String toPie() {
        return "pie";
    }

    @RequestMapping("/queryPie")
    @ResponseBody
    public List<NocvData> queryPieData() {
        QueryWrapper<NocvData> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id").last("limit 34");
        List<NocvData> list = indexService.list(queryWrapper);
        return list;
    }

    @RequestMapping("/toBar")
    public String toBar() {
        return "bar";
    }

    @RequestMapping("/queryBar")
    @ResponseBody
    public Map<String, List<Object>> queryBarData() {
        QueryWrapper<NocvData> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id").last("limit 34");
        List<NocvData> list = indexService.list(queryWrapper);
        List<String> cityList = new ArrayList<>();
        for (NocvData nocvData : list) {
            cityList.add(nocvData.getName());
        }
        List<Integer> dataList = new ArrayList<>();
        for (NocvData nocvData : list) {
            dataList.add(nocvData.getValue());
        }
        Map map = new HashMap<>();
        map.put("cityList", cityList);
        map.put("dataList", dataList);
        return map;
    }

    @RequestMapping("/toLine")
    public String toLine() {
        return "line";
    }

    @RequestMapping("/queryLine")
    @ResponseBody
    public Map<String, List<Object>> queryLineData() {
        List<LineTrend> list7Data = indexService.findSevenData();

        List<Integer> confirmList = new ArrayList<>();
        List<Integer> isolationList = new ArrayList<>();
        List<Integer> cureList = new ArrayList<>();
        List<Integer> deadList = new ArrayList<>();
        List<Integer> similarList = new ArrayList<>();
        for (LineTrend data :list7Data) {
            confirmList.add(data.getConfirm());
            isolationList.add(data.getIsolation());
            cureList.add(data.getCure());
            deadList.add(data.getDead());
            similarList.add(data.getSimilar());
        }

        Map map = new HashMap<>();
        map.put("confirmList", confirmList);
        map.put("isolationList", isolationList);
        map.put("cureList", cureList);
        map.put("deadList", deadList);
        map.put("similarList", similarList);
        return map;
    }

}
