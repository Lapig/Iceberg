package xyz.lapig.iceberg;

/**
 * Created by Lapig on 5/6/2017.
 */

class Globals {
    public static String widgetText;
    public static String getWidgetText(){
        return widgetText;
    }
    public static void setWidgetText(String s){
   /*     long startTime = System.nanoTime();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        */
        widgetText=s;
    }
}
