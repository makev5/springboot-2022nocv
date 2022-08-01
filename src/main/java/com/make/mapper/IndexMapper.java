package com.make.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.make.entity.LineTrend;
import com.make.entity.NocvData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IndexMapper extends BaseMapper<NocvData> {

    @Select("select * from line_trend order by create_time limit 7")
    List<LineTrend> findSevenData();

}
