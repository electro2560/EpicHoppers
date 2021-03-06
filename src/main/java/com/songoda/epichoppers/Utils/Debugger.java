package com.songoda.epichoppers.Utils;

import com.songoda.epichoppers.EpicHoppers;

/**
 * Created by songoda on 3/21/2017.
 */
public class Debugger {


    public static void runReport(Exception e) {
        if (isDebug()) {
            System.out.println("==============================================================");
            System.out.println("The following is an error encountered in EpicHoppers.");
            System.out.println("--------------------------------------------------------------");
            e.printStackTrace();
            System.out.println("==============================================================");
        }
        sendReport(e);
    }

    public static void sendReport(Exception e) {

    }

    public static boolean isDebug() {
        EpicHoppers instance = EpicHoppers.getInstance();
        return instance.getConfig().getBoolean("System.Debugger Enabled");
    }

}
