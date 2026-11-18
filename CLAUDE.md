# CLAUDE.md

本文件为 Claude Code 在本仓库工作时的项目上下文指南。详细的对外说明见 `README.md`。

## 项目概述

`smart5c` 是公安智慧园区项目后端服务（Spring Boot 2.5.4 / JDK 8），提供园区组织机构、设备、任务、巡检、缺陷管理及权限体系等能力。打包为可执行 `jar`，启动类 `com.nbk.smart5c.Application`。

## 技术栈要点

- 持久层：MyBatis-Plus 3.5.7 + MyBatis-Plus-Join 1.5.4，HikariCP 连接池。Mapper XML 位于 `src/main/resources/mapper/`。
- 数据库：MySQL 8.0 与人大金仓 KingbaseES 双适配，通过 profile 切换；表前缀 `nbk_`。另有 `oceanbase` 分支做 OceanBase 适配。
- 配置加密：jasypt 3.0.5，`application*.yml` 中的敏感字段（数据库/SFTP 密码等）使用 `ENC(...)` 密文，主密钥在 `application.yml` 的 `jasypt.encryptor.password`（生产环境用 `-Djasypt.encryptor.password=xxx` 启动参数覆盖）。
- 鉴权：自实现 JWT（jjwt 0.9.1）+ Bouncycastle RSA 加密；过滤器 `AuthenticationFilter` 拦截请求。
- 接口文档：Knife4j 3.0.3，路径 `/doc.html`。
- 通讯：Spring WebSocket、ZeroMQ (jeromq)、Forest 声明式 HTTP 客户端。
- 音视频：JavaCV / FFmpeg / JavaCPP（巡检图像处理、RTSP 截帧等）。
- 定时任务：Spring Quartz（`schedules/C5Schedule.java`）。
- 日志：Log4j2（`src/main/resources/log4j2-spring.xml`），不打印 MyBatis SQL（生产配置 `NoLoggingImpl`）。
- 主类 `com.nbk.smart5c.Application` 启用 `@EnableAsync`、`@EnableAspectJAutoProxy`，扫描 `com.nbk.smart5c`，`@MapperScan("com.nbk.smart5c.mapper")`、`@ForestScan("com.nbk.smart5c.client")`。

## 目录结构

```
src/main/java/com/nbk/smart5c
├── Application.java          # 启动入口
├── controller/               # REST 控制器，统一前缀 /smart5cApi/**（/web/** 用于回调）
├── service/  Impl/           # 业务接口与实现，泛型 <Mapper, Entity>
├── mapper/                   # MyBatis-Plus Mapper
├── entity/                   # 数据库实体，均继承 BaseEntity
├── pojo/  qo/ vo/ bo/        # 查询对象 / 视图对象 / 业务对象（SaveBO/UpdateBO 成对出现）
├── client/                   # Forest 声明式 HTTP 客户端（AiClient 等）
├── schedules/                # Quartz 定时任务（C5Schedule）
├── filter/                   # AuthenticationFilter、UserMessageManager
├── exception/                # ServiceException（业务异常）
├── properties/               # 配置属性绑定（NotAuthUrlProperties 等）
└── common/
    ├── config/               # MVC / 跨域 / 异步 / Swagger / MyBatis-Plus / 日志切面 / 全局异常
    ├── websocket/            # WebSocket Server/Client 及会话管理
    ├── annotations/          # @RoleAccess、@AroundLog、@BeforeLog、@CreatedDate/@UpdatedDate 等
    ├── enums/                # 业务枚举（任务状态、设备状态、缺陷类型、审核状态等）
    ├── constants/            # SystemConstants、RSASecret
    ├── client/               # PythonServiceClient
    ├── utils/                # 工具类（JwtTokenUtil、RsaUtil、SnowFlakeUtil、CommonResult、CommonPage 等）
    └── utils/holder/         # CurrentUserHolder（ThreadLocal 当前用户）
```

## 架构与约定

### 分层模式
- Controller 仅做参数接收与结果封装，不写业务逻辑；统一返回 `CommonResult<T>`（`common/utils/CommonResult.java`），分页使用 `CommonPage`。
- Service 接口在 `service/`，实现在 `service/Impl/`，命名 `XxxService` / `XxxServiceImpl`。
- 实体继承 `entity/BaseEntity`，自动填充 `created_user/created_time/updated_user/updated_time`（由 `MybatisPlusConfig` 的 `MetaObjectHandler` 注入，当前用户取自 `CurrentUserHolder`）。
- POJO 分层：`qo` 接收查询入参，`bo` 承载业务入参（`XxxSaveBO` / `XxxUpdateBO` 成对），`vo` 返回前端。
- 逻辑删除：`logic-delete-value: 1`，`logic-not-delete-value: 0`，实体字段 `deleted`（参考各实体）。
- ID 策略：`id-type: auto`，表前缀 `nbk_`。

### 鉴权与当前用户
- `AuthenticationFilter` 校验请求头 `Authorization: Bearer <jwt>`，白名单见 `application.yml` 中 `access.filter.shouldSkipUrls`。
- 校验通过后将当前 `UserInfo` 放入 `CurrentUserHolder`（ThreadLocal），业务层通过 `CurrentUserHolder.get()` 取用户。
- `@RoleAccess` 注解 + `RoleAccessAspect` 已存在但当前 AOP 逻辑被注释，**不要假设其生效**。
- JWT 密钥与过期时间在 `application.yml` 的 `jwt.*`。

### 异常处理
- 业务层抛 `ServiceException`（`exception/ServiceException.java`），由 `GlobalExceptionHandler` 统一转 `CommonResult`。
- 不要在 Controller 直接 try/catch 吞异常。

### 数据库适配
- 双数据库：`dev`（MySQL 192.168.5.99/nbk_smart_5c）、`kingbase`（人大金仓 192.168.11.26:54321）。建表脚本在 `sql/`：
  - `5C-mysql建表语句.sql` / `5C-kingbase低版本建表语句.sql`
  - `init_data.sql` 初始化数据
  - `5C-YYYYMMDD.sql` 按日期归档的增量脚本（最新至 `5C-20260830.sql`）
- 写 SQL 时注意 MySQL 与 KingbaseES 语法差异（关键字、函数、分页等）。
- 注意：`nbk_inspection_data` / `nbk_inspection_review_result` 按月分表（`_yyyyMM` 后缀）的代码在 `dev_jn_分表` 分支，当前 `dev_jn` 分支为主表方案，改动这两张表时留意分支差异。

### 业务模块
- 组织机构：Bureau → Department → Line → Section → Station（层级关系）
- 权限：UserInfo / RoleInfo / Menu / RoleMenu / UserRole
- 设备：Device
- 任务巡检：Task / TaskFile / InspectionData / InspectionReviewResult（巡检数据分析由 `C5Schedule` 定时扫描 + AI 服务分析，状态机见 `InspectionDataServiceImpl`；实时推送统计用 `userMaxInspectionIdCache` 快照）
- 缺陷：DefectStorage（缺陷确认 confirmDefect 涉及图片写入缺陷仓库）、FolderCreate
- 文件：FileUploadRecords / DataUpload（上传回调走 `/web/dataUpload/**`，免鉴权）
- 首页统计：HomePage
- 操作日志：OperateLog

## 构建与运行

```bash
# 编译（项目 settings.local.json 已允许：mvn -q compile -DskipTests）
mvn -q compile -DskipTests

# 打包（生成 smart5c-1.0.jar 与 jar-with-dependencies）
mvn clean package

# 运行
java -jar target/smart5c-1.0.jar
java -jar target/smart5c-1.0.jar --spring.profiles.active=kingbase
```

默认 profile `dev`，端口 `5160`（`application.yml` 中 `server.port=8081`，由 dev profile 覆盖为 5160）。接口文档：`http://localhost:5160/doc.html`（账号 `admin` / 密码 `123`，见 `swagger.*`）。

## 环境与关键配置

| Profile | 端口 | 数据库 |
| --- | --- | --- |
| dev（默认） | 5160 | MySQL 192.168.5.99/nbk_smart_5c |
| test | 8081 | MySQL localhost/nbk_smart_5c |
| kingbase | 5160 | 人大金仓 192.168.11.26:54321/nbk_smart_5c |

- 数据上传根路径：`inspect.basepath=/nbk/app/smart5c/data/rawdata`，缺陷路径 `inspect.defectpath=/nbk/app/smart5c/data/defectstorage`
- AI 服务：`ai.ip=127.0.0.1`，`ai.port=5000`（通过 `client/AiClient.java` Forest 客户端调用）
- SFTP：`sftp.host=192.168.5.99`，`sftp.username=sftpuser`
- 文件上传上限：单文件 1GB，整请求 5GB

## 开发约定

- 新增实体必须继承 `BaseEntity`，并补 `nbk_` 前缀建表脚本到 `sql/5C-YYYYMMDD.sql`。
- 新增业务接口遵循 `XxxController` / `XxxService` / `XxxServiceImpl` / `XxxMapper` / `XxxSaveBO` / `XxxUpdateBO` / `XxxQO` / `XxxVO` 命名。
- Controller 路径前缀统一 `/smart5cApi/<模块>`（对外业务）或 `/web/<模块>`（回调/免鉴权）。
- 需要免鉴权的接口要显式加到 `application.yml` 的 `access.filter.shouldSkipUrls`。
- 时间字段统一 `LocalDateTime`，序列化格式 `yyyy-MM-dd HH:mm:ss`（Jackson 全局配置 GMT+8）。
- 修改数据库结构时**同步更新 MySQL 与 KingbaseES 两份建表脚本**。
- 工具类优先复用 `common/utils/`（Hutool、`FileUtil`、`DateUtil`、`JwtTokenUtil`、`RsaUtil`、`SnowFlakeUtil` 等），避免重复造轮子。
- 日志使用 Lombok `@Slf4j`，业务异常用 `ServiceException`，不要在 Controller 层吞异常。

## 测试

- 测试位于 `src/test/java/com/nbk/smart5c/`，目前仅有 `client/AiClientTest` 与 `service/`（`BaseTest`、`InspectionDataServiceImplTest`）下少量测试。
- 运行单测：`mvn -q test -Dtest=<TestClass>`（如 `InspectionDataServiceImplTest`）。
