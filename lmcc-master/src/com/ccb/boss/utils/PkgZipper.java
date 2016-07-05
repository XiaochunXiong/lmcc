/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccb.boss.utils;

import static com.ccb.boss.Main.stage;
import com.ccb.boss.gui.FXOptionPane;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.*;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author limei
 */
public class PkgZipper {
    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    private static Logger logger = Logger.getLogger(PkgZipper.class);
    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator 
                + "conf" + File.separator + "log4j.xml");
    }
    

    public static void main(String argv[]) {
        PkgZipper pkgZipper = new PkgZipper();
        String url = "http://www.ca-cn.org/download/boss.zip";
        pkgZipper.unzip(url);
    }
   

    public void unzip(String url) {
        String destDirectory = System.getenv("BOSS_HOME");
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        InputStream is;
        try {
            is = new URL(url).openStream();
        } catch (MalformedURLException ex) {
            logger.warn("Failed to openStream due to MalformedURLException " + ex.getMessage());
            return;
        } catch (IOException ex) {
            logger.warn("Failed to openStream due to IOException " + ex.getMessage());
            return;
        }
        ZipInputStream zipIn = new ZipInputStream(is);
        ZipEntry entry;
        try {
            entry = zipIn.getNextEntry();
        } catch (IOException ex) {
            logger.warn("Failed to getNextEntry() due to MalformedURLException " + ex.getMessage());
            return;
        }
        boolean isReboot=false;
        // iterates over entries in the zip file
        try {
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
                if(!isReboot && filePath.contains("gblogin.jar")) {
                    isReboot = true;
                }
            }
            is.close();
            zipIn.close();
        } catch (IOException e) {

        }
        if(isReboot) {
            String msg = "Please reboot computer after updating BOSS";
            FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
        }

    }

    /**
     * Extracts a zip entry (file entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
