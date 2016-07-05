/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccb.boss.security;

import com.ccb.boss.utils.ConfigReader;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class AuthConnection {
    /*http://www.lmccinternational.com/php/test.php?name=jerry&pwd=slm720427&
     cpuid=cpuid001&mbid=mbdid001&hddsn=hddsn001&phyadd=A0-1D-48-E5-09-D7&ipvfadd=192.168.12.118&‬
     ipvsadd=fe80::9137:3a95:2e92:41f1
     */

    private static Logger logger = Logger.getLogger(AuthConnection.class);

    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }

    private String name;
    private String pwd;
    private String cpuid;
    private String mbid;
    private String hddsn;
    private String phyadd;
    private String ipvfadd;
    private String ipvsadd;
    //private static InfoCollection infoCollection = null;
    private String authURL = "";
    private final String USER_AGENT = "Mozilla/5.0";

    private Date expiredDate = null;
    private String downloadURL = null;
    String hostName = "";
    String companyName = "";
    String dbVersion ="";    
    String appVersion="";
    public ArrayList<String> updateInfo = new ArrayList<String>();
    
    
    private static ConfigReader xmlReader = new ConfigReader();
    private static String xmlFile = System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "boss.xml";

    public AuthConnection(String dbVersion, String appVersion) {
        this();
        this.dbVersion = dbVersion;
        this.appVersion= appVersion;
    }
    public AuthConnection() {
        InfoCollection infoCollection = null;
        String osName = System.getProperty("os.name");
        if (osName.trim().toLowerCase().startsWith("linux")) {
            infoCollection = new InfoCollectionUnix();
        } else {
            infoCollection = new InfoCollection();
        }
        
        companyName = xmlReader.getSingleTCByxPath(xmlFile,
					"boss/commonData/company", "@name");
        //authURL = "http://www.lmccinternational.com/php/"+companyName + ".php?";
        //authURL = "http://www.lmccinternational.com/php/test.php?";
        authURL = "http://www.ca-cn.org/php/"+companyName + ".php?";
        authURL = "http://www.ca-cn.org/php/test.php?";
        
        logger.debug("authURL=" + authURL);
        if (infoCollection != null) {
            cpuid = infoCollection.getCPUSN();
            mbid = infoCollection.getBiosSN();
            hddsn = infoCollection.getDiskSN();
            phyadd = infoCollection.getUUID();
            ipvfadd = infoCollection.getIPv4Address();            
            hostName = infoCollection.getHostName();
            ipvsadd = infoCollection.getIPv6Address();
        }

    }

    public static void main(String[] args) throws Exception {

        AuthConnection http = new AuthConnection();

        logger.debug("Testing 1 - Send Http GET request");
        http.sendGet();
        /*
         logger.debug("\nTesting 2 - Send Http POST request");
         http.sendPost();
         */

    }
    
    public ArrayList<String> getUpdateInfo(){
        return updateInfo;
    }
    private String currentVersion="";
    public String getCurrentVersion(){
        return currentVersion;
    }

    // HTTP GET request
    public boolean sendGet() throws Exception {
        String url = authURL + "cname=" + companyName + "&hname=" + hostName 
                + "&aver=" + appVersion + "&ver=" + dbVersion 
                + "&cpuid=" + cpuid + "&mbid=" + mbid + "&hddsn=" + hddsn 
                + "&phyadd=" + phyadd + "&ipvfadd=" + ipvfadd + "&ipvsadd=" + ipvsadd;
        logger.debug("url: " + url);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();

        StringBuilder responseText = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responseText.append(inputLine);
        }
        in.close();

        logger.debug(responseText);
        if (responseCode != 200) {
            logger.debug("\nSending 'GET' request to URL : " + url);
            logger.debug("Response Code\n: " + responseCode);
            logger.debug("Response Context\n: " + responseText);
            return true;
        }
        
        String responseString = responseText.toString().trim();
        if (responseString.isEmpty()){
            return false;
        }
        String[] responseList = responseString.split(";");
        if (responseList.length<3){
            return false;
        }
        for (int i=0; i<responseList.length-3; i++) {
            updateInfo.add(responseList[i]);            
        }
        /*
        updateTarget=Integer.parseInt(responseList[responseList.length - 3]);
        for (int i=0; i<updateTarget; i++) {
            int index = i + 4;
            updateInfo.add(responseList[responseList.length - index]);
        }
        */
    //private ArrayList<String> updateInfo = new ArrayList<String>();
        currentVersion = responseList[responseList.length - 3];
        downloadURL    = responseList[responseList.length - 2];
        String dateString = responseList[responseList.length - 1];
        expiredDate = convertStringtoDate(dateString, "yyyy-MM-dd HH:mm:ss");
        boolean isAvaiable = expiredDate.after(new Date());
        logger.debug("currentVersion=" + currentVersion + "; downloadURL=" 
                + downloadURL + "; expiredDate=" + expiredDate + "; isAvaiable=" + isAvaiable);

        return isAvaiable;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public static Date convertStringtoDate(String intputString, String format) {
        logger.debug("intputString" + intputString + "; format" + format);
        Date date = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            date = dateFormat.parse(intputString);
        } catch (ParseException e) {
            logger.error("FormatDateString.getDateFromString() failed to parse "
                    + intputString + " using follow format: "
                    + format);
            e.printStackTrace();
        }
        return date;
    }

    // HTTP POST request
    private void sendPost() throws Exception {

        String url = "https://selfsolve.apple.com/wcResults.do";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        logger.debug("\nSending 'POST' request to URL : " + url);
        logger.debug("Post parameters : " + urlParameters);
        logger.debug("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        logger.debug(response.toString());

    }

}
