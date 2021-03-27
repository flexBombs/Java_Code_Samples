package com.persistentsystems.DisplayBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DataParser {
    public static final Logger logger = Logger.getLogger(DataParser.class.getName());
    private static final String TT_IDENTIFIER = "TT";
    private static final int TT_SERIAL_LEN = 6;
    private static final String E3_IDENTIFIER = "9550023901";
    private static final String FINAL_LABEL_IDENTIFIER = "[)>0618S4YBU2";

    public static String createFullSerial(String vendor, String PLM, String rev){
        if ((vendor == null || vendor.isEmpty()) &&
                (PLM != null && !PLM.isEmpty()) &&
                (rev == null || rev.isEmpty())) {
            return PLM;
        }

        if ((vendor != null && !vendor.isEmpty()) &&
                (PLM != null || !PLM.isEmpty()) &&
                (rev != null || !rev.isEmpty())){
            return vendor + "-" + PLM + "-" + rev;
        }
        return "err";
    }

    public static Object[] parseFullPartNum(String str, boolean psLabel){
        int firstDelimiterIndex = 0;
        int secondDelimiterIndex = 0;
        int thirdDelimiterIndex = 0;

        String vendorCode = "";
        String plmNumber = "";
        String rev = "";
        String subStrPLM = "";
        String subStrRev = "";
        String subStrSerial = "";
        int serial = 0;

        try {
            str = str.trim();
            firstDelimiterIndex = str.indexOf("-");
            vendorCode = str.substring(0, firstDelimiterIndex);
            subStrPLM = str.substring(firstDelimiterIndex + 1);
            secondDelimiterIndex = subStrPLM.indexOf("-");
            plmNumber = subStrPLM.substring(0,secondDelimiterIndex);
            subStrRev = subStrPLM.substring(secondDelimiterIndex + 1);

            if(vendorCode.length() != 3 || plmNumber.length() != 6 || subStrRev.length() <1) {
                return new Object[]{false};
            }else {
                if (!psLabel) {
                    thirdDelimiterIndex = subStrRev.indexOf("-");
                    rev = subStrRev.substring(0, thirdDelimiterIndex);

                    if (rev.length() < 1 || thirdDelimiterIndex == -1) {
                        return new Object[]{false};
                    }else {
                        subStrSerial = subStrRev.substring(thirdDelimiterIndex + 1);
                        serial = Integer.parseInt(subStrSerial);

                        return (subStrSerial.length() < 1) ? new Object[]{false} : new Object[]{true, vendorCode, plmNumber, rev, serial};
                    }
                } else {
                    return (subStrRev.length() < 1) ? new Object[]{false} : new Object[]{true, vendorCode, plmNumber, subStrRev, 0};
                }
            }
        }catch(Exception e){
            logger.log(Level.SEVERE, "Failed to validate format full part number in String: " + str);
            e.printStackTrace();
            return new Object [] {false};
        }
    }

    public static Object[] parseSpecialPCBA_RCE$(String str, boolean psLabel){
        int firstDelimiterIndex = 0;
        int secondDelimiterIndex = 0;
        int thirdDelimiterIndex = 0;

        String vendorCode = "";
        String plmNumber = "";
        String rev = "";
        String subStrPLM = "";
        String subStrRev = "";
        String subStrSerial = "";
        int serial = 0;

        String firstDelimiter = "$";
        String secondDelimiter = "-$";
        int firstDelimiterLen = firstDelimiter.length();
        int secondDelimiterLen = secondDelimiter.length();

        try {
            str = str.trim();
            firstDelimiterIndex = str.indexOf(firstDelimiter);
            vendorCode = str.substring(0, firstDelimiterIndex);
            subStrPLM = str.substring(firstDelimiterIndex + firstDelimiterLen);
            secondDelimiterIndex = subStrPLM.indexOf(secondDelimiter);
            plmNumber = subStrPLM.substring(0,secondDelimiterIndex);
            subStrRev = subStrPLM.substring(secondDelimiterIndex + secondDelimiterLen);

            if(vendorCode.length() != 3 || plmNumber.length() != 6 || subStrRev.length() <1) {
                return new Object[]{false};
            }else {
                if (!psLabel) {
                    thirdDelimiterIndex = subStrRev.indexOf(secondDelimiter);
                    rev = subStrRev.substring(0, thirdDelimiterIndex);

                    if (rev.length() < 1 || thirdDelimiterIndex == -1)
                        return new Object[]{false};

                    subStrSerial = subStrRev.substring(thirdDelimiterIndex + secondDelimiterLen);
                    serial = Integer.parseInt(subStrSerial);

                    if (subStrSerial.length() < 1)
                        return new Object[]{false};

                    return new Object[]{true, vendorCode, plmNumber, rev, serial};
                } else {
                    return (subStrRev.length() < 1) ? new Object[]{false} : new Object[]{true, vendorCode, plmNumber, subStrRev, 0};
                }
            }
        }catch(Exception e){
            logger.log(Level.SEVERE, "Failed to validate format: special PCBA RCE$: " + str);
            e.printStackTrace();;
            return new Object [] {false};
        }
    }

    public static Object[] parseSpecialScreen_TT(String str, boolean psLabel){
        String firstTwo = "";
        String plmNum = "";
        String subStrSerial = "";
        int serial = 0;

        if(! psLabel){
            try {
                str = str.trim();
                firstTwo = str.substring(0, 2);
                if (firstTwo.equals(TT_IDENTIFIER)) {
                    plmNum = str.substring(0,str.length() - TT_SERIAL_LEN);
                    subStrSerial = str.substring(str.length() - TT_SERIAL_LEN);
                    serial = Integer.parseInt(subStrSerial);

                    return new Object[] {true, null, plmNum, null, serial};
                }else {
                    return new Object [] {false};
                }
            }catch(Exception e){
                logger.log(Level.SEVERE, "Failed to validate format: special screen TT: " + str);
                e.printStackTrace();
                return new Object [] {false};
            }
        }else{
            return new Object [] {false};
        }
    }

    public static Object[] parseSpecialScreen_E3(String str, boolean psLabel){
        int lastDelimiterIndex = 0;
        int firstDelimiterIndex = 0;
        String plmNum = "";
        String substrSerial = "";
        int serial = 0;
        boolean isCheckApproved = true;

        if (! psLabel) {
            try {
                str = str.trim();
                lastDelimiterIndex = str.lastIndexOf("-");
                firstDelimiterIndex = str.indexOf("-");
                if(isCheckApproved){
                    plmNum = str.substring(0, lastDelimiterIndex);
                }else{
                    plmNum = str.substring(0, firstDelimiterIndex);
                }
                substrSerial = str.substring(lastDelimiterIndex + 1);
                serial = Integer.parseInt(substrSerial);

                return new Object[] {true, null, plmNum, null, serial};
            }catch(Exception e){
                logger.log(Level.SEVERE, "Failed to validate format: special screen E3: " + str);
                e.printStackTrace();
                return new Object [] {false};
            }
        }else{
            return new Object [] {false};
        }
    }

    public static int getSerial(String str){
        int lastDelimiterIndex = 0;
        String substrSerial = "";
        int serial = 0;
        try {
            lastDelimiterIndex = str.lastIndexOf("-");
            if(lastDelimiterIndex != -1) {
                substrSerial = str.substring(lastDelimiterIndex + 1);
                serial = Integer.parseInt(substrSerial);
                return serial;
            }else {
                try{
                    serial = Integer.parseInt(str);
                    return serial;
                }catch (Exception e){
                    logger.log(Level.SEVERE, "Failed to convert serial to int with Data Parser");
                    e.printStackTrace();
                }
                try{
                    lastDelimiterIndex = str.lastIndexOf(FINAL_LABEL_IDENTIFIER);
                    if(lastDelimiterIndex == 0) {
                        substrSerial = str.substring(FINAL_LABEL_IDENTIFIER.length());
                        serial = Integer.parseInt(substrSerial);
                        return serial;
                    }else{
                        return 0;
                    }
                }catch(Exception e){
                    logger.log(Level.SEVERE, "Failed to convert serial to int with Data Parser");
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            logger.log(Level.SEVERE, "Failed to get serial with Data Parser");
            e.printStackTrace();
            return 0;
        }
        return 0;
    }
}
