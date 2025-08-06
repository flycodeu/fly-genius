-- 创建应用表
CREATE TABLE IF NOT EXISTS `app` (
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