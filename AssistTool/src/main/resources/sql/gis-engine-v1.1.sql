-- Sequence: public.district_province_3857_id_seq

-- DROP SEQUENCE public.district_province_3857_id_seq;

CREATE SEQUENCE public.district_province_3857_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 34
  CACHE 1;
ALTER TABLE public.district_province_3857_id_seq
  OWNER TO postgres;




-- Table: public.district_province_3857

-- DROP TABLE public.district_province_3857;

CREATE TABLE public.district_province_3857
(
  id integer NOT NULL DEFAULT nextval('district_province_3857_id_seq'::regclass),
  geom geometry(MultiPolygon,3857),
  userid integer,
  province character varying(254),
  pyname character varying(150),
  admincode character varying(254),
  aliasname character varying(254),
  CONSTRAINT district_province_3857_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.district_province_3857
  OWNER TO postgres;





-- Sequence: public.district_city_3857_id_seq

-- DROP SEQUENCE public.district_city_3857_id_seq;

CREATE SEQUENCE public.district_city_3857_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 360
  CACHE 1;
ALTER TABLE public.district_city_3857_id_seq
  OWNER TO postgres;


-- Table: public.district_city_3857

-- DROP TABLE public.district_city_3857;

CREATE TABLE public.district_city_3857
(
  id integer NOT NULL DEFAULT nextval('district_city_3857_id_seq'::regclass),
  geom geometry(MultiPolygon,3857),
  userid integer,
  province character varying(254),
  city character varying(254),
  pyname character varying(254),
  admincode character varying(254),
  abbreviati character varying(254),
  aliasname character varying(254),
  provinceco character varying(50),
  CONSTRAINT district_city_3857_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.district_city_3857
  OWNER TO postgres;



-- Sequence: public.district_county_3857_id_seq

-- DROP SEQUENCE public.district_county_3857_id_seq;

CREATE SEQUENCE public.district_county_3857_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 2903
  CACHE 1;
ALTER TABLE public.district_county_3857_id_seq
  OWNER TO postgres;

-- Table: public.district_county_3857

-- DROP TABLE public.district_county_3857;

CREATE TABLE public.district_county_3857
(
  id integer NOT NULL DEFAULT nextval('district_county_3857_id_seq'::regclass),
  geom geometry(MultiPolygon,3857),
  userid integer,
  province character varying(20),
  city character varying(30),
  county character varying(30),
  admincode character varying(6),
  pyname character varying(254),
  abbreviati character varying(254),
  aliasname character varying(254),
  citycode character varying(6),
  provinceco character varying(6),
  CONSTRAINT district_county_3857_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.district_county_3857
  OWNER TO postgres;



-- Table: public.mypoly

-- DROP TABLE public.mypoly;

CREATE TABLE public.mypoly
(
  id bigint NOT NULL,
  geom geometry(MultiPolygon,3857),
  code character varying(80),
  name character varying(80),
  CONSTRAINT mypoly_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.mypoly
  OWNER TO postgres;


-- Table: public.myline

-- DROP TABLE public.myline;

CREATE TABLE public.myline
(
  id bigint NOT NULL,
  geom geometry(MultiLineString,3857),
  code character varying(80),
  name character varying(80),
  type character varying(80),
  CONSTRAINT myline_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.myline
  OWNER TO postgres;


-- Table: public.mypoint

-- DROP TABLE public.mypoint;

CREATE TABLE public.mypoint
(
  id bigint NOT NULL,
  geom geometry(MultiPoint,3857),
  code character varying(80),
  name character varying(80),
  type character varying(80),
  CONSTRAINT mypoint_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.mypoint
  OWNER TO postgres;
