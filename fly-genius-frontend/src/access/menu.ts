// src/utils/menu.ts
import { useLoginUserStore } from '@/stores/loginUser'
import router from '@/router'
import checkAccess from '@/access/checkAccess'
import type { RouteRecordRaw } from 'vue-router'

export function getMenuItemsFromRoutes() {
  const loginUserStore = useLoginUserStore()
  const loginUser = loginUserStore.loginUser

  interface MenuItem {
    key: string
    label?: string
    title?: string
    children?: MenuItem[]
  }

  function filterRoutes(routes: RouteRecordRaw[]): MenuItem[] {
    return routes
      .filter(route => {
        // 隐藏菜单项
        if (route.meta?.hideInMenu) return false
        // 不展示登录、注册等页面
        if (["/user/login", "/user/register", "/noAuth"].includes(String(route.path))) return false
        // 权限校验
        const needAccess = route.meta?.access
        if (typeof needAccess === 'string' && !checkAccess(loginUser, needAccess)) return false
        return true
      })
      .map(route => ({
        key: String(route.path),
        label: (route.name as string) || (route.meta?.title as string) || undefined,
        title: (route.name as string) || (route.meta?.title as string) || undefined,
        children: Array.isArray(route.children) && route.children.length > 0 ? filterRoutes(route.children as RouteRecordRaw[]) : undefined,
      }))
  }

  return filterRoutes(router.options.routes as RouteRecordRaw[])
}