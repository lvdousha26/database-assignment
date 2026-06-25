package com.mingbo.mapper;

import com.mingbo.pojo.Operation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OperationMapper {

    @Select("select * from tb_operation where id = #{id}")
    Operation selectById(Long id);

    @Select("<script>" +
            "select o.*, w.well_name as wellName, w.well_type as wellType, t.type_name as operationTypeName " +
            "from tb_operation o " +
            "left join tb_well w on o.well_id = w.id " +
            "left join tb_operation_type t on o.operation_type_id = t.id " +
            "where 1=1" +
            "<if test='wellId != null'> and o.well_id = #{wellId}</if>" +
            "<if test='status != null and status != \"\"'> and o.status = #{status}</if>" +
            "<if test='operationName != null and operationName != \"\"'> and o.operation_name like concat('%', #{operationName}, '%')</if>" +
            "order by o.id desc" +
            "</script>")
    List<Operation> selectByCondition(@Param("wellId") Long wellId,
                                       @Param("status") String status,
                                       @Param("operationName") String operationName);

    @Select("select o.*, w.well_name as wellName, w.well_type as wellType, t.type_name as operationTypeName " +
            "from tb_operation o " +
            "left join tb_well w on o.well_id = w.id " +
            "left join tb_operation_type t on o.operation_type_id = t.id " +
            "order by o.id desc")
    List<Operation> selectAll();

    @Insert("insert into tb_operation (well_id, operation_type_id, operation_name, start_date, end_date, " +
            "team_name, team_leader, team_members, status, notes) " +
            "values (#{wellId}, #{operationTypeId}, #{operationName}, #{startDate}, #{endDate}, " +
            "#{teamName}, #{teamLeader}, #{teamMembers}, #{status}, #{notes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Operation operation);

    @Update("update tb_operation set well_id=#{wellId}, operation_type_id=#{operationTypeId}, " +
            "operation_name=#{operationName}, start_date=#{startDate}, end_date=#{endDate}, " +
            "team_name=#{teamName}, team_leader=#{teamLeader}, team_members=#{teamMembers}, " +
            "status=#{status}, notes=#{notes} where id=#{id}")
    void update(Operation operation);

    @Delete("delete from tb_operation where id = #{id}")
    void deleteById(Long id);

    @Select("select count(*) from tb_operation")
    Long countAll();

    @Select("select count(*) from tb_operation where date_format(start_date, '%Y-%m') = date_format(now(), '%Y-%m')")
    Long countMonthly();

    @Select("select o.*, w.well_name as wellName, t.type_name as operationTypeName " +
            "from tb_operation o " +
            "left join tb_well w on o.well_id = w.id " +
            "left join tb_operation_type t on o.operation_type_id = t.id " +
            "order by o.id desc limit 10")
    List<Operation> selectLatest();

    @Select("select count(*) from tb_operation where status = #{status}")
    Long countByStatus(String status);

    @Select("select count(*) from tb_operation where well_id = #{wellId}")
    Long countByWellId(Long wellId);
}
