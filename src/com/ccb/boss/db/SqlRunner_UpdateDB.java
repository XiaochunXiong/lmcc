package com.ccb.boss.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.ccb.boss.db.table.InstallHistory_T;
import com.ccb.boss.update.FileDownloader;
//import com.ccb.boss.utils.XMLReader;

public class SqlRunner_UpdateDB {

    private static Logger logger = Logger.getLogger(SqlRunner_UpdateDB.class);

    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }
    static String runHome = System.getenv("BOSS_HOME");
    static String xmlFile = runHome + File.separator + "conf" + File.separator + "boss.xml";

    public static final String DELIMITER_LINE_REGEX = "(?i)DELIMITER.+";
    public static final String DELIMITER_LINE_SPLIT_REGEX = "(?i)DELIMITER";
    public static final String DEFAULT_DELIMITER = ";";
    private boolean autoCommit, stopOnError;
    private Connection connection;
    private String delimiter = SqlRunner_UpdateDB.DEFAULT_DELIMITER;
    private PrintWriter out, err;

    private String version = "";

    public SqlRunner_UpdateDB() {
    }

    private ArrayList<String> fetchSQL() {
        ArrayList<String> vt = new ArrayList<String>();

        String dbScript = runHome + File.separator + "sql" + File.separator + "dbUpdate.sql";
        if (!new File(dbScript).exists()) {
            return null;
        }

        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(dbScript));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        StringBuffer command = null;
        if (reader == null) {
            return vt;
        }
        try {
            final LineNumberReader lineReader = new LineNumberReader(reader);

            String line = null;
            while ((line = lineReader.readLine()) != null) {
                if (command == null) {
                    command = new StringBuffer();
                }
                String trimmedLine = line.trim();

                if (trimmedLine.startsWith("--") || trimmedLine.startsWith("//") || trimmedLine.startsWith("#")) {
                    if (trimmedLine.startsWith("#Version")) {
                        int index = trimmedLine.indexOf(" ");
                        if (index > 0) {
                            version = trimmedLine.substring(index + 1, trimmedLine.length());
                        }

                    }

                    // Line is a comment
                    logger.debug(trimmedLine);

                } else if (trimmedLine.endsWith(this.delimiter)) {

                    // Line is end of statement
                    // Support new delimiter
                    final Pattern pattern = Pattern.compile(SqlRunner_UpdateDB.DELIMITER_LINE_REGEX);
                    final Matcher matcher = pattern.matcher(trimmedLine);
                    if (matcher.matches()) {
                        delimiter = trimmedLine.split(SqlRunner_UpdateDB.DELIMITER_LINE_SPLIT_REGEX)[1].trim();

                        // New delimiter is processed, continue on next
                        // statement
                        line = lineReader.readLine();
                        if (line == null) {
                            break;
                        }
                        trimmedLine = line.trim();
                    }

                    // Append
                    command.append(line.substring(0, line.lastIndexOf(this.delimiter)));
                    command.append(" ");
                    vt.add(command.toString());
                    command = new StringBuffer();

                } else {
                    int index = line.indexOf("--");
                    if (index > 0) {
                        line = line.substring(0, index);
                    }

                    // Line is middle of a statement
                    // Support new delimiter
                    final Pattern pattern = Pattern.compile(SqlRunner_UpdateDB.DELIMITER_LINE_REGEX);
                    final Matcher matcher = pattern.matcher(trimmedLine);
                    if (matcher.matches()) {
                        delimiter = trimmedLine.split(SqlRunner_UpdateDB.DELIMITER_LINE_SPLIT_REGEX)[1].trim();
                        line = lineReader.readLine();
                        if (line == null) {
                            break;
                        }
                        trimmedLine = line.trim();
                    }

                    command.append(line);
                    command.append(" ");
                }
            }
        } catch (final IOException e) {
            e.fillInStackTrace();
        }

        return vt;
    }

    public boolean runScript() {
        ArrayList<String> vt = fetchSQL();
        Connection conn = DBConnection.getInstance().getConnect();
        boolean isUpdated = new InstallHistory_T().isNewVersion(version);
        logger.debug("version: " + version + "; isUpdated=" + isUpdated);
        if (!isUpdated) {
            return true;
        }

        if (vt == null || vt.isEmpty()) {
            return true;
        }
        String activedate = FileDownloader.getDateString(null, "yyyy-MM-dd HH:mm:ss");
        String updateVerSQL = "INSERT INTO installhistory_t (version, activedate) "
                + "VALUES ('" + version + "', '" + activedate + "')";
        
        Statement stmt;
        try {
            stmt = conn.createStatement();
        } catch (final SQLException e) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTrace : stackTraceElements) {
                logger.error(stackTrace.getClassName() + "  " + stackTrace.getMethodName() + " " + stackTrace.getLineNumber());
            }
            return false;
        }
        for (int ii = 0; ii < vt.size(); ii++) {
            String command = vt.get(ii).trim();
            if (command == null || "".equals(command) || "null".equalsIgnoreCase(command)) {
                continue;
            }
            
            logger.debug(command);
            try {                
                boolean executeResult = stmt.execute(command);
                logger.debug("executeResult: " + executeResult + "; command=" + command);
                if (ii == vt.size() - 1) {
                    logger.debug(updateVerSQL);
                    executeResult = stmt.execute(updateVerSQL);                    
                    logger.debug("executeResult: " + executeResult + "; updateVerSQL=" + updateVerSQL);
                }
            } catch (final SQLException e) {
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                for (StackTraceElement stackTrace : stackTraceElements) {
                    logger.error(stackTrace.getClassName() + "  " + stackTrace.getMethodName() + " " + stackTrace.getLineNumber());
                }
            }
        }
        try {
            conn.commit();
            if (stmt != null) {
                stmt.close();
            }
            DBConnection.getInstance().returnConnect(conn);
            conn.close();
        } catch (final SQLException e) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTrace : stackTraceElements) {
                logger.error(stackTrace.getClassName() + "  " + stackTrace.getMethodName() + " " + stackTrace.getLineNumber());
            }
        }
        return true;

    }

}
