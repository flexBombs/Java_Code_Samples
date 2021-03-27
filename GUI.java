package com.persistentsystems.GUI;


//import org.apache.logging.log4j.Level;

import com.persistentsystems.DisplayBuilder.DisplayBuildValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;

public class GUI {
    private static PS_Display_Builder guiForm;
    private static int progress = 0;
    public static Color p_blue = new Color(0,84,164);
    public static Color p_lightGray = new Color(221,219,219);
    public static Color p_red = new Color(238, 49,50);
    public static Color p_green = new Color(65,173,73);
    public static ArrayList<JCheckBox> checklist = new ArrayList<>();
    public static ArrayList<String> checklist_str = new ArrayList<>();
    private static JFrame frame;
    private static boolean debugmode;
    private static boolean embeddedmode;
    private static boolean engineeringmode;
    private static boolean PCBA_PSL = false;
    private static boolean SCREEN_PSL = false;
    private static boolean HOUSING_PSL = true;
    private static boolean CAMERA_PSL = true;

    private static String guiPCBAStr;
    private static String guiScreenStr;
    private static String guiHousingStr;
    private static String guiCameraStr;
    private static String guiRB_PCBAStr;
    private static String guiRB_ScreenStr;
    private static String guiRB_HousingStr;
    private static String guiRB_CameraStr;

    private static DisplayBuildValidator displayBuildValidator;

    public GUI(){
//        createAndShowGUI();
    }

    public static void createAndShowGUI(boolean engineering){
        guiForm = new PS_Display_Builder();
        frame = new JFrame("PS QC Display Builder");
        guiForm.versionNumberLabel.setText("PS QC Display Builder | v 1.0");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        engineeringmode = engineering;

        //Display PCBA and Screen PS Labeling
        if(!PCBA_PSL) {
            guiForm.chkPCBASerial.setEnabled(false);
            guiForm.chkRB_PSL_PCBASerial.setEnabled(false);
        }
        if(!SCREEN_PSL) {
            guiForm.chkScreenSerial.setEnabled(false);
            guiForm.chkRB_PSL_HousingSerial.setEnabled(false);
        }
        if(!HOUSING_PSL) {
            guiForm.chkScreenSerial.setEnabled(false);
            guiForm.chkRB_PSL_HousingSerial.setEnabled(false);
        }
        if(!CAMERA_PSL) {
            guiForm.chkCameraSerial.setEnabled(false);
            guiForm.chkRB_PSL_CameraSerial.setEnabled(false);
        }

        updateLeakTestCombo();
        displayBuildValidator = new DisplayBuildValidator(guiForm);

        frame.setContentPane(guiForm.mainPanel);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        guiForm.chkPCBASerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkPCBASerial.isSelected() == true){
                    toggleVisEnable(guiForm.cmbPCBASerial, guiForm.txtPCBASerial);
                }else if (guiForm.chkPCBASerial.isSelected() == false){
                    toggleVisEnable(guiForm.txtPCBASerial, guiForm.cmbPCBASerial);
                }
            }
        });

        guiForm.chkScreenSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkScreenSerial.isSelected() == true){
                    toggleVisEnable(guiForm.cmbScreenSerial, guiForm.txtScreenSerial);
                }else if (guiForm.chkScreenSerial.isSelected() == false){
                    toggleVisEnable(guiForm.txtScreenSerial, guiForm.cmbScreenSerial);
                }
            }
        });

        guiForm.chkHousingSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkHousingSerial.isSelected() == true){
                    toggleVisEnable(guiForm.cmbHousingSerial, guiForm.txtHousingSerial);
                }else if (guiForm.chkHousingSerial.isSelected() == false){
                    toggleVisEnable(guiForm.txtHousingSerial, guiForm.cmbHousingSerial);
                }
            }
        });

        guiForm.chkCameraSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkCameraSerial.isSelected() == true){
                    toggleVisEnable(guiForm.cmbCameraSerial, guiForm.txtCameraSerial);
                }else if (guiForm.chkCameraSerial.isSelected() == false){
                    toggleVisEnable(guiForm.txtCameraSerial, guiForm.cmbCameraSerial);
                }
            }
        });

        guiForm.chkRB_PSL_PCBASerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkRB_PSL_PCBASerial.isSelected() == true){
                    toggleVisEnable(guiForm.cmbRB_PCBASerial, guiForm.txtRB_PCBASerial);
                }else if (guiForm.chkRB_PSL_PCBASerial.isSelected() == false){
                    toggleVisEnable(guiForm.txtRB_PCBASerial, guiForm.cmbRB_PCBASerial);
                }
            }
        });

        guiForm.chkRB_PSL_ScreenSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkRB_PSL_ScreenSerial.isSelected() == true){
                    toggleVisEnable(guiForm.cmbRB_ScreenSerial, guiForm.txtRB_ScreenSerial);
                }else if (guiForm.chkRB_PSL_ScreenSerial.isSelected() == false){
                    toggleVisEnable(guiForm.txtRB_ScreenSerial, guiForm.cmbRB_ScreenSerial);
                }
            }
        });

        guiForm.chkRB_PSL_HousingSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkRB_PSL_HousingSerial.isSelected() == true){
                    toggleVisEnable(guiForm.cmbRB_HousingSerial, guiForm.txtRB_HousingSerial);
                }else if (guiForm.chkRB_PSL_HousingSerial.isSelected() == false){
                    toggleVisEnable(guiForm.txtRB_HousingSerial, guiForm.cmbRB_HousingSerial);
                }
            }
        });

        guiForm.chkRB_PSL_CameraSerial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkRB_PSL_CameraSerial.isSelected() == true){
                    toggleVisEnable(guiForm.cmbRB_CameraSerial, guiForm.txtRB_CameraSerial);
                }else if (guiForm.chkRB_PSL_CameraSerial.isSelected() == false){
                    toggleVisEnable(guiForm.txtRB_CameraSerial, guiForm.cmbRB_CameraSerial);
                }
            }
        });

        guiForm.chkRB_PCBA.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent a) {
                 if (guiForm.chkRB_PCBA.isSelected() == true){
                     if(PCBA_PSL) {
                         toggleVisEnable(guiForm.chkRB_PSL_PCBASerial, null);
                     }else{
                         guiForm.chkRB_PSL_PCBASerial.setVisible(true);
                     }
                     if (guiForm.chkRB_PSL_PCBASerial.isSelected() == true){
                         toggleVisEnable(guiForm.cmbRB_PCBASerial, guiForm.txtRB_PCBASerial);
                     }else if (guiForm.chkPCBASerial.isSelected() == false){
                         toggleVisEnable(guiForm.txtRB_PCBASerial, guiForm.cmbRB_PCBASerial);
                     }
                 }else if (guiForm.chkRB_PCBA.isSelected() == false){
                     toggleVisEnable(null,guiForm.chkRB_PSL_PCBASerial);
                     toggleVisEnable(null,guiForm.txtRB_PCBASerial);
                     toggleVisEnable(null,guiForm.cmbRB_PCBASerial);
                 }
             }
        });
        guiForm.chkRB_Screen.addActionListener(new ActionListener() {
               @Override
               public void actionPerformed(ActionEvent a) {
                   if (guiForm.chkRB_Screen.isSelected() == true){
                       if(SCREEN_PSL) {
                           toggleVisEnable(guiForm.chkRB_PSL_ScreenSerial, null);
                       }else{
                           guiForm.chkRB_PSL_ScreenSerial.setVisible(true);
                       }
                       if (guiForm.chkRB_PSL_ScreenSerial.isSelected() == true){
                           toggleVisEnable(guiForm.cmbRB_ScreenSerial, guiForm.txtRB_ScreenSerial);
                       }else if (guiForm.chkScreenSerial.isSelected() == false){
                           toggleVisEnable(guiForm.txtRB_ScreenSerial, guiForm.cmbRB_ScreenSerial);
                       }
                   }else if (guiForm.chkRB_Screen.isSelected() == false){
                       toggleVisEnable(null,guiForm.chkRB_PSL_ScreenSerial);
                       toggleVisEnable(null,guiForm.txtRB_ScreenSerial);
                       toggleVisEnable(null,guiForm.cmbRB_ScreenSerial);
                   }
               }
        });
        guiForm.chkRB_Housing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkRB_Housing.isSelected() == true){
                    if(HOUSING_PSL) {
                        toggleVisEnable(guiForm.chkRB_PSL_HousingSerial, null);
                    }else{
                        guiForm.chkRB_PSL_HousingSerial.setVisible(true);
                    }
                    if (guiForm.chkRB_PSL_HousingSerial.isSelected() == true){
                        toggleVisEnable(guiForm.cmbRB_HousingSerial, guiForm.txtRB_HousingSerial);
                    }else if (guiForm.chkRB_PSL_HousingSerial.isSelected() == false){
                        toggleVisEnable(guiForm.txtRB_HousingSerial, guiForm.cmbRB_HousingSerial);
                    }
                }else if (guiForm.chkRB_Housing.isSelected() == false){
                    toggleVisEnable(null,guiForm.chkRB_PSL_HousingSerial);
                    toggleVisEnable(null,guiForm.txtRB_HousingSerial);
                    toggleVisEnable(null,guiForm.cmbRB_HousingSerial);
                }
            }
        });
        guiForm.chkRB_Camera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                if (guiForm.chkRB_Camera.isSelected() == true){
                    if(CAMERA_PSL) {
                        toggleVisEnable(guiForm.chkRB_PSL_CameraSerial, null);
                    }else{
                        guiForm.chkRB_PSL_CameraSerial.setVisible(true);
                    }
                    if (guiForm.chkRB_PSL_CameraSerial.isSelected() == true){
                        toggleVisEnable(guiForm.cmbRB_CameraSerial, guiForm.txtRB_CameraSerial);
                    }else if (guiForm.chkRB_PSL_CameraSerial.isSelected() == false){
                        toggleVisEnable(guiForm.txtRB_CameraSerial, guiForm.cmbRB_CameraSerial);
                    }
                }else if (guiForm.chkRB_Camera.isSelected() == false){
                    toggleVisEnable(null,guiForm.chkRB_PSL_CameraSerial);
                    toggleVisEnable(null,guiForm.txtRB_CameraSerial);
                    toggleVisEnable(null,guiForm.cmbRB_CameraSerial);
                }
            }
        });

        guiForm.btnBuildDisplay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                buildDisplay();
            }
        });

        guiForm.btnLookupBuild.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                lookupBuild("0");
            }
        });

        guiForm.btnReworkDisplay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reworkBuild();
            }
        });

        guiForm.btnLeakTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateLeakTest();
            }
        });

        guiForm.btnPrintLabels.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printLabels();
            }
        });

        guiForm.btnClearText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiForm.txtDisplaySerial.setText("");
                guiForm.txtDisplaySerial.requestFocus();
            }
        });

        guiForm.btnInternal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiForm.txtCustomerID.setText("Internal");
            }
        });

        guiForm.btnExternal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guiForm.txtCustomerID.setText("External");
            }
        });

        guiForm.btnUpdateTraceability.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { updateTraceability(); }
        });

        guiForm.btnTraceabilityOriginal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { updateTraceabilityOriginal(); }
        });

        guiForm.btnLU_Component.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { lookupByComponent(); }
        });

                displayBuildValidator.updateCombos(guiForm.cmbPCBASerial, guiForm.cmbScreenSerial, guiForm.cmbHousingSerial, guiForm.cmbCameraSerial);
        displayBuildValidator.updateCombos(guiForm.cmbRB_PCBASerial, guiForm.cmbRB_ScreenSerial, guiForm.cmbRB_HousingSerial, guiForm.cmbRB_CameraSerial);

    }

    private static void toggleVisEnable(JComponent show, JComponent hide){
        if(show != null) {
            show.setEnabled(true);
            show.setVisible(true);
        }

        if(hide != null) {
            hide.setEnabled(false);
            hide.setVisible(false);
        }
    }

    //DISPLAY BUILDER

    //Some validation in form
    public static void  buildDisplay(){
        boolean ready2Validate = true;
        String errorMsg = "";

        guiPCBAStr = guiForm.chkPCBASerial.isSelected() ?
            guiForm.cmbPCBASerial.getSelectedItem().toString() : guiForm.txtPCBASerial.getText();
        guiScreenStr = guiForm.chkScreenSerial.isSelected() ?
            guiForm.cmbScreenSerial.getSelectedItem().toString() : guiForm.txtScreenSerial.getText();
        guiHousingStr = guiForm.chkHousingSerial.isSelected() ?
            guiForm.cmbHousingSerial.getSelectedItem().toString() : guiForm.txtHousingSerial.getText();
        guiCameraStr = guiForm.chkCameraSerial.isSelected() ?
            guiForm.cmbCameraSerial.getSelectedItem().toString() : guiForm.txtCameraSerial.getText();

        if(guiPCBAStr.isEmpty() || guiPCBAStr == null){
            ready2Validate = false;
            errorMsg += "Scan, Enter, or Select a PCBA Serial\n";
        }
        if(guiScreenStr.isEmpty() || guiScreenStr == null){
            ready2Validate = false;
            errorMsg += "Scan, Enter, or Select a Screen Serial\n";
        }
        if(guiHousingStr.isEmpty() || guiHousingStr == null){
            ready2Validate = false;
            errorMsg +=  "Scan, Enter, or Select a Housing Serial\n";
        }
        if(guiCameraStr.isEmpty() || guiCameraStr == null){
            ready2Validate = false;
            errorMsg +=  "Scan, Enter, or Select a Camera Serial\n";
        }
        if(ready2Validate) {

            if (displayBuildValidator.validateNewBuild(guiPCBAStr, true, guiForm.chkPCBASerial.isSelected(),
                    guiScreenStr, true, guiForm.chkScreenSerial.isSelected(),
                    guiHousingStr, true, guiForm.chkHousingSerial.isSelected(),
                    guiCameraStr, true, guiForm.chkCameraSerial.isSelected())){
                if(!guiForm.chkPCBASerial.isSelected())
                    guiForm.txtPCBASerial.setText("");
                if(!guiForm.chkScreenSerial.isSelected())
                    guiForm.txtScreenSerial.setText("");
                if(!guiForm.chkHousingSerial.isSelected())
                    guiForm.txtScreenSerial.setText("");
                if(!guiForm.chkCameraSerial.isSelected())
                    guiForm.txtHousingSerial.setText("");

                guiForm.txtPCBASerial.requestFocus();
            }

        }else{
            guiForm.txtMsg.setText( errorMsg);
        }
    }

    public static void updateTraceabilityOriginal(){
        if (guiForm.txtDisplaySerial.getText().isEmpty() || guiForm.txtDisplaySerial.getText() == null) {
            guiForm.txtMsg.setText( "Enter a serial to lookup");
        } else {
            try {
                ResultSet rst = displayBuildValidator.lookupDisplayBuild(guiForm.txtDisplaySerial.getText());
                if (rst != null) {
                    rst.next();
                    guiForm.txtCustomerID.setText(rst.getString("Customer_ID"));
                    guiForm.txtComments.setText(rst.getString("Comments"));
                }else{
                    guiForm.txtMsg.setText( "Could not lookup build: Error with SQL String");
                }
            } catch (Exception e) {
                guiForm.txtMsg.setText( "Could not lookup build");
                e.printStackTrace();
            }
        }
    }

    public static void lookupByComponent(){
        int serial;
        if (guiForm.txtLU_Component.getText().isEmpty() || guiForm.txtLU_Component.getText() == null) {
            guiForm.txtMsg.setText("Enter a component serial to lookup");
            clearLookup();
        }else{
            serial = displayBuildValidator.getDisplayByComponent(guiForm.txtLU_Component.getText());
            if(serial != 0){
                lookupBuild(String.valueOf(serial));
            }else{
                guiForm.txtMsg.setText("Could not find component");
                clearLookup();
            }
        }
    }

    public static void lookupBuild(String serial) {
        String LU_Serial = "0";
        boolean readyToSearch = true;

        if (! serial.equals("0")){
            LU_Serial = serial;
        }else{
            if (guiForm.txtDisplaySerial.getText().isEmpty() || guiForm.txtDisplaySerial.getText() == null) {
                guiForm.txtMsg.setText("Enter a serial to lookup");
                readyToSearch = false;
                clearLookup();
            }else{
                LU_Serial = guiForm.txtDisplaySerial.getText();
            }
        }

        if(readyToSearch)
            try {
                ResultSet rst = displayBuildValidator.lookupDisplayBuild(LU_Serial);
                if (rst != null) {
                    rst.next();
                    guiForm.txtLU_TimeStamp.setText(rst.getString("Date_Time"));
                    guiForm.txtLU_UserName.setText(rst.getString("User_Name"));
                    guiForm.txtLU_ComputerName.setText(rst.getString("Computer_Name"));
                    guiForm.txtLU_DisplayPLM.setText(rst.getString("Display_PLM"));
                    guiForm.txtLU_DisplayRev.setText(rst.getString("Display_Rev"));
                    guiForm.txtLU_DisplaySerial.setText(rst.getString("Display_Serial"));
                    guiForm.txtLU_WRPartNumber.setText(rst.getString("WR_Part_Number"));
                    guiForm.txtLU_PCBAVendor.setText(rst.getString("PCBA_Vendor"));
                    guiForm.txtLU_PCBAPLM.setText(rst.getString("PCBA_PLM"));
                    guiForm.txtLU_PCBARev.setText(rst.getString("PCBA_Rev"));
                    guiForm.txtLU_PCBASerial.setText(rst.getString("PCBA_Serial"));
                    guiForm.txtLU_PCBPLM.setText(rst.getString("PCB_PLM"));
                    guiForm.txtLU_PCBRev.setText(rst.getString("PCB_Rev"));
                    guiForm.txtLU_ScreenVendor.setText(rst.getString("Screen_Vendor"));
                    guiForm.txtLU_ScreenPLM.setText(rst.getString("Screen_PLM"));
                    guiForm.txtLU_ScreenRev.setText(rst.getString("Screen_Rev"));
                    guiForm.txtLU_ScreenSerial.setText(rst.getString("Screen_Serial"));
                    guiForm.txtLU_HousingVendor.setText(rst.getString("Housing_Vendor"));
                    guiForm.txtLU_HousingPLM.setText(rst.getString("Housing_PLM"));
                    guiForm.txtLU_HousingRev.setText(rst.getString("Housing_Rev"));
                    guiForm.txtLU_HousingSerial.setText(rst.getString("Housing_Serial"));
                    guiForm.txtLU_CameraVendor.setText(rst.getString("Camera_Vendor"));
                    guiForm.txtLU_CameraPLM.setText(rst.getString("Camera_PLM"));
                    guiForm.txtLU_CameraRev.setText(rst.getString("Camera_Rev"));
                    guiForm.txtLU_CameraSerial.setText(rst.getString("Camera_Serial"));
                    guiForm.txtLU_LeakResult.setText(rst.getString("Leak_Test_Pass").equals("1") ? "True" : "False");
                    guiForm.txtLU_LeakValue.setText(rst.getString("Leak_Test_Value"));
                    guiForm.txtLU_QCAppResult.setText(rst.getString("Test_App_Pass").equals("1") ? "True" : "False");
                    guiForm.txtLU_ReadyToShip.setText(rst.getString("Ready_For_Shipping").equals("1") ? "True" : "False");
                    guiForm.txtLU_CustomerID.setText(rst.getString("Customer_ID"));
                    guiForm.txtLU_Comments.setText(rst.getString("Comments"));
                }else{
                    guiForm.txtMsg.setText( "Could not lookup build: Error with SQL String");
                    clearLookup();
                }
            } catch (Exception e) {
                guiForm.txtMsg.setText( "Could not lookup build");
                clearLookup();
                e.printStackTrace();
            }
    }

    public static void clearLookup(){
        guiForm.txtLU_TimeStamp.setText("");
        guiForm.txtLU_UserName.setText("");
        guiForm.txtLU_ComputerName.setText("");
        guiForm.txtLU_DisplayPLM.setText("");
        guiForm.txtLU_DisplayRev.setText("");
        guiForm.txtLU_DisplaySerial.setText("");
        guiForm.txtLU_WRPartNumber.setText("");
        guiForm.txtLU_PCBAVendor.setText("");
        guiForm.txtLU_PCBAPLM.setText("");
        guiForm.txtLU_PCBARev.setText("");
        guiForm.txtLU_PCBASerial.setText("");
        guiForm.txtLU_PCBPLM.setText("");
        guiForm.txtLU_PCBRev.setText("");
        guiForm.txtLU_ScreenVendor.setText("");
        guiForm.txtLU_ScreenPLM.setText("");
        guiForm.txtLU_ScreenRev.setText("");
        guiForm.txtLU_ScreenSerial.setText("");
        guiForm.txtLU_HousingVendor.setText("");
        guiForm.txtLU_HousingPLM.setText("");
        guiForm.txtLU_HousingRev.setText("");
        guiForm.txtLU_HousingSerial.setText("");
        guiForm.txtLU_CameraVendor.setText("");
        guiForm.txtLU_CameraPLM.setText("");
        guiForm.txtLU_CameraRev.setText("");
        guiForm.txtLU_CameraSerial.setText("");
        guiForm.txtLU_LeakResult.setText("");
        guiForm.txtLU_LeakValue.setText("");
        guiForm.txtLU_QCAppResult.setText("");
        guiForm.txtLU_ReadyToShip.setText("");
        guiForm.txtLU_CustomerID.setText("");
        guiForm.txtLU_Comments.setText("");
    }

    public static void reworkBuild(){
        boolean ready2Validate = true;
        String errorMsg = "";

        guiRB_PCBAStr = guiForm.chkRB_PSL_PCBASerial.isSelected() ?
                guiForm.cmbRB_PCBASerial.getSelectedItem().toString() : guiForm.txtRB_PCBASerial.getText();
        guiRB_ScreenStr = guiForm.chkRB_PSL_ScreenSerial.isSelected() ?
                guiForm.cmbRB_ScreenSerial.getSelectedItem().toString() : guiForm.txtRB_ScreenSerial.getText();
        guiRB_HousingStr = guiForm.chkRB_PSL_HousingSerial.isSelected() ?
                guiForm.cmbRB_HousingSerial.getSelectedItem().toString() : guiForm.txtRB_HousingSerial.getText();
        guiRB_CameraStr = guiForm.chkRB_PSL_CameraSerial.isSelected() ?
                guiForm.cmbRB_CameraSerial.getSelectedItem().toString() : guiForm.txtRB_CameraSerial.getText();

        if(guiForm.txtDisplaySerial.getText().isEmpty() || guiForm.txtDisplaySerial.getText() == null){
            ready2Validate = false;
            errorMsg += "Scan, Enter, or Select an Assembly Serial to rework\n";
        }
        if(guiForm.chkRB_PCBA.isSelected() && (guiRB_PCBAStr.isEmpty() || guiRB_PCBAStr == null)){
            ready2Validate = false;
            errorMsg += "Scan, Enter, or Select a new PCBA Serial\n";
        }
        if(guiForm.chkRB_Screen.isSelected() && (guiRB_ScreenStr.isEmpty() || guiRB_ScreenStr == null)){
            ready2Validate = false;
            errorMsg += "Scan, Enter, or Select a new Screen Serial\n";
        }
        if(guiForm.chkRB_Housing.isSelected() && (guiRB_HousingStr.isEmpty() || guiRB_HousingStr == null)){
            ready2Validate = false;
            errorMsg +=  "Scan, Enter, or Select a new Housing Serial\n";
        }
        if(guiForm.chkRB_Camera.isSelected() && (guiRB_CameraStr.isEmpty() || guiRB_CameraStr == null)){
            ready2Validate = false;
            errorMsg +=  "Scan, Enter, or Select a new Camera Serial\n";
        }
        if(guiForm.chkRB_PCBA.isSelected() && guiForm.chkRB_Screen.isSelected() &&
                guiForm.chkRB_Housing.isSelected() && guiForm.chkRB_Camera.isSelected() ){
            ready2Validate = false;
            errorMsg += "Cannot rework all components at once";
        }
        if(!guiForm.chkRB_PCBA.isSelected() && !guiForm.chkRB_Screen.isSelected() &&
                !guiForm.chkRB_Housing.isSelected() && !guiForm.chkRB_Camera.isSelected() ){
            ready2Validate = false;
            errorMsg += "Select at least one component to rework";
        }

        if(ready2Validate) {
            if(displayBuildValidator.validateReworkBuild(guiForm.txtDisplaySerial.getText(),
                    guiRB_PCBAStr, guiForm.chkRB_PCBA.isSelected(), guiForm.chkRB_PSL_PCBASerial.isSelected(),
                    guiRB_ScreenStr, guiForm.chkRB_Screen.isSelected(), guiForm.chkRB_PSL_ScreenSerial.isSelected(),
                    guiRB_HousingStr, guiForm.chkRB_Housing.isSelected(), guiForm.chkRB_PSL_HousingSerial.isSelected(),
                    guiRB_CameraStr, guiForm.chkRB_Camera.isSelected(), guiForm.chkRB_PSL_CameraSerial.isSelected())){
                guiForm.txtRB_PCBASerial.setText("");
                guiForm.txtRB_ScreenSerial.setText("");
                guiForm.txtRB_HousingSerial.setText("");
                guiForm.txtRB_CameraSerial.setText("");
                guiForm.chkRB_PCBA.setSelected(false);
                guiForm.chkRB_Screen.setSelected(false);
                guiForm.chkRB_Housing.setSelected(false);
                guiForm.chkRB_Camera.setSelected(false);
                guiForm.chkRB_PSL_PCBASerial.setSelected(false);
                guiForm.chkRB_PSL_ScreenSerial.setSelected(false);
                guiForm.chkRB_PSL_HousingSerial.setSelected(false);
                guiForm.chkRB_PSL_CameraSerial.setSelected(false);
            }

        }else{
            guiForm.txtMsg.setText( errorMsg);
        }
    }

    public static void updateLeakTest(){
        String displaySerial = guiForm.txtDisplaySerial.getText();
        String leakTestResult = guiForm.cmbLeakTestResult.getSelectedItem().toString();
        String leakTestValue = guiForm.txtLeakTestValue.getText();
        String errorMsg = "";

        boolean readyToUpdate = true;

        if(displaySerial.isEmpty() || displaySerial == null){
            readyToUpdate  = false;
            errorMsg += "Enter or scan unit to update\n";
        }

        if(leakTestResult.isEmpty() || leakTestResult == null){
            readyToUpdate  = false;
            errorMsg += "Select Leak Test Pass/Fail Status\n";
        }

        if(leakTestValue.isEmpty() || leakTestValue == null){
            readyToUpdate  = false;
            errorMsg += "Enter a leak test value to update\n";
        }else if(leakTestValue.indexOf(".") == -1){
            readyToUpdate  = false;
            errorMsg += "Incorrect pressure decay value added, must be a decimal '0.0000'\nMissing Decimal Point";
        }else if(leakTestValue.indexOf(".") != 1){
            readyToUpdate  = false;
            errorMsg += "Incorrect pressure decay value added, must be a decimal '0.0000'\nDecimal Point in Wrong Location";
        }else if(leakTestValue.length() != 6){
            readyToUpdate  = false;
            errorMsg += "Incorrect pressure decay value added, must be a decimal '0.0000'\nValue needs to be 6 characters long";
        }

        if(readyToUpdate){
            if(displayBuildValidator.updateLeak(displaySerial,leakTestResult,leakTestValue)){
                guiForm.txtMsg.setText("Success: Leak test record of Display Build is updated");
                guiForm.cmbLeakTestResult.setSelectedIndex(0);
                guiForm.txtLeakTestValue.setText("");
            }else{
                guiForm.txtMsg.setText("Error: Could not update record of Display Build");
            }
        }else{
            guiForm.txtMsg.setText( errorMsg);
        }


    }

    public static void updateTraceability(){
        String displaySerial = guiForm.txtDisplaySerial.getText();
        String customerID = guiForm.txtCustomerID.getText();
        String comments = guiForm.txtComments.getText();
        String errorMsg = "";
        boolean readyToUpdate = true;

        if(displaySerial.isEmpty() || displaySerial == null) {
            errorMsg += "Enter or scan unit to update\n";
            readyToUpdate = false;
        }

        if(customerID.isEmpty() || customerID == null) {
            errorMsg += "Enter Customer ID, can enter N/A if no data is avaiable\n";
            readyToUpdate = false;
        }

        if(comments.isEmpty() || comments == null) {
            errorMsg += "Enter Comments, can enter N/A if no data is avaiable\n";
            readyToUpdate = false;
        }

        if (! readyToUpdate){
            guiForm.txtMsg.setText(errorMsg);
        }else{
            if(displayBuildValidator.updateTraceability(displaySerial,customerID,comments)){
                guiForm.txtMsg.setText("Success: Traceability record of Display Build is updated");
                guiForm.txtCustomerID.setText("");
                guiForm.txtComments.setText("");
            }else{
                guiForm.txtMsg.setText("Error: Could not update record of Display Build");
            }
        }
    }

    public static void updateLeakTestCombo(){
        guiForm.cmbLeakTestResult.addItem("Fail");
        guiForm.cmbLeakTestResult.addItem("Pass");
        guiForm.cmbLeakTestResult.setSelectedIndex(0);
    }

    public static void printLabels(){
        String assemblySerial = guiForm.txtDisplaySerial.getText();
        boolean isCHKFinal = guiForm.chkPL_Final.isSelected();
        boolean isCHKAssmbleyWIP = guiForm.chkPL_DisplayWIP.isSelected();
        boolean isCHKPCBA = guiForm.chkPL_PCBA.isSelected();
        boolean isCHKScreen = guiForm.chkPL_Screen.isSelected();
        boolean isCHKHousing = guiForm.chkPL_Housing.isSelected();
        boolean isCHKCamera = guiForm.chkPL_Camera.isSelected();
        boolean isCHKRTV = guiForm.chkRTV.isSelected();
        boolean isCHKLubricant = guiForm.chkLubricant.isSelected();

        String errorMsg = "";

        boolean readyToPrint = true;

        if((assemblySerial.isEmpty() || assemblySerial == null) && isCHKRTV == false && isCHKLubricant == false){
            readyToPrint  = false;
            errorMsg += "Enter or scan unit to print labels for\n";
        }

        if(isCHKFinal == false && isCHKAssmbleyWIP == false && isCHKPCBA == false && isCHKScreen == false && isCHKHousing == false && isCHKCamera == false &&
                isCHKRTV == false && isCHKLubricant == false){
            readyToPrint  = false;
            errorMsg += "Select at least one label to print\n";
        }

        if((isCHKFinal == true || isCHKAssmbleyWIP == true || isCHKPCBA == true || isCHKScreen == true || isCHKHousing == true || isCHKCamera == true) &&
                (isCHKRTV == true || isCHKLubricant == true)){
            readyToPrint  = false;
            errorMsg += "Cannot print WIP labels and RTV/Lubricant labels at the same time\n";
        }else if(isCHKRTV == true || isCHKLubricant == true){
            readyToPrint  = false;
            if (displayBuildValidator.printDispenseLabel(isCHKRTV, isCHKLubricant) == true){
                guiForm.chkRTV.setSelected(false);
                guiForm.chkLubricant.setSelected(false);
            }
        }

        if(readyToPrint){
            if (displayBuildValidator.validatePrintRequest(assemblySerial, isCHKFinal, isCHKAssmbleyWIP, isCHKPCBA, isCHKScreen, isCHKHousing, isCHKCamera)){
                guiForm.txtMsg.setText("Label log updated, label printing... ");
                guiForm.chkPL_Final.setSelected(false);
                guiForm.chkPL_DisplayWIP.setSelected(false);
                guiForm.chkPL_PCBA.setSelected(false);
                guiForm.chkPL_Screen.setSelected(false);
                guiForm.chkPL_Housing.setSelected(false);
                guiForm.chkPL_Camera.setSelected(false);
            }

        }else{
            guiForm.txtMsg.setText( errorMsg);
        }

    }
}


