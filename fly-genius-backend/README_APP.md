# App 模块功能说明

## 概述
App 模块提供了完整的应用管理功能，包括用户应用管理和管理员应用管理。

## 功能列表

### 用户功能
1. **创建应用** (`POST /app/create`)
   - 用户创建应用，必须填写应用名称和初始化提示词
   - 需要用户登录

2. **修改应用** (`POST /app/update`)
   - 用户只能修改自己的应用
   - 目前只支持修改应用名称
   - 需要用户登录

3. **删除应用** (`POST /app/delete`)
   - 用户只能删除自己的应用
   - 需要用户登录

4. **查看应用详情** (`GET /app/get`)
   - 根据应用 ID 查看应用详情
   - 无需登录

5. **分页查询自己的应用列表** (`POST /app/my/list`)
   - 支持根据应用名称查询
   - 每页最多 20 个
   - 需要用户登录

6. **分页查询精选应用列表** (`POST /app/featured/list`)
   - 支持根据应用名称查询
   - 每页最多 20 个
   - 无需登录

### 管理员功能
1. **删除任意应用** (`POST /app/admin/delete`)
   - 管理员可以删除任意应用
   - 需要管理员权限

2. **更新任意应用** (`POST /app/admin/update`)
   - 管理员可以更新任意应用
   - 支持更新应用名称、应用封面、优先级
   - 需要管理员权限

3. **分页查询应用列表** (`POST /app/admin/list`)
   - 支持根据除时间外的任何字段查询
   - 每页数量不限
   - 需要管理员权限

4. **查看应用详情** (`GET /app/admin/get`)
   - 管理员根据 ID 查看应用详情
   - 需要管理员权限

## 数据库表结构

### app 表
```sql
CREATE TABLE `app` (
  `id` bigint NOT NULL COMMENT 'id',
  `appName` varchar(256) NOT NULL COMMENT '应用名称',
  `appDescription` text COMMENT '应用描述',
  `appCover` varchar(1024) COMMENT '应用封面',
  `initPrompt` text NOT NULL COMMENT '初始化提示词',
  `userId` bigint NOT NULL COMMENT '创建者id',
  `priority` int NOT NULL DEFAULT '0' COMMENT '优先级（数字越大优先级越高）',
  `isFeatured` tinyint NOT NULL DEFAULT '0' COMMENT '是否精选（0-否，1-是）',
  `editTime` datetime COMMENT '编辑时间',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_userId` (`userId`),
  KEY `idx_isFeatured` (`isFeatured`),
  KEY `idx_priority` (`priority`),
  KEY `idx_createTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用表';
```

## 文件结构

```
src/main/java/com/flycode/flygenius/
├── entity/
│   ├── constants/
│   │   └── AppConstant.java          # 应用常量
│   ├── model/
│   │   └── App.java                  # 应用实体类
│   ├── request/app/
│   │   ├── AppAddRequest.java        # 应用添加请求
│   │   ├── AppUpdateRequest.java     # 应用更新请求
│   │   └── AppQueryRequest.java      # 应用查询请求
│   └── vo/
│       └── AppVo.java                # 应用视图对象
├── mapper/
│   └── AppMapper.java                # 应用映射层
├── service/
│   ├── AppService.java               # 应用服务接口
│   └── impl/
│       └── AppServiceImpl.java       # 应用服务实现
└── controller/
    └── AppController.java            # 应用控制层

src/main/resources/
├── mapper/
│   └── AppMapper.xml                 # 应用映射 XML
└── sql/
    └── app.sql                       # 应用表创建 SQL

src/test/java/com/flycode/flygenius/
└── AppServiceTest.java               # 应用服务测试
```

## 权限控制

- **用户权限**：用户只能操作自己创建的应用
- **管理员权限**：管理员可以操作所有应用
- **精选应用**：只有管理员可以设置应用为精选状态

## 注意事项

1. 创建应用时必须填写应用名称和初始化提示词
2. 用户查询自己的应用列表时，每页最多返回 20 个
3. 精选应用列表查询时，每页最多返回 20 个
4. 管理员查询应用列表时，每页数量不限
5. 所有时间字段都会自动维护
6. 支持逻辑删除，不会真正删除数据 