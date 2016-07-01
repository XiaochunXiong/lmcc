package com.ccb.boss.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.ccb.boss.antcrack.DeserializeObject;

public class DBConnection {

    public static Logger logger = Logger.getLogger(DBConnection.class);
    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator 
                + "conf" + File.separator + "log4j.xml");
    }

    private static DBConnection cm = null;
    private String driverName=null;
    private String connectURL=null;
    private String dbname=null;
    private String userName=null;
    private String password=null;
    
    private static final List connectList = new ArrayList(5);
    private static HashMap<Integer, String[]> recordHM=null;

    private DBConnection() {
        if (recordHM==null) {
            String runHome = System.getenv("BOSS_HOME");
            String inputFile = runHome + File.separator + "conf" 
                    + File.separator + "aboss.dat";
            recordHM = new DeserializeObject().deserialize(inputFile);
        }
        String[] configArray = recordHM.get(recordHM.size() - 1);
        driverName = configArray[0];
        connectURL = configArray[1];
        dbname = configArray[2];
        userName = configArray[3];
        password = configArray[4];
    }
    
    public static DBConnection getInstance() {
        if (cm == null) {
            cm = new DBConnection();
        }
        return cm;
    }
    
    
    public Connection getConnect() {
        int connectionNumber = connectList.size();
        if (connectionNumber == 0) {
            initConnect();
        }
        connectionNumber = connectList.size();
        Connection c = (Connection) connectList.get(connectionNumber - 1);
        connectList.remove(connectionNumber - 1);
        return c;
    }

    public String getConnectionURL() {
        return connectURL + ", " + userName + ", " + password;
    }

    public String getConnectionDriver() {
        return driverName;
    }

    public void initConnect() throws DAOException{
        Connection connection = null;
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException ex) {
            String msg = "Couldn't find the driver: " + driverName+ " due to " + ex.getMessage();
            logger.error(msg);
            throw new DAOException(msg);
        }
        
        DriverManager.setLoginTimeout(100);
        try {
            connection = DriverManager.getConnection(connectURL, userName, password);
            connection.setAutoCommit(true);
        } catch (SQLException ex) {            
            logger.error("Couldn't connect database with below setting: ");
            logger.error("\tconnectURL: " + connectURL);
            logger.error("\tuserName:   " + userName);
            logger.error("The error message is " + ex.getMessage());
            String msg = "Couldn't connect database due to " + ex.getMessage();
            throw new DAOException(msg);
        }
        connectList.add(connection);
    }

    public void returnConnect(Connection connection) {
        connectList.add(connection);
    }

    public void disConnect() {
        for (int i = 0; i < connectList.size(); i++) {
            Connection c = (Connection) connectList.get(i);
            try {
                c.close();
            } catch (SQLException sqlException) {
                c = null;
                logger.info("Couldn't disconnect: print out a stack trace and exit.");
                sqlException.printStackTrace();
                System.exit(1);
            }
        }
        connectList.clear();
    }

}
