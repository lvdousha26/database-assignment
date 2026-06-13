<script setup>
import { ref } from "vue";
import {
  employeeSelectAllService,
  employeeSelectOneService,
  employeeAddService,
  employeeEditService,
  employeeSelectByPageAndConditionService,
  employeeDelOneService,
  employeeDelIdsService,
	employeeEnableService
} from "@/api/employee.js";
import { useUserStore } from "@/stores";
import { useRouter } from "vue-router";
import { onMounted } from "vue";
import {
  SwitchButton,
  Avatar,
  SuccessFilled,
  Stamp,
  Menu,
  Delete,
  Edit,
} from "@element-plus/icons-vue";
//显示的用户名
const welcomeName = ref("");
const title = ref("");
// 每页显示的条数
const pageSize = ref(5);
// 总记录数
const totalCount = ref(100);
// 当前页码
const currentPage = ref(1);
// 添加数据对话框是否展示的标记
const dialogVisible = ref(false);
const dialogVisibleTwo = ref(false);
//搜索数据
const employeeSearch = ref({
  username: "",
});

// 学生模型数据
const employee = ref({
  status: "",
  username: "",
  avatar: "",
  id: "",
  gender: "",
  addr: "",
  password: ""
});
// 被选中的id数组
const selectedIds = ref([]);
// 复选框选中数据集合
const multipleSelection = ref([]);
// 表格数据
const tableData = ref([
  {
    username: "zhangsan",
    avatar: "@/assets/logo.svg",
    gender: "男",
    status: "1",
  },
  {
    username: "lisi",
    avatar: "@/assets/logo.svg",
    gender: "女",
    status: "1",
  },
  {
    username: "wangwu",
    avatar: "@/assets/logo.svg",
    gender: "男",
    status: "1",
  },
  {
    username: "zhaoliu",
    avatar: "@/assets/logo.svg",
    gender: "女",
    status: "1",
  },
]);
const uploadRef = ref();
const selectedFile = ref(null);
// 自动上传头像地址
const uploadUrl = ref("http://localhost:8080/uploads/avatar");

const onSelectFile = (uploadFile) => {
  selectedFile.value = uploadFile.raw;
  const reader = new FileReader();
  reader.readAsDataURL(uploadFile.raw);
  // reader.onload = () => {
  // employee.value.avatar = reader.result;
  // };
};
// 上传头像成功后的回调函数
const onUploadSuccess = (response, file, fileList) => {
  // 处理上传成功的逻辑
  console.log("头像上传成功:", response);
	
  // 假设服务器返回的响应中有图片的 URL
  employee.value.avatar = response.data;
	console.log(employee.value.avatar);
	
};

onMounted(() => {
  //当页面加载完成后，发送异步请求，获取数据
  selectAll();
  const userStore = useUserStore();
  const userInfo = userStore.user;

  welcomeName.value = userInfo.username;
});
const cancel = () => {
  dialogVisible.value = false;
  dialogVisibleTwo.value = false;
  resetemployee();
};
// 点击新增按钮
const dialogBox = (n) => {
  title.value = n;
  dialogVisible.value = true;
};
const contentById = async (id, n) => {
  title.value = n;
  dialogVisibleTwo.value = true;
  // 发送ajax请求，添加数据
  const resp = await employeeSelectOneService(id);

  // 将返回的数据赋值给employee对象
  employee.value = resp.data.data;
};
const editById = async (id, n) => {
  title.value = n;
  dialogVisible.value = true;
  // 发送ajax请求，添加数据
  const resp = await employeeSelectOneService(id);

  // 将返回的数据赋值给employee对象
  employee.value = resp.data.data;
};
// 查询分页数据
const selectAll = async () => {
  resetemployeeSearch();
  const resp = await employeeSelectAllService({
    currentPage: currentPage.value,
    pageSize: pageSize.value,
    username: 'admin'
  });
  //设置表格数据
  tableData.value = resp.data.data.records; // {rows:[],totalCount:100}
  //设置总记录数
  totalCount.value = resp.data.data.total;
};

const tableRowClassName = ({ row, rowIndex }) => {
  if (rowIndex === 1) {
    return "warning-row";
  } else if (rowIndex === 3) {
    return "success-row";
  }
  return "";
};
// 复选框选中后执行的方法
const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};
// 查询方法
const onSubmit = () => {
  selectByPageAndCondition();
};
// 添加或修改学生
const addemployee = async (title) => {
  try {
    let resp;
    if (title == "新增") {
      resp = await employeeAddService(employee.value);
    } else if (title == "修改") {
      resp = await employeeEditService(employee.value);
    }

    if (resp.data && resp.data.msg === "success") {
      dialogVisible.value = false; // 关闭窗口
      selectAll(); // 重新查询数据
      resetemployee();
      ElMessage.success(title === "新增" ? "添加成功" : "修改成功");
    } else {
      ElMessage.error(resp.data.msg || "操作失败，请联系管理员");
    }
  } catch (err) {
    console.error("操作失败:", err);
    ElMessage.error("操作失败，请稍后再试");
  }
};
const selectByPageAndCondition = async () => {
  const resp = await employeeSelectByPageAndConditionService({
    currentPage: currentPage.value,
    pageSize: pageSize.value,
    username: employeeSearch.value.username,
  });
  //设置表格数据
  tableData.value = resp.data.data.records; // {rows:[],totalCount:100}
  //设置总记录数
  totalCount.value = resp.data.data.total;
};
// 重置学生数据
const resetemployeeSearch = () => {
  employeeSearch.value.username = "";
};
const resetemployee = () => {
  employee.value.id = "",
  employee.value.status = "",
  employee.value.username = "",
  employee.value.avatar = "",
  employee.value.gender = "",
  employee.value.addr = "",
  employee.value.password = ""
};
//根据id删除一条数据
const deleteById = (id) => {
  // 弹出确认提示框

  ElMessageBox.confirm("此操作将删除该数据, 是否继续?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      //用户点击确认按钮

      // 发送ajax请求，添加数据
      const resp = await employeeDelOneService(id);

      if (resp.data.msg == "success") {
        //删除成功

        // 重新查询数据
        selectAll();
        // 弹出消息提示
        ElMessage({
          message: "操作成功",
          type: "success",
        });
      }
    })
    .catch(() => {
      //用户点击取消按钮

      ElMessage({
        type: "info",
        message: "已取消删除",
      });
    });
};

//分页
const handleSizeChange = (val) => {
  //console.log(`每页 ${val} 条`);
  // 重新设置每页显示的条数
  pageSize.value = val;
  selectByPageAndCondition();
};
const handleCurrentChange = (val) => {
  //console.log(`当前页: ${val}`);
  // 重新设置当前页码
  currentPage.value = val;
  selectByPageAndCondition();
};
const router = useRouter();
const removeToken = () => {
  const userStore = useUserStore();
  userStore.removeToken();
  router.push("/login");
};

//启用禁用学生账号
const enableById = async (status, id) => {
  try {
    // 弹出确认提示框
    await ElMessageBox.confirm("此操作将启用/禁用该用户账号, 是否继续?", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 用户点击确认按钮
    status = status === 1 ? 0 : 1;

    // 发送ajax请求，添加数据
    const resp = await employeeEnableService(status, id);

    if (resp.data.data.msg === "success") {
      // 操作成功
      // 更新表格中对应行的状态
      const index = tableData.value.findIndex(item => item.id === id);
      if (index !== -1) {
        tableData.value[index].status = newStatus;
      }
      // 重新查询数据
      selectAll();
      // 弹出消息提示
      ElMessage({
        message: "操作成功",
        type: "success",
      });
    } else {
      // 服务器返回非成功信息
      ElMessage({
        message: "操作失败，请稍后重试",
        type: "error",
      });
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户点击取消按钮
      ElMessage({
        type: "info",
        message: "已取消操作",
      });
    } else {
      // 请求失败
      ElMessage({
        message: "请求出错，请稍后重试",
        type: "error",
      });
    }
  }
};
//批量删除
const deleteByIds = async () => {
  try {
    // 弹出确认提示框
    await ElMessageBox.confirm("此操作将删除该数据, 是否继续?", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 用户点击确认按钮
    // 1. 创建 id 数组 [1,2,3], 从 this.multipleSelection 获取即可
    for (let i = 0; i < multipleSelection.value.length; i++) {
      const selectionElement = multipleSelection.value[i];
      selectedIds.value[i] = selectionElement.id;
    }

    // 2. 发送 AJAX 请求
    const resp = await employeeDelIdsService(selectedIds.value);

    if (resp.data.msg === "success") {
      // 删除成功
      // 重新查询数据
      selectAll();
      // 弹出消息提示
      ElMessage({
        message: "删除成功",
        type: "success",
      });
    } else {
      console.log(resp.data);
      ElMessage({
        message: "删除失败，请稍后重试",
        type: "error",
      });
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户点击取消按钮
      ElMessage({
        type: "info",
        message: "已取消删除",
      });
    } else {
      // 请求出错
      ElMessage({
        message: "请求出错，请稍后重试",
        type: "error",
      });
    }
  }
};
</script>

<template>
  <page-container title2="用户管理">
    <el-main class="main1">
      <!--搜索表单-->
      <el-form :inline="true" :model="employeeSearch" class="demo-form-inline">
        <el-form-item label="用户名">
          <el-input
            v-model="employeeSearch.username"
            placeholder="用户名"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">查询</el-button>
        </el-form-item>
      </el-form>

      <!--按钮-->

      <el-row>
        <el-button type="danger" plain @click="deleteByIds">批量删除</el-button>
        <el-button type="primary" plain @click="dialogBox('新增')"
          >新增</el-button
        >
      </el-row>
      <!--添加数据对话框表单-->
      <el-dialog
        v-model="dialogVisible"
        :title="title"
        width="600"
        align-center
      >
        <el-form ref="form" :model="employee" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="employee.username"></el-input>
          </el-form-item>
          <el-form-item label="密码">
            <el-input type="password" v-model="employee.password"></el-input>
          </el-form-item>
          <el-form-item label="">
            <el-upload
              ref="uploadRef"
              :auto-upload="true"
              class="avatar-uploader"
              :show-file-list="false"
              :on-change="onSelectFile"
              :on-success="onUploadSuccess"
              :on-error="onUploadError"
              :action="uploadUrl"
            >
              <img
                :src="employee.avatar"
                class="avatar"
              />
            </el-upload>
          </el-form-item>

          <el-form-item label="用户性别">
            <el-input v-model="employee.gender"></el-input>
          </el-form-item>

          <el-form-item label="用户地址">
            <el-input type="textarea" v-model="employee.addr"></el-input>
          </el-form-item>

          <el-form-item label="用户状态">
            <el-switch
              v-model="employee.status"
              active-value= 1
              inactive-value= 0
            ></el-switch>
          </el-form-item>
          <el-form-item>
            <div class="dialog-footer">
              <el-button
                style="margin-left: 300px"
                type="primary"
                @click="addemployee(title)"
                >提交</el-button
              >
              <el-button @click="cancel">取消</el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-dialog>
      <!-- 详情dialog -->
      <el-dialog
        v-model="dialogVisibleTwo"
        :title="title"
        width="600"
        align-center
      >
        <el-form ref="form" :model="employee" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="employee.username"></el-input>
          </el-form-item>
          <el-form-item label="用户头像">
            <img :src="employee.avatar || '@/assets/logo.svg'" class="avatar" />
          </el-form-item>

          <el-form-item label="用户性别">
            <el-input v-model="employee.gender"></el-input>
          </el-form-item>

          <el-form-item label="用户地址">
            <el-input type="textarea" v-model="employee.addr"></el-input>
          </el-form-item>

          <el-form-item label="用户状态">
            <el-switch
              v-model="employee.status"
              active-value="1"
              inactive-value="0"
            ></el-switch>
          </el-form-item>
          <el-form-item>
            <div class="dialog-footer">
              <el-button
                type="primary"
                style="margin-left: 300px"
                @click="cancel"
                >返回</el-button
              >
            </div>
          </el-form-item>
        </el-form>
      </el-dialog>

      <!--表格-->
      <el-table
        :data="tableData"
        style="width: 100%"
        :row-class-name="tableRowClassName"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"> </el-table-column>
        <el-table-column type="index" width="50"> </el-table-column>

        <el-table-column prop="username" label="用户名" align="center">
        </el-table-column>
        <el-table-column prop="avatar" label="用户头像" align="center" width="80">
          <template #default="scope">
						<el-row>
							<el-col>
            <img
              :src="scope.row.avatar"
              class="avatar-icon"
            />
						</el-col>
						</el-row>
          </template>
        </el-table-column>
        <el-table-column prop="gender" align="center" label="用户性别">
        </el-table-column>
        <el-table-column prop="status" align="center" label="用户状态">
					<template #default="scope">
						<el-row>
							<el-col>
                <el-tag :type="scope.row.status == 1 ? 'success' : 'danger'">
						      {{ scope.row.status == 1 ? "启用" : "禁用" }}
                </el-tag>
							</el-col>
						</el-row>
					</template>
        </el-table-column>

        <el-table-column align="center" label="操作">
          <template #default="scope">
            <el-row>
							<el-col>
              <el-button @click="contentById(scope.row.id, '详情')" type="warning"
                >详情
              </el-button>
              <el-button @click="editById(scope.row.id, '修改')" type="primary" :disabled="scope.row.username == 'admin'"
                >修改
              </el-button>
              <el-button @click="deleteById(scope.row.id)" type="danger" :disabled="scope.row.username == 'admin'"
                >删除</el-button
              >
							<el-button @click="enableById(scope.row.status, scope.row.id)" :type="scope.row.status == 1 ? 'info' : 'success'" :disabled="scope.row.username == 'admin'">
								{{ scope.row.status == 1 ? "禁用" : "启用" }}
							</el-button>
							</el-col>
            </el-row>
          </template>
        </el-table-column>
      </el-table>

      <!--分页工具条-->
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[5, 10, 15, 20]"
        :page-size="5"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount"
      >
      </el-pagination>
    </el-main>
  </page-container>
</template>


<style scoped>
.aside {
  width: 200px;
  display: block;
  background-color: #d3dce6;
}
.main1 {
  width: 100%;
  display: block;
}
.aside-color {
  box-sizing: border-box;
  height: 60px;
  line-height: 60px;
  text-indent: 0.5em;
  font-size: 20px;
}
.aside-color a {
  color: #333;
  text-decoration: none;
}
.el-table .warning-row {
  background: oldlace;
}

.el-table .success-row {
  background: #f0f9eb;
}
.aside-color .el-icon {
  vertical-align: middle;
}
.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
.avatar-icon {
	width: 60px;
	height: 60px;
	display: block;
}
.el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}
.el-upload:hover {
  border-color: var(--el-color-primary);
}
.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
}
</style>