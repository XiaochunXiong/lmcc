/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccb.boss.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author limei
 */
public class FileDownloader {
    private static Logger logger = Logger.getLogger(FileDownloader.class);
    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }
    private String newFileName; 
    private String filename;
    private String urlString;

    public FileDownloader() {
    }
    
    public static void main(String[] args){
        String downloadInfo = "http://www.lmccinternational.com/download/SynergyRehab/boss.xml|conf/boss.xml";
                //+ ";http://www.lmccinternational.com/download/common/dbUpdate.sql|sql/dbUpdate.sql;http://www.lmccinternational.com/download/common/DTPage.fxml|gui/DTPage.fxml;http://www.lmccinternational.com/download/common/bossLogin.jar|bin/app/bossLogin.jar;http://www.lmccinternational.com/download/common/package.cfg|bin/app/package.cfg;http://www.lmccinternational.com/download/common/test.jar;2015-05-18 11:07:12";
        FileDownloader fileDownloader = new FileDownloader(downloadInfo);
        fileDownloader.saveBinaryFromUrl();
    }

    public FileDownloader(String downloadInfo) {
        String[] di = downloadInfo.split("\\|");
        for (int i=0; i<di.length; i++) {
            System.out.println("di[" + i + "]" + di[i]);
        }
        urlString = di[0];
        filename = System.getenv("BOSS_HOME") + File.separator + di[1].replaceAll("/", Matcher.quoteReplacement(File.separator));
        System.out.println("urlString: " + urlString + "; filename: " + filename);
    }

    private boolean renameFile(){
        File file = new File(filename);
        if (!file.exists()) {
            return true;
        }
        newFileName = filename + "_" + getDateString(new Date(), "yyyyMMddHHmmss");
    
        File file2 = new File(newFileName);
        if(file2.exists()) {
            return false;
        }

        // Rename file (or directory)
        boolean success = file.renameTo(file2);
        return success;
    }
    public void saveBinaryFromUrl() {
        boolean success = renameFile();
        if (!success) {
            System.out.println("Failed to rename file " + filename + " as " + newFileName);
            return;
        }
        
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (MalformedURLException ex) {
            logger.error("Failed in downfile due to MalformedURLException" 
                    + ex.getMessage() + " from link: " + urlString);
        } catch (IOException ex) {            
            logger.error("Failed in downfile due to IOException" 
                    + ex.getMessage() + " from link: " + urlString);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    logger.error("Failed in close URL handler due to IOException" 
                    + ex.getMessage());
                }
            }
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException ex) {
                    logger.error("Failed in close file handler due to IOException" 
                    + ex.getMessage());
                }
            }
        }
    }
    
    public static String getDateString(Date date, String outputFormat) {
        if (date == null) {
            date = new Date();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(outputFormat);
        return dateFormat.format(date);
    }

}
