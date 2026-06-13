package com.mingbo.mapper;

import com.mingbo.pojo.Echarts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EchartsMapper {

    @Select("select * from tb_echarts")
    List<Echarts> getEchartsList();
}
