package com.oilwell.mapper;

import com.oilwell.pojo.CostDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CostDetailMapper {

    @Select("select * from tb_cost_detail where id = #{id}")
    CostDetail selectById(Long id);

    @Select("<script>" +
            "select d.*, o.operation_name as operationName, c.category_name as categoryName " +
            "from tb_cost_detail d " +
            "left join tb_operation o on d.operation_id = o.id " +
            "left join tb_cost_category c on d.category_id = c.id " +
            "where 1=1" +
            "<if test='operationId != null'> and d.operation_id = #{operationId}</if>" +
            "<if test='categoryId != null'> and d.category_id = #{categoryId}</if>" +
            "order by d.id desc" +
            "</script>")
    List<CostDetail> selectByCondition(@Param("operationId") Long operationId,
                                        @Param("categoryId") Long categoryId);

    @Select("select d.*, o.operation_name as operationName, c.category_name as categoryName " +
            "from tb_cost_detail d " +
            "left join tb_operation o on d.operation_id = o.id " +
            "left join tb_cost_category c on d.category_id = c.id " +
            "order by d.id desc")
    List<CostDetail> selectAll();

    @Insert("insert into tb_cost_detail (operation_id, category_id, item_name, quantity, unit_price, " +
            "amount, cost_date, payee, notes) " +
            "values (#{operationId}, #{categoryId}, #{itemName}, #{quantity}, #{unitPrice}, " +
            "#{amount}, #{costDate}, #{payee}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(CostDetail costDetail);

    @Update("update tb_cost_detail set operation_id=#{operationId}, category_id=#{categoryId}, " +
            "item_name=#{itemName}, quantity=#{quantity}, unit_price=#{unitPrice}, " +
            "amount=#{amount}, cost_date=#{costDate}, payee=#{payee}, notes=#{notes} where id=#{id}")
    void update(CostDetail costDetail);

    @Delete("delete from tb_cost_detail where id = #{id}")
    void deleteById(Long id);

    @Select("select sum(amount) from tb_cost_detail where operation_id = #{operationId}")
    Double sumAmountByOperationId(Long operationId);

    @Select("select c.category_name as categoryName, sum(d.amount) as totalAmount " +
            "from tb_cost_detail d " +
            "left join tb_cost_category c on d.category_id = c.id " +
            "group by c.category_name")
    List<Map<String, Object>> sumByCategory();

    @Select("select date_format(cost_date, '%Y-%m') as month, sum(amount) as totalAmount " +
            "from tb_cost_detail " +
            "group by date_format(cost_date, '%Y-%m') " +
            "order by month")
    List<Map<String, Object>> sumByMonth();

    @Select("select sum(amount) from tb_cost_detail")
    Double sumAll();

    @Select("select coalesce(sum(amount), 0) from tb_cost_detail " +
            "where date_format(cost_date, '%Y-%m') = date_format(now(), '%Y-%m')")
    Double sumMonthly();

    @Select("select date_format(cost_date, '%Y-%m') as month, coalesce(sum(amount), 0) as total " +
            "from tb_cost_detail " +
            "where cost_date >= date_sub(now(), interval 12 month) " +
            "group by date_format(cost_date, '%Y-%m') " +
            "order by month")
    List<Map<String, Object>> sumMonthlyTrend();
}
