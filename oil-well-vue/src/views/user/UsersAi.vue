<template>
  <page-container-view title3="AI助手">
    <div class="ai-chat-container">
      <div class="chat-header">
        <h1>豆包AI聊天助手</h1>
      </div>

      <div class="chat-messages">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          class="message"
          :class="msg.type"
        >
          <div class="avatar-container">
            <div class="avatar" :class="msg.type" v-if="msg.type === 'ai'">
              <i class="el-icon-chat-line-round"></i>
            </div>
            <div class="avatar" :class="msg.type" v-if="msg.type === 'user'">
              <i class="el-icon-user"></i>
            </div>
          </div>
          <div class="message-content-wrapper">
            <div class="message-content">{{ msg.content }}</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="question"
          type="textarea"
          :rows="3"
          placeholder="请输入您的问题..."
          @keyup.enter="sendQuestion"
        ></el-input>
        <el-button type="primary" :loading="isLoading" @click="sendQuestion">发送</el-button>
      </div>
    </div>
  </page-container-view>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { chatWithAI } from "@/api/aiChat";
import { ElMessage } from "element-plus";

const messages = ref([]);
const question = ref("");
const isLoading = ref(false);
let abortController = null;
// 发送问题到AI
const sendQuestion = async () => {
  if (!question.value.trim()) {
    ElMessage.warning("请输入问题内容");
    return;
  }

  if (abortController) {
    abortController.abort();
  }
  abortController = new AbortController();

  addMessage("user", question.value);
  isLoading.value = true;
  addMessage("ai", "思考中...");

  try {
    const response = await chatWithAI(question.value);

    console.log("response:", response);
    const answer = response.data?.answer || "抱歉，出错了";
    console.log("answer:", answer);
    if (response) {
      messages.value[messages.value.length - 1].content = answer;
      ElMessage.success("获取回答成功");
    } else {
      handleErrorResponse(response);
    }
    
  } catch (error) {
    if (error.name === "AbortError") {
      console.log("请求已取消");
    } else {
      handleNetworkError(error);
    }
  } finally {
    isLoading.value = false;
    question.value = "";
  }
};

// 添加消息到对话
const addMessage = (type, content) => {
  if (typeof type !== "string" || typeof content !== "string") {
    console.error("消息类型或内容格式错误");
    return;
  }
  messages.value.push({
    type,
    content,
    timestamp: new Date(),
  });
};

// 处理JSON解析错误
const handleJsonParseError = (answer, error) => {
  console.error("JSON解析失败", error);
  messages.value[messages.value.length - 1].content = `回答格式异常: ${answer}`;
  ElMessage.error("获取的回答格式有误，请稍后再试");
};

// 处理无效数据
const handleInvalidData = (response) => {
  console.error("响应数据无效:", response);
  messages.value[messages.value.length - 1].content =
    "抱歉，获取的回答数据格式有误";
  ElMessage.error("获取回答失败，数据格式异常");
};

// 处理错误响应
const handleErrorResponse = (response) => {
  console.log("错误响应:", response);
  const errorMsg =
    response?.message ||
    `请求失败，状态码: ${response?.status || "未知"}`;
  messages.value[messages.value.length - 1].content = "抱歉，获取回答失败";
  ElMessage.error(errorMsg);
};

// 处理网络错误
const handleNetworkError = (error) => {
  //console.log("返回数据:", error);

  let errorMsg = "网络连接失败，请检查您的网络";
  let answerContent = "抱歉，获取回答失败";

  if (error) {
    errorMsg = error.message || errorMsg;
    //console.log("error.data?.answer:", error.data?.answer);
    answerContent = error.data?.answer || answerContent;
    //console.log("errorMsg:", errorMsg);
    //console.log("answerContent:", answerContent);
    messages.value[messages.value.length - 1].content = answerContent;
    
  } else if (error.request) {
    errorMsg = "请求已发出，但未收到响应";
  } else {
    errorMsg = error.message || errorMsg;
  }
  //ElMessage.success(errorMsg);
};

onMounted(() => {
  addMessage("ai", "您好！我是豆包AI助手，请问有什么可以帮您？");
});

onUnmounted(() => {
  if (abortController) {
    abortController.abort();
  }
});
</script>

<style scoped>
.ai-chat-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.chat-header {
  text-align: center;
  margin-bottom: 20px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.message {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.message.user {
  flex-direction: row-reverse; /* 用户消息从右向左布局 */
  justify-content: flex-start;
}

.message.ai {
  flex-direction: row; /* AI消息从左向右布局 */
  justify-content: flex-start;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
}

.message.user .avatar {
  order: 2; /* 用户头像在右侧 */
  margin-left: 10px;
}

.message.ai .avatar {
  order: 1; /* AI头像在左侧 */
  margin-right: 10px;
}

.message-content-wrapper {
  max-width: 70%;
  position: relative;
}

.message-content {
  padding: 12px 15px;
  border-radius: 10px;
  position: relative;
}

.message.user .message-content {
  background-color: #409eff;
  color: white;
  border-bottom-right-radius: 2px;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  border-bottom-left-radius: 10px;
  margin-left: auto; /* 用户消息靠右 */
}

.message.ai .message-content {
  background-color: #ffffff;
  color: #333;
  border-bottom-left-radius: 2px;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
  margin-right: auto; /* AI消息靠左 */
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-input {
  display: flex;
  gap: 10px;
}

.chat-input .el-input {
  flex: 1;
}
</style>