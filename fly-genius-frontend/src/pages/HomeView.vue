<template>
  <div>
    <a-button @click="testHealth">点击</a-button>
  </div>
</template>

<script setup lang="ts">
import { testHealthOk } from '@/api/healthController'

const testHealth = async () => {
  const res = await testHealthOk()
  console.log(JSON.stringify(res.data))
  alert(JSON.stringify(res.data))
}
</script>
