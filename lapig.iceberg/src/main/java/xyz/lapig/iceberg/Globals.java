package xyz.lapig.iceberg;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by Lapig on 5/6/2017.
 */

public class Globals extends Application{
    private static String widgetText;
    private static String user;
    private static String limit;

    private static HashMap<String, String> globalStore = new HashMap<String, String>();

    private static boolean updateNeeded=false;

    public static void saveValue(String k, String v){
        globalStore.put(k.toLowerCase(),v.toLowerCase());
    }

    public static boolean isUpdateNeeded() {
        return updateNeeded;
    }
    public static void setUpdateNeeded(boolean updateNeeded) {
        Globals.updateNeeded = updateNeeded;
    }

    public static String getLimit() {
        return limit;
    }
    public static void setLimit(String limit) {
        Globals.limit = limit;
    }

    public static String getUser() {
        return user;
    }
    public static void setUser(String user) {
        Globals.user = user;
    }

    public static String getWidgetText(){
        return widgetText;
    }
    public static void setWidgetText(String s){
        widgetText=s;
    }
}
