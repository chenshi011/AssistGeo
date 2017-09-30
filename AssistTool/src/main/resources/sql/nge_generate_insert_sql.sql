-- function 生成 postgis insert sql
-- modify by gzx
-- 参数
-- p_schema schema名称
-- p_table 表名
-- p_geom 几何字段名 ，没有几何字段填写''
-- p_srid 坐标系SRID，空坐标系srid为0
CREATE OR REPLACE FUNCTION nge_generate_insert_sql(IN p_schema text, IN p_table text, IN p_geom text, IN p_srid text)
  RETURNS SETOF text AS
$BODY$
DECLARE
	-- 拼接查询
    selquery text;
    -- 拼接值
    valquery text;
    -- 拼接列
    colvalue text;
    -- 拼接几何
    geomsql text;
    -- 表结构的列名记录
    colrec record;
BEGIN
	-- 要执行的sql
	-- SELECT 'INSERT INTO products (id,name,description) VALUES (' ||
    --            quote_nullable(ID) || ',' || quote_nullable(name) || ',' ||
    --            quote_nullable(description) || ');' FROM products;");

    selquery := 'SELECT ' || '''INSERT INTO ' || p_schema || '.' || p_table || ' (';

    valquery := ' VALUES ( ';

    -- 循环表结构字段，并拼接sql，事先排除几何字段 类型为USER-DEFINED
    FOR colrec IN SELECT table_schema, table_name, column_name, data_type
                  FROM information_schema.columns 
                  WHERE table_name = p_table and table_schema = p_schema and data_type != 'USER-DEFINED'
                  ORDER BY ordinal_position 
    LOOP
      selquery := selquery || ' "' || colrec.column_name || '",';

      colvalue := ' '' || quote_nullable("' || colrec.column_name || '") || '',';

      valquery := valquery || colvalue;

    END LOOP;

    -- 判断是否有几何字段并进行最终拼接
    if char_length(trim(p_geom)) > 0 then
    --空间表
      selquery := substring(selquery,1,length(selquery)-1) || ', "' || p_geom || '")';

      geomsql := ', ST_GeomFromtext('' || quote_nullable(ST_AsText("' || p_geom || '")) || '',' || p_srid || ') ';

      valquery := substring(valquery,1,length(valquery)-1) || geomsql || ')';
    else
    -- 非空间表
      selquery := substring(selquery,1,length(selquery)-1) || ')';
      valquery := substring(valquery,1,length(valquery)-1) || ')';
    end if;
    
    selquery := selquery || valquery || ';'' as sqltxt from ' || p_schema || '.' || p_table;
    
    return query EXECUTE selquery;

END
$BODY$
  LANGUAGE plpgsql VOLATILE;;