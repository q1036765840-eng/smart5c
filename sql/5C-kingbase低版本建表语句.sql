DROP TABLE IF EXISTS nbk_department;
CREATE TABLE nbk_department (
  id BIGSERIAL PRIMARY KEY, -- Kingbase 使用 BIGSERIAL 实现自增主键
  department_code VARCHAR(50),
  department_name VARCHAR(100),
  department_type SMALLINT, -- MySQL tinyint 对应 Kingbase smallint
  parent_id INTEGER,
  remark TEXT,
  created_user VARCHAR(50) NOT NULL DEFAULT 'system',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50) NOT NULL DEFAULT 'system',
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 添加注释 (Kingbase 使用 COMMENT ON 语法)
COMMENT ON TABLE nbk_department IS '部门信息表';
COMMENT ON COLUMN nbk_department.id IS '主键';
COMMENT ON COLUMN nbk_department.department_code IS '部门编码';
COMMENT ON COLUMN nbk_department.department_name IS '部门名称';
COMMENT ON COLUMN nbk_department.department_type IS '部门类型(0:供电部,1:供电段,2:车间)';
COMMENT ON COLUMN nbk_department.parent_id IS '父机构id';
COMMENT ON COLUMN nbk_department.remark IS '备注';
COMMENT ON COLUMN nbk_department.created_user IS '创建人';
COMMENT ON COLUMN nbk_department.created_time IS '创建时间';
COMMENT ON COLUMN nbk_department.updated_user IS '修改人';
COMMENT ON COLUMN nbk_department.updated_time IS '修改时间';

-- 创建唯一索引
CREATE UNIQUE INDEX idx_nbk_department_code ON nbk_department(department_code);

-- 创建普通索引 (Kingbase 默认就是 B-Tree，无需显式指定 USING BTREE)
CREATE INDEX idx_nbk_department_type ON nbk_department(department_type);
CREATE INDEX idx_nbk_department_parent_id ON nbk_department(parent_id);

-- ============================================================
-- 1. nbk_bureau (供电部信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_bureau;

CREATE TABLE nbk_bureau (
  id BIGSERIAL PRIMARY KEY,
  bureau_code VARCHAR(20) NOT NULL,
  bureau_name VARCHAR(100) NOT NULL,
  description TEXT,
  created_user VARCHAR(50),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50),
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_bureau IS '供电部信息表';
COMMENT ON COLUMN nbk_bureau.id IS '主键';
COMMENT ON COLUMN nbk_bureau.bureau_code IS '供电部编码';
COMMENT ON COLUMN nbk_bureau.bureau_name IS '供电部名称';
COMMENT ON COLUMN nbk_bureau.description IS '供电部描述';
COMMENT ON COLUMN nbk_bureau.created_user IS '创建人';
COMMENT ON COLUMN nbk_bureau.created_time IS '创建时间';
COMMENT ON COLUMN nbk_bureau.updated_user IS '更新人';
COMMENT ON COLUMN nbk_bureau.updated_time IS '修改时间';

-- ============================================================
-- 2. nbk_device (设备信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_device;

CREATE TABLE nbk_device (
  id BIGSERIAL PRIMARY KEY,
  station_id BIGINT NOT NULL,
  device_code VARCHAR(50) NOT NULL,
  device_type VARCHAR(50),
  installation_date DATE,
  manufacturer VARCHAR(100),
  pillar_no VARCHAR(50),
  model VARCHAR(100),
  status SMALLINT,
  last_maintenance_date DATE,
  next_maintenance_date DATE,
  parameters JSONB,
  fix_location VARCHAR(255),
  km_sign VARCHAR(50),
  created_user VARCHAR(50),
  updated_user VARCHAR(50),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_device IS '设备信息表';
COMMENT ON COLUMN nbk_device.id IS '主键';
COMMENT ON COLUMN nbk_device.station_id IS '区站id';
COMMENT ON COLUMN nbk_device.device_code IS '设备编码';
COMMENT ON COLUMN nbk_device.device_type IS '设备类型';
COMMENT ON COLUMN nbk_device.installation_date IS '安装日期';
COMMENT ON COLUMN nbk_device.manufacturer IS '制造商';
COMMENT ON COLUMN nbk_device.pillar_no IS '杆号';
COMMENT ON COLUMN nbk_device.model IS '型号';
COMMENT ON COLUMN nbk_device.status IS '状态(0:停用,1:运行,2:维修)';
COMMENT ON COLUMN nbk_device.last_maintenance_date IS '上次维护日期';
COMMENT ON COLUMN nbk_device.next_maintenance_date IS '下次维护日期';
COMMENT ON COLUMN nbk_device.parameters IS '参数';
COMMENT ON COLUMN nbk_device.fix_location IS '安装位置';
COMMENT ON COLUMN nbk_device.km_sign IS '公里标';
COMMENT ON COLUMN nbk_device.created_user IS '创建人';
COMMENT ON COLUMN nbk_device.updated_user IS '更新人';
COMMENT ON COLUMN nbk_device.created_time IS '创建时间';
COMMENT ON COLUMN nbk_device.updated_time IS '更新时间';

CREATE UNIQUE INDEX idx_nbk_device_code_station ON nbk_device(device_code, station_id);

-- ============================================================
-- 3. nbk_inspection_data (巡查数据表)
-- ============================================================
DROP TABLE IF EXISTS nbk_inspection_data;

CREATE TABLE nbk_inspection_data (
  id BIGSERIAL PRIMARY KEY,
  device_id BIGINT NOT NULL,
  section_id BIGINT NOT NULL,
  line_id BIGINT NOT NULL,
  station_id BIGINT NOT NULL,
  inspection_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  locomotive_number VARCHAR(50),
  analysis_status SMALLINT,
  km_sign VARCHAR(50),
  pantograph_status SMALLINT,
  locomotive_image VARCHAR(255),
  created_user VARCHAR(50),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50),
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_inspection_data IS '巡查数据表';
COMMENT ON COLUMN nbk_inspection_data.id IS '主键';
COMMENT ON COLUMN nbk_inspection_data.device_id IS '设备id';
COMMENT ON COLUMN nbk_inspection_data.section_id IS '机务段id';
COMMENT ON COLUMN nbk_inspection_data.line_id IS '线路id';
COMMENT ON COLUMN nbk_inspection_data.station_id IS '区站id';
COMMENT ON COLUMN nbk_inspection_data.inspection_time IS '巡查时间';
COMMENT ON COLUMN nbk_inspection_data.locomotive_number IS '机车号';
COMMENT ON COLUMN nbk_inspection_data.analysis_status IS '分析状态(未分析:0,待审批:1,已审批:2)';
COMMENT ON COLUMN nbk_inspection_data.km_sign IS '公里标';
COMMENT ON COLUMN nbk_inspection_data.pantograph_status IS '受电弓状态(正常:1,异常:2)';
COMMENT ON COLUMN nbk_inspection_data.locomotive_image IS '机车图';
COMMENT ON COLUMN nbk_inspection_data.created_user IS '创建人';
COMMENT ON COLUMN nbk_inspection_data.created_time IS '创建时间';
COMMENT ON COLUMN nbk_inspection_data.updated_user IS '更新人';
COMMENT ON COLUMN nbk_inspection_data.updated_time IS '更新时间';

-- 注意：MySQL 的 ON UPDATE CURRENT_TIMESTAMP 在此处简化为默认值。
-- 如需自动更新，请参考脚本末尾的触发器部分。

-- ============================================================
-- 4. nbk_inspection_review_result (巡查数据审核结果表)
-- ============================================================
DROP TABLE IF EXISTS nbk_inspection_review_result;

CREATE TABLE nbk_inspection_review_result (
  id BIGSERIAL PRIMARY KEY,
  inspection_id BIGINT NOT NULL,
  image_path VARCHAR(255),
  problem_desc TEXT,
  pantograph_status INTEGER,
  defect_level INTEGER,
  image_valid SMALLINT,
  marked_regions JSONB,
  locomotive_number VARCHAR(50),
  reliability VARCHAR(20),
  created_user VARCHAR(50) NOT NULL DEFAULT 'system',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50) NOT NULL DEFAULT 'system',
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_inspection_review_result IS '巡查数据审核结果表';
COMMENT ON COLUMN nbk_inspection_review_result.id IS '主键';
COMMENT ON COLUMN nbk_inspection_review_result.inspection_id IS '巡检id';
COMMENT ON COLUMN nbk_inspection_review_result.image_path IS '缺陷图路径';
COMMENT ON COLUMN nbk_inspection_review_result.problem_desc IS '缺陷描述';
COMMENT ON COLUMN nbk_inspection_review_result.pantograph_status IS '受电弓状态(0:异常,1:正常)';
COMMENT ON COLUMN nbk_inspection_review_result.defect_level IS '缺陷等级';
COMMENT ON COLUMN nbk_inspection_review_result.image_valid IS '图片是否合规';
COMMENT ON COLUMN nbk_inspection_review_result.marked_regions IS '标记区域';
COMMENT ON COLUMN nbk_inspection_review_result.locomotive_number IS '机车号';
COMMENT ON COLUMN nbk_inspection_review_result.reliability IS '置信度';
COMMENT ON COLUMN nbk_inspection_review_result.created_user IS '创建人';
COMMENT ON COLUMN nbk_inspection_review_result.created_time IS '创建时间';
COMMENT ON COLUMN nbk_inspection_review_result.updated_user IS '修改人';
COMMENT ON COLUMN nbk_inspection_review_result.updated_time IS '修改时间';

CREATE UNIQUE INDEX idx_nbk_review_inspection_img ON nbk_inspection_review_result(inspection_id, image_path);
CREATE INDEX idx_ps_status_inspection ON nbk_inspection_review_result(pantograph_status, inspection_id);

-- ============================================================
-- 5. nbk_line (线路信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_line;

CREATE TABLE nbk_line (
  id BIGSERIAL PRIMARY KEY,
  section_id INTEGER NOT NULL,
  line_code VARCHAR(20) NOT NULL,
  line_name VARCHAR(100) NOT NULL,
  description TEXT,
  created_user VARCHAR(50),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50),
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_line IS '线路信息表';
COMMENT ON COLUMN nbk_line.id IS '主键';
COMMENT ON COLUMN nbk_line.section_id IS '区域id';
COMMENT ON COLUMN nbk_line.line_code IS '线路编码';
COMMENT ON COLUMN nbk_line.line_name IS '线路名称';
COMMENT ON COLUMN nbk_line.description IS '描述';
COMMENT ON COLUMN nbk_line.created_user IS '创建人';
COMMENT ON COLUMN nbk_line.created_time IS '创建时间';
COMMENT ON COLUMN nbk_line.updated_user IS '更新人';
COMMENT ON COLUMN nbk_line.updated_time IS '更新时间';

CREATE UNIQUE INDEX idx_nbk_line_name_section ON nbk_line(line_name, section_id);

-- ============================================================
-- 6. nbk_menu (菜单权限表)
-- ============================================================
DROP TABLE IF EXISTS nbk_menu;

CREATE TABLE nbk_menu (
  id BIGSERIAL PRIMARY KEY,
  menu_name VARCHAR(50) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  order_num INTEGER DEFAULT 0,
  path VARCHAR(200) DEFAULT '',
  component VARCHAR(255),
  query_param VARCHAR(255), -- 改名避免与 SQL 关键字冲突，原名为 query
  route_name VARCHAR(50) DEFAULT '',
  is_frame INTEGER DEFAULT 1,
  is_cache INTEGER DEFAULT 0,
  menu_type CHAR(1) DEFAULT '',
  visible CHAR(1) DEFAULT '0',
  status CHAR(1) DEFAULT '0',
  perms VARCHAR(100),
  icon VARCHAR(100) DEFAULT '#',
  created_user VARCHAR(50) DEFAULT '',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50) DEFAULT '',
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remark VARCHAR(500) DEFAULT ''
);

COMMENT ON TABLE nbk_menu IS '菜单权限表';
COMMENT ON COLUMN nbk_menu.id IS '菜单ID';
COMMENT ON COLUMN nbk_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN nbk_menu.parent_id IS '父菜单ID';
COMMENT ON COLUMN nbk_menu.order_num IS '显示顺序';
COMMENT ON COLUMN nbk_menu.path IS '路由地址';
COMMENT ON COLUMN nbk_menu.component IS '组件路径';
COMMENT ON COLUMN nbk_menu.query_param IS '路由参数';
COMMENT ON COLUMN nbk_menu.route_name IS '路由名称';
COMMENT ON COLUMN nbk_menu.is_frame IS '是否为外链（0是 1否）';
COMMENT ON COLUMN nbk_menu.is_cache IS '是否缓存（0缓存 1不缓存）';
COMMENT ON COLUMN nbk_menu.menu_type IS '菜单类型（M目录 C菜单 F按钮）';
COMMENT ON COLUMN nbk_menu.visible IS '菜单状态（0显示 1隐藏）';
COMMENT ON COLUMN nbk_menu.status IS '菜单状态（0正常 1停用）';
COMMENT ON COLUMN nbk_menu.perms IS '权限标识';
COMMENT ON COLUMN nbk_menu.icon IS '菜单图标';
COMMENT ON COLUMN nbk_menu.created_user IS '创建者';
COMMENT ON COLUMN nbk_menu.created_time IS '创建时间';
COMMENT ON COLUMN nbk_menu.updated_user IS '更新者';
COMMENT ON COLUMN nbk_menu.updated_time IS '更新时间';
COMMENT ON COLUMN nbk_menu.remark IS '备注';

-- ============================================================
-- 7. nbk_operate_log (操作日志表)
-- ============================================================
DROP TABLE IF EXISTS nbk_operate_log;

CREATE TABLE nbk_operate_log (
  id BIGSERIAL PRIMARY KEY,
  operate_user VARCHAR(50),
  operate_time TIMESTAMP,
  operate_type SMALLINT,
  class_name VARCHAR(100),
  method_name VARCHAR(100),
  method_params TEXT,
  return_value TEXT,
  cost_time BIGINT,
  operate_desc TEXT
);

COMMENT ON TABLE nbk_operate_log IS '操作日志表';
COMMENT ON COLUMN nbk_operate_log.id IS '主键';
COMMENT ON COLUMN nbk_operate_log.operate_user IS '操作人';
COMMENT ON COLUMN nbk_operate_log.operate_time IS '操作时间';
COMMENT ON COLUMN nbk_operate_log.operate_type IS '操作类型(0:删除,1:新增,2:修改)';
COMMENT ON COLUMN nbk_operate_log.class_name IS '操作类名';
COMMENT ON COLUMN nbk_operate_log.method_name IS '操作方法名';
COMMENT ON COLUMN nbk_operate_log.method_params IS '操作方法参数';
COMMENT ON COLUMN nbk_operate_log.return_value IS '操作方法返回值';
COMMENT ON COLUMN nbk_operate_log.cost_time IS '操作耗时';
COMMENT ON COLUMN nbk_operate_log.operate_desc IS '操作详细描述';

-- ============================================================
-- 8. nbk_role_info (角色信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_role_info;

CREATE TABLE nbk_role_info (
  id SERIAL PRIMARY KEY, -- int(10) 用 SERIAL 即可
  role_code VARCHAR(30),
  role_name VARCHAR(50),
  role_level INTEGER,
  created_user VARCHAR(50) NOT NULL DEFAULT 'system',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50) NOT NULL DEFAULT 'system',
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_role_info IS '角色信息表';
COMMENT ON COLUMN nbk_role_info.id IS 'primary key';
COMMENT ON COLUMN nbk_role_info.role_code IS '角色编码';
COMMENT ON COLUMN nbk_role_info.role_name IS '角色名称(系统管理员、技术专工、检测员、只读用户)';
COMMENT ON COLUMN nbk_role_info.role_level IS '角色等级';
COMMENT ON COLUMN nbk_role_info.created_user IS '创建人';
COMMENT ON COLUMN nbk_role_info.created_time IS '创建时间';
COMMENT ON COLUMN nbk_role_info.updated_user IS '修改人';
COMMENT ON COLUMN nbk_role_info.updated_time IS '修改时间';

CREATE UNIQUE INDEX idx_nbk_role_code ON nbk_role_info(role_code);

-- ============================================================
-- 9. nbk_role_menu (角色和菜单关联表)
-- ============================================================
DROP TABLE IF EXISTS nbk_role_menu;

CREATE TABLE nbk_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, menu_id)
);

COMMENT ON TABLE nbk_role_menu IS '角色和菜单关联表';
COMMENT ON COLUMN nbk_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN nbk_role_menu.menu_id IS '菜单ID';

-- ============================================================
-- 10. nbk_section (机务段信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_section;

CREATE TABLE nbk_section (
  id BIGSERIAL PRIMARY KEY,
  section_code VARCHAR(20) NOT NULL,
  section_name VARCHAR(100) NOT NULL,
  description TEXT,
  created_user VARCHAR(50),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50),
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_section IS '机务段信息表';
COMMENT ON COLUMN nbk_section.id IS '主键';
COMMENT ON COLUMN nbk_section.section_code IS '机务段编码';
COMMENT ON COLUMN nbk_section.section_name IS '机务段名称';
COMMENT ON COLUMN nbk_section.description IS '机务段描述';
COMMENT ON COLUMN nbk_section.created_user IS '创建人';
COMMENT ON COLUMN nbk_section.created_time IS '创建时间';
COMMENT ON COLUMN nbk_section.updated_user IS '更新人';
COMMENT ON COLUMN nbk_section.updated_time IS '修改时间';

CREATE UNIQUE INDEX idx_nbk_section_name ON nbk_section(section_name);

-- ============================================================
-- 11. nbk_station (区站信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_station;

CREATE TABLE nbk_station (
  id BIGSERIAL PRIMARY KEY,
  line_id BIGINT NOT NULL,
  station_code VARCHAR(20) NOT NULL,
  station_name VARCHAR(100) NOT NULL,
  description TEXT,
  created_user VARCHAR(50),
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50),
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_station IS '区站信息表';
COMMENT ON COLUMN nbk_station.id IS 'primary key';
COMMENT ON COLUMN nbk_station.line_id IS '线路id';
COMMENT ON COLUMN nbk_station.station_code IS '区站编码';
COMMENT ON COLUMN nbk_station.station_name IS '区站名';
COMMENT ON COLUMN nbk_station.description IS '区站描述';
COMMENT ON COLUMN nbk_station.created_user IS '创建人';
COMMENT ON COLUMN nbk_station.created_time IS '创建时间';
COMMENT ON COLUMN nbk_station.updated_user IS '修改人';
COMMENT ON COLUMN nbk_station.updated_time IS '更新时间';

CREATE UNIQUE INDEX idx_nbk_station_line_name ON nbk_station(line_id, station_name);

-- ============================================================
-- 12. nbk_user_info (用户信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_user_info;

CREATE TABLE nbk_user_info (
  id BIGSERIAL PRIMARY KEY,
  user_name VARCHAR(50),
  password VARCHAR(50),
  account_no VARCHAR(50) NOT NULL,
  telephone VARCHAR(30),
  avatar VARCHAR(255),
  department_id INTEGER,
  login_times INTEGER DEFAULT 0,
  email VARCHAR(50),
  lock_status SMALLINT DEFAULT 0,
  position SMALLINT,
  last_login TIMESTAMP,
  created_user VARCHAR(50) NOT NULL DEFAULT 'system',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50) NOT NULL DEFAULT 'system',
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_user_info IS '用户信息表';
COMMENT ON COLUMN nbk_user_info.id IS '主键';
COMMENT ON COLUMN nbk_user_info.user_name IS '用户名';
COMMENT ON COLUMN nbk_user_info.password IS '密码';
COMMENT ON COLUMN nbk_user_info.account_no IS '账号';
COMMENT ON COLUMN nbk_user_info.telephone IS '电话';
COMMENT ON COLUMN nbk_user_info.avatar IS '头像链接';
COMMENT ON COLUMN nbk_user_info.department_id IS '部门id';
COMMENT ON COLUMN nbk_user_info.login_times IS '登录错误次数（最大次数）';
COMMENT ON COLUMN nbk_user_info.email IS '邮箱地址';
COMMENT ON COLUMN nbk_user_info.lock_status IS '锁定状态（0：未锁定，1：锁定）';
COMMENT ON COLUMN nbk_user_info.position IS '岗位(0:系统管理员,1:技术专工,2:检测员,3:只读用户)';
COMMENT ON COLUMN nbk_user_info.last_login IS '最后登录时间';
COMMENT ON COLUMN nbk_user_info.created_user IS '创建人';
COMMENT ON COLUMN nbk_user_info.created_time IS '创建时间';
COMMENT ON COLUMN nbk_user_info.updated_user IS '修改人';
COMMENT ON COLUMN nbk_user_info.updated_time IS '更新时间';

CREATE INDEX idx_nbk_user_dept ON nbk_user_info(department_id);

-- ============================================================
-- 13. nbk_user_role (用户角色信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_user_role;

CREATE TABLE nbk_user_role (
  id SERIAL PRIMARY KEY, -- int(10) unsigned 在 PG/Kingbase 中直接用 SERIAL (范围足够)
  user_id BIGINT,
  role_id BIGINT,
  created_user VARCHAR(50) NOT NULL DEFAULT 'system',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50) NOT NULL DEFAULT 'system',
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_user_role IS '用户角色信息表';
COMMENT ON COLUMN nbk_user_role.id IS '主键';
COMMENT ON COLUMN nbk_user_role.user_id IS '用户id';
COMMENT ON COLUMN nbk_user_role.role_id IS '角色id';
COMMENT ON COLUMN nbk_user_role.created_user IS '创建人';
COMMENT ON COLUMN nbk_user_role.created_time IS '创建时间';
COMMENT ON COLUMN nbk_user_role.updated_user IS '修改人';
COMMENT ON COLUMN nbk_user_role.updated_time IS '修改时间';

-- ============================================================
-- 14. nbk_export_info (系统导出信息表)
-- ============================================================
DROP TABLE IF EXISTS nbk_export_info;

CREATE TABLE nbk_export_info (
  id BIGSERIAL PRIMARY KEY,
  task_no VARCHAR(50),
  source_page VARCHAR(100),
  export_type VARCHAR(50),
  file_name VARCHAR(255),
  quantity VARCHAR(50),
  file_size VARCHAR(20),
  query_param TEXT,
  query_api VARCHAR(255),
  export_api VARCHAR(255),
  export_status SMALLINT,
  created_user VARCHAR(50) NOT NULL DEFAULT 'system',
  created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50) NOT NULL DEFAULT 'system',
  updated_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_export_info IS '系统导出信息表';
COMMENT ON COLUMN nbk_export_info.id IS '主键';
COMMENT ON COLUMN nbk_export_info.task_no IS '导出任务编号';
COMMENT ON COLUMN nbk_export_info.source_page IS '导出所属页面';
COMMENT ON COLUMN nbk_export_info.export_type IS '导出类型';
COMMENT ON COLUMN nbk_export_info.file_name IS '导出文件名';
COMMENT ON COLUMN nbk_export_info.quantity IS '数据量';
COMMENT ON COLUMN nbk_export_info.file_size IS '文件大小';
COMMENT ON COLUMN nbk_export_info.query_param IS '查询参数';
COMMENT ON COLUMN nbk_export_info.query_api IS '查询接口路径';
COMMENT ON COLUMN nbk_export_info.export_api IS '导出接口路径';
COMMENT ON COLUMN nbk_export_info.export_status IS '导出状态(0:导出中,1:成功,2:失败)';
COMMENT ON COLUMN nbk_export_info.created_user IS '创建人';
COMMENT ON COLUMN nbk_export_info.created_time IS '创建时间';
COMMENT ON COLUMN nbk_export_info.updated_user IS '修改人';
COMMENT ON COLUMN nbk_export_info.updated_time IS '更新时间';

CREATE TABLE nbk_realtime_analysis (
  id BIGSERIAL PRIMARY KEY,
  source_filepath VARCHAR(500) NOT NULL,
  dest_filepath VARCHAR(500),
  copy_status VARCHAR(10),
  parse_status VARCHAR(10),
  created_user VARCHAR(50) DEFAULT 'system',
  created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_user VARCHAR(50) DEFAULT 'system',
  updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE nbk_realtime_analysis IS '实时分析表';
COMMENT ON COLUMN nbk_realtime_analysis.id IS '主键';
COMMENT ON COLUMN nbk_realtime_analysis.source_filepath IS '源数据目录路径';
COMMENT ON COLUMN nbk_realtime_analysis.dest_filepath IS '复制后的目标目录路径';
COMMENT ON COLUMN nbk_realtime_analysis.copy_status IS '复制状态(Y成功,N失败)';
COMMENT ON COLUMN nbk_realtime_analysis.parse_status IS '解析状态(Y成功,N失败)';
COMMENT ON COLUMN nbk_realtime_analysis.created_user IS '创建人';
COMMENT ON COLUMN nbk_realtime_analysis.created_time IS '创建时间';
COMMENT ON COLUMN nbk_realtime_analysis.updated_user IS '修改人';
COMMENT ON COLUMN nbk_realtime_analysis.updated_time IS '修改时间';

-- ============================================================
-- 附加：自动更新时间触发器 (可选)
-- MySQL 的 ON UPDATE CURRENT_TIMESTAMP 在 Kingbase 中通常通过触发器实现。
-- 如果你需要 updated_time 字段在记录更新时自动变为当前时间，请执行以下代码。
-- 如果不需要，可跳过此部分。
-- ============================================================

-- 创建通用触发器函数
CREATE OR REPLACE FUNCTION update_updated_time_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_time = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为需要自动更新的表绑定触发器
CREATE TRIGGER trg_update_bureau_time BEFORE UPDATE ON nbk_bureau FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_device_time BEFORE UPDATE ON nbk_device FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_inspection_data_time BEFORE UPDATE ON nbk_inspection_data FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_review_result_time BEFORE UPDATE ON nbk_inspection_review_result FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_line_time BEFORE UPDATE ON nbk_line FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_menu_time BEFORE UPDATE ON nbk_menu FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_section_time BEFORE UPDATE ON nbk_section FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_station_time BEFORE UPDATE ON nbk_station FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_user_info_time BEFORE UPDATE ON nbk_user_info FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_user_role_time BEFORE UPDATE ON nbk_user_role FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_role_info_time BEFORE UPDATE ON nbk_role_info FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_export_info_time BEFORE UPDATE ON nbk_export_info FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();
CREATE TRIGGER trg_update_realtime_analysis_time BEFORE UPDATE ON nbk_realtime_analysis FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();