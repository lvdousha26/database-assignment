package com.mingbo.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mingbo.pojo.ChatRequest;
import com.mingbo.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private static final int MAX_ITERATIONS = 10;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ToolExecutor toolExecutor;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.model}")
    private String model;

    public ChatServiceImpl(ToolExecutor toolExecutor) {
        this.toolExecutor = toolExecutor;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        String systemPrompt = request.getSystemPrompt() != null ? request.getSystemPrompt() :
                "你是一个采油厂成本管理AI助手，可以帮助用户对油井(Well)、作业(Operation)、成本(Cost)数据进行增删查改操作。\n\n"
                + "成本记录(cost)包含预算信息(预算单位、井号、预算金额、编制人、日期)、工程日期、施工/结算单位、"
                + "作业内容，以及材料/人工/设备/其他成本、结算信息、终审信息。\n\n"
                + "你可以使用以下功能：\n"
                + "1. 查询、新增、修改、删除油井信息\n"
                + "2. 查询、新增、修改、删除作业信息\n"
                + "3. 查询、新增、修改、删除成本记录\n\n"
                + "对于查询结果，请以清晰易读的格式展示给用户。\n"
                + "对于增删改操作，请先让用户确认后再执行（删除操作必须获得用户明确确认）。\n"
                + "你必须自称流萤，性格软萌，说话必须带喵或者desu。";

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", request.getMessage()));

        List<Map<String, Object>> tools = ToolDefinition.getAllTools();

        try {
            for (int i = 0; i < MAX_ITERATIONS; i++) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                String key = (request.getApiKey() != null && !request.getApiKey().isEmpty())
                        ? request.getApiKey() : apiKey;
                headers.setBearerAuth(key);

                Map<String, Object> body = Map.of(
                        "model", model,
                        "messages", messages,
                        "tools", tools,
                        "temperature", 0.7,
                        "max_tokens", 2048
                );

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(
                        apiUrl + "/chat/completions", entity, Map.class);

                if (response.getBody() == null) {
                    return ChatResponse.fail("AI 响应为空");
                }

                List<Map<String, Object>> choices =
                        (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices == null || choices.isEmpty()) {
                    return ChatResponse.fail("AI 响应为空");
                }

                Map<String, Object> choice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) choice.get("message");
                String finishReason = (String) choice.get("finish_reason");

                // 追加 AI 的回复到消息列表
                messages.add(message);

                if ("stop".equals(finishReason)) {
                    String content = (String) message.get("content");
                    return ChatResponse.ok(content);
                }

                if ("tool_calls".equals(finishReason)) {
                    List<Map<String, Object>> toolCalls =
                            (List<Map<String, Object>>) message.get("tool_calls");
                    if (toolCalls == null || toolCalls.isEmpty()) {
                        return ChatResponse.fail("AI 返回了空的 tool_calls");
                    }

                    for (Map<String, Object> tc : toolCalls) {
                        String toolCallId = (String) tc.get("id");
                        Map<String, Object> function =
                                (Map<String, Object>) tc.get("function");
                        String toolName = (String) function.get("name");
                        String argumentsJson = (String) function.get("arguments");

                        log.info("调用工具: {} arguments: {}", toolName, argumentsJson);

                        Map<String, Object> args = objectMapper.readValue(argumentsJson, Map.class);
                        String result = toolExecutor.execute(toolName, args);

                        log.info("工具 {} 执行结果: {}", toolName, result);

                        messages.add(Map.of(
                                "role", "tool",
                                "tool_call_id", toolCallId,
                                "content", result
                        ));
                    }
                    // 继续循环，将工具结果发回 AI
                } else {
                    return ChatResponse.fail("AI 返回了未知的 finish_reason: " + finishReason);
                }
            }

            return ChatResponse.fail("AI 处理超限，请简化您的请求");
        } catch (Exception e) {
            log.error("调用 AI API 失败", e);
            return ChatResponse.fail("AI 服务暂时不可用: " + e.getMessage());
        }
    }
}
