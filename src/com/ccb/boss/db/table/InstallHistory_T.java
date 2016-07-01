package com.ccb.boss.db.table;

import com.ccb.boss.db.DBConnection;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class InstallHistory_T {

    private static Logger logger = Logger.getLogger(InstallHistory_T.class);

    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }

    public InstallHistory_T() {
    }

    public String getVersion() {
        String query = "select max(version) as version from installhistory_t";
        String fieldName = "version";
        String version = querySCTable(query, fieldName);
        return version;
    }

    public static void main(String[] args) {
        InstallHistory_T installHistory_T = new InstallHistory_T();
        System.out.println("Old Version: " + installHistory_T.getVersion());
    }

    public String querySCTable(String query, String fieldName) {
        String outputData = "";
        Statement stmt = null;
        ResultSet rs = null;
        Connection connection = DBConnection.getInstance().getConnect();

        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(query);

            ResultSetMetaData rsmd = rs.getMetaData();
            String columnTypeName = rsmd.getColumnTypeName(1);

            while (rs.next()) {
                if ("int4".equalsIgnoreCase(columnTypeName)
                        || "int8".equalsIgnoreCase(columnTypeName)
                        || "serial".equalsIgnoreCase(columnTypeName)
                        || "INTEGER".equalsIgnoreCase(columnTypeName)) {
                    outputData = String.valueOf(rs.getInt(fieldName));
                } else if ("varchar".equalsIgnoreCase(columnTypeName)
                        || "text".equalsIgnoreCase(columnTypeName)
                        || "bpchar".equalsIgnoreCase(columnTypeName)) {
                    // inputData = rs.getString(fieldNames[i]);
                    outputData = rs.getString(fieldName);
                } else {
                    logger.debug("\tFailed to fetch value for column "
                            + fieldName
                            + " because column type name "
                            + columnTypeName + " did not supported");
                }
                logger.debug("serialNumber=" + outputData + "; query: " + query);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            String errorMessage = "Failed to execute: " + query;
            logger.error(errorMessage + "; " + e.getMessage());
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTrace : stackTraceElements) {
                logger.error(stackTrace.getClassName() + "  " + stackTrace.getMethodName() + " " + stackTrace.getLineNumber());
            }
        } finally {
            stmt = null;
            rs = null;
        }
        DBConnection.getInstance().returnConnect(connection);
        return outputData;
    }

    public boolean isNewVersion(String newVersion) {

        String fieldName = "version";
        String query = "SELECT max(" + fieldName + ") as " + fieldName
                + " FROM InstallHistory_T";
        String version = querySCTable(query, fieldName);

        boolean isNewVersion = newVersion.compareTo(version) > 0 ? true : false;
        return isNewVersion;
    }

}
