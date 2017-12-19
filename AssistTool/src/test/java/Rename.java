import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.feature.type.GeometryDescriptorImpl;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.sql.SqlUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GOT.hodor on 2017/12/19.
 */
public class Rename {

    public static void main(String[] args) {
        DataStore dataStore = connect(connParams());
        rename(dataStore);
    }

    private static Map<String, Object> connParams() {
        Map<String, Object> params = new HashMap<>();
        params.put( "dbtype", "postgis");
        params.put( "host", "127.0.0.1");
        params.put( "port", 6432);
        params.put( "schema", "public");
        params.put( "database", "rocket-demo");
        params.put( "user", "postgres");
        params.put( "passwd", "postgres");
        return params;
    }

    private static DataStore connect(Map<String, Object> params) {
        try{
            DataStore dataStore = DataStoreFinder.getDataStore(params);
            return dataStore;
        }catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static void rename(DataStore dataStore) {
        try{
            JDBCDataStore jdbcDataStore = (JDBCDataStore)dataStore;

            Transaction t = new DefaultTransaction("handle");

            t.putProperty("hint", new Integer(7));
            Connection connection = jdbcDataStore.getConnection(t);

            StringBuilder sb = new StringBuilder();
            sb.append("ALTER TABLE ")
                    .append(" ? ")
                    .append(" RENAME TO ")
                    .append("camera_de")
                    .append(";");


            SqlUtil.PreparedStatementBuilder builder = SqlUtil.prepare(connection, sb.toString());
            PreparedStatement ps = builder.statement();

            StringBuilder sb2 = new StringBuilder()
                    .append("ALTER TABLE ")
                    .append("building ")
                    .append(" RENAME TO ")
                    .append("building_de")
                    .append(";");

            ps.addBatch(sb2.toString());

            ps.execute();
            t.commit();

            ps.close();
            t.close();

        }catch (IOException e) {
            e.printStackTrace();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
