package com.hikvision.energy.energis.fxtool.AssitTool.service.impl;

import com.hikvision.energy.energis.fxtool.AssitTool.init.DataStorage;
import com.hikvision.energy.energis.fxtool.AssitTool.service.IExportService;
import org.apache.commons.io.IOUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.CRS;
import org.geotools.sql.SqlUtil;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GOT.hodor on 2017/9/28.
 */

@Service
public class ExportService implements IExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    /**
     * 存放sql语句的HashMap
     */
    private final HashMap<String, String> sqlMap = new HashMap<>();

    @Autowired
    private DataStorage dataStorage;

    /**
     * 连接
     * @param connName
     * @param params
     * @return
     */
    public boolean connect(String connName, Map<String, Object> params) {
        return dataStorage.getDataStoreManager().storeConnection(connName, params);
    }

    /**
     * 断开连接
     * @param connName
     */
    public void dispose(String connName) {
        dataStorage.getDataStoreManager().removeConnection(connName);
    }

    /**
     * 导出SQL文件
     * @param connName
     * @param path
     * @throws IOException
     * @throws SQLException
     */
    public void export(String connName, String gisVer, String path, String efn) throws IOException, SQLException {
        File file = new File(path);

        DataStore dataStore = dataStorage.getDataStoreManager().fetchDataStore(connName);
        if (dataStore == null) {
            throw new IOException();
        }
        String[] typeNames = dataStore.getTypeNames();

        JDBCDataStore jdbcDataStore = (JDBCDataStore)dataStore;
        if (jdbcDataStore == null) {
            throw new IOException();
        }

        Transaction t = new DefaultTransaction("handle");
        t.putProperty("hint", new Integer(7));

        Connection conn = jdbcDataStore.getConnection(t);

        //导出所需函数是否存在
        findFunctionSQl(conn);

        //扩展extension
        String extension = gisExtensionSQL(gisVer);

        //序列
        StringBuilder seq = combineSequenceSQL(conn);

        //表
        StringBuilder table = new StringBuilder();
        for (int i=0; i < typeNames.length; i++) {
            table.append(combineTableScriptSQL(conn, typeNames[i]));
        }



        StringBuffer sb = new StringBuffer();
        sb.append(extension)
                .append(seq)
                .append(table);

        //写文件
        String sqlPath = file.getPath() + "/" + efn;

        log.info("write file to [{}]", sqlPath);

        File file1 = new File(sqlPath);
        if (file1.exists()){
            file1.delete();
        }
        file1.createNewFile();

        OutputStream out = null;
        try{
            out = new FileOutputStream(file1);

            byte[] array = stringBufferToByteArray(sb);
            out.write(array);

            //数据
            StringBuffer data = new StringBuffer();
            for (int i=0; i < typeNames.length; i++) {
                StringBuffer dataBuffer = combineDataSqlScript(jdbcDataStore, conn, typeNames[i]);
                byte[] dataByte = stringBufferToByteArray(dataBuffer);
                out.write(dataByte);
            }

        }catch (IOException e) {
            log.error("write sql file occured some error");
        }finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
        log.info("write sql txt end");
        t.close();

    }

    /**
     * string buffer 转 byte[]
     * @param stringBuffer
     * @return
     * @throws CharacterCodingException
     */
    private byte[] stringBufferToByteArray(StringBuffer stringBuffer) throws CharacterCodingException {
        Charset charset = StandardCharsets.UTF_8;
        CharsetEncoder charsetEncoder = charset.newEncoder();
        CharBuffer charBuffer = CharBuffer.wrap(stringBuffer);
        ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);

        byte[] array;
        int arrayLen = byteBuffer.limit();
        if (arrayLen == byteBuffer.capacity()) {
            array = byteBuffer.array();
        } else {
            // This will place two copies of the byte sequence in memory,
            // until byteBuffer gets garbage-collected (which should happen
            // pretty quickly once the reference to it is null'd).

            array = new byte[arrayLen];
            byteBuffer.get(array);
        }

        byteBuffer = null;
        return array;
    }

    /**
     * 合并生成表SQL
     * @param conn
     * @param tableName
     * @return
     * @throws IOException
     * @throws SQLException
     */
    private StringBuilder combineTableScriptSQL(Connection conn, String tableName) throws IOException, SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("-- table region \r\n");

        //region table script
        String sql = tableScriptSQL(tableName);
        SqlUtil.PreparedStatementBuilder statementBuilder1 = SqlUtil.prepare(conn, sql);
        PreparedStatement statement1 = statementBuilder1.statement();
        ResultSet resultSet = statement1.executeQuery();

        if (resultSet != null) {
            while (resultSet.next()){
                sb.append(resultSet.getString(1)).append("\n");
            }
            resultSet.close();
            statement1.close();
        }
        //endregion

        //region constraint
        String sql2 = constraintSQL(tableName);
        SqlUtil.PreparedStatementBuilder statementBuilder2 = SqlUtil.prepare(conn, sql2);
        PreparedStatement statement2 = statementBuilder2.statement();
        ResultSet resultSet2 = statement2.executeQuery();

        if (resultSet2 != null) {
            while (resultSet2.next()) {
                String consName = resultSet2.getString(2);
                String constraint = resultSet2.getString(3);
                sb.append("ALTER TABLE ")
                        .append(tableName)
                        .append(" ADD CONSTRAINT ")
                        .append(consName)
                        .append(" ")
                        .append(constraint)
                        .append(";\n");
            }
            resultSet2.close();
            statement2.close();
        }

        //endregion

        //region index
        String sql3 = indexSQL(tableName);
        SqlUtil.PreparedStatementBuilder statementBuilder3 = SqlUtil.prepare(conn, sql3);
        PreparedStatement statement3 = statementBuilder3.statement();
        ResultSet resultSet3 = statement3.executeQuery();

        if (resultSet3 != null) {
            while (resultSet3.next()) {
                sb.append(resultSet3.getString(5))
                        .append(";\n");
            }
        }
        resultSet3.close();
        statement3.close();
        sb.append("\r\n");
        //endregion

        //log.info("\r\n"+sb.toString());

        return sb;
    }

    /**
     * 合并序列SQL
     * @param conn
     * @return
     */
    private StringBuilder combineSequenceSQL(Connection conn) throws SQLException, IOException {
        String sql = sequenceSQL();
        SqlUtil.PreparedStatementBuilder statementBuilder = SqlUtil.prepare(conn, sql);
        PreparedStatement statement = statementBuilder.statement();
        ResultSet resultSet = statement.executeQuery();

        StringBuilder sb = new StringBuilder();
        sb.append("-- sequence region \r\n");
        if (resultSet != null) {
            while (resultSet.next()) {
                String schema = resultSet.getString(1);
                String seq = resultSet.getString(2);
                sb.append("CREATE SEQUENCE ")
                        .append(schema)
                        .append(".")
                        .append(seq)
                        .append("\n INCREMENT 1 \n")
                        .append(" MINVALUE 1 \n")
                        .append(" MAXVALUE 9223372036854775807 \n")
                        .append(" START 1 \n")
                        .append(" CACHE 1; \n")
                        .append(" ALTER TABLE ").append(schema).append(".").append(seq).append(" \n")
                        .append(" OWNER TO postgres;")
                        .append("\r\n");
            }
        }
        resultSet.close();
        statement.close();

        return sb;

    }


    /**
     * 合并数据SQL
     * @param conn
     * @param tableName
     * @return
     */
    private StringBuffer combineDataSqlScript(JDBCDataStore jdbcDataStore, Connection conn, String tableName) throws IOException, SQLException {
        StringBuffer stringBuffer = new StringBuffer();

        SimpleFeatureType schema = jdbcDataStore.getSchema(tableName);
        List<AttributeType> list =  schema.getTypes();
        String sql = dataSQL(tableName, schema.getGeometryDescriptor());

        log.info(sql);

        SqlUtil.PreparedStatementBuilder statementBuilder = SqlUtil.prepare(conn, sql);
        PreparedStatement statement = statementBuilder.statement();
        ResultSet resultSet = statement.executeQuery();
        if (resultSet != null) {
            while (resultSet.next()) {
                stringBuffer.append(resultSet.getString(1))
                        .append(";\r\n");
            }
        }
        resultSet.close();
        statement.close();

        return stringBuffer;
    }

    /**
     * 拼接生成建表SQL
     * @param tableName
     * @return
     */
    private String tableScriptSQL(String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("select nge_generate_create_table_sql('")
                .append(tableName)
                .append("')");
        return sb.toString();
    }

    /**
     * 索引SQL
     * @param tableName
     * @return
     */
    private String indexSQL(String tableName) {
        /**
         * <p>
         *
            SELECT * FROM pg_indexes WHERE tablename = 'camera' and position('UNIQUE INDEX' in indexdef) = 0;
         *
         * </p>
         */

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM pg_indexes WHERE tablename = '")
                .append(tableName)
                .append("' and position('UNIQUE INDEX' in indexdef) = 0;");
        return sb.toString();
    }

    /**
     * 约束SQL
     * @param tableName
     * @return
     */
    private String constraintSQL(String tableName) {
        /**
         * <p>
         *
             select conrelid::regclass AS table_from, conname, pg_get_constraintdef(c.oid)
             from   pg_constraint c
             join   pg_namespace n ON n.oid = c.connamespace
             where  contype in ('f', 'p','c','u') and c.conrelid='camera'::regclass order by contype
         *
         * </p>
         */
        StringBuilder sb = new StringBuilder();
        sb.append("select conrelid::regclass AS table_from, conname, pg_get_constraintdef(c.oid)\n ")
                .append("from   pg_constraint c\n ")
                .append("join   pg_namespace n ON n.oid = c.connamespace\n ")
                .append("where  contype in ('f', 'p','c','u') and c.conrelid='")
                .append(tableName)
                .append("'::regclass order by contype");
        return sb.toString();
    }

    /**
     * 序列SQL
     * @return
     */
    private String sequenceSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("select sequence_schema, sequence_name \n")
                .append("from information_schema.sequences where sequence_schema='public';");
        return sb.toString();
    }

    /**
     * 使用 function 拼接 insert sql
     * @param tableName
     * @param type
     * @return
     */
    private String dataSQL(String tableName, GeometryDescriptor type) {
        StringBuilder sb = new StringBuilder();
        sb.append("select nge_generate_insert_sql(")
                .append("'public', '")
                .append(tableName)
                .append("', '");

        if (type != null) {
            String geom = type.getName().toString();
            String srid = CRS.toSRS(type.getCoordinateReferenceSystem(), true);
            if (StringUtils.isEmpty(srid)) {
                srid = "0";
            }

            sb.append(geom)
                    .append("', '")
                    .append(srid)
                    .append("');");
        }else{
            sb.append("', '0');");
        }

        return sb.toString();
    }

    /**
     * 使用 String 拼接 insert sql
     * @param tableName
     * @param list
     * @return
     */
    private String dataSQL(String tableName, List<AttributeType> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT 'INSERT INTO ")
                .append(tableName)
                .append(" VALUES(");
        /**
        sb.append("SELECT 'INSERT INTO products (id,name,description) VALUES (' ||\n" +
                "quote_nullable(ID) || ',' || quote_nullable(name) || ',' ||\n" +
                "quote_nullable(description) || ');' FROM products;");
                **/

        for (int i=0; i < list.size(); i++) {
            AttributeType attributeType = list.get(i);
            if (attributeType instanceof GeometryType) {
                sb.append(geometrySQL((GeometryType)attributeType));
            }else{
                sb.append(propertySQL(attributeType));
            }
            if (i != list.size()-1) {
                sb.append(",");
            }
        }

        sb.append(");' as sqltxt from ")
                .append(tableName);

        log.info(sb.toString());

        return sb.toString();
    }

    /**
     * 使用 String 拼接 属性字段
     * @param attributeType
     * @return
     */
    private StringBuilder propertySQL(AttributeType attributeType) {
        StringBuilder sb = new StringBuilder();


            sb.append("' || quote_nullable(\"")
                    .append(attributeType.getName().toString())
                    .append("\") || '");

        attributeType.getName().toString();

        return sb;
    }

    /**
     * 使用 String 拼接 几何字段
     * @param geometryType
     * @return
     */
    private StringBuilder geometrySQL(GeometryType geometryType) {
        StringBuilder sb = new StringBuilder();

        String srid = CRS.toSRS(geometryType.getCoordinateReferenceSystem(), true);

        if (srid != null) {
            sb.append(" ST_GeomFromText(' || quote_nullable(ST_AsText(\"")
                    .append(geometryType.getName().toString())
                    .append("\")) || ', ")
                    .append(srid)
                    .append(") ||");
        }else {
            sb.append(" ST_GeomFromText(' || quote_nullable(ST_AsText(\"")
                    .append(geometryType.getName().toString())
                    .append("\")) || ' ")
                    .append(") ||");
        }





        return sb;
    }

    /**
     * 检查数据库函数是否存在
     * @param funcName
     * @return
     */
    private String functionExistSQL(String funcName) {
        StringBuilder sb = new StringBuilder();
        sb.append("select exists(select * from pg_proc where proname = '")
                .append(funcName)
                .append("');");
        return sb.toString();
    }

    /**
     *
     * @param funcName
     * @return
     */
    private String readFunctionSQL(String funcName) throws IOException {

        InputStream in = this.getClass().getResourceAsStream("/sql/" + funcName + ".sql");

        if (in != null) {
            try {
                Charset charset = StandardCharsets.UTF_8;
                CharsetDecoder decoder = charset.newDecoder();

                byte[] bytes = IOUtils.toByteArray(in);
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

                CharBuffer charBuffer = decoder.decode(byteBuffer);

                if (charBuffer != null) {
                    return charBuffer.toString();
                }


            }catch (IOException e){
                log.error("read function sql error");
            }finally {
                in.close();
            }
        }

        return null;
    }

    /**
     *
     * @param conn
     * @throws IOException
     * @throws SQLException
     */
    private void findFunctionSQl(Connection conn) throws IOException, SQLException {
        String[] funcSQLs = {"nge_generate_create_table_sql", "nge_generate_insert_sql"};

        List<String> notExistFunc = new ArrayList<>();

        for (int i=0; i < funcSQLs.length; i++) {
            String funcName = funcSQLs[i];

            String sql = functionExistSQL(funcName);

            SqlUtil.PreparedStatementBuilder statementBuilder = SqlUtil.prepare(conn, sql);

            PreparedStatement statement = statementBuilder.statement();
            ResultSet resultSet = statement.executeQuery();

            if (resultSet != null) {
                Boolean exist = false;
                while (resultSet.next()) {
                     exist = resultSet.getBoolean(1);
                }
                if (!exist) {
                    notExistFunc.add(funcName);
                }
                resultSet.close();
                statement.close();
            }
        }

        if (notExistFunc.size() > 0) {
            notExistFunc.stream()
                    .forEach(funcSQLName -> {
                        putIntoFunctionSQL(conn, funcSQLName);
                    });
        }
    }

    /**
     *
     * @param conn
     * @param funcName
     */
    private void putIntoFunctionSQL(Connection conn, String funcName) {
        if (!sqlMap.containsKey(funcName)) {
            try{
                String sql = readFunctionSQL(funcName);
                if (sql != null) {
                    sqlMap.put(funcName, sql);
                }
            }catch (IOException e) {
                log.error("read func sql [{}] error", funcName, e);
            }

        }

        if (sqlMap.get(funcName) != null) {
            Statement statement = null;
            try{
                try{
                    statement = conn.createStatement();
                    statement.executeUpdate(sqlMap.get(funcName));

                    log.info("function sql [{}] put into db success", funcName);
                }catch (SQLException e) {
                    log.error("function sql [{}] put into db error", funcName, e);
                }finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            }catch (SQLException sqle) {
                log.error("function sql put into db error");
            }



        }
    }

    private String gisExtensionSQL(String version) {
        StringBuilder sb = new StringBuilder();
        sb.append("-- Extension: postgis")
                .append("-- DROP EXTENSION postgis;\r\n")
                .append("CREATE EXTENSION postgis")
                .append(" SCHEMA public")
                .append(" VERSION \"").append(version).append("\";")
                .append("\r\n")
                .append("-- Schema: topology\r\n")
                .append("-- DROP SCHEMA topology;\r\n")
                .append("CREATE SCHEMA topology")
                .append(" AUTHORIZATION postgres;")
                .append("\r\n")
                .append("-- Extension: postgis_topology\r\n")
                .append("-- DROP EXTENSION postgis_topology;\r\n")
                .append("CREATE EXTENSION postgis_topology")
                .append(" SCHEMA topology")
                .append(" VERSION \"").append(version).append("\";\r\n");

        return sb.toString();
    }



    //region SQL Description
    /**
     * <p>
     *
     *
     1.查询表名-----------------------------------------

     SELECT table_name
     FROM information_schema.tables
     WHERE table_type = 'BASE TABLE'
     AND table_schema NOT IN
     ('pg_catalog', 'information_schema');

     2.查询表结构字段-----------------------------------

     SELECT * FROM information_schema.columns
     WHERE table_name = 'camera';

    3.主键
     SELECT a.attname, format_type(a.atttypid, a.atttypmod) AS data_type
     FROM   pg_index i
     JOIN   pg_attribute a ON a.attrelid = i.indrelid
     AND a.attnum = ANY(i.indkey)
     WHERE  i.indrelid = 'camera'::regclass
     AND    i.indisprimary;

     4. 建表
     -- function 表结构创建sql
     -- Function: public.generate_create_table_statement(character varying)

     -- DROP FUNCTION public.generate_create_table_statement(character varying);

     CREATE OR REPLACE FUNCTION public.nge_generate_create_table_sql(p_table_name character varying)
     RETURNS text AS
     $BODY$
     DECLARE
     v_table_ddl   text;
     column_record record;
     BEGIN
     FOR column_record IN
     SELECT
     b.nspname as schema_name,
     b.relname as table_name,
     a.attname as column_name,
     pg_catalog.format_type(a.atttypid, a.atttypmod) as column_type,
     CASE WHEN
     (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128)
     FROM pg_catalog.pg_attrdef d
     WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum AND a.atthasdef) IS NOT NULL
     THEN
     'DEFAULT '|| (SELECT substring(pg_catalog.pg_get_expr(d.adbin, d.adrelid) for 128)
     FROM pg_catalog.pg_attrdef d
     WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum AND a.atthasdef)
     ELSE
     ''
     END as column_default_value,
     CASE WHEN a.attnotnull = true THEN
     'NOT NULL'
     ELSE
     'NULL'
     END as column_not_null,
     a.attnum as attnum,
     e.max_attnum as max_attnum
     FROM
     pg_catalog.pg_attribute a
     INNER JOIN
     (SELECT c.oid,
     n.nspname,
     c.relname
     FROM pg_catalog.pg_class c
     LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
     WHERE c.relname ~ ('^('||p_table_name||')$')
     AND pg_catalog.pg_table_is_visible(c.oid)
     ORDER BY 2, 3) b
     ON a.attrelid = b.oid
     INNER JOIN
     (SELECT
     a.attrelid,
     max(a.attnum) as max_attnum
     FROM pg_catalog.pg_attribute a
     WHERE a.attnum > 0
     AND NOT a.attisdropped
     GROUP BY a.attrelid) e
     ON a.attrelid=e.attrelid
     WHERE a.attnum > 0
     AND NOT a.attisdropped
     ORDER BY a.attnum
     LOOP
     IF column_record.attnum = 1 THEN
     v_table_ddl:='CREATE TABLE '||column_record.schema_name||'.'||
     column_record.table_name||' (';
     ELSE
     v_table_ddl:=v_table_ddl||',';
     END IF;

     IF column_record.attnum <= column_record.max_attnum THEN
     v_table_ddl:=v_table_ddl||chr(10)||
     '    '||column_record.column_name||' '||column_record.column_type||' '||
     column_record.column_default_value||' '||column_record.column_not_null;
     END IF;
     END LOOP;

     v_table_ddl:=v_table_ddl||');';
     RETURN v_table_ddl;
     END;
     $BODY$
     LANGUAGE plpgsql VOLATILE
     COST 100;
     ALTER FUNCTION public.generate_create_table_statement(character varying)
     OWNER TO postgres;

     5.数据
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

     geomsql := ' ST_GeomFromtext('' || quote_nullable(ST_AsText("' || p_geom || '")) || '',' || p_srid || ') ';

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
     LANGUAGE plpgsql VOLATILE;

     * </p>
     */
    //endregion


}
