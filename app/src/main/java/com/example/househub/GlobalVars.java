package com.example.househub;

import android.app.Application;

public class GlobalVars extends Application {

    private static String familyNameId;

    public static String getFamilyNameId(){
        return familyNameId;
    }

    public static void setFamilyNameId(String familyNameId) {
        GlobalVars.familyNameId = familyNameId;
    }
}
