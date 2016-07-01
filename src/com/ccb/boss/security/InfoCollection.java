package com.ccb.boss.security;

import static com.ccb.boss.Main.stage;
import com.ccb.boss.gui.FXOptionPane;
import com.ccb.boss.security.CommandExecution;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class InfoCollection {

    private static Logger logger = Logger.getLogger(InfoCollection.class);

    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }

    
    String macAddress;
    String ipv4Address;
    String ipv6Address="";
    String hostName   ="";
    CommandExecution commandExecution;

    //private static InfoCollection infoCollection = null;
    
    

    public InfoCollection() {
        commandExecution = new CommandExecution();
        String msg = "BOSS can not startup without Internet!!!";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            ipv4Address = ip.getHostAddress();
            hostName    = ip.getHostName();
            logger.debug("Current IP address : " + ipv4Address + "; hostName=" + hostName);

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network==null) {
                FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
                System.exit(0);
            }
            /*
            logger.debug("getDisplayName: " + network.getDisplayName());
            logger.debug("getHardwareAddress: " + network.getHardwareAddress());
            logger.debug("getInetAddresses: " + network.getInetAddresses());
            logger.debug("getInterfaceAddresses: " + network.getInterfaceAddresses());
            logger.debug("getSubInterfaces: " + network.getSubInterfaces());
            logger.debug("getParent: " + network.getParent());
            logger.debug("getName: " + network.getName());
            */
            
            byte[] mac = network.getHardwareAddress();
            if (mac==null) {
                FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
                System.exit(0);
            }
            //logger.debug("Current MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            macAddress = sb.toString();
            //logger.debug(macAddress);
            
            //Inet6Address ipAddress6 = (Inet6Address)Inet6Address..getLocalHost();
            //ipv6Address = ipAddress6.getHostAddress();
            //logger.debug("Current IP address : " + ipv6Address);

        } catch (UnknownHostException e) {
            msg = msg + " dut to " + e.getMessage();
            FXOptionPane.showMessageDialog(stage, msg, "Notification Window");

        } catch (SocketException e) {            
            msg = msg + " dut to " + e.getMessage();
            FXOptionPane.showMessageDialog(stage, msg, "Notification Window");

        }

    }
    
    
/*
    public static InfoCollection getInstance() {
        if (infoCollection == null) {
            infoCollection = new InfoCollection();
        }
        return infoCollection;
    }*/
    
    public String getIPv6Address(){
        return ipv6Address;
    }
    
    public String getHostName(){
        return hostName;
    }
    
    public String getIPv4Address(){
        return ipv4Address;
    }
    
    public String getMacAddress(){
        return macAddress;
    }


    public String getBiosSN() {
        String cmdString = "wmic bios get serialnumber";
        return (commandExecution.execute("SerialNumber", cmdString));
    }

    public String getDiskSN() {
        String cmdString = "wmic DISKDRIVE GET SerialNumber";
        return (commandExecution.execute("SerialNumber", cmdString));
    }

    public String getUUID() {
        String cmdString = "wmic csproduct get uuid";
        return (commandExecution.execute("UUID", cmdString));
    }

    public String getCPUSN() {
        String cmdString = "wmic cpu get ProcessorId";
        return (commandExecution.execute("ProcessorId", cmdString));
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        logger.debug(111);
        InfoCollection ic = new InfoCollection();
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
