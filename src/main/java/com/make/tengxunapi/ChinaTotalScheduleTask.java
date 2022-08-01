package com.make.tengxunapi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.make.entity.ChinaTotal;
import com.make.entity.NocvData;
import com.make.service.ChinaTotalService;
import com.make.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ChinaTotalScheduleTask {

    @Autowired
    private ChinaTotalService chinaTotalService;

    @Autowired
    private IndexService indexService;

    /**
     * 每小时更新一次
     * @throws Exception
     */
//    @Scheduled(fixedDelay = 10000)
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void updateChinaTotalToDB() throws Exception {
        HttpUtils httpUtils = new HttpUtils();
        String string = httpUtils.getData();
        // System.out.println("数据：" + string);

        // 1.所有数据的alibaba格式
        JSONObject jsonObject = JSONObject.parseObject(string);
        Object data = jsonObject.get("data");
        System.out.println("解析data: " + data);
        // 2.data
        JSONObject jsonObjectData =JSONObject.parseObject(data.toString());
        Object chinaTotal = jsonObjectData.get("chinaTotal");
        Object lastUpdateTime = jsonObjectData.get("overseaLastUpdateTime");
        System.out.println("解析chinaTotal: " + chinaTotal);
        System.out.println("解析lastUpdateTime: " + lastUpdateTime);

        // 3.total全中国整体疫情数据
        JSONObject jsonObjectTotal = JSONObject.parseObject(chinaTotal.toString());
        Object total = jsonObjectTotal.get("total");
        System.out.println("解析数据total:" + total);
        // 4.全国数据total
        JSONObject totalData = JSONObject.parseObject(total.toString());
        Object confirm = totalData.get("confirm");
        Object input = totalData.get("input");
        Object severe = totalData.get("severe");
        Object heal = totalData.get("heal");
        Object dead = totalData.get("dead");
        Object suspect = totalData.get("suspect");
        ChinaTotal dataEntity = new ChinaTotal();
        dataEntity.setConfirm((Integer) confirm);
        dataEntity.setInput((Integer) input);
        dataEntity.setSevere((Integer) severe);
        dataEntity.setHeal((Integer) heal);
        dataEntity.setDead((Integer) dead);
        dataEntity.setSuspect((Integer) suspect);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        dataEntity.setUpdateTime(format.parse(String.valueOf(lastUpdateTime)));
        System.out.println(dataEntity);

        // 插入数据库
        chinaTotalService.save(dataEntity);


        // 中国各省份数据
        JSONArray areaTree = jsonObjectData.getJSONArray("areaTree");
        Object[] objects = areaTree.toArray();
        // 遍历所有国家
        /*for (Object object : objects) {
            JSONObject jsonObject1 = JSONObject.parseObject(object.toString());
            Object name = jsonObject1.get("name");
            System.out.println(name);
        }*/
        // 拿到中国数据
        JSONObject jsonObject1 = JSONObject.parseObject(objects[2].toString());
        JSONArray children = jsonObject1.getJSONArray("children");
        Object[] objects1 = children.toArray(); //各个省份

        List<NocvData> list = new ArrayList<>();
        for (int i = 0; i < objects1.length; i++) {
            NocvData nocvData = new NocvData();
            JSONObject jsonObject2 = JSONObject.parseObject(objects1[i].toString());
            Object name = jsonObject2.get("name"); //省份
            Object timePro = jsonObject2.get("lastUpdateTime"); //省份更新数据时间
            Object total1 = jsonObject2.get("total");
            JSONObject jsonObject3 = JSONObject.parseObject(total1.toString());// total
            Object confirm1 = jsonObject3.get("confirm"); //确诊数量

            //获取累计死亡人数 治愈人数
            Object heal1 = jsonObject3.get("heal");
            Object dead1 = jsonObject3.get("dead");

            // 现存确诊
            int xiancunConfirm = Integer.parseInt(confirm1.toString()) - Integer.parseInt(heal1.toString()) - Integer.parseInt(dead1.toString());

            // System.out.println("省份=》》" + name + "confirm：" + confirm1);
            nocvData.setName(name.toString());
            nocvData.setValue(xiancunConfirm);
            if (timePro==null){
                nocvData.setUpdateTime(new Date());
            }else {
                nocvData.setUpdateTime(format.parse(String.valueOf(timePro)));
            }

            list.add(nocvData);
        }

        //批量插入
        indexService.saveBatch(list);

        // 删除缓存
        Jedis jedis = new Jedis("localhost");
        if (jedis!=null){
            jedis.flushDB(); // 对应点
        }
    }
}
