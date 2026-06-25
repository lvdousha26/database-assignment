package com.mingbo.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mingbo.pojo.*;
import com.mingbo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ToolExecutor {

    private final ObjectMapper objectMapper;
    private final WellService wellService;
    private final OperationService operationService;
    private final CostService costService;
    private final OperationTypeService operationTypeService;
    private final CostCategoryService costCategoryService;

    public ToolExecutor(WellService wellService, OperationService operationService,
                        CostService costService, OperationTypeService operationTypeService,
                        CostCategoryService costCategoryService) {
        this.wellService = wellService;
        this.operationService = operationService;
        this.costService = costService;
        this.operationTypeService = operationTypeService;
        this.costCategoryService = costCategoryService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public String execute(String toolName, Map<String, Object> args) {
        try {
            return switch (toolName) {
                case "get_well" -> getWell(args);
                case "list_wells" -> listWells(args);
                case "add_well" -> addWell(args);
                case "update_well" -> updateWell(args);
                case "delete_well" -> deleteWell(args);
                case "list_operations" -> listOperations(args);
                case "add_operation" -> addOperation(args);
                case "update_operation" -> updateOperation(args);
                case "delete_operation" -> deleteOperation(args);
                case "list_costs" -> listCosts(args);
                case "add_cost" -> addCost(args);
                case "update_cost" -> updateCost(args);
                case "delete_cost" -> deleteCost(args);
                case "sum_cost_by_category" -> sumCostByCategory();
                case "sum_cost_by_month" -> sumCostByMonth();
                case "list_operation_types" -> listOperationTypes();
                case "list_cost_categories" -> listCostCategories();
                default -> "未知工具: " + toolName;
            };
        } catch (Exception e) {
            log.error("执行工具 {} 失败", toolName, e);
            return "操作失败: " + e.getMessage();
        }
    }

    // ========== Well ==========

    private String getWell(Map<String, Object> args) {
        Long id = getLong(args, "id");
        Well well = wellService.getById(id);
        return well != null ? toJson(well) : "未找到 ID 为 " + id + " 的油井";
    }

    private String listWells(Map<String, Object> args) {
        String wellName = getString(args, "wellName");
        String wellType = getString(args, "wellType");
        String wellStatus = getString(args, "wellStatus");
        List<Well> list = wellService.list(wellName, wellType, wellStatus);
        return toJson(list);
    }

    private String addWell(Map<String, Object> args) {
        Well well = Well.builder()
                .wellName(getString(args, "wellName"))
                .wellType(getString(args, "wellType"))
                .wellStatus(getString(args, "wellStatus"))
                .fieldName(getString(args, "fieldName"))
                .layer(getString(args, "layer"))
                .depth(getBigDecimal(args, "depth"))
                .operator(getString(args, "operator"))
                .drillingDate(getLocalDate(args, "drillingDate"))
                .address(getString(args, "address"))
                .notes(getString(args, "notes"))
                .build();
        wellService.add(well);
        return "操作成功: 已新增油井，ID = " + well.getId();
    }

    private String updateWell(Map<String, Object> args) {
        Long id = getLong(args, "id");
        Well well = wellService.getById(id);
        if (well == null) return "未找到 ID 为 " + id + " 的油井";
        if (args.containsKey("wellName")) well.setWellName(getString(args, "wellName"));
        if (args.containsKey("wellType")) well.setWellType(getString(args, "wellType"));
        if (args.containsKey("wellStatus")) well.setWellStatus(getString(args, "wellStatus"));
        if (args.containsKey("fieldName")) well.setFieldName(getString(args, "fieldName"));
        if (args.containsKey("layer")) well.setLayer(getString(args, "layer"));
        if (args.containsKey("depth")) well.setDepth(getBigDecimal(args, "depth"));
        if (args.containsKey("operator")) well.setOperator(getString(args, "operator"));
        if (args.containsKey("drillingDate")) well.setDrillingDate(getLocalDate(args, "drillingDate"));
        if (args.containsKey("address")) well.setAddress(getString(args, "address"));
        if (args.containsKey("notes")) well.setNotes(getString(args, "notes"));
        wellService.update(well);
        return "操作成功: 已更新油井 (ID = " + id + ")";
    }

    private String deleteWell(Map<String, Object> args) {
        Long id = getLong(args, "id");
        wellService.delete(id);
        return "操作成功: 已删除油井 (ID = " + id + ")";
    }

    // ========== Operation ==========

    private String listOperations(Map<String, Object> args) {
        Long wellId = getLong(args, "wellId");
        String status = getString(args, "status");
        String operationName = getString(args, "operationName");
        List<Operation> list = operationService.list(wellId, status, operationName);
        return toJson(list);
    }

    private String addOperation(Map<String, Object> args) {
        Operation op = Operation.builder()
                .wellId(getLong(args, "wellId"))
                .operationTypeId(getLong(args, "operationTypeId"))
                .operationName(getString(args, "operationName"))
                .startDate(getLocalDate(args, "startDate"))
                .endDate(getLocalDate(args, "endDate"))
                .teamName(getString(args, "teamName"))
                .teamLeader(getString(args, "teamLeader"))
                .teamMembers(getInteger(args, "teamMembers"))
                .status(getString(args, "status"))
                .notes(getString(args, "notes"))
                .build();
        operationService.add(op);
        return "操作成功: 已新增作业，ID = " + op.getId();
    }

    private String updateOperation(Map<String, Object> args) {
        Long id = getLong(args, "id");
        Operation op = operationService.getById(id);
        if (op == null) return "未找到 ID 为 " + id + " 的作业";
        if (args.containsKey("wellId")) op.setWellId(getLong(args, "wellId"));
        if (args.containsKey("operationTypeId")) op.setOperationTypeId(getLong(args, "operationTypeId"));
        if (args.containsKey("operationName")) op.setOperationName(getString(args, "operationName"));
        if (args.containsKey("startDate")) op.setStartDate(getLocalDate(args, "startDate"));
        if (args.containsKey("endDate")) op.setEndDate(getLocalDate(args, "endDate"));
        if (args.containsKey("teamName")) op.setTeamName(getString(args, "teamName"));
        if (args.containsKey("teamLeader")) op.setTeamLeader(getString(args, "teamLeader"));
        if (args.containsKey("teamMembers")) op.setTeamMembers(getInteger(args, "teamMembers"));
        if (args.containsKey("status")) op.setStatus(getString(args, "status"));
        if (args.containsKey("notes")) op.setNotes(getString(args, "notes"));
        operationService.update(op);
        return "操作成功: 已更新作业 (ID = " + id + ")";
    }

    private String deleteOperation(Map<String, Object> args) {
        Long id = getLong(args, "id");
        operationService.delete(id);
        return "操作成功: 已删除作业 (ID = " + id + ")";
    }

    // ========== CostDetail ==========

    private String listCosts(Map<String, Object> args) {
        Long operationId = getLong(args, "operationId");
        Long categoryId = getLong(args, "categoryId");
        List<CostDetail> list = costService.list(operationId, categoryId);
        return toJson(list);
    }

    private String addCost(Map<String, Object> args) {
        BigDecimal quantity = getBigDecimal(args, "quantity");
        BigDecimal unitPrice = getBigDecimal(args, "unitPrice");
        BigDecimal amount = getBigDecimal(args, "amount");
        if (amount == null && quantity != null && unitPrice != null) {
            amount = quantity.multiply(unitPrice);
        }
        CostDetail cost = CostDetail.builder()
                .operationId(getLong(args, "operationId"))
                .categoryId(getLong(args, "categoryId"))
                .itemName(getString(args, "itemName"))
                .quantity(quantity)
                .unitPrice(unitPrice)
                .amount(amount)
                .costDate(getLocalDate(args, "costDate"))
                .payee(getString(args, "payee"))
                .notes(getString(args, "notes"))
                .build();
        costService.add(cost);
        return "操作成功: 已新增成本明细，ID = " + cost.getId();
    }

    private String updateCost(Map<String, Object> args) {
        Long id = getLong(args, "id");
        CostDetail cost = costService.getById(id);
        if (cost == null) return "未找到 ID 为 " + id + " 的成本明细";
        if (args.containsKey("operationId")) cost.setOperationId(getLong(args, "operationId"));
        if (args.containsKey("categoryId")) cost.setCategoryId(getLong(args, "categoryId"));
        if (args.containsKey("itemName")) cost.setItemName(getString(args, "itemName"));
        if (args.containsKey("quantity")) cost.setQuantity(getBigDecimal(args, "quantity"));
        if (args.containsKey("unitPrice")) cost.setUnitPrice(getBigDecimal(args, "unitPrice"));
        if (args.containsKey("amount")) cost.setAmount(getBigDecimal(args, "amount"));
        if (args.containsKey("costDate")) cost.setCostDate(getLocalDate(args, "costDate"));
        if (args.containsKey("payee")) cost.setPayee(getString(args, "payee"));
        if (args.containsKey("notes")) cost.setNotes(getString(args, "notes"));
        costService.update(cost);
        return "操作成功: 已更新成本明细 (ID = " + id + ")";
    }

    private String deleteCost(Map<String, Object> args) {
        Long id = getLong(args, "id");
        costService.delete(id);
        return "操作成功: 已删除成本明细 (ID = " + id + ")";
    }

    private String sumCostByCategory() {
        List<Map<String, Object>> result = costService.sumByCategory();
        return toJson(result);
    }

    private String sumCostByMonth() {
        List<Map<String, Object>> result = costService.sumByMonth();
        return toJson(result);
    }

    // ========== OperationType ==========

    private String listOperationTypes() {
        List<OperationType> list = operationTypeService.list();
        return toJson(list);
    }

    // ========== CostCategory ==========

    private String listCostCategories() {
        List<CostCategory> list = costCategoryService.list();
        return toJson(list);
    }

    // ========== Helpers ==========

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败", e);
            return "数据序列化失败";
        }
    }

    private Long getLong(Map<String, Object> args, String key) {
        Object v = args.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).longValue();
        try { return Long.parseLong(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    private Integer getInteger(Map<String, Object> args, String key) {
        Object v = args.get(key);
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try { return Integer.parseInt(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    private BigDecimal getBigDecimal(Map<String, Object> args, String key) {
        Object v = args.get(key);
        if (v == null) return null;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        try { return new BigDecimal(v.toString()); } catch (NumberFormatException e) { return null; }
    }

    private String getString(Map<String, Object> args, String key) {
        Object v = args.get(key);
        return v != null ? v.toString() : null;
    }

    private LocalDate getLocalDate(Map<String, Object> args, String key) {
        Object v = args.get(key);
        if (v == null) return null;
        try { return LocalDate.parse(v.toString()); } catch (Exception e) { return null; }
    }
}
