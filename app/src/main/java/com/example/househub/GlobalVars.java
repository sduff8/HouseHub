package com.example.househub;

import android.app.Application;

public class GlobalVars extends Application {

    private static String familyNameId;
    private static Boolean checkIfInFamily;

    public static String getFamilyNameId(){
        return familyNameId;
    }

    public static void setFamilyNameId(String familyNameId) {
        GlobalVars.familyNameId = familyNameId;
    }

    public static Boolean getCheckIfInFamily() {
        return checkIfInFamily;
    }

    public static void setCheckIfInFamily(Boolean checkIfInFamily) {
        GlobalVars.checkIfInFamily = checkIfInFamily;
    }
}
