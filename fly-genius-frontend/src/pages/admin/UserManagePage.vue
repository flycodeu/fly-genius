<template>
  <div>
    <div id="userManagePage">
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="searchParams" @finish="doSearch">
        <a-form-item label="账号">
          <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" allow-clear />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input v-model:value="searchParams.userName" placeholder="输入用户名" allow-clear />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
        </a-form-item>
      </a-form>
      <a-divider />
    </div>

    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">管理员</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">普通用户</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button @click="doEdit(record.id)">修改</a-button>
          <a-button danger @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editModalOpen"
      title="编辑用户"
      @ok="handleEditSubmit"
      @cancel="handleEditCancel"
      :confirm-loading="editLoading"
    >
      <a-form :model="editForm" :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
        <a-form-item label="账号">
          <a-input v-model:value="editForm.userAccount" disabled />
        </a-form-item>
        <a-form-item label="用户名">
          <a-input v-model:value="editForm.userName" />
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model:value="editForm.userProfile" aria-rowspan="3" />
        </a-form-item>
        <a-form-item label="角色">
          <a-select v-model:value="editForm.userRole">
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="user">普通用户</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="头像">
          <a-input v-model:value="editForm.userAvatar" />
          <div v-if="editForm.userAvatar" style="margin-top: 8px">
            <a-image :src="editForm.userAvatar" :width="80" />
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, getUserById, getUserVoPage, updateUser } from '@/api/userController'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    key: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    key: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    key: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
    key: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    key: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]
// 数据
const data = ref<API.UserVo[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await getUserVoPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
}

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.pageNum ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: Number(total.value),
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 获取数据
const doSearch = () => {
  // 重置页码
  searchParams.pageNum = 1
  fetchData()
}

const doDelete = async (id: number) => {
  if (!id) {
    return
  }
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    await fetchData()
    message.success('删除成功')
  } else {
    message.error('删除失败')
  }
}

const editModalOpen = ref(false)
const editLoading = ref(false)
const editForm = reactive({
  id: undefined as number | undefined,
  userAccount: '',
  userName: '',
  userProfile: '',
  userRole: '',
  userAvatar: '',
})

const doEdit = async (id: number) => {
  if (!id) return
  const res = await getUserById({ id })
  if (res.data.code === 0 && res.data.data) {
    const user = res.data.data
    console.log(user)
    // 只拷贝需要的字段，并做类型转换
    editForm.id = user.id
    editForm.userAccount = user.userAccount || ''
    editForm.userName = user.userName || ''
    editForm.userProfile = user.userProfile || ''
    editForm.userRole = user.userRole || ''
    editForm.userAvatar = user.userAvatar || ''
    editModalOpen.value = true
  } else {
    message.error('获取用户信息失败')
  }
}
const handleEditCancel = () => {
  editModalOpen.value = false
}

const handleEditSubmit = async () => {
  editLoading.value = true
  const res = await updateUser(editForm)
  editLoading.value = false
  if (res.data.code === 0) {
    message.success('修改成功')
    editModalOpen.value = false
    await fetchData()
  } else {
    message.error('修改失败')
  }
}
</script>

<style scoped></style>
