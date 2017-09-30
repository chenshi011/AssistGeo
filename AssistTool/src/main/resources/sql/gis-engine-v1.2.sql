-- tmap table

-- whk -------------------------------------------------->
-- Table: public.nge_classify

-- DROP TABLE public.nge_classify;

CREATE TABLE public.nge_classify
(
  id bigserial NOT NULL, -- 资源分类id
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  declare character varying(255), -- 描述
  pcode character varying(64) , -- 父级分类CODE
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  create_time timestamp without time zone, -- 创建时间
  CONSTRAINT nge_classify_pkey PRIMARY KEY (id),
  CONSTRAINT nge_classify_code_usability_key UNIQUE (code, usability)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_classify
  OWNER TO postgres;
COMMENT ON COLUMN public.nge_classify.id IS '资源标签id';
COMMENT ON COLUMN public.nge_classify.code IS '编码';
COMMENT ON COLUMN public.nge_classify.name IS '名称';
COMMENT ON COLUMN public.nge_classify.declare IS '描述';
COMMENT ON COLUMN public.nge_classify.pcode IS '父级标签CODE';
COMMENT ON COLUMN public.nge_classify.usability IS '可用性';
COMMENT ON COLUMN public.nge_classify.create_time IS '创建时间';

-- Table: public.nge_layer

-- DROP TABLE public.nge_layer;

CREATE TABLE public.nge_layer
(
  id bigserial NOT NULL, -- 图层ID
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  declare character varying(255), -- 描述
  visible boolean DEFAULT true, -- 可见性
  opacity double precision, -- 透明度
  extent_top double precision, -- 显示范围 上
  extent_bottom double precision, -- 显示范围 下
  extent_left double precision, -- 显示范围 左
  extent_right double precision, -- 显示范围 右
  min_res double precision, -- 最小分辨率
  max_res double precision, -- 最大分辨率
  type character varying(64), -- 图层类型
  info_id bigint NOT NULL , -- 图层参数ID
  workspace_id bigint NOT NULL, -- 工作空间ID
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  create_time timestamp without time zone, -- 创建时间
  CONSTRAINT nge_layer_pkey PRIMARY KEY (id),
  CONSTRAINT nge_layer_code_workspace_id_usability_key UNIQUE (code, workspace_id, usability),
  CONSTRAINT nge_layer_opacity_check CHECK (opacity >= 0::double precision AND opacity <= 1::double precision)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_layer
  OWNER TO postgres;
COMMENT ON TABLE public.nge_layer
  IS '存储图层基础属性，图层类型，图层参数ID。';
COMMENT ON COLUMN public.nge_layer.id IS '图层ID';
COMMENT ON COLUMN public.nge_layer.code IS '编码';
COMMENT ON COLUMN public.nge_layer.name IS '名称';
COMMENT ON COLUMN public.nge_layer.declare IS '描述';
COMMENT ON COLUMN public.nge_layer.visible IS '可见性';
COMMENT ON COLUMN public.nge_layer.opacity IS '透明度';
COMMENT ON COLUMN public.nge_layer.extent_top IS '显示范围 上';
COMMENT ON COLUMN public.nge_layer.extent_bottom IS '显示范围 下';
COMMENT ON COLUMN public.nge_layer.extent_left IS '显示范围 左';
COMMENT ON COLUMN public.nge_layer.extent_right IS '显示范围 右';
COMMENT ON COLUMN public.nge_layer.min_res IS '最小分辨率';
COMMENT ON COLUMN public.nge_layer.max_res IS '最大分辨率';
COMMENT ON COLUMN public.nge_layer.type IS '图层类型';
COMMENT ON COLUMN public.nge_layer.info_id IS '图层参数ID';
COMMENT ON COLUMN public.nge_layer.workspace_id IS '工作空间ID';
COMMENT ON COLUMN public.nge_layer.usability IS '可用性';
COMMENT ON COLUMN public.nge_layer.create_time IS '创建时间';

-- Table: public.nge_layer_group

-- DROP TABLE public.nge_layer_group;

CREATE TABLE public.nge_layer_group
(
  id bigserial NOT NULL, -- 图层组ID
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  declare character varying(255), -- 描述
  visible boolean DEFAULT true, -- 可见性
  opacity double precision, -- 透明度
  extent_top double precision, -- 显示范围 上
  extent_bottom double precision, -- 显示范围 下
  extent_left double precision, -- 显示范围 左
  extent_right double precision, -- 显示范围 右
  max_res double precision, -- 最大分辨率
  min_res double precision, -- 最小分辨率
  workspace_id bigint NOT NULL, -- 工作空间ID
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  create_time timestamp without time zone, -- 创建时间
  CONSTRAINT nge_layer_group_pkey PRIMARY KEY (id),
  CONSTRAINT nge_layer_group_code_workspace_id_usability_key UNIQUE (code, workspace_id, usability),
  CONSTRAINT nge_layer_group_opacity_check CHECK (opacity >= 0::double precision AND opacity <= 1::double precision)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_layer_group
  OWNER TO postgres;
COMMENT ON COLUMN public.nge_layer_group.id IS '图层组ID';
COMMENT ON COLUMN public.nge_layer_group.code IS '编码';
COMMENT ON COLUMN public.nge_layer_group.name IS '名称';
COMMENT ON COLUMN public.nge_layer_group.declare IS '描述';
COMMENT ON COLUMN public.nge_layer_group.visible IS '可见性';
COMMENT ON COLUMN public.nge_layer_group.opacity IS '透明度';
COMMENT ON COLUMN public.nge_layer_group.extent_top IS '显示范围 上';
COMMENT ON COLUMN public.nge_layer_group.extent_bottom IS '显示范围 下';
COMMENT ON COLUMN public.nge_layer_group.extent_left IS '显示范围 左';
COMMENT ON COLUMN public.nge_layer_group.extent_right IS '显示范围 右';
COMMENT ON COLUMN public.nge_layer_group.max_res IS '最大分辨率';
COMMENT ON COLUMN public.nge_layer_group.min_res IS '最小分辨率';
COMMENT ON COLUMN public.nge_layer_group.workspace_id IS '工作空间ID';
COMMENT ON COLUMN public.nge_layer_group.usability IS '可用性';
COMMENT ON COLUMN public.nge_layer_group.create_time IS '创建时间';

-- Table: public.nge_source_vector

-- DROP TABLE public.nge_source_vector;

CREATE TABLE public.nge_source_vector
(
  id bigserial NOT NULL, -- 数据源ID
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  declare character varying(255), -- 描述
  type character varying(64) NOT NULL, -- 类型
  stname character varying(255) NOT NULL, -- 空间表名
  def boolean NOT NULL DEFAULT false, -- 是否转义
  cql text, -- 过滤条件
  workspace_id bigint NOT NULL, -- 工作空间id
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  create_time timestamp without time zone, -- 创建时间
  CONSTRAINT nge_source_vector_pkey PRIMARY KEY (id),
  CONSTRAINT nge_source_vector_code_workspace_id_usability_key UNIQUE (code, workspace_id, usability)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_source_vector
  OWNER TO postgres;
COMMENT ON COLUMN public.nge_source_vector.id IS '数据源ID';
COMMENT ON COLUMN public.nge_source_vector.code IS '编码';
COMMENT ON COLUMN public.nge_source_vector.name IS '名称';
COMMENT ON COLUMN public.nge_source_vector.declare IS '描述';
COMMENT ON COLUMN public.nge_source_vector.type IS '类型';
COMMENT ON COLUMN public.nge_source_vector.stname IS '空间表名';
COMMENT ON COLUMN public.nge_source_vector.def IS '是否转义';
COMMENT ON COLUMN public.nge_source_vector.cql IS '过滤条件';
COMMENT ON COLUMN public.nge_source_vector.workspace_id IS '工作空间id';
COMMENT ON COLUMN public.nge_source_vector.usability IS '可用性';
COMMENT ON COLUMN public.nge_source_vector.create_time IS '创建时间';

-- Table: public.nge_style_conf

-- DROP TABLE public.nge_style_conf;

CREATE TABLE public.nge_style_conf
(
  id bigserial NOT NULL, -- 样式配置ID
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  declare character varying(255), -- 描述
  anno_id bigint, -- 标注样式ID
  image_id bigint, -- 点样式ID
  stroke_id bigint, -- 线样式ID
  fill_id bigint, -- 填充样式ID
  workspace_id bigint NOT NULL, -- 工作空间ID
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  create_time timestamp without time zone, -- 创建时间
  CONSTRAINT nge_style_conf_pkey PRIMARY KEY (id),
  CONSTRAINT nge_style_conf_code_workspace_id_usability_key UNIQUE (code, workspace_id, usability)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_style_conf
  OWNER TO postgres;
COMMENT ON COLUMN public.nge_style_conf.id IS '样式配置ID';
COMMENT ON COLUMN public.nge_style_conf.code IS '编码';
COMMENT ON COLUMN public.nge_style_conf.name IS '名称';
COMMENT ON COLUMN public.nge_style_conf.declare IS '描述';
COMMENT ON COLUMN public.nge_style_conf.anno_id IS '标注样式ID';
COMMENT ON COLUMN public.nge_style_conf.image_id IS '点样式ID';
COMMENT ON COLUMN public.nge_style_conf.stroke_id IS '线样式ID';
COMMENT ON COLUMN public.nge_style_conf.fill_id IS '填充样式ID';
COMMENT ON COLUMN public.nge_style_conf.workspace_id IS '工作空间ID';
COMMENT ON COLUMN public.nge_style_conf.usability IS '可用性';
COMMENT ON COLUMN public.nge_style_conf.create_time IS '创建时间';

-- Table: public.nge_style_def

-- DROP TABLE public.nge_style_def;

CREATE TABLE public.nge_style_def
(
  id bigserial NOT NULL, -- 样式定义ID
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  declare character varying(255), -- 描述
  type character varying(64) NOT NULL, -- 样式类型
  info_id bigint NOT NULL, -- 样式参数id
  workspace_id bigint NOT NULL, -- 工作空间id
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  create_time timestamp without time zone, -- 创建时间
  CONSTRAINT nge_style_def_pkey PRIMARY KEY (id),
  CONSTRAINT nge_style_def_code_workspace_id_usability_key UNIQUE (code, workspace_id, usability)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_style_def
  OWNER TO postgres;
COMMENT ON TABLE public.nge_style_def
  IS '存储样式基础属性，样式类型，样式参数ID。';
COMMENT ON COLUMN public.nge_style_def.id IS '样式定义ID';
COMMENT ON COLUMN public.nge_style_def.code IS '编码';
COMMENT ON COLUMN public.nge_style_def.name IS '名称';
COMMENT ON COLUMN public.nge_style_def.declare IS '描述';
COMMENT ON COLUMN public.nge_style_def.type IS '样式类型';
COMMENT ON COLUMN public.nge_style_def.info_id IS '样式参数id';
COMMENT ON COLUMN public.nge_style_def.workspace_id IS '工作空间id';
COMMENT ON COLUMN public.nge_style_def.usability IS '可用性';
COMMENT ON COLUMN public.nge_style_def.create_time IS '创建时间';

-- Table: public.nge_thematic_map

-- DROP TABLE public.nge_thematic_map;

CREATE TABLE public.nge_thematic_map
(
  id bigserial NOT NULL, -- 专题地图ID
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  declare character varying(255), -- 描述
  crs character varying(64), -- 坐标系
  center character varying(64), -- 中心点
  zoom_level smallint, -- 缩放层级
  max_zoom smallint NOT NULL DEFAULT 28, -- 缩放层级最大值
  min_zoom smallint NOT NULL DEFAULT 0, -- 缩放层级最小值
  roate double precision, -- 旋转
  pixel_ratio double precision, -- 像素比例
  workspace_id bigint NOT NULL, -- 工作空间ID
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  create_time timestamp(6) without time zone, -- 创建时间
  CONSTRAINT nge_thematic_map_pkey PRIMARY KEY (id),
  CONSTRAINT nge_thematic_map_code_workspace_id_usability_key UNIQUE (code, workspace_id, usability)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_thematic_map
  OWNER TO postgres;
COMMENT ON COLUMN public.nge_thematic_map.id IS '专题地图ID';
COMMENT ON COLUMN public.nge_thematic_map.code IS '编码';
COMMENT ON COLUMN public.nge_thematic_map.name IS '名称';
COMMENT ON COLUMN public.nge_thematic_map.declare IS '描述';
COMMENT ON COLUMN public.nge_thematic_map.crs IS '坐标系';
COMMENT ON COLUMN public.nge_thematic_map.center IS '中心点';
COMMENT ON COLUMN public.nge_thematic_map.zoom_level IS '缩放层级';
COMMENT ON COLUMN public.nge_thematic_map.max_zoom IS '缩放层级最大值';
COMMENT ON COLUMN public.nge_thematic_map.min_zoom IS '缩放层级最小值';
COMMENT ON COLUMN public.nge_thematic_map.roate IS '旋转';
COMMENT ON COLUMN public.nge_thematic_map.pixel_ratio IS '像素比例';
COMMENT ON COLUMN public.nge_thematic_map.workspace_id IS '工作空间ID';
COMMENT ON COLUMN public.nge_thematic_map.usability IS '可用性';
COMMENT ON COLUMN public.nge_thematic_map.create_time IS '创建时间';

-- Table: public.nge_workspace

-- DROP TABLE public.nge_workspace;

CREATE TABLE public.nge_workspace
(
  id bigserial NOT NULL, -- 工作空间id
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  declare character varying(255), -- 描述
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  create_time timestamp without time zone, -- 创建时间
  CONSTRAINT nge_workspace_pkey PRIMARY KEY (id),
  CONSTRAINT nge_workspace_code_usability_key UNIQUE (code, usability)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_workspace
  OWNER TO postgres;
COMMENT ON COLUMN public.nge_workspace.id IS '工作空间id';
COMMENT ON COLUMN public.nge_workspace.code IS '编码';
COMMENT ON COLUMN public.nge_workspace.name IS '名称';
COMMENT ON COLUMN public.nge_workspace.declare IS '描述';
COMMENT ON COLUMN public.nge_workspace.usability IS '可用性';
COMMENT ON COLUMN public.nge_workspace.create_time IS '创建时间';

-- whk --------------------------------------------------------------<



-- -----------------------------------------------------gzx --->
-- Table: public.nge_style_def_anno

-- DROP TABLE public.nge_style_def_anno;

-- 标注样式参数

CREATE TABLE public.nge_style_def_anno
(
  id bigserial, -- ID
  style_id bigint NOT NULL, -- 样式ID
  font character varying(255), -- 字体
  offset_x smallint, -- x偏移
  offset_y smallint, -- y偏移
  scale double precision, -- 缩放
  rotate boolean DEFAULT FALSE, -- 是否旋转
  rotation double precision, -- 旋转角度
  align character varying(64), -- 垂直位置
  baseline character varying(64), -- 水平位置
  fill_id bigint NOT NULL, -- 填充样式ID
  stroke_id bigint, -- 线条样式ID
  CONSTRAINT nge_style_def_anno_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_style_def_anno
  OWNER TO postgres;
COMMENT ON TABLE public.nge_style_def_anno IS '标注样式参数';
COMMENT ON COLUMN public.nge_style_def_anno.id IS 'ID';
COMMENT ON COLUMN public.nge_style_def_anno.style_id IS '样式ID';
COMMENT ON COLUMN public.nge_style_def_anno.font IS '字体';
COMMENT ON COLUMN public.nge_style_def_anno.offset_x IS 'x偏移';
COMMENT ON COLUMN public.nge_style_def_anno.offset_y IS 'y偏移';
COMMENT ON COLUMN public.nge_style_def_anno.scale IS '缩放';
COMMENT ON COLUMN public.nge_style_def_anno.rotate IS '是否旋转';
COMMENT ON COLUMN public.nge_style_def_anno.rotation IS '旋转角度';
COMMENT ON COLUMN public.nge_style_def_anno.align IS '垂直位置';
COMMENT ON COLUMN public.nge_style_def_anno.baseline IS '水平位置';
COMMENT ON COLUMN public.nge_style_def_anno.fill_id IS '填充样式ID';
COMMENT ON COLUMN public.nge_style_def_anno.stroke_id IS '线条样式ID';


-- 点要素图标样式ID序列
-- Sequence: public.nge_style_def_icon_id_seq

-- DROP SEQUENCE public.nge_style_def_icon_id_seq;

CREATE SEQUENCE public.nge_style_def_icon_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_style_def_icon_id_seq
  OWNER TO postgres;


-- Table: public.nge_style_def_icon

-- DROP TABLE public.nge_style_def_icon;

-- 点要素图标样式参数


CREATE TABLE public.nge_style_def_icon
(
  id bigint NOT NULL DEFAULT nextval('nge_style_def_icon_id_seq'::regclass), -- ID
  style_id bigint NOT NULL, -- 样式ID
  anchor_x double precision, -- 锚点x
  anchor_y double precision, -- 锚点y
  anchor_x_unit character varying(64), -- 锚点x单位
  anchor_y_unit character varying(64), -- 锚点Y单位
  color character varying(64), -- 着色
  cross_origin boolean, -- 是否跨域
  offset_x double precision, -- 偏移x
  offset_y double precision, -- 偏移y
  opacity double precision, -- 透明度
  scale double precision, -- 缩放
  rotate boolean DEFAULT FALSE, -- 是否随地图旋转
  rotation double precision, -- 旋转角度
  icon_width double precision, -- 图片宽
  icon_height double precision, -- 图片高
  img_width double precision, -- 图像数据宽
  img_height double precision, -- 图像数据高
  src text, -- 图片url
  img text, -- base64图像
  CONSTRAINT nge_style_def_icon_id_pk PRIMARY KEY (id),
  CONSTRAINT nge_style_def_opacity_check CHECK (opacity >= 0::double precision AND opacity <= 1::double precision)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_style_def_icon
  OWNER TO postgres;
COMMENT ON TABLE public.nge_style_def_icon IS '点要素图标样式参数';
COMMENT ON COLUMN public.nge_style_def_icon.id IS 'ID';
COMMENT ON COLUMN public.nge_style_def_icon.style_id IS '样式ID';
COMMENT ON COLUMN public.nge_style_def_icon.anchor_x IS '锚点x';
COMMENT ON COLUMN public.nge_style_def_icon.anchor_y IS '锚点y';
COMMENT ON COLUMN public.nge_style_def_icon.anchor_x_unit IS '锚点x单位';
COMMENT ON COLUMN public.nge_style_def_icon.anchor_y_unit IS '锚点Y单位';
COMMENT ON COLUMN public.nge_style_def_icon.color IS '着色';
COMMENT ON COLUMN public.nge_style_def_icon.cross_origin IS '是否跨域';
COMMENT ON COLUMN public.nge_style_def_icon.offset_x IS '偏移x';
COMMENT ON COLUMN public.nge_style_def_icon.offset_y IS '偏移y';
COMMENT ON COLUMN public.nge_style_def_icon.opacity IS '透明度';
COMMENT ON COLUMN public.nge_style_def_icon.scale IS '缩放';
COMMENT ON COLUMN public.nge_style_def_icon.rotate IS '是否随地图旋转';
COMMENT ON COLUMN public.nge_style_def_icon.rotation IS '旋转角度';
COMMENT ON COLUMN public.nge_style_def_icon.icon_width IS '图片宽';
COMMENT ON COLUMN public.nge_style_def_icon.icon_height IS '图片高';
COMMENT ON COLUMN public.nge_style_def_icon.img_width IS '图像数据宽';
COMMENT ON COLUMN public.nge_style_def_icon.img_height IS '图像数据高';
COMMENT ON COLUMN public.nge_style_def_icon.src IS '图片url';
COMMENT ON COLUMN public.nge_style_def_icon.img IS 'base64图像';



-- 点要素图形样式ID序列
-- Sequence: public.nge_style_def_shape_id_seq

-- DROP SEQUENCE public.nge_style_def_shape_id_seq;

CREATE SEQUENCE public.nge_style_def_shape_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_style_def_shape_id_seq
  OWNER TO postgres;

-- 点要素图形样式
-- Table: public.nge_style_def_shape

-- DROP TABLE public.nge_style_def_shape;

CREATE TABLE public.nge_style_def_shape
(
  id bigint NOT NULL DEFAULT nextval('nge_style_def_shape_id_seq'::regclass), -- ID
  style_id bigint NOT NULL, -- 样式ID
  stroke_id bigint, -- 线条样式ID
  fill_id bigint, -- 填充ID
  points smallint, -- 点数或者边数
  radius double precision, -- 外径
  radius2 double precision, -- 内径
  angle double precision, -- 角度
  rotate boolean DEFAULT FALSE, -- 是否随地图旋转
  rotation double precision, -- 旋转角度
  CONSTRAINT nge_style_def_shape_id_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_style_def_shape
  OWNER TO postgres;
COMMENT ON TABLE public.nge_style_def_shape IS '点要素图形样式参数';
COMMENT ON COLUMN public.nge_style_def_shape.id IS 'ID';
COMMENT ON COLUMN public.nge_style_def_shape.style_id IS '样式ID';
COMMENT ON COLUMN public.nge_style_def_shape.stroke_id IS '线条样式ID ';
COMMENT ON COLUMN public.nge_style_def_shape.fill_id IS '填充ID';
COMMENT ON COLUMN public.nge_style_def_shape.points IS '点数或者边数';
COMMENT ON COLUMN public.nge_style_def_shape.radius IS '外径';
COMMENT ON COLUMN public.nge_style_def_shape.radius2 IS '内径';
COMMENT ON COLUMN public.nge_style_def_shape.angle IS '角度';
COMMENT ON COLUMN public.nge_style_def_shape.rotate IS '是否随地图旋转';
COMMENT ON COLUMN public.nge_style_def_shape.rotation IS '旋转角度';


-- 线形样式ID序列
-- Sequence: public.nge_style_def_stroke_id_seq

-- DROP SEQUENCE public.nge_style_def_stroke_id_seq;

CREATE SEQUENCE public.nge_style_def_stroke_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_style_def_stroke_id_seq
  OWNER TO postgres;


-- 线形样式参数
-- Table: public.nge_style_def_stroke

-- DROP TABLE public.nge_style_def_stroke;

CREATE TABLE public.nge_style_def_stroke
(
  id bigint NOT NULL DEFAULT nextval('nge_style_def_stroke_id_seq'::regclass), -- ID
  style_id bigint NOT NULL, -- 样式ID
  color character varying(64), -- 颜色
  cap character varying(64), -- 线帽
  "join" character varying(64), -- 交汇边角
  dash character varying(64), -- 虚线
  dash_offset smallint, -- 虚线偏移
  miter_limit smallint, -- 斜接长度
  width smallint, -- 线宽
  CONSTRAINT nge_style_def_stroke_id_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_style_def_stroke
  OWNER TO postgres;
COMMENT ON TABLE public.nge_style_def_stroke IS '线条样式参数';
COMMENT ON COLUMN public.nge_style_def_stroke.id IS 'ID';
COMMENT ON COLUMN public.nge_style_def_stroke.style_id IS '样式ID';
COMMENT ON COLUMN public.nge_style_def_stroke.color IS '颜色';
COMMENT ON COLUMN public.nge_style_def_stroke.cap IS '线帽';
COMMENT ON COLUMN public.nge_style_def_stroke."join" IS '交汇边角';
COMMENT ON COLUMN public.nge_style_def_stroke.dash IS '虚线';
COMMENT ON COLUMN public.nge_style_def_stroke.dash_offset IS '虚线偏移';
COMMENT ON COLUMN public.nge_style_def_stroke.miter_limit IS '斜接长度';
COMMENT ON COLUMN public.nge_style_def_stroke.width IS '线宽';


-- 填充样式ID
-- Sequence: public.nge_style_def_fill_id_seq

-- DROP SEQUENCE public.nge_style_def_fill_id_seq;

CREATE SEQUENCE public.nge_style_def_fill_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_style_def_fill_id_seq
  OWNER TO postgres;


-- Table: public.nge_style_def_fill

-- DROP TABLE public.nge_style_def_fill;

CREATE TABLE public.nge_style_def_fill
(
  id bigint NOT NULL DEFAULT nextval('nge_style_def_fill_id_seq'::regclass), -- ID
  style_id bigint NOT NULL, -- 样式ID
  color character varying(64), -- 颜色
  CONSTRAINT nge_style_def_fill_id_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_style_def_fill
  OWNER TO postgres;
COMMENT ON TABLE public.nge_style_def_fill IS '填充样式参数';
COMMENT ON COLUMN public.nge_style_def_fill.id IS 'ID';
COMMENT ON COLUMN public.nge_style_def_fill.style_id IS '样式ID';
COMMENT ON COLUMN public.nge_style_def_fill.color IS '颜色';

-- 工作空间内容ID序列
-- Sequence: public.nge_workspace_cont_id_seq

-- DROP SEQUENCE public.nge_workspace_cont_id_seq;

CREATE SEQUENCE public.nge_workspace_cont_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_workspace_cont_id_seq
  OWNER TO postgres;


-- 工作空间内容表
-- Table: public.nge_workspace_cont

-- DROP TABLE public.nge_workspace_cont;

CREATE TABLE public.nge_workspace_cont
(
  id bigint NOT NULL DEFAULT nextval('nge_workspace_cont_id_seq'::regclass), -- ID
  workspace_id bigint NOT NULL, -- 工作空间id
  res_type character varying(64) NOT NULL, -- 资源类型
  res_id bigint NOT NULL, -- 资源ID
  CONSTRAINT nge_workspace_info_id_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_workspace_cont OWNER TO postgres;
COMMENT ON TABLE public.nge_workspace_cont IS '工作空间内容表';
COMMENT ON COLUMN public.nge_workspace_cont.id IS 'ID';
COMMENT ON COLUMN public.nge_workspace_cont.workspace_id IS '工作空间id';
COMMENT ON COLUMN public.nge_workspace_cont.res_type IS '资源类型';
COMMENT ON COLUMN public.nge_workspace_cont.res_id IS '资源ID';


-- 资源标签内容
-- Sequence: public.nge_classify_cont_id_seq

-- DROP SEQUENCE public.nge_classify_cont_id_seq;

CREATE SEQUENCE public.nge_classify_cont_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_classify_cont_id_seq
  OWNER TO postgres;

-- 资源标签
-- Table: public.nge_classify_cont

-- DROP TABLE public.nge_classify_cont;

CREATE TABLE public.nge_classify_cont
(
  id bigint NOT NULL DEFAULT nextval('nge_classify_cont_id_seq'::regclass), -- ID
  classify_id bigint NOT NULL, -- 标签id
  res_type character varying(64) NOT NULL, -- 资源类型
  res_id bigint NOT NULL, -- 资源ID
  CONSTRAINT nge_classify_cont_id_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_classify_cont
  OWNER TO postgres;
COMMENT ON TABLE public.nge_classify_cont IS '资源标签内容';
COMMENT ON COLUMN public.nge_classify_cont.id IS 'ID';
COMMENT ON COLUMN public.nge_classify_cont.classify_id IS '标签ID';
COMMENT ON COLUMN public.nge_classify_cont.res_type IS '资源类型';
COMMENT ON COLUMN public.nge_classify_cont.res_id IS '资源ID';


-- 业务数据结构信息表
-- Sequence: public.nge_schema_custom_id_seq

-- DROP SEQUENCE public.nge_schema_custom_id_seq;

CREATE SEQUENCE public.nge_schema_custom_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_schema_custom_id_seq
  OWNER TO postgres;


-- 业务数据结构信息表
-- Table: public.nge_schema_custom

-- DROP TABLE public.nge_schema_custom;

CREATE TABLE public.nge_schema_custom
(
  id bigint NOT NULL DEFAULT nextval('nge_schema_custom_id_seq'::regclass), -- ID
  name character varying(64) NOT NULL, -- 表名
  declare character varying(255), -- 描述
  workspace_id bigint NOT NULL, -- 工作空间ID
  usability bigint NOT NULL DEFAULT 0, -- 可用性
  pk_name character varying(64), -- 主键字段名
  type character varying(64), -- 表类型
  create_time timestamp without time zone DEFAULT current_timestamp, -- 创建时间
  CONSTRAINT nge_schema_custom_id_pk PRIMARY KEY (id),
  CONSTRAINT nge_schema_custom_name_uniq UNIQUE (name, usability)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_schema_custom
  OWNER TO postgres;
COMMENT ON TABLE public.nge_schema_custom IS '业务数据结构信息';
COMMENT ON COLUMN public.nge_schema_custom.id IS 'ID';
COMMENT ON COLUMN public.nge_schema_custom.name IS '表名';
COMMENT ON COLUMN public.nge_schema_custom.declare IS '描述';
COMMENT ON COLUMN public.nge_schema_custom.workspace_id IS '工作空间ID';
COMMENT ON COLUMN public.nge_schema_custom.usability IS '可用性';
COMMENT ON COLUMN public.nge_schema_custom.pk_name IS '主键字段名';
COMMENT ON COLUMN public.nge_schema_custom.type IS '表类型';
COMMENT ON COLUMN public.nge_schema_custom.create_time IS '创建时间';


-- 地图元数据字典-ID-序列

-- Sequence: public.nge_map_meta_id_seq

-- DROP SEQUENCE public.nge_map_meta_id_seq;

CREATE SEQUENCE public.nge_map_meta_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_map_meta_id_seq
  OWNER TO postgres;


-- 地图元数据字典

-- Table: public.nge_map_meta

-- DROP TABLE public.nge_map_meta;

CREATE TABLE public.nge_map_meta
(
  id bigint NOT NULL DEFAULT nextval('nge_map_meta_id_seq'::regclass), -- ID
  code character varying(64) NOT NULL, -- 编码
  name character varying(64) NOT NULL, -- 名称
  type character varying(64) NOT NULL, -- 类型
  table_name character varying(64) NOT NULL, -- 对应表名
  declare character varying(255), -- 描述
  CONSTRAINT nge_map_meta_id_pk PRIMARY KEY (id),
  CONSTRAINT nge_map_meta_code_uniq UNIQUE (code)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_map_meta
  OWNER TO postgres;
COMMENT ON TABLE public.nge_map_meta IS '地图元数据字典';
COMMENT ON COLUMN public.nge_map_meta.id IS 'ID';
COMMENT ON COLUMN public.nge_map_meta.code IS '编码';
COMMENT ON COLUMN public.nge_map_meta.name IS '名称';
COMMENT ON COLUMN public.nge_map_meta.type IS '类型';
COMMENT ON COLUMN public.nge_map_meta.table_name IS '对应表名';
COMMENT ON COLUMN public.nge_map_meta.declare IS '描述';

INSERT INTO "public"."nge_map_meta" VALUES ('1', 'map', '地图', 'meta', 'nge_thematic_map', '地图');
INSERT INTO "public"."nge_map_meta" VALUES ('2', 'layer', '图层', 'meta', 'nge_layer', '图层');
INSERT INTO "public"."nge_map_meta" VALUES ('3', 'layer_group', '图层组', 'meta', 'nge_layer_group', '图层组');
INSERT INTO "public"."nge_map_meta" VALUES ('4', 'style_def', '样式定义', 'meta', 'nge_style_def', '样式定义');
INSERT INTO "public"."nge_map_meta" VALUES ('5', 'style_conf', '样式配置', 'meta', 'nge_style_conf', '样式配置');
INSERT INTO "public"."nge_map_meta" VALUES ('6', 'workspace', '工作空间', 'meta', 'nge_workspace', '工作空间');
INSERT INTO "public"."nge_map_meta" VALUES ('7', 'classify', '资源标签', 'meta', 'nge_classify', '资源标签');
INSERT INTO "public"."nge_map_meta" VALUES ('8', 'layer_tile', '瓦片图层参数', 'layer', 'nge_layer_tile', '瓦片图层参数');
INSERT INTO "public"."nge_map_meta" VALUES ('9', 'layer_image', '图像图层参数', 'layer', 'nge_layer_image', '图像图层参数');
INSERT INTO "public"."nge_map_meta" VALUES ('10', 'layer_vector', '矢量图层参数', 'layer', 'nge_layer_vector', '矢量图层参数');
INSERT INTO "public"."nge_map_meta" VALUES ('11', 'style_anno', '标注样式', 'style_def', 'nge_style_def_anno', '标注样式');
INSERT INTO "public"."nge_map_meta" VALUES ('12', 'style_icon', '点要素图标样式', 'style_def', 'nge_style_def_icon', '点要素图标样式');
INSERT INTO "public"."nge_map_meta" VALUES ('13', 'style_shape', '点要素图形样式', 'style_def', 'nge_style_def_shape', '点要素图形样式');
INSERT INTO "public"."nge_map_meta" VALUES ('14', 'style_stroke', '线条样式', 'style_def', 'nge_style_def_stroke', '线条样式');
INSERT INTO "public"."nge_map_meta" VALUES ('15', 'style_fill', '填充样式', 'style_def', 'nge_style_def_fill', '填充样式');
INSERT INTO "public"."nge_map_meta" VALUES ('16', 'source_vector', '数据源', 'meta', 'nge_source_vector', '数据源');


-- gzx ------------------------------------------------------------<



-- hyt ------------------------------------------------------------>
-- 地图-图层关系
-- Table: public.nge_map_layer_rel

-- DROP TABLE public.nge_map_layer_rel;

CREATE TABLE public.nge_map_layer_rel
(
    id bigserial NOT NULL, -- ID
    map_id bigint NOT NULL, -- 专题地图ID
    layer_id bigint NOT NULL, -- 图层ID
    index smallint NOT NULL, -- 图层次序
    CONSTRAINT pk_nge_map_layer_rel PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.nge_map_layer_rel
    OWNER to postgres;

COMMENT ON COLUMN public.nge_map_layer_rel.id IS 'ID';
COMMENT ON COLUMN public.nge_map_layer_rel.map_id IS '专题地图ID';
COMMENT ON COLUMN public.nge_map_layer_rel.layer_id IS '图层ID';
COMMENT ON COLUMN public.nge_map_layer_rel.index IS '图层次序';



-- 地图-图层组关系
-- Table: public.nge_map_layer_group_rel

-- DROP TABLE public.nge_map_layer_group_rel;

CREATE TABLE public.nge_map_layer_group_rel
(
    id bigserial NOT NULL, -- ID
    map_id bigint NOT NULL, -- 专题地图ID
    layer_group_id bigint NOT NULL, -- 图层组ID
    index smallint NOT NULL, -- 图层组次序
    CONSTRAINT pk_nge_map_layer_group_rel PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.nge_map_layer_group_rel
    OWNER to postgres;

COMMENT ON COLUMN public.nge_map_layer_group_rel.id
    IS 'ID';

COMMENT ON COLUMN public.nge_map_layer_group_rel.map_id
    IS '专题地图ID';

COMMENT ON COLUMN public.nge_map_layer_group_rel.layer_group_id
    IS '图层组ID';


-- 矢量图层参数
-- Table: public.nge_layer_vector

-- DROP TABLE public.nge_layer_vector;

CREATE TABLE public.nge_layer_vector
(
    id bigserial NOT NULL, -- ID
    layer_id bigint NOT NULL, -- 图层ID
    source_id bigint NOT NULL, -- 数据源ID
    style_id bigint, -- 样式ID
    anno_id bigint, -- 标注ID
    anno_field character varying(64), -- 标注字段
    strict boolean default FALSE, -- 严格模式
    options text, -- 选项参数
    CONSTRAINT pk_nge_layer_vector PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.nge_layer_vector
    OWNER to postgres;

COMMENT ON COLUMN public.nge_layer_vector.id
    IS 'ID';

COMMENT ON COLUMN public.nge_layer_vector.layer_id
    IS '矢量图层ID';

COMMENT ON COLUMN public.nge_layer_vector.source_id
    IS '数据源ID';

COMMENT ON COLUMN public.nge_layer_vector.style_id
    IS '样式配置ID';

COMMENT ON COLUMN public.nge_layer_vector.anno_id
    IS '标注样式';

COMMENT ON COLUMN public.nge_layer_vector.anno_field
    IS '标注字段';
COMMENT ON COLUMN public.nge_layer_vector.options
    IS '选项参数';

COMMENT ON COLUMN public.nge_layer_vector.strict
    IS '严格模式';


-- 瓦片图层参数

-- Table: public.nge_layer_tile

-- DROP TABLE public.nge_layer_tile;

CREATE TABLE public.nge_layer_tile
(
    id bigserial NOT NULL, -- ID
    layer_id bigint NOT NULL, -- 图层ID
    type character varying(64) NOT NULL, -- 瓦片类型
    url text, -- 瓦片地址
    options text, -- 选项参数
    CONSTRAINT pk_nge_layer_tile PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.nge_layer_tile OWNER to postgres;

COMMENT ON COLUMN public.nge_layer_tile.id IS 'ID';
COMMENT ON COLUMN public.nge_layer_tile.layer_id IS '瓦片图层ID';
COMMENT ON COLUMN public.nge_layer_tile.type IS '瓦片图层类型';
COMMENT ON COLUMN public.nge_layer_tile.url IS '瓦片图层地址';


-- 图像图层参数

-- Table: public.nge_layer_image

-- DROP TABLE public.nge_layer_image;

CREATE TABLE public.nge_layer_image
(
    id bigserial NOT NULL, -- ID
    layer_id bigint NOT NULL, -- 图层ID
    type character varying(64) NOT NULL, -- 类型
    url text NOT NULL, -- 地址
    options text, -- 选项参数
    CONSTRAINT pk_nge_layer_image PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.nge_layer_image OWNER to postgres;

COMMENT ON COLUMN public.nge_layer_image.id IS 'ID';
COMMENT ON COLUMN public.nge_layer_image.layer_id IS '矢量图层ID';
COMMENT ON COLUMN public.nge_layer_image.type IS '类型';
COMMENT ON COLUMN public.nge_layer_image.url IS '地址';




-- 图层组-图层关系

-- Table: public.nge_layer_group_rel

-- DROP TABLE public.nge_layer_group_rel;

CREATE TABLE public.nge_layer_group_rel
(
    id bigserial NOT NULL, -- ID
    layer_group_id bigint NOT NULL, -- 图层组ID
    layer_id bigint NOT NULL, -- 图层ID
    index smallint NOT NULL, -- 图层次序
    CONSTRAINT pk_nge_layer_group_rel PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.nge_layer_group_rel
    OWNER to postgres;

COMMENT ON COLUMN public.nge_layer_group_rel.id IS 'ID';
COMMENT ON COLUMN public.nge_layer_group_rel.layer_group_id IS '图层组ID';
COMMENT ON COLUMN public.nge_layer_group_rel.layer_id IS '图层ID';
COMMENT ON COLUMN public.nge_layer_group_rel.index IS '图层次序';

CREATE TABLE public.nge_user_info
(
   id bigserial NOT NULL,
   username character varying(64) NOT NULL,
   password character varying(64) NOT NULL,
   status SMALLINT ,
   create_time timestamp without time zone DEFAULT current_timestamp, -- 创建时间
   update_time timestamp without time zone DEFAULT current_timestamp, -- 更新时间
   last_update_time timestamp without time zone, -- 最近一次修改密码时间
  CONSTRAINT pk_nge_user_info PRIMARY KEY (id)
)WITH(
 OIDS = FALSE
)
TABLESPACE pg_default;
ALTER TABLE public.nge_user_info
    OWNER to postgres;

-- CREATE SEQUENCE public.nge_user_info_id_seq
--   INCREMENT 1
--   MINVALUE 1
--   MAXVALUE 9223372036854775807
--   START 1
--   CACHE 1;
-- ALTER TABLE public.nge_user_info_id_seq
--   OWNER TO postgres;



-- hyt ------------------------------------------------------------<

INSERT INTO nge_workspace(code, name, declare, usability, create_time) VALUES('default','默认','默认工作空间',0,now());
INSERT INTO nge_classify(code, name, declare, usability, create_time) VALUES('default','默认','默认标签',0,now());
INSERT INTO nge_classify_cont(classify_id, res_type, res_id) VALUES(1, 'workspace',1);
INSERT INTO nge_user_info(username,password,status,create_time,update_time) VALUES('admin','c03be2ee6c42e603ddd631c513dd9a0041e0b5bd5c7cb6bc9ca110592f213a4a',1,now(),now());



-- 数据源字段转义配置表 序列
-- Sequence: public.nge_vector_def_rel_id_seq

-- DROP SEQUENCE public.nge_vector_def_rel_id_seq;

CREATE SEQUENCE public.nge_vector_def_rel_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.nge_vector_def_rel_id_seq
  OWNER TO postgres;


-- 数据源字段转义配置表
-- Table: public.nge_vector_def_rel

-- DROP TABLE public.nge_vector_def_rel;

CREATE TABLE public.nge_vector_def_rel
(
  id bigint NOT NULL DEFAULT nextval('nge_vector_def_rel_id_seq'::regclass), -- ID
  vec_id bigint, -- 数据源ID
  col character varying(64), -- 数据库表字段名
  def character varying(64), -- 转义字段名
  alias character varying(64), -- 转义字段别名
  suffix character varying(64), -- 后缀(单位)
  idx smallint -- 顺序
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.nge_vector_def_rel
  OWNER TO postgres;
COMMENT ON TABLE public.nge_vector_def_rel
  IS '数据源转义关系配置表';
COMMENT ON COLUMN public.nge_vector_def_rel.id IS 'ID';
COMMENT ON COLUMN public.nge_vector_def_rel.vec_id IS '数据源ID';
COMMENT ON COLUMN public.nge_vector_def_rel.col IS '数据库表字段名';
COMMENT ON COLUMN public.nge_vector_def_rel.def IS '转义字段名';
COMMENT ON COLUMN public.nge_vector_def_rel.alias IS '转义字段别名
';
COMMENT ON COLUMN public.nge_vector_def_rel.suffix IS '后缀(单位)';
COMMENT ON COLUMN public.nge_vector_def_rel.idx IS '顺序';

