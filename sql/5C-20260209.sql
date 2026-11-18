CREATE UNIQUE INDEX nbk_section_section_name_IDX USING BTREE ON nbk_section (section_name);

CREATE UNIQUE INDEX nbk_line_line_name_IDX USING BTREE ON nbk_line (line_name,section_id);

CREATE UNIQUE INDEX nbk_station_line_id_IDX USING BTREE ON nbk_station (line_id,station_name);

ALTER TABLE nbk_device DROP INDEX nbk_device_device_code_IDX;
CREATE UNIQUE INDEX nbk_device_device_code_IDX USING BTREE ON nbk_device (device_code,station_id);

ALTER TABLE nbk_device DROP KEY uq_nbk_device_device_code;

