/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccb.boss.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author limei
 */
public class AppVersionReader {

    private static Logger logger = Logger.getLogger(AppVersionReader.class);

    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator
                + "conf" + File.separator + "log4j.xml");
    }
    private String appVersion="";
    private static final String VERION_TAG = "App Version: ";
    private static final String readmeFile = System.getenv("BOSS_HOME")
            + File.separator + "conf" + File.separator + "readMe.txt";

    public String getAppVersion() {
        return appVersion;
    }

    private ArrayList<String> readmeContext = null;

    private void fetchReadmeContext() {
        readmeContext = new ArrayList<>();
        
        File file = new File(readmeFile);
        if (!file.exists()) {
            logger.info("readmeFile did not existed " + readmeFile );
            return;
        }
        
        String line;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(readmeFile));
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                readmeContext.add(line);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            logger.warn("Failed to fetch Version due to FileNotFoundException "
                    + readmeFile + " with exception message " + e.getMessage());
        } catch (IOException e) {
            logger.warn("Failed to fetch Version due to IOException "
                    + readmeFile + " with exception message " + e.getMessage());
        }
    }

    public AppVersionReader() {
        fetchReadmeContext();
        for (int i = 0; i < readmeContext.size(); i++) {
            String line = readmeContext.get(i);
            if (line.startsWith(VERION_TAG)) {
                appVersion = line.replaceAll(VERION_TAG, "").trim();
                break;
            }
        }

    }

    public void updateAppVersion(String newVersion) {
        if (newVersion.equals(appVersion)) {
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS.sss");
        Date date = new Date();
        String dateString = "Update Time: " + dateFormat.format(date);

        StringBuilder sb = new StringBuilder();
        sb.append(VERION_TAG + " " + newVersion + "\n");
        sb.append(dateString + "\n\n");
        
        if (readmeContext==null) {
            fetchReadmeContext();
        }
        for (int i = 0; i < readmeContext.size(); i++) {
            String line = readmeContext.get(i);
            if (line.startsWith(VERION_TAG)) {
                sb.append(line);
            }
        }
        
        try {
            File file = new File(readmeFile);
            Writer output = new BufferedWriter(new FileWriter(file, false));
            output.write(sb.toString());
            output.close();
        } catch (IOException e) {
            logger.warn("Failed to write Version due to IOException "
                    + readmeFile + " with exception message " + e.getMessage());
        }
    }
}
