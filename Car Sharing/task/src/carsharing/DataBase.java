package carsharing;

import org.h2.expression.aggregate.JavaAggregate;

import java.sql.*;

public class DataBase {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:./src/carsharing/db/";

    //  Database credentials
    static final String USER = "";
    static final String PASS = "";

    Connection conn = null;




    public DataBase(String name) {

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            if (name != null) {
                name = DB_URL + name;
            } else {
                name = DB_URL + "anything";
            }
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(name, USER, PASS);
            conn.setAutoCommit(true);

        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } //end try
    };

   

    public DataBase() {

        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL + "anything",USER,PASS);
            conn.setAutoCommit(true);


        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } //end try
    } ;

    public void closeConnection() {
        try {
            conn.close();
            } catch(SQLException se) {
                //Handle errors for JDBC
                se.printStackTrace();
            } catch(Exception e) {
                //Handle errors for Class.forName
                e.printStackTrace();
            } finally {
                //finally block used to close resources
             try {
                if(conn!=null) conn.close();
                } catch(SQLException se){
                 se.printStackTrace();
                } //end finally try
         } //end try

    }

    public void updateQuery(String sql) {

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();

        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally, block used to close resources
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            } // nothing we can do, end finally try

            } //end try

    };

    public int runSql(String sql) {
        Statement stmt = null;
        int rs = 0;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeUpdate(sql);
            stmt.close();
            return rs;

        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally, block used to close resources
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            } // nothing we can do, end finally try

        } //end try
return rs;
    };



    public void showSchema() {

        //This does not work?


        String sql, res;

        sql = "SHOW SCHEMAS";
        try {
        Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
        res= rs.getString(0);
            System.out.println(res);
        }}
            catch (SQLException se) {
                se.printStackTrace();
            }

    };

    public String[][] query(String sql) {
        try {
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = stmt.executeQuery(sql);
            String[][] strings = null;

            if (rs == null) {
                return null;
            } else {
                ResultSetMetaData rsmd = rs.getMetaData();
                int columns = rsmd.getColumnCount();
                int rows;
                if (rs.last()) {
                    rows = rs.getRow();
                    rs.beforeFirst();
                    strings = new String[rows][columns];
                    for (int row = 0; row < rows; row++) {
                        if (!rs.next()) break;
                        for (int col = 0; col < columns; col++) {
                            strings[row][col] = rs.getString(col + 1);
                        }
                    }
                } else {
                    rows = 0;
                }

                return strings;
                }
            }
        catch (SQLException se) {
            se.printStackTrace();
            return null;
        }

    };
}
