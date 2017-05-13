package xyz.lapig.iceberg;

import android.app.Application;

/**
 * Created by Lapig on 5/6/2017.
 */

public class Globals extends Application{
    private static String widgetText;

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        Globals.user = user;
    }

    private static String user;
    public static String getWidgetText(){
        return widgetText;
    }
    public static void setWidgetText(String s){
        widgetText=s;
    }
}
