<script setup>
	import UsersHead from "@/components/UsersHead.vue";
	
</script>
<template>
	<div>
		<UsersHead></UsersHead>
		<el-container class="con3">
			<el-container style="display: block; width: 100%">
				<router-view></router-view>
			</el-container>
		</el-container>
	</div>
</template>

<style scoped>
	.con3 {
		overflow: auto;
	}
</style>