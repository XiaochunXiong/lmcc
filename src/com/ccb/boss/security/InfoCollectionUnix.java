package com.ccb.boss.security;


import com.ccb.boss.security.CommandExecution;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class InfoCollectionUnix extends InfoCollection{

    private static Logger logger = Logger.getLogger(InfoCollectionUnix.class);

    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }

    

    //private static InfoCollectionUnix infoCollection = null;
    
    /*
    CPU ID: echo Xxc720427|sudo dmidecode -t 4|grep ID
        ID: 01 0F 63 00 FF FB 8B 17
    
    Baseboard 
    echo xxc720427|sudo dmidecode -t 2 | grep 'Serial Number:'        Serial Number: 131219861201949

    
    */

    InfoCollectionUnix() {       

    }
    
    
    

    public String getBiosSN() {
        /*
        String cmdString = "wmic bios get serialnumber";
        return (commandExecution.execute("SerialNumber", cmdString));
        */
        return "getBiosSN";
    }

    public String getDiskSN() {
        /*
        String cmdString = "wmic DISKDRIVE GET SerialNumber";
        return (commandExecution.execute("SerialNumber", cmdString));
        */
        return "getDiskSN";
    }

    public String getUUID() {
        /*
        String cmdString = "wmic csproduct get uuid";
        return (commandExecution.execute("UUID", cmdString));
        */
        return "unixUUID";
    }

    public String getCPUSN() {
        /*
        String cmdString = "cat /proc/cpuinfo|grep microcode|tail -1|cut -d':' -f2";
        String info = commandExecution.execute("microcode", cmdString).trim();
        */
        return "getCPUSN";
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        logger.debug(111);
        InfoCollectionUnix ic = new InfoCollectionUnix();
        
        logger.debug(222);
        logger.debug("getBiosSN: "+ic.getBiosSN());
        logger.debug("getDiskSN: "+ic.getDiskSN());
        logger.debug("getCPUSN:  "+ic.getCPUSN());
        logger.debug("getUUID:   "+ic.getUUID());    
        logger.debug(333);
                
        logger.debug("v4Address: " + ic.getIPv4Address());        
        logger.debug("v6Address: " + ic.getIPv6Address());      
        logger.debug("MaAddress: " + ic.getMacAddress());
        logger.debug(444);
    }

}
