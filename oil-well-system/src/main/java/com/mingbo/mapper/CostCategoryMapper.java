package com.oilwell.mapper;

import com.oilwell.pojo.CostCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CostCategoryMapper {

    @Select("select * from tb_cost_category where id = #{id}")
    CostCategory selectById(Long id);

    @Select("select c.*, p.category_name as parentName " +
            "from tb_cost_category c " +
            "left join tb_cost_category p on c.parent_id = p.id " +
            "order by c.id")
    List<CostCategory> selectAll();

    @Insert("insert into tb_cost_category (category_name, parent_id, description) " +
            "values (#{categoryName}, #{parentId}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CostCategory costCategory);

    @Update("update tb_cost_category set category_name=#{categoryName}, parent_id=#{parentId}, " +
            "description=#{description} where id=#{id}")
    void update(CostCategory costCategory);

    @Delete("delete from tb_cost_category where id = #{id}")
    void deleteById(Long id);
}
