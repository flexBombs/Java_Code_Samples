package com.persistentsystems.DisplayBuilder;

import com.persistentsystems.GUI.PS_Display_Builder;

import javax.swing.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DisplayBuildValidator {
    public static final Logger logger = Logger.getLogger(DisplayBuildValidator.class.getName());

    private static final String SQL_INSERT_COLUMNS = "Date_Time, User_Name, Computer_Name, Display_PLM, Display_Rev, " +
            "Display_Serial, WR_Part_Number, PCBA_Vendor, PCBA_PLM, PCBA_Rev, " +
            "PCBA_Serial, PCB_PLM, PCB_Rev, Screen_Vendor, Screen_PLM, Screen_Rev, Screen_Serial, " +
            "Housing_Vendor, Housing_PLM, Housing_Rev, Housing_Serial, Camera_Vendor, Camera_PLM, " +
            "Camera_Rev, Camera_Serial, Leak_Test_Pass, Leak_Test_Value, Test_App_Pass, Ready_For_Shipping, " +
            "Customer_ID, Comments";
    private static final String SQL_INSERT_COLUMNS_LOG = "Date_Time, User_Name, Computer_Name, Transaction_Type, " +
            "Display_PLM, Display_Rev, Display_Serial, WR_Part_Number, PCBA_Vendor, PCBA_PLM, PCBA_Rev, " +
            "PCBA_Serial, PCB_PLM, PCB_Rev, Screen_Vendor, Screen_PLM, Screen_Rev, Screen_Serial, " +
            "Housing_Vendor, Housing_PLM, Housing_Rev, Housing_Serial, Camera_Vendor, Camera_PLM, " +
            "Camera_Rev, Camera_Serial, Comments";
    private static final String SQL_INSERT_LABEL_PRINT_LOG = "Date_Time, User_Name, Computer_Name, Label_Type, " +
            "Display_PLM, Display_Rev, Display_Serial, WR_Part_Number, PCBA_Vendor, PCBA_PLM, PCBA_Rev, " +
            "PCBA_Serial, PCB_PLM, PCB_Rev, Screen_Vendor, Screen_PLM, Screen_Rev, Screen_Serial, " +
            "Housing_Vendor, Housing_PLM, Housing_Rev, Housing_Serial, Camera_Vendor, Camera_PLM, " +
            "Camera_Rev, Camera_Serial, Print_WIP_Assembly, Print_WIP_PCBA, Print_WIP_Screen, Print_WIP_Housing, Print_WIP_Camera, Print_Final_Label, " +
            "Print_RTV_Label, Print_Lubricant_Label, Printer";

    private static PS_Display_Builder guiForm;
    private static SQLExecutor sqlExec;

    public DisplayBuildValidator(PS_Display_Builder guiForm) {
        this.guiForm = guiForm;
        sqlExec = new SQLExecutor(false, guiForm);
    }

    public void updateCombos(JComboBox cmbPCBA, JComboBox cmbScreen, JComboBox cmbHousing, JComboBox cmbCamera) {
        sqlExec.updateCombos(cmbPCBA, cmbScreen, cmbHousing, cmbCamera);
    }

    public boolean validateNewBuild(String guiPCBAStr, boolean parsePCBA, boolean pslPCBA,
                                    String guiScreenStr, boolean parseScreen, boolean pslScreen,
                                    String guiHousingStr, boolean parseHousing, boolean pslHousing,
                                    String guiCameraStr, boolean parseCamera, boolean pslCamera) {

        logger.log(Level.INFO, "Starting Display Build Validation");

        DisplayBuild displayBuild = new DisplayBuild();

        if (parseFullStringPartNumbers(displayBuild, guiPCBAStr, parsePCBA, pslPCBA, guiScreenStr, parseScreen,
                pslScreen, guiHousingStr, parseHousing, pslHousing, guiCameraStr, parseCamera, pslCamera)) {
            if (checkIfBuildApproved(displayBuild)) {
                if(setNewBuildSerials(displayBuild, false, pslPCBA, pslScreen,pslHousing, pslCamera)){
                    if (validateBuildUnique(displayBuild,false,true,true,true,true)){
                        if (insertBuild(displayBuild)){
                            guiForm.txtMsg.setText( "Build Validated");
                            if(!insertPrintLog(displayBuild, "New_Build", true,false,
                                    pslPCBA, pslScreen, pslHousing, pslCamera))
                                guiForm.txtMsg.setText( "Could not print label, reprint");
                            return true;
                        }else {
                            guiForm.txtMsg.setText( "Could not insert validated build");

                        }
                    }
                }else{

                }
            } else {
                guiForm.txtMsg.setText( "Build is not approved");
            }
        } else {

        }
        return false;
    }

    public boolean validateReworkBuild(String guiRB_AssemblySerial, String guiRB_PCBAStr, boolean chkRB_PCBA, boolean chkRB_PSL_PCBA,
                                       String guiRB_ScreenStr, boolean chkRB_Screen, boolean chkRB_PSL_Screen,
                                       String guiRB_HousingStr, boolean chkRB_Housing, boolean chkRB_PSL_Housing,
                                       String guiRB_CameraStr, boolean chkRB_Camera, boolean chkRB_PSL_Camera) {

        logger.log(Level.INFO, "Starting Display Build Rework Validation");

        DisplayBuild displayBuild = new DisplayBuild();

        displayBuild = getExistingDisplayBuild(guiRB_AssemblySerial, displayBuild);


        if (parseFullStringPartNumbers(displayBuild, guiRB_PCBAStr, chkRB_PCBA, chkRB_PSL_PCBA, guiRB_ScreenStr, chkRB_Screen,
                chkRB_PSL_Screen, guiRB_HousingStr, chkRB_Housing, chkRB_PSL_Housing, guiRB_CameraStr, chkRB_Camera, chkRB_PSL_Camera)) {
                if (checkIfBuildApproved(displayBuild)) {
                    if(setNewBuildSerials(displayBuild,true,chkRB_PSL_PCBA,chkRB_PSL_Screen,chkRB_PSL_Housing, chkRB_PSL_Camera)){
                        if (validateBuildUnique(displayBuild, true, chkRB_PCBA, chkRB_Screen, chkRB_Housing, chkRB_Camera)){
                            if (updateBuild(displayBuild, chkRB_PCBA, chkRB_Screen, chkRB_Housing, chkRB_Camera)){
                                guiForm.txtMsg.setText("Build Validated");
                                if(!insertPrintLog(displayBuild, "Reworked_Build", chkRB_Housing ? true : false,false,
                                        chkRB_PCBA && chkRB_PSL_PCBA, chkRB_Screen && chkRB_PSL_Screen,
                                        chkRB_Housing && chkRB_PSL_Housing, chkRB_Camera && chkRB_PSL_Camera))
                                    guiForm.txtMsg.setText( "Rework: Could not print label, reprint");
                                return true;
                            }else {
                                guiForm.txtMsg.setText( "Rework: Could not insert validated build");
                                logger.log(Level.SEVERE, "Rework: Could not insert validated build");
                            }
                        }
                    }else{

                    }
                } else {
                    guiForm.txtMsg.setText("Rework: Build is not approved");
                }
            } else {

            }
        return false;
    }

    public boolean parseFullStringPartNumbers(DisplayBuild displayBuild, String guiPCBAStr, boolean parsePCBA, boolean pslPCBA,
                                              String guiScreenStr, boolean parseScreen, boolean pslScreen,
                                              String guiHousingStr, boolean parseHousing, boolean pslHousing,
                                              String guiCameraStr, boolean parseCamera, boolean pslCamera) {

        boolean isPCBAValid = false, isScreenValid = false, isHousingValid = false, isCameraValid = false;

        String userErrMsg = "";

        //Passing info through Object arrays and type casting, format is {isValid?, PCBA, PLM, REV, (PS Label ? null : serial)}
        //For 'non-standard' barcodes serial parsed. The rest of string will be stored in the PLM field

        try {
            if (parsePCBA) {
                Object[] pcbaParseResults = DataParser.parseFullPartNum(guiPCBAStr, pslPCBA);
                Object[] pcbaParseSpecialRSE$ = DataParser.parseSpecialPCBA_RCE$(guiPCBAStr, pslPCBA);
                if ((boolean) pcbaParseResults[0] || (boolean) pcbaParseSpecialRSE$[0]) {
                    isPCBAValid = true;
                    if ((boolean) pcbaParseResults[0]) {
                        displayBuild.setPcbaVendor((String) pcbaParseResults[1]);
                        displayBuild.setPcbaPLM((String) pcbaParseResults[2]);
                        displayBuild.setPcbaRev((String) pcbaParseResults[3]);
                        if (pcbaParseResults[4] != null)
                            displayBuild.setPcbaSerial((Integer) pcbaParseResults[4]);
                    } else if ((boolean) pcbaParseSpecialRSE$[0]) {
                        displayBuild.setPcbaVendor((String) pcbaParseSpecialRSE$[1]);
                        displayBuild.setPcbaPLM((String) pcbaParseSpecialRSE$[2]);
                        displayBuild.setPcbaRev((String) pcbaParseSpecialRSE$[3]);
                        if (pcbaParseSpecialRSE$[4] != null)
                            displayBuild.setPcbaSerial((Integer) pcbaParseSpecialRSE$[4]);
                    }
                } else {
                    userErrMsg += "Could not validate PCBA input string\n";
                }
            }else{
                isPCBAValid = true;
            }

            if (parseScreen) {
                Object[] screenParseResults = DataParser.parseFullPartNum(guiScreenStr, pslScreen);
                Object[] screenParseSpecialTT = DataParser.parseSpecialScreen_TT(guiScreenStr, pslScreen);
                Object[] screenParseSpecialE3 = DataParser.parseSpecialScreen_E3(guiScreenStr, pslScreen);
                if ((boolean) screenParseResults[0] || (boolean) screenParseSpecialE3[0] || (boolean) screenParseSpecialTT[0]) {
                    isScreenValid = true;
                    if ((boolean) screenParseResults[0]) {
                        displayBuild.setScreenVendor((String) screenParseResults[1]);
                        displayBuild.setScreenPLM((String) screenParseResults[2]);
                        displayBuild.setScreenRev((String) screenParseResults[3]);
                        if (screenParseResults[4] != null)
                            displayBuild.setScreenSerial((Integer) screenParseResults[4]);
                    } else if ((boolean) screenParseSpecialE3[0]) {
                        displayBuild.setScreenVendor((String) screenParseSpecialE3[1]);
                        displayBuild.setScreenPLM((String) screenParseSpecialE3[2]);
                        displayBuild.setScreenRev((String) screenParseSpecialE3[3]);
                        if (screenParseSpecialE3[4] != null)
                            displayBuild.setScreenSerial((Integer) screenParseSpecialE3[4]);
                    } else if ((boolean) screenParseSpecialTT[0]) {
                        displayBuild.setScreenVendor((String) screenParseSpecialTT[1]);
                        displayBuild.setScreenPLM((String) screenParseSpecialTT[2]);
                        displayBuild.setScreenRev((String) screenParseSpecialTT[3]);
                        if (screenParseSpecialTT[4] != null)
                            displayBuild.setScreenSerial((Integer) screenParseSpecialTT[4]);
                    }
                } else {
                    userErrMsg += "Could not validate Screen input string\n";
                }
            }else{
                isScreenValid = true;
            }

            if (parseHousing) {
                Object[] housingParseResults = DataParser.parseFullPartNum(guiHousingStr, pslHousing);
                if ((boolean) housingParseResults[0]) {
                    isHousingValid = true;
                    displayBuild.setHousingVendor((String) housingParseResults[1]);
                    displayBuild.setHousingPLM((String) housingParseResults[2]);
                    displayBuild.setHousingRev((String) housingParseResults[3]);
                    if (housingParseResults[4] != null)
                        displayBuild.setHousingSerial((Integer) housingParseResults[4]);
                } else {
                    userErrMsg += "Could not validate Housing input string\n";
                }
            }else{
                isHousingValid = true;
            }

            if (parseCamera) {
                Object[] cameraParseResults = DataParser.parseFullPartNum(guiCameraStr, pslCamera);
                if ((boolean) cameraParseResults[0]) {
                    isCameraValid = true;
                    displayBuild.setCameraVendor((String) cameraParseResults[1]);
                    displayBuild.setCameraPLM((String) cameraParseResults[2]);
                    displayBuild.setCameraRev((String) cameraParseResults[3]);
                    if (cameraParseResults[4] != null)
                        displayBuild.setCameraSerial((Integer) cameraParseResults[4]);
                } else {
                    userErrMsg += "Could not validate Camera input string\n";
                }
            }else{
                isCameraValid = true;
            }

            if (isPCBAValid && isScreenValid && isHousingValid && isCameraValid) {
                return true;
            } else {
                guiForm.txtMsg.setText( "Failed to validate all components: \n" + userErrMsg);
                logger.log(Level.SEVERE, "Failed to validate all components: \n" + userErrMsg);
                return false;
            }
        } catch (Exception e) {
            guiForm.txtMsg.setText( "Error with validating serial string formats");
            logger.log(Level.SEVERE, "Error with parse results object array");
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkIfBuildApproved(DisplayBuild displayBuild) {
        String sqlGetCountHead = "";
        String sqlRecordHead = "";
        String sqlWhereFields = "";
        String sqlGetCount = "";
        String sqlGetRecord = "";

        try {
            sqlGetCountHead = "SELECT COUNT(*) as Count FROM " + sqlExec.getAPPROVED_DISPLAY_BUILDS_TABLE_NAME();
            sqlRecordHead = "SELECT * FROM " + sqlExec.getAPPROVED_DISPLAY_BUILDS_TABLE_NAME();
            sqlWhereFields = " WHERE PCBA_Vendor" + sqlFieldFormatter(displayBuild.getPcbaVendor()) +
                    "AND PCBA_PLM" + sqlFieldFormatter(displayBuild.getPcbaPLM()) +
                    "AND PCBA_Rev" + sqlFieldFormatter(displayBuild.getPcbaRev()) +
                    "AND Screen_Vendor" + sqlFieldFormatter(displayBuild.getScreenVendor()) +
                    "AND Screen_PLM" + sqlFieldFormatter(displayBuild.getScreenPLM()) +
                    "AND Screen_Rev" + sqlFieldFormatter(displayBuild.getScreenRev()) +
                    "AND Housing_Vendor" + sqlFieldFormatter(displayBuild.getHousingVendor()) +
                    "AND Housing_PLM" + sqlFieldFormatter(displayBuild.getHousingPLM()) +
                    "AND Housing_Rev" + sqlFieldFormatter(displayBuild.getHousingRev()) +
                    "AND Camera_Vendor" + sqlFieldFormatter(displayBuild.getCameraVendor()) +
                    "AND Camera_PLM" + sqlFieldFormatter(displayBuild.getCameraPLM()) +
                    "AND Camera_Rev" + sqlFieldFormatter(displayBuild.getCameraRev());

            sqlGetCount = sqlGetCountHead + sqlWhereFields;
            sqlGetRecord = sqlRecordHead + sqlWhereFields;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to build SQL String: " + sqlGetCount + "," + sqlGetRecord);
            e.printStackTrace();
            return false;
        }

        try {
            ResultSet rstCount = sqlExec.selectSQL(sqlGetCount);
            ResultSet rstRecord = sqlExec.selectSQL(sqlGetRecord);
            if(rstCount != null && rstRecord != null) {
                rstCount.next();
                rstRecord.next();
                if (rstCount.getInt("Count") == 1) {
                    displayBuild.setAssemblyPLM(rstRecord.getString("Display_PLM"));
                    displayBuild.setAssemblyRev(rstRecord.getString("Display_Rev"));
                    displayBuild.setWrPartNumber(rstRecord.getString("WR_Part_Number"));
                    displayBuild.setPcbPLM(rstRecord.getString("PCB_PLM"));
                    displayBuild.setPcbRev(rstRecord.getString("PCB_Rev"));

                    return (rstRecord.getBoolean("Active") == true) ? true : false;
                } else {
                    return false;
                }
            }else{
                logger.log(Level.SEVERE, "Error with SQL while checking if build approved");
                return false;
            }
        } catch (java.sql.SQLException e) {
            logger.log(Level.SEVERE, "Failed to get data from RecordSet while checking if build approved");
            return false;
        }
    }

    public boolean setNewBuildSerials(DisplayBuild displayBuild, boolean rework, boolean pslPCBA, boolean pslScreen, boolean pslHousing, boolean pslCamera) {
        String sqlRecordHead = "SELECT TOP 1 * FROM " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME();

        String userErrMsg = "";
        try {

        if (! rework){
            String sqlWhereAssembly = " ORDER BY Display_Serial DESC";
            ResultSet rstAssemblyRecords;
            rstAssemblyRecords = sqlExec.selectSQL(sqlRecordHead + sqlWhereAssembly);
            if(rstAssemblyRecords == null){
                userErrMsg += userErrMsg + "Error with SQL while checking for next Assembly serial on build\n";
                logger.log(Level.SEVERE, "Error with SQL while checking for next Assembly serial on build");
                return false;
            }
            rstAssemblyRecords.next();
            displayBuild.setAssemblySerial(rstAssemblyRecords.getInt("Display_Serial") + 1);
        }

        if(pslPCBA){
            String sqlWherePCBA = " WHERE PCBA_Vendor" + sqlFieldFormatter(displayBuild.getPcbaVendor()) +
                    "AND PCBA_PLM" + sqlFieldFormatter(displayBuild.getPcbaPLM()) +
                    "AND PCBA_Rev" + sqlFieldFormatter(displayBuild.getPcbaRev());
            ResultSet rstPCBARecords;
            if(displayBuild.getPcbaSerial() == 0) {
                rstPCBARecords = sqlExec.selectSQL(sqlRecordHead + sqlWherePCBA + " ORDER BY PCBA_Serial DESC");
                if(rstPCBARecords == null){
                    userErrMsg += userErrMsg + "Error with SQL while checking for next PCBA serial on build\n";
                    logger.log(Level.SEVERE, "Error with SQL while checking for next PCBA serial on build");
                }else {
                    rstPCBARecords.next();
                    displayBuild.setPcbaSerial(rstPCBARecords.getInt("PCBA_Serial") + 1);
                }
            }
        }

        if(pslScreen){
            String sqlWhereScreen = " WHERE Screen_Vendor" + sqlFieldFormatter(displayBuild.getScreenVendor()) +
                    "AND Screen_PLM" + sqlFieldFormatter(displayBuild.getScreenPLM()) +
                    "AND Screen_Rev" + sqlFieldFormatter(displayBuild.getScreenRev());
            ResultSet rstScreenRecords;
            if(displayBuild.getScreenSerial() == 0) {
                rstScreenRecords = sqlExec.selectSQL(sqlRecordHead + sqlWhereScreen + " ORDER BY Screen_Serial DESC");
                if(rstScreenRecords == null){
                    userErrMsg += userErrMsg + "Error with SQL while checking for next Screen serial on build\n";
                    logger.log(Level.SEVERE, "Error with SQL while checking for next Screen serial on build");
                }else {
                    rstScreenRecords.next();
                    displayBuild.setScreenSerial(rstScreenRecords.getInt("Screen_Serial") + 1);
                }
            }
        }

        if(pslHousing){
            String sqlWhereHousing = " WHERE Housing_Vendor" + sqlFieldFormatter(displayBuild.getHousingVendor()) +
                    "AND Housing_PLM" + sqlFieldFormatter(displayBuild.getHousingPLM()) +
                    "AND Housing_Rev" + sqlFieldFormatter(displayBuild.getHousingRev());
            ResultSet rstHousingRecords;
            if(displayBuild.getHousingSerial() == 0) {
                rstHousingRecords = sqlExec.selectSQL(sqlRecordHead + sqlWhereHousing + " ORDER BY Housing_Serial DESC");
                if(rstHousingRecords == null){
                    userErrMsg += userErrMsg + "Error with SQL while checking for next Housing serial on build\n";
                    logger.log(Level.SEVERE, "Error with SQL while checking for next Housing serial on build");
                }else {
                    rstHousingRecords.next();
                    displayBuild.setHousingSerial(rstHousingRecords.getInt("Housing_Serial") + 1);
                }
            }
        }

        if(pslCamera){
            String sqlWhereCamera = " WHERE Camera_Vendor" + sqlFieldFormatter(displayBuild.getCameraVendor()) +
                    "AND Camera_PLM" + sqlFieldFormatter(displayBuild.getCameraPLM()) +
                    "AND Camera_Rev" + sqlFieldFormatter(displayBuild.getCameraRev());
            ResultSet rstCameraRecords;
            if(displayBuild.getCameraSerial() == 0) {
                rstCameraRecords = sqlExec.selectSQL(sqlRecordHead + sqlWhereCamera + " ORDER BY Camera_Serial DESC");
                if(rstCameraRecords == null){
                    userErrMsg += userErrMsg + "Error with SQL while checking for next Housing serial on build\n";
                    logger.log(Level.SEVERE, "Error with SQL while checking for next Camera serial on build");
                }else {
                    rstCameraRecords.next();
                    displayBuild.setCameraSerial(rstCameraRecords.getInt("Camera_Serial") + 1);
                }
            }
        }

        if(displayBuild.getPcbaSerial() != 0 && displayBuild.getScreenSerial() != 0 &&
                displayBuild.getHousingSerial() != 0 && displayBuild.getCameraSerial() != 0){
            return true;
        }else{
            guiForm.txtMsg.setText( "Could not get new serials for some components:\n" + userErrMsg);
            return false;
        }

        }catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to execute SQL string for serial lookup/validation");
            e.printStackTrace();
            return false;
        }
    }

    public boolean validateBuildUnique(DisplayBuild displayBuild, boolean rework,
        boolean validatePCBA, boolean validateScreen, boolean validateHousing, boolean validateCamera ){

        boolean isAssembly;
        boolean isPCBA;
        boolean isScreen;
        boolean isHousing;
        boolean isCamera;

        String errMsg = "";

        String sqlCountHead = "SELECT COUNT(*) AS Count FROM " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME();

        try {
            if(!rework) {
                String sqlWhereAssembly = " WHERE Display_Serial" + sqlFieldFormatter(displayBuild.getAssemblySerial());
                ResultSet rstAssemblyCount;
                isAssembly = false;
                rstAssemblyCount = sqlExec.selectSQL(sqlCountHead + sqlWhereAssembly);
                rstAssemblyCount.next();
                if (rstAssemblyCount == null) {
                    logger.log(Level.SEVERE, "Error with SQL while checking for if assembly in build is valid");
                    return false;
                }
                if (rstAssemblyCount.getInt("Count") == 0){
                    isAssembly = true;
                }else{
                    errMsg = errMsg + "Assembly Serial already exists in build table\n";
                }
            }else{
                isAssembly = true;
            }

            if(validatePCBA) {
                String sqlWherePCBA = " WHERE PCBA_Vendor" + sqlFieldFormatter(displayBuild.getPcbaVendor()) +
                        "AND PCBA_PLM" + sqlFieldFormatter(displayBuild.getPcbaPLM()) +
                        "AND PCBA_Rev" + sqlFieldFormatter(displayBuild.getPcbaRev()) +
                        "AND PCBA_Serial" + sqlFieldFormatter(displayBuild.getPcbaSerial());
                ResultSet rstPCBACount;
                isPCBA = false;
                rstPCBACount = sqlExec.selectSQL(sqlCountHead + sqlWherePCBA);
                rstPCBACount.next();
                if (rstPCBACount == null) {
                    logger.log(Level.SEVERE, "Error with SQL while checking for if PCBA in build is valid");
                    return false;
                }
                if (rstPCBACount.getInt("Count") == 0){
                    isPCBA = true;
                }else{
                    errMsg = errMsg + "PCBA Component already exists in build table\n";
                }
            }else{
                isPCBA = true;
            }

            if(validateScreen) {
                String sqlWhereScreen = " WHERE Screen_Vendor" + sqlFieldFormatter(displayBuild.getScreenVendor()) +
                        "AND Screen_PLM" + sqlFieldFormatter(displayBuild.getScreenPLM()) +
                        "AND Screen_Rev" + sqlFieldFormatter(displayBuild.getScreenRev()) +
                        "AND Screen_Serial" + sqlFieldFormatter(displayBuild.getScreenSerial());
                ResultSet rstScreenCount;
                isScreen = false;
                rstScreenCount = sqlExec.selectSQL(sqlCountHead + sqlWhereScreen);
                rstScreenCount.next();
                if (rstScreenCount == null) {
                    logger.log(Level.SEVERE, "Error with SQL while checking for if Screen in build is valid");
                    return false;
                }
                if (rstScreenCount.getInt("Count") == 0){
                    isScreen = true;
                }else{
                    errMsg = errMsg + "Screen Component already exists in build table\n";
                }
            }else{
                isScreen = true;
            }

            if(validateHousing){
                String sqlWhereHousing = " WHERE Housing_Vendor" + sqlFieldFormatter(displayBuild.getHousingVendor()) +
                        "AND Housing_PLM" + sqlFieldFormatter(displayBuild.getHousingPLM()) +
                        "AND Housing_Rev" + sqlFieldFormatter(displayBuild.getHousingRev()) +
                        "AND Housing_Serial" + sqlFieldFormatter(displayBuild.getHousingSerial());
                ResultSet rstHousingCount;
                isHousing = false;
                rstHousingCount = sqlExec.selectSQL(sqlCountHead + sqlWhereHousing);
                rstHousingCount.next();
                if (rstHousingCount == null) {
                    logger.log(Level.SEVERE, "Error with SQL while checking for if Housing in build is valid");
                    return false;
                }
                if (rstHousingCount.getInt("Count") == 0){
                    isHousing = true;
                }else{
                    errMsg = errMsg + "Housing Component already exists in build table\n";
                }
            }else{
                isHousing = true;
            }

            if(validateCamera){
                String sqlWhereCamera = " WHERE Camera_Vendor" + sqlFieldFormatter(displayBuild.getCameraVendor()) +
                        "AND Camera_PLM" + sqlFieldFormatter(displayBuild.getCameraPLM()) +
                        "AND Camera_Rev" + sqlFieldFormatter(displayBuild.getCameraRev()) +
                        "AND Camera_Serial" + sqlFieldFormatter(displayBuild.getCameraSerial());
                ResultSet rstCameraCount;
                isCamera = false;
                rstCameraCount = sqlExec.selectSQL(sqlCountHead + sqlWhereCamera);
                rstCameraCount.next();
                if (rstCameraCount == null) {
                    logger.log(Level.SEVERE, "Error with SQL while checking for if Camera in build is valid");
                    return false;
                }
                if (rstCameraCount.getInt("Count") == 0){
                    isCamera = true;
                }else{
                    errMsg = errMsg + "Camera Component already exists in build table\n";
                }
            }else {
                isCamera = true;
            }

            if(isAssembly && isPCBA && isScreen && isHousing && isCamera){
                return true;
            }else{
                guiForm.txtMsg.setText( errMsg);
                return false;
            }
        }catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to check if components not already in Build Table");
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertBuild(DisplayBuild displayBuild) {
        String sep = ",";

        Timestamp dateTime = new Timestamp(System.currentTimeMillis());
        String userName = System.getProperty("user.name");
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        }catch (UnknownHostException e){
            logger.log(Level.SEVERE, "Failed to get computer name");
            e.printStackTrace();
            return false;
        }

        String insertSQLValues = sqlFieldFormatterInsert(dateTime) + sep +
                sqlFieldFormatterInsert(userName) + sep +
                sqlFieldFormatterInsert(computerName) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblyPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblyRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblySerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getWrPartNumber()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaPLM())  + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingPLM())  + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraSerial())  + sep +
                0  + sep + 0  + sep + 0  + sep + 0  + sep + null  + sep + null;

        String insertSQLStatement = "INSERT INTO " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME() + " (" +
                SQL_INSERT_COLUMNS + ") VALUES (" + insertSQLValues + ")";

        try{
            if (insertDisplayBuildLog(displayBuild,"New_Build",dateTime,userName,computerName, null)) {
                int insertSQL = sqlExec.updateSQL(insertSQLStatement);
                if (insertSQL == 1) {
                    return true;
                } else {
                    logger.log(Level.SEVERE, "Error with SQL while inserting new build, returned rows: " + insertSQL);
                    return false;
                }
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error inserting validated build");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean insertDisplayBuildLog(DisplayBuild displayBuild, String transaction, Timestamp dateTime, String userName, String computerName, String comments){
        String sep = ",";
        String insertSQLValues = sqlFieldFormatterInsert(dateTime) + sep +
                sqlFieldFormatterInsert(userName) + sep +
                sqlFieldFormatterInsert(computerName) + sep +
                sqlFieldFormatterInsert(transaction) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblyPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblyRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblySerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getWrPartNumber()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaPLM())  + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingPLM())  + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraSerial())  + sep + comments;

        String insertSQLStatement = "INSERT INTO " + sqlExec.getDISPLAY_BUILD_LOG_TABLE_NAME() + " (" +
                SQL_INSERT_COLUMNS_LOG + ") VALUES (" + insertSQLValues + ")";

        try{
            int sqlInsert = sqlExec.updateSQL(insertSQLStatement);
            if (sqlInsert == 1) {
                return true;
            }else{
                logger.log(Level.SEVERE, "Error with SQL while inserting new build in to log, rows returned: " + sqlInsert);
                return false;
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error inserting validated build into log");
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertPrintLog(DisplayBuild displayBuild, String labelType, boolean printWIPAssemblyLabel, boolean  printFinalLabel,
                                  boolean printPCBA, boolean printScreen, boolean printHousing, boolean printCamera){
        Timestamp dateTime = new Timestamp(System.currentTimeMillis());
        String userName = System.getProperty("user.name");
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        }catch (UnknownHostException e){
            logger.log(Level.SEVERE, "Failed to get computer name");
            e.printStackTrace();
            return false;
        }

        String sep = ",";
        String insertSQLValues = sqlFieldFormatterInsert(dateTime) + sep +
                sqlFieldFormatterInsert(userName) + sep +
                sqlFieldFormatterInsert(computerName) + sep +
                sqlFieldFormatterInsert(labelType) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblyPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblyRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getAssemblySerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getWrPartNumber()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaPLM())  + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbaSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getPcbRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getScreenSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingPLM())  + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getHousingSerial()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraVendor()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraPLM()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraRev()) + sep +
                sqlFieldFormatterInsert(displayBuild.getCameraSerial())  + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(printWIPAssemblyLabel)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(printPCBA ? true : false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(printScreen ? true : false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(printHousing ? true : false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(printCamera ? true : false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(printFinalLabel)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(false)) + sep +
                //TODO update for printer selection
                sqlFieldFormatterInsert("qc2_4x1");

        String insertSQLStatement = "INSERT INTO " + sqlExec.getDISPLAY_PRINT_LOG_TABLE_NAME() + " (" +
                SQL_INSERT_LABEL_PRINT_LOG + ") VALUES (" + insertSQLValues + ")";

        try{
            int insertSQL = sqlExec.updateSQL(insertSQLStatement);
            if (insertSQL == 1) {
                return true;
            }else{
                logger.log(Level.SEVERE, "Error with SQL while inserting in print log, rows returned: " + insertSQL);
                return false;
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error inserting into print log");
            e.printStackTrace();
            return false;
        }
    }

    public int sqlBoolFormatter(boolean bool){
        return bool ? 1 : 0;
    }

    public String sqlFieldFormatter(Object obj){
        if (obj == null){
            return " IS NULL ";
        }else if (obj instanceof String ) {
            return " = '" + obj.toString() + "' ";
        }else{
            return " = " + obj.toString() + " ";
        }
    }

    public String sqlFieldFormatterInsert(Object obj){
        if (obj == null){
            return " NULL ";
        }else if (obj instanceof String || obj instanceof Timestamp) {
            return " '" + obj.toString() + "' ";
        }else{
            return " " + obj.toString() + " ";
        }
    }

    public int getDisplayByComponent(String compSerial){
        String vendor = "";
        String PLM = "";
        String rev = "";
        int serial = 0;

        String sql = "SELECT * FROM " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME() + " WHERE ";

        try{
            Object[] pcbaParseResults = DataParser.parseFullPartNum(compSerial, false);
            Object[] pcbaParseSpecialRSE$ = DataParser.parseSpecialPCBA_RCE$(compSerial, false);

            Object[] screenParseResults = DataParser.parseFullPartNum(compSerial, false);
            Object[] screenParseSpecialTT = DataParser.parseSpecialScreen_TT(compSerial, false);
            Object[] screenParseSpecialE3 = DataParser.parseSpecialScreen_E3(compSerial, false);

            Object[] housingParseResults = DataParser.parseFullPartNum(compSerial, false);

            Object[] cameraParseResults = DataParser.parseFullPartNum(compSerial, false);

            if((boolean)pcbaParseResults[0] == true && (boolean)screenParseResults[0] == true &&
                    (boolean)housingParseResults[0] == true && (boolean)cameraParseResults[0] == true){
                vendor = (String)pcbaParseResults[1];
                PLM = (String)pcbaParseResults[2];
                rev = (String)pcbaParseResults[3];
                serial = (Integer)pcbaParseResults[4];
            }else if((boolean)pcbaParseSpecialRSE$[0] == true){
                vendor = (String)pcbaParseSpecialRSE$[1];
                PLM = (String)pcbaParseSpecialRSE$[2];
                rev = (String)pcbaParseSpecialRSE$[3];
                serial = (Integer)pcbaParseSpecialRSE$[4];
            }else if((boolean)screenParseSpecialTT[0] == true){
                vendor = (String)screenParseSpecialTT[1];
                PLM = (String)screenParseSpecialTT[2];
                rev = (String)screenParseSpecialTT[3];
                serial = (Integer)screenParseSpecialTT[4];
            }else if((boolean)screenParseSpecialE3[0] == true){
                vendor = (String)screenParseSpecialE3[1];
                PLM = (String)screenParseSpecialE3[2];
                rev = (String)screenParseSpecialE3[3];
                serial = (Integer)screenParseSpecialE3[4];
            }

            sql += "(PCBA_Vendor" + sqlFieldFormatter(vendor) + " AND ";
            sql += "PCBA_PLM" + sqlFieldFormatter(PLM) + " AND ";
            sql += "PCBA_Rev" + sqlFieldFormatter(rev) + " AND ";
            sql += "PCBA_Serial" + sqlFieldFormatter(serial) + " ) OR  ";

            sql += "(Screen_Vendor" + sqlFieldFormatter(vendor) + " AND ";
            sql += "Screen_PLM" + sqlFieldFormatter(PLM) + " AND ";
            sql += "Screen_Rev" + sqlFieldFormatter(rev) + " AND ";
            sql += "Screen_Serial" + sqlFieldFormatter(serial) + " ) OR  ";

            sql += "(Housing_Vendor" + sqlFieldFormatter(vendor) + " AND ";
            sql += "Housing_PLM" + sqlFieldFormatter(PLM) + " AND ";
            sql += "Housing_Rev" + sqlFieldFormatter(rev) + " AND ";
            sql += "Housing_Serial" + sqlFieldFormatter(serial) + " ) OR  ";

            sql += "(Camera_Vendor" + sqlFieldFormatter(vendor) + " AND ";
            sql += "Camera_PLM" + sqlFieldFormatter(PLM) + " AND ";
            sql += "Camera_rev" + sqlFieldFormatter(rev) + " AND ";
            sql += "Camera_Serial" + sqlFieldFormatter(serial) + " )";

            ResultSet compLookUp = sqlExec.selectSQL(sql);
            if(compLookUp != null){
                compLookUp.next();
                return compLookUp.getInt("Display_Serial");
            }else{
                logger.log(Level.SEVERE, "Lookup By Component: Could not find any records");
                return 0;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lookup By Component: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public ResultSet lookupDisplayBuild(String str){
        int serial = DataParser.getSerial(str);
        if (serial != 0) {
            try {
                String sql = "SELECT * FROM " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME() + " WHERE Display_Serial = " + serial;
                return sqlExec.selectSQL(sql);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Could not lookup Display with Serial, could not execute SQL");
                e.printStackTrace();
                return null;
            }
        }else{
            logger.log(Level.SEVERE, "Could not parse the serial for lookup");
            return null;
        }
    }

    public DisplayBuild getExistingDisplayBuild(String AssemblySerial, DisplayBuild displayBuild){
        ResultSet rstGetExistingDisplayBuild = lookupDisplayBuild(AssemblySerial);

        if (rstGetExistingDisplayBuild != null){
            try {
                rstGetExistingDisplayBuild.next();
                //(rstGetExistingDisplayBuild.getString("Date_Time"));
                //(rstGetExistingDisplayBuild.getString("User_Name"));
                //(rstGetExistingDisplayBuild.getString("Computer_Name"));
                displayBuild.setWrPartNumber(rstGetExistingDisplayBuild.getString("WR_Part_Number"));
                displayBuild.setAssemblyPLM(rstGetExistingDisplayBuild.getString("Display_PLM"));
                displayBuild.setAssemblyRev(rstGetExistingDisplayBuild.getString("Display_Rev"));
                displayBuild.setAssemblySerial(rstGetExistingDisplayBuild.getInt("Display_Serial"));
                displayBuild.setPcbaVendor(rstGetExistingDisplayBuild.getString("PCBA_Vendor"));
                displayBuild.setPcbaPLM(rstGetExistingDisplayBuild.getString("PCBA_PLM"));
                displayBuild.setPcbaRev(rstGetExistingDisplayBuild.getString("PCBA_Rev"));
                displayBuild.setPcbaSerial(rstGetExistingDisplayBuild.getInt("PCBA_Serial"));
                displayBuild.setPcbPLM(rstGetExistingDisplayBuild.getString("PCB_PLM"));
                displayBuild.setPcbRev(rstGetExistingDisplayBuild.getString("PCB_Rev"));
                displayBuild.setScreenVendor(rstGetExistingDisplayBuild.getString("Screen_Vendor"));
                displayBuild.setScreenPLM(rstGetExistingDisplayBuild.getString("Screen_PLM"));
                displayBuild.setScreenRev(rstGetExistingDisplayBuild.getString("Screen_Rev"));
                displayBuild.setScreenSerial(rstGetExistingDisplayBuild.getInt("Screen_Serial"));
                displayBuild.setHousingVendor(rstGetExistingDisplayBuild.getString("Housing_Vendor"));
                displayBuild.setHousingPLM(rstGetExistingDisplayBuild.getString("Housing_PLM"));
                displayBuild.setHousingRev(rstGetExistingDisplayBuild.getString("Housing_Rev"));
                displayBuild.setHousingSerial(rstGetExistingDisplayBuild.getInt("Housing_Serial"));
                displayBuild.setCameraVendor(rstGetExistingDisplayBuild.getString("Camera_Vendor"));
                displayBuild.setCameraPLM(rstGetExistingDisplayBuild.getString("Camera_PLM"));
                displayBuild.setCameraRev(rstGetExistingDisplayBuild.getString("Camera_Rev"));
                displayBuild.setCameraSerial(rstGetExistingDisplayBuild.getInt("Camera_Serial"));
                displayBuild.setLeakTestPass(rstGetExistingDisplayBuild.getBoolean("Leak_Test_Pass"));
                //(rstGetExistingDisplayBuild.getString("Leak_Test_Value"));
                displayBuild.setTestAppPass(rstGetExistingDisplayBuild.getBoolean("Test_App_Pass"));
                //(rstGetExistingDisplayBuild.getString("Ready_For_Shipping").equals("1") ? "True" : "False");
                //(rstGetExistingDisplayBuild.getString("Customer_ID"));
                //(rstGetExistingDisplayBuild.getString("Comments"));
                return displayBuild;
            }catch(Exception e){
                logger.log(Level.SEVERE, "Could not lookup Display with Serial, could not get data from result set");
                e.printStackTrace();
                return null;
            }
        }else{
            logger.log(Level.SEVERE, "Could not get results for display build lookup");
            return null;
        }
    }

    public boolean updateBuild(DisplayBuild displayBuild , boolean updatePCB, boolean updateScreen, boolean updateHousing, boolean updateCamera){
        Timestamp dateTime = new Timestamp(System.currentTimeMillis());
        String userName = System.getProperty("user.name");
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        }catch (UnknownHostException e){
            logger.log(Level.SEVERE, "Failed to get computer name");
            e.printStackTrace();
            return false;
        }

        String updateSQLHeader = "UPDATE " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME() + " SET ";
        String updateSQLCondition = " WHERE Display_Serial" + sqlFieldFormatter(displayBuild.getAssemblySerial());

        String sep = ",";
        String updateSQLStatement = "";

        if(updatePCB)
        updateSQLStatement += "PCBA_Vendor =" + sqlFieldFormatterInsert(displayBuild.getPcbaVendor()) + sep +
                "PCBA_PLM =" + sqlFieldFormatterInsert(displayBuild.getPcbaPLM()) + sep +
                "PCBA_Rev =" + sqlFieldFormatterInsert(displayBuild.getPcbaRev()) + sep +
                "PCBA_Serial =" + sqlFieldFormatterInsert(displayBuild.getPcbaSerial()) + sep +
                "PCB_PLM =" + sqlFieldFormatterInsert(displayBuild.getPcbPLM()) + sep +
                "PCB_Rev =" + sqlFieldFormatterInsert(displayBuild.getPcbRev()) + sep;

        if(updateScreen)
        updateSQLStatement += "Screen_Vendor =" + sqlFieldFormatterInsert(displayBuild.getScreenVendor()) + sep +
                "Screen_PLM =" + sqlFieldFormatterInsert(displayBuild.getScreenPLM()) + sep +
                "Screen_Rev =" + sqlFieldFormatterInsert(displayBuild.getScreenRev()) + sep +
                "Screen_Serial =" + sqlFieldFormatterInsert(displayBuild.getScreenSerial()) + sep;

        if(updateHousing)
        updateSQLStatement += "Housing_Vendor =" + sqlFieldFormatterInsert(displayBuild.getHousingVendor()) + sep +
                "Housing_PLM =" + sqlFieldFormatterInsert(displayBuild.getHousingPLM()) + sep +
                "Housing_Rev =" + sqlFieldFormatterInsert(displayBuild.getHousingRev()) + sep +
                "Housing_Serial =" + sqlFieldFormatterInsert(displayBuild.getHousingSerial()) + sep;

        if(updateCamera)
        updateSQLStatement += "Camera_Vendor =" + sqlFieldFormatterInsert(displayBuild.getCameraVendor()) + sep +
                "Camera_PLM =" + sqlFieldFormatterInsert(displayBuild.getCameraPLM()) + sep +
                "Camera_Rev =" + sqlFieldFormatterInsert(displayBuild.getCameraRev()) + sep +
                "Camera_Serial =" + sqlFieldFormatterInsert(displayBuild.getCameraSerial()) + sep;

        updateSQLStatement +=  "Leak_Test_Pass =" + sqlFieldFormatterInsert(0) + sep +
                "Test_App_Pass = " + sqlFieldFormatterInsert(0);

        updateSQLStatement = updateSQLHeader + updateSQLStatement + updateSQLCondition;

        try{
            if (insertDisplayBuildLog(displayBuild,"Rework_Build",dateTime,userName,computerName, null)) {
                int updateSQL = sqlExec.updateSQL(updateSQLStatement);
                if (updateSQL == 1) {
                    return true;
                } else {
                    logger.log(Level.SEVERE, "Error with SQL while inserting in print log, rows returned: " + updateSQL);
                    return false;
                }
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error updating rework in print log");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean updateLeak(String assemblySerial, String leakTestResult, String leakTestValue){
        int assmSerialInt;
        double leakTestReal;
        try {
            assmSerialInt = DataParser.getSerial(assemblySerial);
            leakTestReal = Double.parseDouble(leakTestValue);

        }
        catch(Exception e){
            logger.log(Level.SEVERE, "Leak Test: Could not convert String to INT");
            e.printStackTrace();
            return false;
        }

        String updateSQLHeader = "UPDATE " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME() + " SET ";
        String updateSQLCondition = " WHERE Display_Serial" + sqlFieldFormatter(assmSerialInt);

        String sep = ",";
        String updateSQLStatement = "";

        updateSQLStatement += "Leak_Test_Pass" + sqlFieldFormatter(sqlPassFailBoolFormatter(leakTestResult)) + sep;
        updateSQLStatement += "Leak_Test_Value" + sqlFieldFormatter(leakTestReal) ;

        updateSQLStatement = updateSQLHeader + updateSQLStatement + updateSQLCondition;

        try{
            int updateSQL = sqlExec.updateSQL(updateSQLStatement);
            if (updateSQL == 1) {
                return true;
            }else{
                logger.log(Level.SEVERE, "Error with SQL while inserting in display table, rows returned: " + updateSQL);
                return false;
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error updating leak test result, does build exist?");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTraceability(String assemblySerial, String customerID, String comments){
        int displaySerialInt;
        double leakTestReal;
        try {
            displaySerialInt = DataParser.getSerial(assemblySerial);

        }
        catch(Exception e){
            logger.log(Level.SEVERE, "Traceability: Could not convert String to INT");
            e.printStackTrace();
            return false;
        }

        String updateSQLHeader = "UPDATE " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME() + " SET ";
        String updateSQLCondition = " WHERE Display_Serial" + sqlFieldFormatter(displaySerialInt);

        String sep = ",";
        String updateSQLStatement = "";

        updateSQLStatement += "Customer_ID" + sqlFieldFormatter(customerID) + sep;
        updateSQLStatement += "Comments" + sqlFieldFormatter(comments) ;

        updateSQLStatement = updateSQLHeader + updateSQLStatement + updateSQLCondition;

        try{
            int updateSQL = sqlExec.updateSQL(updateSQLStatement);
            if (updateSQL == 1) {
                return true;
            }else{
                logger.log(Level.SEVERE, "Traceability: Error with SQL while inserting in display table, rows returned: " + updateSQL);
                return false;
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Traceability: Error updating, does build exist?");
            e.printStackTrace();
            return false;
        }
    }

    public int sqlPassFailBoolFormatter(String str){
        if(str.equals("Pass")){
            return 1;
        }else{
            return 0;
        }
    }

    public boolean validatePrintRequest(String assemblySerial, boolean isCHKFinal, boolean isCHKAssmbleyWIP, boolean isCHKPCBA, boolean isCHKScreen,
                                        boolean isCHKHousing, boolean isCHKCamera){
        DisplayBuild displayBuild = new DisplayBuild();

        int assmSerialInt;
        try {
            assmSerialInt = DataParser.getSerial(assemblySerial);
        }
        catch(Exception e){
            logger.log(Level.SEVERE, "Leak Test: Could not convert String to INT");
            e.printStackTrace();
            return false;
        }
        String usrErrMsg = "Cannot print final label:\n";
        String updateSQLHeader = "UPDATE " + sqlExec.getDISPLAY_BUILDS_TABLE_NAME() + " SET ";
        String updateSQLCondition = " WHERE Display_Serial" + sqlFieldFormatter(assmSerialInt);

        String sep = ",";
        String updateSQLStatement = "";

        updateSQLStatement += "Ready_For_Shipping" + sqlFieldFormatter(1);

        displayBuild = getExistingDisplayBuild(assemblySerial, displayBuild);

        checkIfBuildApproved(displayBuild);

        if(isCHKFinal && displayBuild.isLeakTestPass() == true && displayBuild.isTestAppPass() == true){
            try{
                updateSQLStatement = updateSQLHeader + updateSQLStatement + updateSQLCondition;
                int updateSQL = sqlExec.updateSQL(updateSQLStatement);
                if (updateSQL == 1) {
                    insertPrintLog(displayBuild,"User_Request",isCHKAssmbleyWIP,true, isCHKPCBA,isCHKScreen,isCHKHousing,isCHKCamera);
                    return true;
                }else{
                    logger.log(Level.SEVERE, "Error with SQL while inserting in print log, rows returned: " + updateSQL);
                    return false;
                }
            }catch (Exception e){
                logger.log(Level.SEVERE, "Could not update print log on user request");
                e.printStackTrace();
                return false;
            }
        }else if(isCHKFinal){
            if(! displayBuild.isLeakTestPass())
                usrErrMsg += "DUT has not passed leak test\n";
            if(! displayBuild.isTestAppPass())
                usrErrMsg += "DUT has not passed QC app\n";

            guiForm.txtMsg.setText( usrErrMsg);
            return false;
        }else if(! isCHKFinal){
            try{
                insertPrintLog(displayBuild,"User_Request",isCHKAssmbleyWIP,false, isCHKPCBA,isCHKScreen,isCHKHousing,isCHKCamera);
            }catch (Exception e){
                logger.log(Level.SEVERE, "Could not update print log on user request");
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean printDispenseLabel(boolean printRTV, boolean printLubricant){
        Timestamp dateTime = new Timestamp(System.currentTimeMillis());
        String userName = System.getProperty("user.name");
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        }catch (UnknownHostException e){
            logger.log(Level.SEVERE, "Failed to get computer name");
            e.printStackTrace();
            return false;
        }

        String sep = ",";
        String insertSQLValues = sqlFieldFormatterInsert(dateTime) + sep +
                sqlFieldFormatterInsert(userName) + sep +
                sqlFieldFormatterInsert(computerName) + sep +
                sqlFieldFormatterInsert("User_Dispense") + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0)  + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0)  + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0) + sep +
                sqlFieldFormatterInsert(0)  + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(false)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(printRTV)) + sep +
                sqlFieldFormatterInsert(sqlBoolFormatter(printLubricant)) + sep +
                //TODO update for printer selection
                sqlFieldFormatterInsert("qc2_4x1");

        String insertSQLStatement = "INSERT INTO " + sqlExec.getDISPLAY_PRINT_LOG_TABLE_NAME() + " (" +
                SQL_INSERT_LABEL_PRINT_LOG + ") VALUES (" + insertSQLValues + ")";

        try{
            int insertSQL = sqlExec.updateSQL(insertSQLStatement);
            if (insertSQL == 1) {
                return true;
            }else{
                logger.log(Level.SEVERE, "Error with SQL while inserting in print log, rows returned: " + insertSQL);
                return false;
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error inserting into print log");
            e.printStackTrace();
            return false;
        }
    }
}