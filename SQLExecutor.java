package com.persistentsystems.DisplayBuilder;

import com.persistentsystems.GUI.PS_Display_Builder;

import javax.swing.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLExecutor {
    public static final Logger logger = Logger.getLogger(SQLExecutor.class.getName());

    Connection conn;
    private String userName;
    private String password;
    private String url;


    private final String APPROVED_DISPLAY_BUILDS_TABLE_NAME = "ApprovedDisplayBuilds";
    private final String DISPLAY_BUILDS_TABLE_NAME = "ProductionDisplayBuilds";
    private final String DISPLAY_BUILD_LOG_TABLE_NAME = "DisplayBuildsLog";
    private final String DISPLAY_PRINT_LOG_TABLE_NAME = "DisplayLabelPrintLog";

    private boolean engineering;
    private static PS_Display_Builder guiForm;

    public SQLExecutor(boolean eng, PS_Display_Builder guiForm){
        this.guiForm = guiForm;
        engineering = eng;

        userName = "X";
        password = "X"; 


        url = "X";

        if (initializeDB())
            logger.log(Level.INFO, "Connected to SQL Server Database" );
            guiForm.txtMsg.setText("Connected to SQL Server Database");

    }

    public boolean initializeDB(){
        if(engineering) {return true;}
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, userName, password);

        }  catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            String errMsg = "Could not connect to SQL Server Database";
            logger.log(Level.SEVERE, errMsg);
            guiForm.txtMsg.setText(errMsg);
            System.exit(0);
            return false;
        }
        return true;
    }

    public void updateCombos(JComboBox cmbPCBA, JComboBox cmbScreen, JComboBox cmbHousing, JComboBox cmbCamera ){
        ResultSet rstPCBA, rstScreen, rstHousing, rstCamera;
        try {
            String getLinePCBA = "SELECT DISTINCT PCBA_Vendor, PCBA_PLM, PCBA_Rev FROM " + APPROVED_DISPLAY_BUILDS_TABLE_NAME;
            String getLineScreen ="SELECT DISTINCT Screen_Vendor, Screen_PLM, Screen_Rev FROM " + APPROVED_DISPLAY_BUILDS_TABLE_NAME;
            String getlineHousing ="SELECT DISTINCT Housing_Vendor, Housing_PLM, Housing_Rev FROM " + APPROVED_DISPLAY_BUILDS_TABLE_NAME;
            String getLineCamera ="SELECT DISTINCT Camera_Vendor, Camera_PLM, Camera_Rev FROM " + APPROVED_DISPLAY_BUILDS_TABLE_NAME;

            rstPCBA = selectSQL(getLinePCBA);
            rstScreen = selectSQL(getLineScreen);
            rstHousing = selectSQL(getlineHousing);
            rstCamera = selectSQL(getLineCamera);

            while (rstPCBA.next()) {
                cmbPCBA.addItem(DataParser.createFullSerial(
                        rstPCBA.getString("PCBA_Vendor"),
                        rstPCBA.getString("PCBA_PLM"),
                        rstPCBA.getString("PCBA_Rev")));
            }
            while (rstScreen.next()) {
                cmbScreen.addItem(DataParser.createFullSerial(
                        rstScreen.getString("Screen_Vendor"),
                        rstScreen.getString("Screen_PLM"),
                        rstScreen.getString("Screen_Rev")));
            }
            while (rstHousing.next()) {
                cmbHousing.addItem(DataParser.createFullSerial(
                        rstHousing.getString("Housing_Vendor"),
                        rstHousing.getString("Housing_PLM"),
                        rstHousing.getString("Housing_Rev")));
            }
            while (rstCamera.next()) {
                cmbCamera.addItem(DataParser.createFullSerial(
                        rstCamera.getString("Camera_Vendor"),
                        rstCamera.getString("Camera_PLM"),
                        rstCamera.getString("Camera_Rev")));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get data from SQL DB updating combo boxes for PS Label Components");
            //TODO Handle this exception better
            e.printStackTrace();
        }
    }

    public ResultSet selectSQL(String sqlString){
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(sqlString);
            return resultSet;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get data from SQL DB");
            e.printStackTrace();
            return null;
        }
    }

    public int updateSQL(String sqlString){
        int rows = 0;
        Statement statement;
        try {
            statement = conn.createStatement();
            rows = statement.executeUpdate(sqlString);
            return rows;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get data from SQL DB");
            e.printStackTrace();
            return 0;
        }
    }

    public String getAPPROVED_DISPLAY_BUILDS_TABLE_NAME(){
        return APPROVED_DISPLAY_BUILDS_TABLE_NAME;
    }
    public String getDISPLAY_BUILDS_TABLE_NAME() { return DISPLAY_BUILDS_TABLE_NAME; }
    public String getDISPLAY_PRINT_LOG_TABLE_NAME() { return DISPLAY_PRINT_LOG_TABLE_NAME; }
    public String getDISPLAY_BUILD_LOG_TABLE_NAME() { return DISPLAY_BUILD_LOG_TABLE_NAME; }
}

