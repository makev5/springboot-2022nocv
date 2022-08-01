package com.make.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.make.entity.NocvData;
import com.make.service.IndexService;
import com.make.vo.DataView;
import com.make.vo.NocvDataVo;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ChinaDataAdminController {

    @Autowired
    private IndexService indexService;

    /**
     * 模糊查询带有分页
     * @param nocvDataVo
     * @return
     */
    @GetMapping("/listDataByPage")
    public DataView listDataByPage(NocvDataVo nocvDataVo){
        // 创建分页的对象
        IPage<NocvData> page = new Page<>(nocvDataVo.getPage(), nocvDataVo.getLimit());
        // 创建条件对象
        QueryWrapper<NocvData> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(nocvDataVo.getName() !=null, "name", nocvDataVo.getName());
        queryWrapper.orderByDesc("value");
        indexService.page(page, queryWrapper);
        DataView dataView = new DataView(page.getTotal(), page.getRecords());
        return dataView;
    }

    /**
     * Excel文件上传
     */
    @RequestMapping("/excelImportChina")
    public DataView excelImportChina(@RequestParam("file")MultipartFile file) throws IOException {
        DataView dataView = new DataView();
        // 1.文件不能为空
        if (file.isEmpty()) {
            dataView.setMsg("文件为空，上传失败");

        }
        // 2.POI获取Excel解析数据
        HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = wb.getSheetAt(0);

        // 3.定义一个集合接收文件数据
        List<NocvData> list = new ArrayList<>();
        HSSFRow row = null;

        // 4.解析数据装入集合
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            // 4.1定义实体
            NocvData nocvData = new NocvData();
            // 4.2每一行的数据放入实体类
            row = sheet.getRow(i);
            // 4.3解析数据
            nocvData.setName(row.getCell(0).getStringCellValue());
            nocvData.setValue((int)row.getCell(1).getNumericCellValue());
            // 5.添加list集合
            list.add(nocvData);
        }
        //6.插入数据库
        indexService.saveBatch(list);
        dataView.setCode(200);
        dataView.setMsg("插入成功");
        return dataView;
    }

    /**
     * 导出Excel 中国疫情数据
     */
    @RequestMapping("/excelOutPortChina")
    public void excelOutPortChina(HttpServletResponse response) throws Exception {
        // 1. 查询数据库
        List<NocvData> nocvDataList = indexService.list();
        // 2. 建立excel对象， 封装对象
        response.setCharacterEncoding("UTF-8");
        // 2.1创建Excel对象
        HSSFWorkbook wb = new HSSFWorkbook();
        // 2.1创建sheet对象
        HSSFSheet sheet = wb.createSheet("中国疫情数据sheet1");
        // 2.2创建表头
        HSSFRow hssfRow = sheet.createRow(0);
        hssfRow.createCell(0).setCellValue("城市名称");
        hssfRow.createCell(1).setCellValue("确诊数量");
        // 3.遍历数据，封装Excel对象
        for (NocvData data : nocvDataList){
            HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum()+1);
            dataRow.createCell(0).setCellValue(data.getName());
            dataRow.createCell(1).setCellValue(data.getValue());
        }
        // 4.建立输出流，输出文件
        OutputStream os = null;

        try {
            // 4.1设置excel名字和输出类型
            response.setContentType("application/octet-stream;charset=utf8");
            response.setHeader("content-Disposition", "attachment;filename=" + new String("中国疫情数据表".getBytes(), "ISO-8859-1")+ ".xls");
            // 4.2输出文件
            os = response.getOutputStream();
            wb.write(os);
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os!=null){
                    // 5.关闭输出流
                    os.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
