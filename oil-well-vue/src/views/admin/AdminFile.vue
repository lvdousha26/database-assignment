<template>
  <div class="file-manager-container">
    <div class="file-manager">
      <div class="upload-section">
        <div
            class="drop-area"
            @dragover.prevent="dragOver = true"
            @dragleave="dragOver = false"
            @drop.prevent="handleDrop"
            :class="{ 'drag-over': dragOver }"
        >
          <div v-if="!isUploading" class="drop-content">
            <div class="upload-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="17 8 12 3 7 8"></polyline>
                <line x1="12" y1="3" x2="12" y2="15"></line>
              </svg>
            </div>
            <p class="drop-text">拖拽文件到此处上传</p>
            <p class="or-text">或</p>
            <input
                type="file"
                id="fileInput"
                @change="handleFileSelect"
                style="display: none"
            />
            <button class="select-file-btn" @click="triggerFileInput">选择文件</button>
          </div>
          <div v-else class="upload-progress">
            <p class="uploading-text">上传中... {{ uploadProgress }}%</p>
            <div class="progress-container">
              <progress :value="uploadProgress" max="100"></progress>
            </div>
          </div>
        </div>

        <div class="file-info" v-if="selectedFile">
          <h3 class="file-info-title">文件信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">文件名:</span>
              <span class="info-value">{{ selectedFile.name }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">文件类型:</span>
              <span class="info-value">{{ getFileType(selectedFile.name) }}</span>
            </div>
          </div>
          <div class="form-group">
            <label for="fileDescription" class="form-label">文件描述:</label>
            <input
                type="text"
                id="fileDescription"
                v-model="fileDescription"
                placeholder="输入文件描述"
                class="form-input"
            />
          </div>
          <button
              class="upload-btn"
              @click="uploadFile"
              :disabled="!fileDescription || isUploading"
          >
            上传文件
          </button>
        </div>
      </div>

      <div class="file-list-section">
        <div class="section-header">
          <h2 class="section-title">文件列表</h2>
          <div class="pagination-controls">
            <div class="pagination-buttons">
              <button
                  class="pagination-btn"
                  @click="prevPage"
                  :disabled="currentPage === 1"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="15 18 9 12 15 6"></polyline>
                </svg>
                上一页
              </button>
              <span class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
              <button
                  class="pagination-btn"
                  @click="nextPage"
                  :disabled="currentPage === totalPages"
              >
                下一页
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="9 18 15 12 9 6"></polyline>
                </svg>
              </button>
            </div>
            <select v-model="pageSize" @change="fetchFiles" class="page-size-select">
              <option value="5">每页 5 项</option>
              <option value="10">每页 10 项</option>
              <option value="20">每页 20 项</option>
              <option value="50">每页 50 项</option>
            </select>
          </div>
        </div>

        <div class="table-container">
          <table class="file-table">
            <thead>
            <tr>
              <th>文件名</th>
              <th>文件类型</th>
              <th>描述</th>
              <th>上传时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="file in fileList" :key="file.id">
              <td class="file-name">
                <div class="file-icon">
                  <svg v-if="file.resourceType === 'pdf'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#f40f0f" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                    <polyline points="14 2 14 8 20 8"></polyline>
                    <path d="M10 12v1.5a1.5 1.5 0 0 0 3 0V12a1.5 1.5 0 0 1 3 0v4.5a1.5 1.5 0 0 1-3 0V18"></path>
                    <line x1="7" y1="15" x2="7" y2="12"></line>
                  </svg>
                  <svg v-else-if="['jpg', 'jpeg', 'png', 'gif'].includes(file.resourceType)" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#4CAF50" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                    <circle cx="8.5" cy="8.5" r="1.5"></circle>
                    <polyline points="21 15 16 10 5 21"></polyline>
                  </svg>
                  <svg v-else-if="['doc', 'docx'].includes(file.resourceType)" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#2196F3" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                    <polyline points="14 2 14 8 20 8"></polyline>
                    <line x1="16" y1="13" x2="8" y2="13"></line>
                    <line x1="16" y1="17" x2="8" y2="17"></line>
                    <polyline points="10 9 9 9 8 9"></polyline>
                  </svg>
                  <svg v-else-if="['xls', 'xlsx'].includes(file.resourceType)" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#4CAF50" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                    <polyline points="14 2 14 8 20 8"></polyline>
                    <path d="M8 13v4"></path>
                    <path d="M16 13v4"></path>
                    <path d="M12 13v4"></path>
                  </svg>
                  <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#9E9E9E" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                    <polyline points="14 2 14 8 20 8"></polyline>
                  </svg>
                </div>
                {{ file.originalName }}
              </td>
              <td class="file-type">{{ file.resourceType }}</td>
              <td class="file-desc">{{ file.description }}</td>
              <td class="upload-time">{{ formatDate(file.uploadTime) }}</td>
              <td>
                  <span :class="{'status-badge': true, 'status-active': file.status === 1, 'status-inactive': file.status === 0}">
                    {{ file.status === 1 ? '启用' : '禁用' }}
                  </span>
              </td>
              <td class="actions">
                <button @click="confirmDelete(file.id)" class="delete-btn">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="3 6 5 6 21 6"></polyline>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                    <line x1="10" y1="11" x2="10" y2="17"></line>
                    <line x1="14" y1="11" x2="14" y2="17"></line>
                  </svg>
                  删除
                </button>
              </td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { uploadFile, getFileList, deleteFile } from '@/api/file_operation'
import { useUserStore } from '@/stores/modules/user'

export default {
  name: 'FileManager',
  setup() {
    const userStore = useUserStore()
    return { userStore }
  },
  data() {
    return {
      dragOver: false,
      selectedFile: null,
      fileDescription: '',
      isUploading: false,
      uploadProgress: 0,
      fileList: [],
      currentPage: 1,
      pageSize: 10,
      totalCount: 0
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalCount / this.pageSize)
    },
    userId() {
      return this.userStore.user.id || -1
    }
  },
  created() {
    this.fetchFiles()
  },
  methods: {
    triggerFileInput() {
      document.getElementById('fileInput').click()
    },
    handleFileSelect(event) {
      this.selectedFile = event.target.files[0]
      this.fileDescription = this.selectedFile.name.split('.')[0]
    },
    handleDrop(event) {
      this.dragOver = false
      const files = event.dataTransfer.files
      if (files.length > 0) {
        this.selectedFile = files[0]
        this.fileDescription = this.selectedFile.name.split('.')[0]
      }
    },
    getFileType(filename) {
      const parts = filename.split('.')
      return parts.length > 1 ? parts.pop().toLowerCase() : '未知'
    },
    async uploadFile() {
      if (!this.selectedFile || !this.fileDescription) return

      this.isUploading = true
      this.uploadProgress = 0

      const metaData = {
        id: this.userId,
        resourceType: this.getFileType(this.selectedFile.name),
        description: this.fileDescription
      }

      try {
        const progressInterval = setInterval(() => {
          if (this.uploadProgress < 90) {
            this.uploadProgress += 10
          }
        }, 300)

        const response = await uploadFile(metaData, this.selectedFile)
        clearInterval(progressInterval)
        this.uploadProgress = 100

        if (response.data.code === '1') {
          this.$message.success('文件上传成功')
          this.selectedFile = null
          this.fileDescription = ''
          await this.fetchFiles()
        } else {
          this.$message.error('文件上传失败: ' + response.msg)
        }
      } catch (error) {
        this.$message.error('上传过程中出错: ' + error.message)
      } finally {
        setTimeout(() => {
          this.isUploading = false
          this.uploadProgress = 0
        }, 500)
      }
    },
    async fetchFiles() {
      try {
        const response = await getFileList(this.userId, this.currentPage, this.pageSize)
        if (response.data.code === '1') {
          this.fileList = response.data.data.rows
          this.totalCount = response.data.data.totalCount
        } else {
          this.$message.error('获取文件列表失败: ' + response.data.msg)
        }
      } catch (error) {
        this.$message.error('获取文件列表时出错: ' + error.message)
      }
    },
    async confirmDelete(fileId) {
      this.$confirm('确定要删除这个文件吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await deleteFile(fileId)
          if (response.data.code === '1') {
            this.$message.success('文件删除成功')
            await this.fetchFiles()
          } else {
            this.$message.error('文件删除失败: ' + response.msg)
          }
        } catch (error) {
          this.$message.error('删除文件时出错: ' + error.message)
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.fetchFiles()
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.fetchFiles()
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleString()
    }
  }
}
</script>

<style scoped>
.file-manager-container {
  width: 100%;
  padding: 20px;
  background-color: #f8fafc;
}

.file-manager {
  max-width: 100%;
  margin: 0 auto;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.upload-section {
  padding: 24px;
  border-bottom: 1px solid #e2e8f0;
}

.drop-area {
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  padding: 40px;
  text-align: center;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  background-color: #f8fafc;
}

.drop-area.drag-over {
  border-color: #3b82f6;
  background-color: rgba(59, 130, 246, 0.05);
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
}

.drop-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.upload-icon {
  color: #3b82f6;
  margin-bottom: 12px;
}

.drop-text {
  font-size: 18px;
  font-weight: 500;
  color: #334155;
  margin: 0;
}

.or-text {
  font-size: 14px;
  color: #64748b;
  margin: 8px 0;
}

.select-file-btn {
  padding: 10px 24px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.select-file-btn:hover {
  background-color: #2563eb;
  transform: translateY(-1px);
}

.upload-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.uploading-text {
  font-size: 16px;
  font-weight: 500;
  color: #334155;
  margin: 0;
}

.progress-container {
  width: 100%;
  max-width: 400px;
}

progress {
  width: 100%;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
}

progress::-webkit-progress-bar {
  background-color: #e2e8f0;
  border-radius: 4px;
}

progress::-webkit-progress-value {
  background-color: #3b82f6;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.file-info {
  margin-top: 24px;
}

.file-info-title {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 4px;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #334155;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.upload-btn {
  padding: 10px 24px;
  background-color: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  width: 100%;
}

.upload-btn:hover:not(:disabled) {
  background-color: #059669;
  transform: translateY(-1px);
}

.upload-btn:disabled {
  background-color: #cbd5e1;
  cursor: not-allowed;
}

.file-list-section {
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.pagination-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-btn {
  padding: 8px 12px;
  background-color: white;
  color: #3b82f6;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #f8fafc;
  border-color: #cbd5e1;
}

.pagination-btn:disabled {
  color: #cbd5e1;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #64748b;
}

.page-size-select {
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
  color: #334155;
  background-color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.page-size-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.table-container {
  width: 100%;
  overflow-x: auto;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.file-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 800px;
}

.file-table th {
  background-color: #f8fafc;
  color: #64748b;
  font-weight: 600;
  font-size: 14px;
  text-align: left;
  padding: 12px 16px;
  border-bottom: 1px solid #e2e8f0;
}

.file-table td {
  padding: 12px 16px;
  font-size: 14px;
  color: #334155;
  border-bottom: 1px solid #e2e8f0;
}

.file-table tr:last-child td {
  border-bottom: none;
}

.file-table tr:hover {
  background-color: #f8fafc;
}

.file-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.file-icon {
  display: flex;
  align-items: center;
}

.file-type {
  text-transform: uppercase;
  font-family: monospace;
  color: #64748b;
}

.file-desc {
  max-width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.upload-time {
  white-space: nowrap;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-active {
  background-color: #dcfce7;
  color: #166534;
}

.status-inactive {
  background-color: #fee2e2;
  color: #991b1b;
}

.actions {
  white-space: nowrap;
}

.delete-btn {
  padding: 6px 12px;
  background-color: #fee2e2;
  color: #b91c1c;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.delete-btn:hover {
  background-color: #fecaca;
  color: #991b1b;
}

@media (max-width: 768px) {
  .upload-section {
    padding: 16px;
  }

  .drop-area {
    padding: 24px;
  }

  .file-list-section {
    padding: 16px;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .pagination-controls {
    width: 100%;
    justify-content: space-between;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
