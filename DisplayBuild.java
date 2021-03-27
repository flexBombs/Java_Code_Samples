package com.persistentsystems.DisplayBuilder;

public class DisplayBuild {
    private String guiPCBAStr;
    private boolean chkPCBA;
    private String guiScreenStr;
    private boolean chkScreen;
    private String guiHousingStr;
    private boolean chkHousing;
    private String guiCameraStr;
    private boolean chkCamera;

    private String wrPartNumber;
    private String pcbaVendor, pcbaPLM, pcbaRev;
    private String  pcbPLM, pcbRev;
    private String screenVendor, screenPLM, screenRev;
    private String housingVendor, housingPLM, housingRev;
    private String cameraVendor, cameraPLM, cameraRev;
    private String assemblyPLM;

    public String getAssemblyPLM() {
        return assemblyPLM;
    }

    private String assemblyRev;

    private int assemblySerial, pcbaSerial, screenSerial, housingSerial, cameraSerial;

    private double leakTestValue;
    private boolean leakTestPass, testAppPass, readyForShipping;
    private String customerID, comments;

    public DisplayBuild(){

    }

    public DisplayBuild(String guiPCBAStr, boolean chkPCBA, String guiScreenStr, boolean chkScreen,
                            String guiHousingStr, boolean chkHousing, String guiCameraStr, boolean chkCamera){
        this.guiPCBAStr = guiPCBAStr;
        this.chkPCBA = chkPCBA;
        this.guiScreenStr = guiScreenStr;
        this.chkScreen = chkScreen;
        this.guiHousingStr = guiHousingStr;
        this.chkHousing = chkHousing;
        this.guiCameraStr = guiCameraStr;
        this.chkCamera = chkCamera;
    }

    public String getGuiPCBAStr(){
        return guiPCBAStr;
    }

    public boolean getChkPCBA(){
        return chkPCBA;
    }

    public String getGuiScreenStr(){
        return  guiScreenStr;
    }

    public boolean getChkScreen(){
        return chkScreen;
    }

    public String getGuiHousingStr(){
        return guiHousingStr;
    }

    public boolean getChkHousing(){
        return chkHousing;
    }
    public String getGuiCameraStr(){
        return guiCameraStr;
    }

    public boolean getChkCamera(){
        return chkCamera;
    }

    public String getPcbaVendor() {
        return pcbaVendor;
    }

    public void setPcbaVendor(String pcbaVendor) {
        this.pcbaVendor = pcbaVendor;
    }

    public String getPcbaPLM() {
        return pcbaPLM;
    }

    public void setPcbaPLM(String pcbaPLM) {
        this.pcbaPLM = pcbaPLM;
    }

    public String getPcbaRev() {
        return pcbaRev;
    }

    public void setPcbaRev(String pcbaRev) {
        this.pcbaRev = pcbaRev;
    }

    public String getPcbPLM() {
        return pcbPLM;
    }

    public void setPcbPLM(String pcbPLM) {
        this.pcbPLM = pcbPLM;
    }

    public String getPcbRev() {
        return pcbRev;
    }

    public void setPcbRev(String pcbRev) {
        this.pcbRev = pcbRev;
    }

    public String getScreenVendor() {
        return screenVendor;
    }

    public void setScreenVendor(String screenVendor) {
        this.screenVendor = screenVendor;
    }

    public String getScreenPLM() {
        return screenPLM;
    }

    public void setScreenPLM(String screenPLM) {
        this.screenPLM = screenPLM;
    }

    public String getScreenRev() {
        return screenRev;
    }

    public void setScreenRev(String screenRev) {
        this.screenRev = screenRev;
    }

    public String getHousingVendor() {
        return housingVendor;
    }

    public void setHousingVendor(String housingVendor) {
        this.housingVendor = housingVendor;
    }

    public String getHousingPLM() {
        return housingPLM;
    }

    public void setHousingPLM(String housingPLM) {
        this.housingPLM = housingPLM;
    }

    public String getHousingRev() {
        return housingRev;
    }

    public void setHousingRev(String housingRev) {
        this.housingRev = housingRev;
    }

    public String getCameraVendor() {
        return cameraVendor;
    }

    public void setCameraVendor(String cameraVendor) {
        this.cameraVendor = cameraVendor;
    }

    public String getCameraPLM() {
        return cameraPLM;
    }

    public void setCameraPLM(String cameraPLM) {
        this.cameraPLM = cameraPLM;
    }

    public String getCameraRev() {
        return cameraRev;
    }

    public void setCameraRev(String cameraRev) {
        this.cameraRev = cameraRev;
    }

    public int getAssemblySerial() {
        return assemblySerial;
    }

    public void setAssemblySerial(int assemblySerial) {
        this.assemblySerial = assemblySerial;
    }

    public int getPcbaSerial() {
        return pcbaSerial;
    }

    public void setPcbaSerial(int pcbaSerial) {
        this.pcbaSerial = pcbaSerial;
    }

    public int getScreenSerial() {
        return screenSerial;
    }

    public void setScreenSerial(int screenSerial) {
        this.screenSerial = screenSerial;
    }

    public int getHousingSerial() {
        return housingSerial;
    }

    public void setHousingSerial(int housingSerial) {
        this.housingSerial = housingSerial;
    }

    public int getCameraSerial() {
        return cameraSerial;
    }

    public void setCameraSerial(int cameraSerial) {
        this.cameraSerial = cameraSerial;
    }

    public double getLeakTestValue() {
        return leakTestValue;
    }

    public void setLeakTestValue(double leakTestValue) {
        this.leakTestValue = leakTestValue;
    }

    public boolean isLeakTestPass() {
        return leakTestPass;
    }

    public void setLeakTestPass(boolean leakTestPass) {
        this.leakTestPass = leakTestPass;
    }

    public boolean isTestAppPass() {
        return testAppPass;
    }

    public void setTestAppPass(boolean testAppPass) {
        this.testAppPass = testAppPass;
    }

    public boolean isReadyForShipping() {
        return readyForShipping;
    }

    public void setReadyForShipping(boolean readyForShipping) {
        this.readyForShipping = readyForShipping;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getWrPartNumber() {
        return wrPartNumber;
    }

    public void setWrPartNumber(String wrPartNumber) {
        this.wrPartNumber = wrPartNumber;
    }


    public void setAssemblyPLM(String assemblyPLM) {
        this.assemblyPLM = assemblyPLM;
    }

    public String getAssemblyRev() {
        return assemblyRev;
    }

    public void setAssemblyRev(String assemblyRev) {
        this.assemblyRev = assemblyRev;
    }

}
