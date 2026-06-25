package com.mingbo.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ToolDefinition {

    public static List<Map<String, Object>> getAllTools() {
        return List.of(
                // 油井
                tool("get_well", "按 ID 查询单个油井信息", objectParams(Map.of(
                        "id", strProp("油井 ID")
                ), List.of("id"))),
                tool("list_wells", "按条件查询油井列表，不传参则查询全部", objectParams(Map.of(
                        "wellName", strProp("油井名称（模糊匹配）"),
                        "wellType", strProp("油井类型"),
                        "wellStatus", strProp("油井状态")
                ), List.of())),
                tool("add_well", "新增油井记录", objectParams(Map.of(
                        "wellName", strProp("油井名称"),
                        "wellType", strProp("油井类型"),
                        "wellStatus", strProp("油井状态"),
                        "fieldName", strProp("所属油田"),
                        "layer", strProp("层位"),
                        "depth", strProp("深度（米）"),
                        "operator", strProp("责任人"),
                        "drillingDate", strProp("钻探日期，格式 yyyy-MM-dd"),
                        "address", strProp("位置"),
                        "notes", strProp("备注")
                ), List.of("wellName"))),
                tool("update_well", "修改油井信息，只传需要修改的字段", objectParams(map(
                        "id", strProp("油井 ID"),
                        "wellName", strProp("油井名称"),
                        "wellType", strProp("油井类型"),
                        "wellStatus", strProp("油井状态"),
                        "fieldName", strProp("所属油田"),
                        "layer", strProp("层位"),
                        "depth", strProp("深度（米）"),
                        "operator", strProp("责任人"),
                        "drillingDate", strProp("钻探日期，格式 yyyy-MM-dd"),
                        "address", strProp("位置"),
                        "notes", strProp("备注")
                ), List.of("id"))),
                tool("delete_well", "删除油井记录（请先让用户确认后再执行）", objectParams(Map.of(
                        "id", strProp("油井 ID")
                ), List.of("id"))),

                // 作业
                tool("list_operations", "按条件查询作业列表，不传参则查询全部", objectParams(Map.of(
                        "wellId", strProp("油井 ID"),
                        "status", strProp("作业状态"),
                        "operationName", strProp("作业名称（模糊匹配）")
                ), List.of())),
                tool("add_operation", "新增作业记录", objectParams(Map.of(
                        "wellId", strProp("油井 ID"),
                        "operationTypeId", strProp("作业类型 ID"),
                        "operationName", strProp("作业名称"),
                        "startDate", strProp("开始日期，格式 yyyy-MM-dd"),
                        "endDate", strProp("结束日期，格式 yyyy-MM-dd"),
                        "teamName", strProp("班组名称"),
                        "teamLeader", strProp("班组长"),
                        "teamMembers", strProp("班组人数"),
                        "status", strProp("作业状态"),
                        "notes", strProp("备注")
                ), List.of("wellId", "operationTypeId", "operationName"))),
                tool("update_operation", "修改作业信息，只传需要修改的字段", objectParams(map(
                        "id", strProp("作业 ID"),
                        "wellId", strProp("油井 ID"),
                        "operationTypeId", strProp("作业类型 ID"),
                        "operationName", strProp("作业名称"),
                        "startDate", strProp("开始日期，格式 yyyy-MM-dd"),
                        "endDate", strProp("结束日期，格式 yyyy-MM-dd"),
                        "teamName", strProp("班组名称"),
                        "teamLeader", strProp("班组长"),
                        "teamMembers", strProp("班组人数"),
                        "status", strProp("作业状态"),
                        "notes", strProp("备注")
                ), List.of("id"))),
                tool("delete_operation", "删除作业记录（请先让用户确认后再执行）", objectParams(Map.of(
                        "id", strProp("作业 ID")
                ), List.of("id"))),

                // 成本明细
                tool("list_costs", "按条件查询成本明细列表，不传参则查询全部", objectParams(Map.of(
                        "operationId", strProp("作业 ID"),
                        "categoryId", strProp("成本类别 ID")
                ), List.of())),
                tool("add_cost", "新增成本明细记录", objectParams(Map.of(
                        "operationId", strProp("作业 ID"),
                        "categoryId", strProp("成本类别 ID"),
                        "itemName", strProp("费用项目名称"),
                        "quantity", strProp("数量"),
                        "unitPrice", strProp("单价"),
                        "amount", strProp("金额（不传则自动计算为 quantity * unitPrice）"),
                        "costDate", strProp("费用日期，格式 yyyy-MM-dd"),
                        "payee", strProp("收款方"),
                        "notes", strProp("备注")
                ), List.of("operationId", "categoryId", "itemName"))),
                tool("update_cost", "修改成本明细，只传需要修改的字段", objectParams(Map.of(
                        "id", strProp("成本明细 ID"),
                        "operationId", strProp("作业 ID"),
                        "categoryId", strProp("成本类别 ID"),
                        "itemName", strProp("费用项目名称"),
                        "quantity", strProp("数量"),
                        "unitPrice", strProp("单价"),
                        "amount", strProp("金额"),
                        "costDate", strProp("费用日期，格式 yyyy-MM-dd"),
                        "payee", strProp("收款方"),
                        "notes", strProp("备注")
                ), List.of("id"))),
                tool("delete_cost", "删除成本明细记录（请先让用户确认后再执行）", objectParams(Map.of(
                        "id", strProp("成本明细 ID")
                ), List.of("id"))),
                tool("sum_cost_by_category", "按成本类别汇总金额", objectParams(Map.of(), List.of())),
                tool("sum_cost_by_month", "按月份汇总成本金额", objectParams(Map.of(), List.of())),

                // 作业类型
                tool("list_operation_types", "查询所有作业类型", objectParams(Map.of(), List.of())),

                // 成本类别
                tool("list_cost_categories", "查询所有成本类别", objectParams(Map.of(), List.of()))
        );
    }

    private static Map<String, Object> tool(String name, String description, Map<String, Object> parameters) {
        Map<String, Object> function = new LinkedHashMap<>();
        function.put("name", name);
        function.put("description", description);
        function.put("parameters", parameters);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "function");
        result.put("function", function);
        return result;
    }

    private static Map<String, Object> objectParams(Map<String, Object> properties, List<String> required) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("type", "object");
        params.put("properties", properties);
        params.put("required", required);
        return params;
    }

    private static Map<String, Object> strProp(String description) {
        return Map.of("type", "string", "description", description);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> map(Object... entries) {
        Map<K, V> result = new LinkedHashMap<>();
        for (int i = 0; i < entries.length; i += 2) {
            result.put((K) entries[i], (V) entries[i + 1]);
        }
        return result;
    }
}
