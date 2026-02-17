package mayton.bigdata;

import java.sql.*;

public class DbComponent implements AutoCloseable {

    private Connection connection = null;
    private PreparedStatement preparedStatement1 = null;
    private PreparedStatement preparedStatement2 = null;

    public DbComponent() throws SQLException {
        String asnHome = System.getProperty("user.dir") + "/db/ass.db";

        String jdbcurl = "jdbc:sqlite:" + asnHome;

        connection = DriverManager.getConnection(jdbcurl);
        preparedStatement1 = connection.prepareStatement("select 1 from registry where key = ?");
        preparedStatement2 = connection.prepareStatement("insert into registry(key, size) values (?,?)");
    }

    void initDb() throws SQLException {
        Statement st = connection.createStatement();
        st.execute("create table registry(key string primary key, size bigint)");
        st.close();
    }

    boolean existsKey(String key) throws SQLException {
        preparedStatement1.setString(1,  key);
        ResultSet rs = preparedStatement1.executeQuery();
        boolean res = rs.next();
        rs.close();
        return res;
    }

    boolean upsert(String key) throws SQLException {
        preparedStatement2.setString(1,  key);
        preparedStatement2.execute();
        return false;
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
