package gsconomy;
import gsconomy.ConfigurationManager;
import gsconomy.GSConomy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Twelvee
 */
public class DataBase {

    public String user = ConfigurationManager.getInstance().getConfig().getNode("login").getString();
    public String password = ConfigurationManager.getInstance().getConfig().getNode("password").getString();
    public Logger logger = GSConomy.instance.getLogger();
    
    public String connectionString = "localhost:3306";

    private Connection connection;

    private final String DB_DRIVER = "com.mysql.jdbc.Driver";
    private int dbConnectionTimeOut = 10000;

    public DataBase() {

    }

    public DataBase(String dbUser, String dbPassword, String connectionHost, int dbConnectionTimeOut) {
        user = dbUser;
        password = dbPassword;
        connectionString = connectionHost;
        this.dbConnectionTimeOut = dbConnectionTimeOut;
    }

    public String[] getArrayFromDB(String table, String[] args) {
        return getArrayFromDB(table, args, "");
    }

    /**
     *  класс возвращает нам массив из наших столбцов, который мы указали 
     * @param table name of table
     * @param args columns
     * @param where
     * @return
     */
    public String[] getArrayFromDB(String table, String[] args, String where) {
        String[] result = new String[args.length];
        String query = "SELECT ";
        for (String arg : args)
            query += arg + ", ";
        query = query.substring(0, query.length() - 2) + " FROM " + table;
        if (!where.equals(""))
            query = query + " WHERE " + where;
        query = query + ";";
        ResultSet resultSet = getResultSet(query);
        try {
            if (resultSet.next()) {
                for (int i = 0; i < args.length; i++)
                    result[i] = resultSet.getString(args[i]);
            } else
                for (int i = 0; i < args.length; i++)
                    result[i] = "null";
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    // возвращает все данные  в виде ArrayList<Map<String,Object>>
    public ArrayList getEntities(String table){
        String baseQuery = "SELECT * FROM " + table;
        ArrayList entities = new ArrayList<>();
        try {
            entities = (ArrayList)
                    loadObjectFromResultSet(getResultSet(baseQuery));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  entities;
    }

    public float getUserBalance(String table, int userid) throws SQLException{
        float id;
        Statement st = DriverManager.getConnection("jdbc:mysql://localhost:3306/gameside", user, password).createStatement();
        String sql = ("SELECT `ms` FROM `"+table+"` WHERE `user_id`='"+userid+"'");
        ResultSet rs = st.executeQuery(sql);
        if(rs.next()) { 
         id = rs.getFloat("ms"); 
         return id;
        }else{
            return -1;
        }
    }
    
    public int getItemID(String table, String itemid) throws SQLException{
        int id;
        Statement st = DriverManager.getConnection("jdbc:mysql://localhost:3306/gameside", user, password).createStatement();
        String sql = ("SELECT `id` FROM `"+table+"` WHERE `itemid`='"+itemid+"'");
        ResultSet rs = st.executeQuery(sql);
        if(rs.next()) { 
         id = rs.getInt("id"); 
         return id;
        }else{
            return -1;
        }
    }
    
    public String isExist(String table, String field, String value){
        String baseQuery = "SELECT `id` FROM " + table + " WHERE `"+field+"`='"+value+"'";
        ArrayList entities = new ArrayList<>();
        try {
            entities = (ArrayList)
                    loadObjectFromResultSet(getResultSet(baseQuery));
            if(entities==null || entities.isEmpty()){
                return -1+"";
            }else{
                return  entities.get(0).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1+"";
        }
        //return  entities.get(0).toString();
    }
    
    public ArrayList isInUserInv(String userId, String item_id){
        String baseQuery = "SELECT `id` FROM `gs_user_inventory` WHERE `item_id`='"+item_id+"' AND `user_id`='"+userId+"'";
        ArrayList entities = new ArrayList<>();
        try {
            entities = (ArrayList)
                    loadObjectFromResultSet(getResultSet(baseQuery));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entities;
    }
    
    public int getUserId(String table, String username) throws SQLException{
        int id;
        Statement st = DriverManager.getConnection("jdbc:mysql://localhost:3306/gameside", user, password).createStatement();
        String sql = ("SELECT `user_id` FROM `"+table+"` WHERE `username`='"+username+"'");
        ResultSet rs = st.executeQuery(sql);
        if(rs.next()) { 
         id = rs.getInt("user_id"); 
         return id;
        }else{
            return -1;
        }
    }
    
    public Object loadObjectFromResultSet(ResultSet resultSet) throws Exception
    {
        ArrayList<Object> objectArrayList = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        while(resultSet.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i < columnCount -1 ; i++) {
                String columnName = metaData.getColumnName(i);
                Object objectValue = resultSet.getObject(i);
                map.put(columnName, objectValue);
            }
            objectArrayList.add(map); // после того, как всю строку считали, сохраняем в массив и заново 
        }
        return objectArrayList;
    }


    public void connect() {
        try {
            // Create MySQL Connection
            Class.forName(DB_DRIVER);
            // https://helpx.adobe.com/coldfusion/kb/mysql-error-java-sql-sqlexception.html 0000-00-00 date exception
            setConnection(DriverManager.getConnection("jdbc:mysql://localhost:3306/gameside", user, password));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet getResultSet(String sqlQuery) {
        ResultSet resultSet = null;
        try {
            Statement statement = DriverManager.getConnection("jdbc:mysql://localhost:3306/gameside", user, password).createStatement();
            resultSet = statement.executeQuery(sqlQuery);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return resultSet;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}