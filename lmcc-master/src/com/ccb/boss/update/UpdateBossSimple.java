/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccb.boss.update;

import static com.ccb.boss.Main.stage;
import com.ccb.boss.db.SqlRunner_UpdateDB;
import com.ccb.boss.db.table.InstallHistory_T;
import com.ccb.boss.gui.FXOptionPane;
import com.ccb.boss.utils.PkgZipper;
import java.io.File;
import java.util.ArrayList;
import org.apache.log4j.xml.DOMConfigurator;

/**
 *
 * @author limei
 */
public class UpdateBossSimple {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FileDownloader.class);
    static {
        DOMConfigurator.configure(System.getenv("BOSS_HOME") + File.separator + "conf" + File.separator + "log4j.xml");
    }
    
    public boolean update(ArrayList<String> updateInfo){
        boolean isSucceed = false;
        if (updateInfo==null || updateInfo.isEmpty()) {
            return true;
        } else {
            String msg ="BOSS will be updated, then shutdown automactially.....";
            FXOptionPane.showMessageDialog(stage, msg, "Notification Window");
        }
        for (String ui : updateInfo) {
            logger.debug("Download following link: " + ui); 
            if (ui.contains("")) {
                PkgZipper pkgZipper = new PkgZipper();
                //String url = "http://www.ca-cn.org/download/boss.zip";
                pkgZipper.unzip(ui);
            } else {
                FileDownloader fileDownloader = new FileDownloader(ui);
                fileDownloader.saveBinaryFromUrl();
            }            
        }
        String msg ="Succeed in updating BOSS "
                    + "Please shutdown and reboot computer after BOSS "
                    + "shutdown, then launch BOSS again!" ;
        isSucceed = new SqlRunner_UpdateDB().runScript();
        if (!isSucceed) {
            msg ="Failed to update database";
        }
        
        FXOptionPane.showMessageDialog(stage, msg, "Notification Window");;
        System.exit(0);
        return isSucceed;
    }
    
    private String getCurrentVersion(){
        return new InstallHistory_T().getVersion();
        
    }
    
}
