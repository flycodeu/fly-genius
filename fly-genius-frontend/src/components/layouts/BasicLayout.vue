<template>
  <a-layout class="basic-layout">
    <GlobalHeader :menu-items="menuItems" />
    <a-layout-content class="main-content">
      <router-view />
    </a-layout-content>
    <GlobalFooter style="padding: 0 16px;" />
  </a-layout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import GlobalHeader from './GlobalHeader.vue'
import GlobalFooter from './GlobalFooter.vue'

const menuItems = ref([
  { key: 'home', label: '首页', route: '/' },
  { key: 'appList', label: '我的应用', route: '/app/list' },
  { key: 'userManage', label: '用户管理', route: '/admin/userManage' },
  { key: 'appManage', label: '应用管理', route: '/admin/appManage' },
])
</script>

<style scoped>
.basic-layout {
  background: none;
}
.main-content {
  width: 100%;
  padding: 0;
  background: none;
  margin: 0;
}
</style>
