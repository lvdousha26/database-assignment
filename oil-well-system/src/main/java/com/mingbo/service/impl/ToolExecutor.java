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

    public ToolExecutor(WellService wellService, OperationService operationService,
                        CostService costService) {
        this.wellService = wellService;
        this.operationService = operationService;
        this.costService = costService;
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
                case "get_cost" -> getCost(args);
                case "add_cost" -> addCost(args);
                case "update_cost" -> updateCost(args);
                case "delete_cost" -> deleteCost(args);
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

    // ========== Cost ==========

    private String listCosts(Map<String, Object> args) {
        String wellcode = getString(args, "wellcode");
        String preunit = getString(args, "preunit");
        String content = getString(args, "content");
        List<Cost> list = costService.list(wellcode, preunit, content);
        return toJson(list);
    }

    private String getCost(Map<String, Object> args) {
        String code = getString(args, "code");
        Cost cost = costService.getByCode(code);
        return cost != null ? toJson(cost) : "未找到费用编号为 " + code + " 的成本记录";
    }

    private String addCost(Map<String, Object> args) {
        Cost cost = Cost.builder()
                .code(getString(args, "code"))
                .preunit(getString(args, "preunit"))
                .wellcode(getString(args, "wellcode"))
                .premoney(getBigDecimal(args, "premoney"))
                .person(getString(args, "person"))
                .predate(getLocalDate(args, "predate"))
                .startdate(getLocalDate(args, "startdate"))
                .finish(getLocalDate(args, "finish"))
                .settleunit(getString(args, "settleunit"))
                .content(getString(args, "content"))
                .matcost(getBigDecimal(args, "matcost"))
                .humancost(getBigDecimal(args, "humancost"))
                .equipcost(getBigDecimal(args, "equipcost"))
                .othercost(getBigDecimal(args, "othercost"))
                .settlecost(getBigDecimal(args, "settlecost"))
                .settleperson(getString(args, "settleperson"))
                .settledate(getLocalDate(args, "settledate"))
                .finalcost(getBigDecimal(args, "finalcost"))
                .finalperson(getString(args, "finalperson"))
                .finaldate(getLocalDate(args, "finaldate"))
                .build();
        costService.add(cost);
        return "操作成功: 已新增成本记录，编号 = " + cost.getCode();
    }

    private String updateCost(Map<String, Object> args) {
        String code = getString(args, "code");
        Cost cost = costService.getByCode(code);
        if (cost == null) return "未找到费用编号为 " + code + " 的成本记录";
        if (args.containsKey("preunit")) cost.setPreunit(getString(args, "preunit"));
        if (args.containsKey("wellcode")) cost.setWellcode(getString(args, "wellcode"));
        if (args.containsKey("premoney")) cost.setPremoney(getBigDecimal(args, "premoney"));
        if (args.containsKey("person")) cost.setPerson(getString(args, "person"));
        if (args.containsKey("predate")) cost.setPredate(getLocalDate(args, "predate"));
        if (args.containsKey("startdate")) cost.setStartdate(getLocalDate(args, "startdate"));
        if (args.containsKey("finish")) cost.setFinish(getLocalDate(args, "finish"));
        if (args.containsKey("settleunit")) cost.setSettleunit(getString(args, "settleunit"));
        if (args.containsKey("content")) cost.setContent(getString(args, "content"));
        if (args.containsKey("matcost")) cost.setMatcost(getBigDecimal(args, "matcost"));
        if (args.containsKey("humancost")) cost.setHumancost(getBigDecimal(args, "humancost"));
        if (args.containsKey("equipcost")) cost.setEquipcost(getBigDecimal(args, "equipcost"));
        if (args.containsKey("othercost")) cost.setOthercost(getBigDecimal(args, "othercost"));
        if (args.containsKey("settlecost")) cost.setSettlecost(getBigDecimal(args, "settlecost"));
        if (args.containsKey("settleperson")) cost.setSettleperson(getString(args, "settleperson"));
        if (args.containsKey("settledate")) cost.setSettledate(getLocalDate(args, "settledate"));
        if (args.containsKey("finalcost")) cost.setFinalcost(getBigDecimal(args, "finalcost"));
        if (args.containsKey("finalperson")) cost.setFinalperson(getString(args, "finalperson"));
        if (args.containsKey("finaldate")) cost.setFinaldate(getLocalDate(args, "finaldate"));
        costService.update(cost);
        return "操作成功: 已更新成本记录 (编号 = " + code + ")";
    }

    private String deleteCost(Map<String, Object> args) {
        String code = getString(args, "code");
        costService.delete(code);
        return "操作成功: 已删除成本记录 (编号 = " + code + ")";
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
