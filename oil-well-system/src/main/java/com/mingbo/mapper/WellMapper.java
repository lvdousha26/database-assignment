package com.oilwell.mapper;

import com.oilwell.pojo.Well;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WellMapper {

    @Select("select * from tb_well where id = #{id}")
    Well selectById(Long id);

    @Select("<script>" +
            "select * from tb_well where 1=1" +
            "<if test='wellName != null and wellName != \"\"'> and well_name like concat('%', #{wellName}, '%')</if>" +
            "<if test='wellType != null and wellType != \"\"'> and well_type = #{wellType}</if>" +
            "<if test='wellStatus != null and wellStatus != \"\"'> and well_status = #{wellStatus}</if>" +
            "order by id desc" +
            "</script>")
    List<Well> selectByCondition(@Param("wellName") String wellName,
                                  @Param("wellType") String wellType,
                                  @Param("wellStatus") String wellStatus);

    @Select("select * from tb_well order by id desc")
    List<Well> selectAll();

    @Insert("insert into tb_well (well_name, well_type, well_status, field_name, layer, depth, " +
            "operator, drilling_date, address, notes) " +
            "values (#{wellName}, #{wellType}, #{wellStatus}, #{fieldName}, #{layer}, #{depth}, " +
            "#{operator}, #{drillingDate}, #{address}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Well well);

    @Update("update tb_well set well_name=#{wellName}, well_type=#{wellType}, well_status=#{wellStatus}, " +
            "field_name=#{fieldName}, layer=#{layer}, depth=#{depth}, operator=#{operator}, " +
            "drilling_date=#{drillingDate}, address=#{address}, notes=#{notes} where id=#{id}")
    void update(Well well);

    @Delete("delete from tb_well where id = #{id}")
    void deleteById(Long id);

    @Select("select count(*) from tb_well")
    Long countAll();

    @Select("select count(*) from tb_well where well_status = '生产'")
    Long countProduction();

    @Select("select well_type, count(*) as cnt from tb_well group by well_type")
    List<WellTypeStat> selectWellTypeStats();

    class WellTypeStat {
        private String wellType;
        private Long cnt;

        public String getWellType() { return wellType; }
        public void setWellType(String wellType) { this.wellType = wellType; }
        public Long getCnt() { return cnt; }
        public void setCnt(Long cnt) { this.cnt = cnt; }
    }
}
