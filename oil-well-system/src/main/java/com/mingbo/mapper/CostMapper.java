package com.mingbo.mapper;

import com.mingbo.pojo.Cost;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CostMapper {

    @Select("select * from tb_cost where code = #{code}")
    Cost selectByCode(String code);

    @Select("<script>" +
            "select * from tb_cost where 1=1" +
            "<if test='wellcode != null and wellcode != \"\"'> and wellcode like concat('%', #{wellcode}, '%')</if>" +
            "<if test='preunit != null and preunit != \"\"'> and preunit like concat('%', #{preunit}, '%')</if>" +
            "<if test='content != null and content != \"\"'> and content like concat('%', #{content}, '%')</if>" +
            "order by predate desc" +
            "</script>")
    List<Cost> selectByCondition(@Param("wellcode") String wellcode,
                                 @Param("preunit") String preunit,
                                 @Param("content") String content);

    @Select("select * from tb_cost order by predate desc")
    List<Cost> selectAll();

    @Insert("insert into tb_cost (code, preunit, wellcode, premoney, person, predate, " +
            "startdate, finish, settleunit, content, " +
            "mat1_code, mat1_num, mat1_price, mat1_sub, " +
            "mat2_code, mat2_num, mat2_price, mat2_sub, " +
            "mat3_code, mat3_num, mat3_price, mat3_sub, " +
            "mat4_code, mat4_num, mat4_price, mat4_sub, " +
            "matcost, humancost, equipcost, othercost, " +
            "settlecost, settleperson, settledate, finalcost, finalperson, finaldate) " +
            "values (#{code}, #{preunit}, #{wellcode}, #{premoney}, #{person}, #{predate}, " +
            "#{startdate}, #{finish}, #{settleunit}, #{content}, " +
            "#{mat1Code}, #{mat1Num}, #{mat1Price}, #{mat1Sub}, " +
            "#{mat2Code}, #{mat2Num}, #{mat2Price}, #{mat2Sub}, " +
            "#{mat3Code}, #{mat3Num}, #{mat3Price}, #{mat3Sub}, " +
            "#{mat4Code}, #{mat4Num}, #{mat4Price}, #{mat4Sub}, " +
            "#{matcost}, #{humancost}, #{equipcost}, #{othercost}, " +
            "#{settlecost}, #{settleperson}, #{settledate}, #{finalcost}, #{finalperson}, #{finaldate})")
    void insert(Cost cost);

    @Update("<script>" +
            "update tb_cost set " +
            "<if test='preunit != null'>preunit=#{preunit},</if>" +
            "<if test='wellcode != null'>wellcode=#{wellcode},</if>" +
            "<if test='premoney != null'>premoney=#{premoney},</if>" +
            "<if test='person != null'>person=#{person},</if>" +
            "<if test='predate != null'>predate=#{predate},</if>" +
            "<if test='startdate != null'>startdate=#{startdate},</if>" +
            "<if test='finish != null'>finish=#{finish},</if>" +
            "<if test='settleunit != null'>settleunit=#{settleunit},</if>" +
            "<if test='content != null'>content=#{content},</if>" +
            "<if test='mat1Code != null'>mat1_code=#{mat1Code},</if>" +
            "<if test='mat1Num != null'>mat1_num=#{mat1Num},</if>" +
            "<if test='mat1Price != null'>mat1_price=#{mat1Price},</if>" +
            "<if test='mat1Sub != null'>mat1_sub=#{mat1Sub},</if>" +
            "<if test='mat2Code != null'>mat2_code=#{mat2Code},</if>" +
            "<if test='mat2Num != null'>mat2_num=#{mat2Num},</if>" +
            "<if test='mat2Price != null'>mat2_price=#{mat2Price},</if>" +
            "<if test='mat2Sub != null'>mat2_sub=#{mat2Sub},</if>" +
            "<if test='mat3Code != null'>mat3_code=#{mat3Code},</if>" +
            "<if test='mat3Num != null'>mat3_num=#{mat3Num},</if>" +
            "<if test='mat3Price != null'>mat3_price=#{mat3Price},</if>" +
            "<if test='mat3Sub != null'>mat3_sub=#{mat3Sub},</if>" +
            "<if test='mat4Code != null'>mat4_code=#{mat4Code},</if>" +
            "<if test='mat4Num != null'>mat4_num=#{mat4Num},</if>" +
            "<if test='mat4Price != null'>mat4_price=#{mat4Price},</if>" +
            "<if test='mat4Sub != null'>mat4_sub=#{mat4Sub},</if>" +
            "<if test='matcost != null'>matcost=#{matcost},</if>" +
            "<if test='humancost != null'>humancost=#{humancost},</if>" +
            "<if test='equipcost != null'>equipcost=#{equipcost},</if>" +
            "<if test='othercost != null'>othercost=#{othercost},</if>" +
            "<if test='settlecost != null'>settlecost=#{settlecost},</if>" +
            "<if test='settleperson != null'>settleperson=#{settleperson},</if>" +
            "<if test='settledate != null'>settledate=#{settledate},</if>" +
            "<if test='finalcost != null'>finalcost=#{finalcost},</if>" +
            "<if test='finalperson != null'>finalperson=#{finalperson},</if>" +
            "<if test='finaldate != null'>finaldate=#{finaldate},</if>" +
            "code=#{code} where code=#{code}" +
            "</script>")
    void update(Cost cost);

    @Delete("delete from tb_cost where code = #{code}")
    void deleteByCode(String code);

    @Select("select coalesce(sum(premoney), 0) from tb_cost")
    Double sumAll();

    @Select("select coalesce(sum(premoney), 0) from tb_cost " +
            "where date_format(predate, '%Y-%m') = date_format(now(), '%Y-%m')")
    Double sumMonthly();

    @Select("select date_format(predate, '%Y-%m') as month, coalesce(sum(premoney), 0) as total " +
            "from tb_cost where predate >= date_sub(now(), interval 12 month) " +
            "group by date_format(predate, '%Y-%m') order by month")
    List<java.util.Map<String, Object>> sumMonthlyTrend();
}
