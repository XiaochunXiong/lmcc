/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccb.boss.security;

import com.ccb.boss.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import xeus.jcl.JarClassLoader;

/**
 *
 * @author limei
 */
public class LoadEngine {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(LoadEngine.class);
    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator
                + "conf" + File.separator + "log4j.xml");
    }
    
    public List listClasseNames(String jarName) {
        ArrayList classes = new ArrayList();

        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(
                    jarName));
            JarEntry jarEntry;

            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                String getName = jarEntry.getName();
                if (getName.endsWith(".class")) {
                    String cName = getName.replaceAll("/", "\\.");
                    logger.debug("Found " + getName + "; cName" + cName);
                    classes.add(cName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    public LoadEngine() {
    }

    public ClassLoader getClassLoaderForExtraModule(String filepath) throws IOException {

        List<URL> urls = new ArrayList<URL>(5);
        //foreach( filepath: external file *.JAR) with each external file *.JAR, do as follows
        File jar = new File(filepath);
        JarFile jf = new JarFile(jar);
        urls.add(jar.toURI().toURL());
        Manifest mf = jf.getManifest(); // If the jar has a class-path in it's manifest add it's entries
        if (mf
                != null) {
            String cp
                    = mf.getMainAttributes().getValue("class-path");
            if (cp
                    != null) {
                for (String cpe : cp.split("\\s+")) {
                    File lib
                            = new File(jar.getParentFile(), cpe);
                    urls.add(lib.toURI().toURL());
                }
            }
        }
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if (urls.size() > 0) {
            cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), ClassLoader.getSystemClassLoader());
        }
        return cl;
    }

    private static String jarFileName = System.getenv("BOSS_HOME") + File.separator + "download" + File.separator + "test.jar";

    // HTTP GET request

    public boolean download(String url) throws IOException {
        boolean isSucceed = false;
        //String url = "http://www.lmccinternational.com/download/log4j-1.2.15.jjj";
        logger.debug("url: " + url);
        URL urlOject;
        try {
            urlOject = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(LoadEngine.class.getName()).log(Level.SEVERE, null, ex);
            return isSucceed;
        }

        HttpURLConnection con;
        try {
            con = (HttpURLConnection) urlOject.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(LoadEngine.class.getName()).log(Level.SEVERE, null, ex);
            return isSucceed;
        }

        FileOutputStream out;
        try {
            out = new FileOutputStream(jarFileName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LoadEngine.class.getName()).log(Level.SEVERE, null, ex);
            return isSucceed;
        }
        byte[] downloadByte = new byte[1024];
        int len;
        while ((len = con.getInputStream().read(downloadByte)) > 0) {
            out.write(downloadByte, 0, len);
            logger.debug("readIndex=" + len);
        }
        out.close();
        con.disconnect();

        return true;
    }

    public JarClassLoader loadjar(String url) throws IOException {
        boolean isSucceed = false;
        //String url = "http://www.lmccinternational.com/download/log4j-1.2.15.jjj";
        logger.debug("url: " + url);
        URL urlOject;
        try {
            urlOject = new URL(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(LoadEngine.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        JarClassLoader jcl = new JarClassLoader();
        jcl.add(urlOject);
        /*
         //Loading classes from different sources
         jcl.add("myjar.jar");
         jcl.add(new URL("http://myserver.com/myjar.jar"));
         jcl.add(new FileInputStream("myotherjar.jar"));
         jcl.add("myclassfolder/");

         //Recursively load all jar files in the folder/sub-folder(s)
         jcl.add("myjarlib/");

         JclObjectFactory factory = JclObjectFactory.getInstance();

         //Create object of loaded class
         Object obj = factory.create(jcl, "mypack.MyClass");
         */
        return jcl;
    }
}
