package com.ccb.boss.antcrack;

import com.ccb.boss.db.DAOException;
import static com.ccb.boss.db.DBConnection.logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class DeserializeObject {

    private static String CFG_HOME  ="";
    private static String inputFile = "";    
    private static Logger logger = Logger.getLogger(DeserializeObject.class);
    static {
        CFG_HOME = System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator;
        inputFile = CFG_HOME + "sboss.dat";
        DOMConfigurator.configure(CFG_HOME + File.separator + "log4j.xml");
    }

    public DeserializeObject() {
    }

    public HashMap deserialize() {
        logger.debug("inputFile: " + inputFile);
        return (HashMap) runDeserialize(inputFile);
    }

    public HashMap deserialize(String inputFile) {
        return (HashMap) runDeserialize(inputFile);
    }

    public Object runDeserialize(String inputFile) {
        Object hashMap = null;
        try {
            FileInputStream fileIn = new FileInputStream(inputFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            hashMap = in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            String msg = "Failed in deserialize file " + inputFile+ " due to " + i.getMessage();
            logger.error(msg);
            return null;
        }
        return hashMap;
    }

}
