// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /health/test */
export async function testHealthOk(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/health/test', {
    method: 'GET',
    ...(options || {}),
  })
}
