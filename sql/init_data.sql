INSERT INTO nbk_department
(department_code, department_name, department_type, parent_id, remark, created_user, created_time, updated_user, updated_time, ancestors, `path`)
VALUES('CD_GDB', '成都供电部', 0, NULL, NULL, 'system', '2026-05-24 16:13:32', 'system', '2026-05-24 16:13:32', '0', '/成都供电部');
INSERT INTO nbk_department
(department_code, department_name, department_type, parent_id, remark, created_user, created_time, updated_user, updated_time, ancestors, `path`)
VALUES('CDSL_GDD', '双流供电段', 1, 1, NULL, 'system', '2026-05-24 16:13:32', 'system', '2026-05-24 16:13:32', '１', '/成都供电部/双流供电段');
INSERT INTO nbk_department
(department_code, department_name, department_type, parent_id, remark, created_user, created_time, updated_user, updated_time, ancestors, `path`)
VALUES('CDSL_DSCJ', '东升车间', 2, 2, NULL, 'system', '2026-05-24 16:13:32', 'system', '2026-05-24 16:13:32', '1,2', '/成都供电部/双流供电段/东升车间');


INSERT INTO nbk_menu (menu_name, parent_id, order_num, `path`, component, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, created_user, created_time, updated_user, updated_time, remark) VALUES('系统管理', 0, 1, NULL, NULL, '', 1, 0, 'C', '0', '0', 'smart5cApi:menu:queryAndPaging', '#', 'system', now(), 'system', now(), '');

INSERT INTO nbk_role_info (role_code, role_name, role_level, created_user, created_time, updated_user, updated_time) VALUES('SUPER_ADMIN', '超级管理员', 1, 'system', '2026-01-27 21:46:30', 'system', '2026-01-27 21:46:30');
INSERT INTO nbk_role_info (role_code, role_name, role_level, created_user, created_time, updated_user, updated_time) VALUES('ADMIN', '管理员', 2, 'system', '2026-01-28 10:04:29', 'system', '2026-01-28 10:04:29');
INSERT INTO nbk_role_info (role_code, role_name, role_level, created_user, created_time, updated_user, updated_time) VALUES('COMMON_USER', '普通用户', 3, 'system', '2026-01-27 21:46:30', 'system', '2026-01-27 21:46:30');

INSERT INTO nbk_role_menu (role_id, menu_id) VALUES((select id from nbk_role_info where role_code='SUPER_ADMIN'), (select id from nbk_menu where menu_name='系统管理'));

INSERT INTO nbk_user_info (user_name, password, account_no, telephone, avatar, department_id, login_times, email, lock_status, `position`, last_login, created_user, created_time, updated_user, updated_time) VALUES('超级管理员', 'admin123', 'superadmin', NULL, NULL, select min(id) from nbk_department, 0, NULL, 0, NULL, now(), 'system', now(), 'system', now());

INSERT INTO nbk_user_role (user_id, role_id, created_user, created_time, updated_user, updated_time) VALUES((select id from nbk_user_info where account_no='superadmin'), (select id from nbk_role_info where role_code='SUPER_ADMIN'), 'system', now(), 'system', now());