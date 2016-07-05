/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccb.boss.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author jerry
 */
public class CommandExecution {
    private static String hostName = null;
    private static org.apache.log4j.Logger logger 
            = org.apache.log4j.Logger.getLogger(CommandExecution.class);

    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator 
                + "conf" + File.separator + "log4j.xml");
        if (hostName==null) {
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                RuntimeMXBean rmx = ManagementFactory.getRuntimeMXBean();
                String hostInfo = rmx.getName();
                int index = hostInfo.lastIndexOf("@");
                if (index>0) {
                    hostName = hostInfo.substring(index+1, hostInfo.length());
                } else {
                    hostName = hostInfo;
                }
            }
        }
    }
    
    File file = null;
    FileWriter fileWriter = null;
    Process process = null;
    BufferedReader bufferedReader;
    
    
    public String execute(String target, String wmiccommand) {
        target = target.trim();
        wmiccommand = wmiccommand.trim();
            String result = "";
            try {
                process = Runtime.getRuntime().exec(wmiccommand);
                bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }

            result = result.trim();
            if (result.isEmpty()) {
                return hostName + "-" + target.toLowerCase().replace(" ", "_");
            }
            int spaceIndex = result.lastIndexOf(" ");
            if (spaceIndex>=0) {
                result = result.substring(spaceIndex).trim();
            }             
            return result;
        }
}
