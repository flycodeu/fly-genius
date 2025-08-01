<template>
    <div class="user-profile-page">
      <a-row :gutter="24">
        <!-- 左侧：用户信息展示 -->
        <a-col :span="12">
          <a-card title="个人信息" :bordered="false">
            <a-descriptions :column="1">
              <a-descriptions-item label="头像">
                <a-image v-if="userInfo.userAvatar" :src="userInfo.userAvatar" :width="100" />
                <span v-else>暂无头像</span>
              </a-descriptions-item>
              <a-descriptions-item label="账号">{{ userInfo.userAccount }}</a-descriptions-item>
              <a-descriptions-item label="用户名">{{ userInfo.userName }}</a-descriptions-item>
              <a-descriptions-item label="角色">
                <a-tag :color="userInfo.userRole === 'admin' ? 'green' : 'blue'">
                  {{ userInfo.userRole === 'admin' ? '管理员' : '普通用户' }}
                </a-tag>
              </a-descriptions-item>
              <a-descriptions-item label="简介">{{ userInfo.userProfile || '暂无简介' }}</a-descriptions-item>
            </a-descriptions>
          </a-card>
        </a-col>

        <!-- 右侧：操作区域 -->
        <a-col :span="12">
          <a-card :bordered="false">
            <a-tabs v-model:activeKey="activeTab">
              <!-- 信息修改 -->
              <a-tab-pane key="info" tab="信息修改">
                <a-form :model="editForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
                  <a-form-item label="账号">
                    <a-input v-model:value="editForm.userAccount" disabled />
                  </a-form-item>
                  <a-form-item label="用户名">
                    <a-input v-model:value="editForm.userName" />
                  </a-form-item>
                  <a-form-item label="简介">
                    <a-textarea v-model:value="editForm.userProfile" :rows="3" />
                  </a-form-item>
                  <a-form-item label="头像">
                    <a-input v-model:value="editForm.userAvatar" />
                    <div v-if="editForm.userAvatar" style="margin-top: 8px">
                      <a-image :src="editForm.userAvatar" :width="80" />
                    </div>
                  </a-form-item>
                  <a-form-item :wrapper-col="{ offset: 4, span: 20 }">
                    <a-space>
                      <a-button type="primary" @click="handleSaveInfo" :loading="saveLoading">保存</a-button>
                      <a-button @click="handleCancelInfo">取消</a-button>
                    </a-space>
                  </a-form-item>
                </a-form>
              </a-tab-pane>

              <!-- 修改密码 -->
              <a-tab-pane key="password" tab="修改密码">
                <a-form :model="passwordForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
                  <a-form-item label="旧密码">
                    <a-input-password v-model:value="passwordForm.oldPassword" />
                  </a-form-item>
                  <a-form-item label="新密码">
                    <a-input-password v-model:value="passwordForm.newPassword" />
                  </a-form-item>
                  <a-form-item label="确认密码">
                    <a-input-password v-model:value="passwordForm.confirmPassword" />
                  </a-form-item>
                  <a-form-item :wrapper-col="{ offset: 4, span: 20 }">
                    <a-space>
                      <a-button type="primary" @click="handleSavePassword" :loading="passwordLoading">保存</a-button>
                      <a-button @click="handleCancelPassword">取消</a-button>
                    </a-space>
                  </a-form-item>
                </a-form>
              </a-tab-pane>
            </a-tabs>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </template>


<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getUserVoById, updateUser } from '@/api/userController'
import { useLoginUserStore } from '@/stores/loginUser'

const loginUserStore = useLoginUserStore()
const activeTab = ref('info')
const saveLoading = ref(false)
const passwordLoading = ref(false)

// 用户信息
const userInfo = reactive({
  id: '',
  userAccount: '',
  userName: '',
  userProfile: '',
  userRole: '',
  userAvatar: '',
})

// 编辑表单
const editForm = reactive({
  id: '',
  userAccount: '',
  userName: '',
  userProfile: '',
  userRole: '',
  userAvatar: '',
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 获取用户信息
const fetchUserInfo = async () => {
  const userId = loginUserStore.loginUser.id
  if (!userId) {
    message.error('用户未登录')
    return
  }

  const res = await getUserVoById({ id: userId })
  if (res.data.code === 0 && res.data.data) {
    const user = res.data.data
    Object.assign(userInfo, user)
    Object.assign(editForm, user)
  } else {
    message.error('获取用户信息失败')
  }
}

// 保存信息
const handleSaveInfo = async () => {
  saveLoading.value = true
  const updateData = {
    id: String(editForm.id), // 转换为字符串避免精度丢失
    userAccount: editForm.userAccount,
    userName: editForm.userName,
    userProfile: editForm.userProfile,
    userAvatar: editForm.userAvatar
  }
  const res = await updateUser(updateData as any) // 临时类型断言
  saveLoading.value = false
  
  if (res.data.code === 0) {
    message.success('保存成功')
    // 更新store中的用户信息
    await loginUserStore.fetchLoginUser()
    // 更新展示信息
    Object.assign(userInfo, editForm)
  } else {
    message.error('保存失败')
  }
}

// 取消信息修改
const handleCancelInfo = () => {
  Object.assign(editForm, userInfo)
  message.info('已取消修改')
}

// 保存密码
const handleSavePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }

  passwordLoading.value = true
  // 模拟密码修改
  setTimeout(() => {
    passwordLoading.value = false
    message.success('密码修改成功')
    // 清空密码表单
    Object.assign(passwordForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
    })
  }, 1000)
}

// 取消密码修改
const handleCancelPassword = () => {
  Object.assign(passwordForm, {
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  })
  message.info('已取消修改')
}

onMounted(() => {
  fetchUserInfo()
})
</script>


<style scoped>
.user-profile-page {
  padding: 24px;
}
</style>
