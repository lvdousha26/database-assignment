<script setup>
defineProps({
  title3: {
    required: true,
    type: String
  }
})
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>{{ title3 }}</span>
        <div class="extra">
          <slot name="extra"></slot>
        </div>
      </div>
    </template>
    <slot></slot>
  </el-card>
</template>

<style lang="scss" scoped>
.page-container {
  
  width: 1200px;
  min-height: calc(100vh - 100px);
  overflow: auto;
  box-sizing: border-box;
  margin: 0 auto;
  :deep() {
  	.el-col {
  		width: 100%;
  		display: block;
  	}
	.header {
	  width: 100%;
	  display: block;
	}
  }
  
}
.el-row {
	
}
</style>
