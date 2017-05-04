package xyz.lapig.iceberg.handlers;

import org.json.JSONArray;
import org.json.JSONObject;

public class LastFMContainer {
    private JSONObject rootJSON;
    private String str, uri;
	public LastFMContainer(JSONObject base, String uri){
        rootJSON=base;
        str="";
        this.uri=uri;
    }

    public String toString(){
        if(str.equals("")){
            String curr; String fullText = "";
           try {
               JSONArray arr = rootJSON.getJSONObject("recenttracks").getJSONArray("track");
               for (int i = 0; i < arr.length(); i++) {
                   curr = arr.getJSONObject(i).getJSONObject("artist").getString("#text");
                   curr += "\n" + arr.getJSONObject(i).getString("name");
                   fullText += (i+1) + "." + curr + "\n\n";
               }
               str=fullText;
           }
           catch(Exception e){
               System.err.println("JSON parse error");
           }
        }
        return str;
    }
}
