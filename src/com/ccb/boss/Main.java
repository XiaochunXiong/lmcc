/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package com.ccb.boss;

import com.ccb.boss.db.DAOException;
import com.ccb.boss.db.DBConnection;
import com.ccb.boss.db.table.InstallHistory_T;
import com.ccb.boss.gui.FXOptionPane;
import com.ccb.boss.security.AuthConnection;
import com.ccb.boss.security.LoadEngine;
import com.ccb.boss.update.UpdateBossSimple;
import com.ccb.boss.utils.AppVersionReader;
import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import javafx.application.Application;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javafx.stage.Stage;
import xeus.jcl.JarClassLoader;
import xeus.jcl.JclObjectFactory;

/**
 * Main Application. This class handles navigation and user session.
 */
public class Main extends Application {
    public static Main application = null;
    private static Logger logger = Logger.getLogger(Main.class);
    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }
    public static String BOSS_HOME = System.getenv("BOSS_HOME");

    public static Stage stage;
    private final double MINIMUM_WINDOW_WIDTH = 1024.0;
    private final double MINIMUM_WINDOW_HEIGHT = 768.0;

    private String downloadURL = null;
    private static final String MAINOBJECT = "com.ccb.boss.Boss";
    private static final String MAINMETHOD = "launchFXProject";

    private String msg = null;
    private AppVersionReader appVersionReader = new AppVersionReader();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

    public void loadProject() {
        if (downloadURL==null) {
            return;
        }
        
        JclObjectFactory factory = JclObjectFactory.getInstance();
        Object instance = null;
        LoadEngine loadEngine = new LoadEngine();

        JarClassLoader jarLoader;
        try {
            jarLoader = loadEngine.loadjar(downloadURL);
        } catch (IOException ex) {
            logger.error("Failed in loadEngine.loadjar("
                    + downloadURL + ") due to " + ex.getMessage());
            return;
        }

        try {
            instance = factory.create(jarLoader, MAINOBJECT);
        } catch (IOException ex) {
            logger.error("Failed in factory.create(jarLoader, " + MAINOBJECT
                    + ") due to IOException: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("Failed in factory.create(jarLoader, " + MAINOBJECT
                    + ") due to IllegalArgumentException: " + ex.getMessage());
        } catch (SecurityException ex) {
            logger.error("Failed in factory.create(jarLoader, " + MAINOBJECT
                    + ") due to SecurityException: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            logger.error("Failed in factory.create(jarLoader, " + MAINOBJECT
                    + ") due to ClassNotFoundException: " + ex.getMessage());
        } catch (InstantiationException ex) {
            logger.error("Failed in factory.create(jarLoader, " + MAINOBJECT
                    + ") due to InstantiationException: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            logger.error("Failed in factory.create(jarLoader, " + MAINOBJECT
                    + ") due to IllegalAccessException: " + ex.getMessage());
        } catch (InvocationTargetException ex) {
            logger.error("Failed in factory.create(jarLoader, " + MAINOBJECT
                    + ") due to InvocationTargetException: " + ex.getMessage());
        } catch (NoSuchMethodException ex) {
            logger.error("Failed in factory.create(jarLoader, " + MAINOBJECT
                    + ") due to NoSuchMethodException: " + ex.getMessage());
        }

        if (instance != null) {
            executeMethod(instance, MAINMETHOD);
        }
    }
    
    

    private boolean authorize() {
        String version = new InstallHistory_T().getVersion();
        boolean isAvaiable = false;
        String appVersion = appVersionReader.getAppVersion();
        AuthConnection authConnection = new AuthConnection(version, appVersion);
        try {
            isAvaiable = authConnection.sendGet();
        } catch (Exception ex) {
            msg = "BOSS can not startup without Internet!!!";
            FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
            logger.error(Main.class.getName() + "" + ex.getMessage());
        }
        if (isAvaiable) {
            
            ArrayList<String> updateInfo = authConnection.getUpdateInfo();
            if (updateInfo!=null && !updateInfo.isEmpty()) {
                UpdateBossSimple updateBoss = new UpdateBossSimple();
                updateBoss.update(updateInfo);
            }
            Date expiredDate = authConnection.getExpiredDate();
            downloadURL = authConnection.getDownloadURL();
            Date currentDate = new Date();
            double days = (expiredDate.getTime() - currentDate.getTime()) / (24000.0 * 3600);
            days = Math.ceil(days);
            if (days < 31) {
                msg = "BOSS's license will be expired after " + days
                        + " days, please contact with Limei Sui @ 416 800 9004!";
                FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
                logger.info(msg);
            }
        }
        String newVersion = authConnection.getCurrentVersion();
        appVersionReader.updateAppVersion(newVersion);
        return isAvaiable;
    }

    private boolean connectDB() {
        boolean isAvaiable = false;
        try {
            DBConnection.getInstance().initConnect();
            isAvaiable = true;
        } catch (DAOException ex) {
            msg = "Please check DB server because of failure in connecting DB "
                    + "for below reason: " + ex.getMessage();
            FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
        }

        return isAvaiable;
    }

    private void executeMethod(Object interfaceType, String methodName) {
        Method reflectMethod = null;
        try {
            reflectMethod = interfaceType.getClass().getDeclaredMethod(methodName, Stage.class);
        } catch (NoSuchMethodException noSuchMethodException) {
            logger.error(methodName + "'s fieldName failed beause of NoSuchMethodException: "
                    + methodName + " with inputdate "
                    + noSuchMethodException.getMessage());
        } catch (SecurityException securityException) {
            logger.error(methodName + "'s fieldName failed beause of SecurityException: "
                    + methodName + " with inputdate "
                    + securityException.getMessage());
        }

        if (reflectMethod == null) {
            logger.error("Failed to get Method for " + interfaceType.getClass().getName()
                    + "." + methodName + "()");
            return;
        }

        try {
            reflectMethod.invoke(interfaceType, this.stage);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            ex.printStackTrace();
            msg = "Failed to invoke " + methodName + " due to " + ex.getMessage();
            logger.error(msg);
            FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        boolean isAvaiable = connectDB();

        if (isAvaiable) {
            isAvaiable = authorize();
        }

        if (isAvaiable) {
            loadProject();
        } else {
            msg = "Failed to BOSS without license! Please contact Limei Sui @ 416 800 9004";
            logger.error(msg);
            FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
        }
    }

}
