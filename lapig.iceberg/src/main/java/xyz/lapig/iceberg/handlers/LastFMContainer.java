package xyz.lapig.iceberg.handlers;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import xyz.lapig.iceberg.RestClient;

public class LastFMContainer {
    private JSONObject rootJSON;
    private JSONArray rootJSONarr;
    private String str;
    private String url;
    private String fetch_type, sub_key;
	public LastFMContainer(JSONObject base, String uri){
        rootJSON=base;
        str="";
        url+=uri;
        fetch_type="recenttracks";  sub_key="track";
    }
    public LastFMContainer(String type, String user, String key){
        url="http://ws.audioscrobbler.com/2.0/?method="+type+"&user="+user+"&api_key="+key+"&format=json";
        str="";
        if(type.equals("user.gettopalbums")){
            fetch_type="topalbums"; sub_key="album";
        }
        else if(type.equals("user.getrecenttracks")){
            fetch_type="recenttracks";  sub_key="track";
        }
        else{
            fetch_type="error"; sub_key="error";
        }
        try {
            RestClient.get(url, null, new JsonHttpResponseHandler(){
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                }
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, JSONObject j) {
                }
                @Override
                public void onSuccess(int i, Header[] headers, JSONArray response) {
                    rootJSONarr=response;
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    rootJSON=response;
                }
            });
        }
        catch(Exception e){
            str="failure";
            System.err.println(e.toString());
        }
    }
    public String toRawString(){
        return rootJSON.toString();
    }
    public String toString(){
        if(str.equals("") || str.equals("obj")){
            String curr=""; String fullText = "";
           try {
               JSONArray arr = rootJSON.getJSONObject(fetch_type).getJSONArray(sub_key);
               for (int i = 0; i < arr.length(); i++) {
                   curr = arr.getJSONObject(i).getJSONObject("artist").getString("#text");
                   curr += "\n" + arr.getJSONObject(i).getString("name");
                   fullText += (i+1) + "." + curr + "\n\n";
               }
               str=fullText;
           }
           catch(Exception e){
               System.err.println("JSON parse error");
               return toAlbumsString();
           }
        }
        return str;
    }
    public String toAlbumsString(){
        if(str.equals("") || str.equals("obj")){
            String curr=""; String fullText = "";
            try {
                JSONArray arr = rootJSON.getJSONObject(fetch_type).getJSONArray(sub_key);
                for (int i = 0; i < arr.length(); i++) {
                    curr = arr.getJSONObject(i).getString("name")+"  "+arr.getJSONObject(i).getString("playcount");
                    fullText += curr+"\n";
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
