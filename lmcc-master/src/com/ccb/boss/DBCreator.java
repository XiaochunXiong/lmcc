/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ccb.boss;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author jerry
 */
public class DBCreator {

    static Connection conn;

    public static void main(String[] args) throws Exception {
        System.out.println("Working Directory = " +System.getProperty("user.dir"));
        String driver = "org.apache.derby.jdbc.EmbeddedDriver";
        String dbName = "//mnt/softdata/data/gddb";
        dbName = "51wood";
        String user="boss";
        String password="Slm720427";
        //String connectionURL = "jdbc:derby://UbuntuServer:26282/"
        String connectionURL = "jdbc:derby:" + dbName + ";create=true" + ";user=" + user + ";password="  + password;
        String createString = "CREATE TABLE ADDRESSBOOKTbl (NAME VARCHAR (32) NOT NULL, ADDRESS VARCHAR(50) NOT NULL)";
        Class.forName(driver);
		//test//
        conn = DriverManager.getConnection(connectionURL);

        Statement stmt = conn.createStatement();        
        stmt.executeUpdate("CREATE SCHEMA AUTHORIZATION boss");
        stmt.executeUpdate("SET CURRENT SCHEMA = BOSS");
        stmt.executeUpdate(createString);
        stmt.closeOnCompletion();
/*		//test//
        PreparedStatement psInsert = conn
                .prepareStatement("insert into ADDRESSBOOKTbl values (?,?)");

        psInsert.setString(1, args[0]);
        psInsert.setString(2, args[1]);

        psInsert.executeUpdate();

        Statement stmt2 = conn.createStatement();
        ResultSet rs = stmt2.executeQuery("select * from ADDRESSBOOKTbl");
        System.out.println("Addressed present in your Address Book\n\n");
        int num = 0;

        while (rs.next()) {
            System.out.println(++num + ": Name: " + rs.getString(1) + "\n Address"
                    + rs.getString(2));
        }
        rs.close();
        */
    }
		//Try//
		//I don't know how to do//
}
