<template>
  <a-layout style="min-height: 100vh; background: #f5f5f5;">
    <GlobalHeader :menu-items="menuItems" style="padding: 0 16px;" />
    <a-layout-content style="margin:0 0 0;overflow:initial;padding:0 16px; background: #fff;">
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
