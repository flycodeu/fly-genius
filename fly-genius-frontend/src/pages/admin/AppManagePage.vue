<template>
  <div>
    <div id="appManagePage">
      <!-- 搜索表单 -->
      <a-form layout="inline" :model="searchParams" @finish="doSearch">
        <a-form-item label="应用名称">
          <a-input v-model:value="searchParams.appName" placeholder="输入应用名称" allow-clear />
        </a-form-item>
        <a-form-item label="生成类型">
          <a-select v-model:value="searchParams.codeGenType" placeholder="选择生成类型" allow-clear>
            <a-select-option value="website">网站</a-select-option>
            <a-select-option value="tool">工具</a-select-option>
            <a-select-option value="app">应用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="用户ID">
          <a-input-number v-model:value="searchParams.userId" placeholder="输入用户ID" allow-clear />
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
        <template v-if="column.dataIndex === 'cover'">
          <a-image :src="record.cover" :width="120" />
        </template>
        <template v-else-if="column.dataIndex === 'codeGenType'">
          <a-tag :color="getCodeGenTypeColor(record.codeGenType)">
            {{ getCodeGenTypeText(record.codeGenType) }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'priority'">
          <a-tag v-if="record.priority === 99" color="gold">精选</a-tag>
          <span v-else>{{ record.priority }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button @click="doEdit(record.id)">编辑</a-button>
          <a-button danger @click="doDelete(record.id)">删除</a-button>
          <a-button 
            v-if="record.priority !== 99" 
            type="primary" 
            @click="doFeature(record.id)"
          >
            精选
          </a-button>
          <a-button 
            v-else 
            @click="doUnfeature(record.id)"
          >
            取消精选
          </a-button>
        </template>
      </template>
    </a-table>

    <!-- 编辑弹窗 -->
    <a-modal
      v-model:open="editModalOpen"
      title="编辑应用"
      @ok="handleEditSubmit"
      @cancel="handleEditCancel"
      :confirm-loading="editLoading"
    >
      <a-form :model="editForm" :label-col="{ span: 5 }" :wrapper-col="{ span: 19 }">
        <a-form-item label="应用名称">
          <a-input v-model:value="editForm.appName" />
        </a-form-item>
        <a-form-item label="应用封面">
          <a-input v-model:value="editForm.cover" />
          <div v-if="editForm.cover" style="margin-top: 8px">
            <a-image :src="editForm.cover" :width="80" />
          </div>
        </a-form-item>
        <a-form-item label="优先级">
          <a-input-number v-model:value="editForm.priority" :min="0" :max="99" />
          <div style="margin-top: 4px; color: #999; font-size: 12px;">
            99表示精选应用
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { adminGetAppById, adminGetAppList, adminDeleteApp, adminUpdateApp } from '@/api/appController'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    key: 'appName',
  },
  {
    title: '应用封面',
    dataIndex: 'cover',
    key: 'cover',
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    key: 'codeGenType',
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    key: 'priority',
  },
  {
    title: '用户',
    dataIndex: 'user',
    key: 'user',
    customRender: ({ record }: { record: API.AppVo }) => record.user?.userName || '未知用户',
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
const data = ref<API.AppVo[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.AppQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await adminGetAppList({
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

// 删除应用
const doDelete = async (id: number) => {
  if (!id) {
    return
  }
  const res = await adminDeleteApp({ id })
  if (res.data.code === 0) {
    await fetchData()
    message.success('删除成功')
  } else {
    message.error('删除失败')
  }
}

// 精选应用
const doFeature = async (id: number) => {
  if (!id) {
    return
  }
  const res = await adminUpdateApp({
    id,
    priority: 99
  })
  if (res.data.code === 0) {
    await fetchData()
    message.success('设置精选成功')
  } else {
    message.error('设置精选失败')
  }
}

// 取消精选
const doUnfeature = async (id: number) => {
  if (!id) {
    return
  }
  const res = await adminUpdateApp({
    id,
    priority: 0
  })
  if (res.data.code === 0) {
    await fetchData()
    message.success('取消精选成功')
  } else {
    message.error('取消精选失败')
  }
}

// 编辑相关
const editModalOpen = ref(false)
const editLoading = ref(false)
const editForm = reactive({
  id: undefined as number | undefined,
  appName: '',
  cover: '',
  priority: 0,
})

const doEdit = async (id: number) => {
  if (!id) return
  const res = await adminGetAppById({ id })
  if (res.data.code === 0 && res.data.data) {
    const app = res.data.data
    editForm.id = app.id
    editForm.appName = app.appName || ''
    editForm.cover = app.cover || ''
    editForm.priority = app.priority || 0
    editModalOpen.value = true
  } else {
    message.error('获取应用信息失败')
  }
}

const handleEditCancel = () => {
  editModalOpen.value = false
}

const handleEditSubmit = async () => {
  editLoading.value = true
  const res = await adminUpdateApp(editForm)
  editLoading.value = false
  if (res.data.code === 0) {
    message.success('修改成功')
    editModalOpen.value = false
    await fetchData()
  } else {
    message.error('修改失败')
  }
}

// 获取生成类型颜色
const getCodeGenTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    'website': 'blue',
    'tool': 'green',
    'app': 'purple'
  }
  return colorMap[type] || 'default'
}

// 获取生成类型文本
const getCodeGenTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    'website': '网站',
    'tool': '工具',
    'app': '应用'
  }
  return textMap[type] || type
}
</script>

<style scoped></style> 