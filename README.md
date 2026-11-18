# smart5c

公安智慧园区项目后端服务。基于 Spring Boot 构建，提供园区组织机构、设备、任务、巡检、缺陷管理及权限体系等业务能力。

## 技术栈

| 分类 | 选型 |
| --- | --- |
| 框架 | Spring Boot 2.5.4 / JDK 8 |
| 持久层 | MyBatis-Plus 3.5.7 + MyBatis-Plus-Join 1.5.4，HikariCP 连接池 |
| 数据库 | MySQL 8.0 / 人大金仓（KingbaseES）双适配 |
| 接口文档 | Knife4j 3.0.3（`/doc.html`） |
| 鉴权 | JWT (jjwt) + Bouncycastle 加密 |
| 模板引擎 | Thymeleaf / Freemarker |
| 通讯 | Spring WebSocket、ZeroMQ (jeromq)、Forest HTTP 客户端 |
| 音视频 | JavaCV / FFmpeg / JavaCPP |
| 文档处理 | Apache POI、iText PDF、Thumbnailator |
| 工具库 | Hutool、Fastjson2、Gson、Apache Commons、Pinyin4j |
| 定时任务 | Spring Quartz |
| 构建 | Maven（jar 包） |

## 项目结构

```
src/main/java/com/nbk/smart5c
├── Application.java          # 启动入口
├── controller/               # REST 控制器
├── service/  Impl/           # 业务接口与实现
├── mapper/                   # MyBatis-Plus Mapper
├── entity/                   # 数据库实体
├── pojo/  qo/ vo/ bo/        # 查询对象 / 视图对象 / 业务对象
├── client/                   # Forest 声明式 HTTP 客户端
├── schedules/                # Quartz 定时任务
├── filter/                   # 请求过滤器
├── exception/                # 全局异常处理
├── properties/               # 配置属性绑定
└── common/
    ├── config/               # MVC / 跨域 / 异步 / Swagger / 日志切面等
    ├── websocket/            # WebSocket 处理
    ├── annotations/          # 自定义注解
    ├── enums/  constants/    # 枚举与常量
    ├── client/  utils/       # 工具类
```

## 业务模块

- **组织机构**：局（Bureau）、部门（Department）、线路（Line）、区段（Section）、站点（Station）
- **用户与权限**：用户（UserInfo）、角色（RoleInfo）、菜单（Menu）、角色菜单（RoleMenu）、用户角色（UserRole）
- **设备管理**：Device
- **任务与巡检**：Task、TaskFile、InspectionData、InspectionReviewResult
- **缺陷管理**：DefectStorage
- **文件上传**：FileUploadRecords、DataUpload
- **首页统计**：HomePage
- **操作日志**：OperateLog

## 运行配置

启动类：`com.nbk.smart5c.Application`

### 环境与端口

| Profile | 端口 | 数据库 |
| --- | --- | --- |
| dev（默认） | 5160 | MySQL（192.168.5.99/nbk_smart_5c） |
| test | 8081 | 测试环境 |
| kingbase | 5160 | 人大金仓 KingbaseES |

切换环境：修改 `application.yml` 中 `spring.profiles.active`，或启动参数 `--spring.profiles.active=kingbase`。

### 关键配置项

- 数据库表前缀：`nbk_`
- 逻辑删除：已删除 `1` / 未删除 `0`
- JWT：请求头 `Authorization`，前缀 `Bearer`，密钥与过期时间见 `application.yml` 中 `jwt.*`

## 数据库初始化

`sql/` 目录提供建表与初始数据脚本：

- `5C-mysql建表语句.sql` — MySQL 建表
- `5C-kingbase低版本建表语句.sql` — 人大金仓建表
- `init_data.sql` — 初始化数据
- `5C-2026xxxx.sql` — 按日期归档的增量脚本

## 构建与运行

```bash
# 编译打包（生成 smart5c-1.0.jar 与 smart5c-1.0-jar-with-dependencies.jar）
mvn clean package

# 运行
java -jar target/smart5c-1.0.jar

# 指定环境
java -jar target/smart5c-1.0.jar --spring.profiles.active=dev
```

启动成功后访问接口文档：`http://localhost:5160/doc.html`

## 部署说明

- 打包方式：`jar`
- 主类：`com.nbk.smart5c.Application`
- 日志框架：Log4j2（配置见 `src/main/resources/log4j2-spring.xml`）
- Maven 私服发布地址：`http://192.168.0.229/maven`（见 `pom.xml` 中 `distributionManagement`）
