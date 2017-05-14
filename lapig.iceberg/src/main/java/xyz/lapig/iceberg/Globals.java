package xyz.lapig.iceberg;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lapig on 5/6/2017.
 */

public class Globals extends Application{
    private static String widgetText;
    private static String user;
    private static boolean htmlAvailable;

    public static boolean getHtmlAvailable(Context c) {
        SharedPreferences sharedPref = c.getSharedPreferences("MainActivity",c.MODE_PRIVATE);//stillbad
        return sharedPref.getBoolean("htmlAvailable", false);
    }
    public static void setHtmlAvailable(Context c,boolean htmlAvailable) {
        Globals.htmlAvailable = htmlAvailable;
        SharedPreferences sharedPref = c.getSharedPreferences("MainActivity",c.MODE_PRIVATE);//bad
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("htmlAvailable", htmlAvailable);
        editor.apply();
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
