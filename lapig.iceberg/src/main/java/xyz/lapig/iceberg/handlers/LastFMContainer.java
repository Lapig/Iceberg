package xyz.lapig.iceberg.handlers;

import android.text.Html;
import android.text.Spanned;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Callable;

import cz.msebera.android.httpclient.Header;
import xyz.lapig.iceberg.RestClient;

public class LastFMContainer implements Callable {
    private JSONObject rootJSON;
    private String parsed;
    private int limit=20;
    private String url, type, user, key;
    private String fetch_type, sub_key;
	private Spanned formattedOut;

    public LastFMContainer(String type, String user, String key){
        url="http://ws.audioscrobbler.com/2.0/?method="+type+"&user="+user+"&api_key="+key+"&format=json&limit="+Integer.toString(limit);
        this.user=user; this.type=type; this.key = key;
        parsed="";
        switch (type) {
            case "user.gettopalbums":
                fetch_type = "topalbums";
                sub_key = "album";
                break;
            case "user.getrecenttracks":
                fetch_type = "recenttracks";
                sub_key = "track";
                break;
            case "user.gettopartists":
                fetch_type = "topartists";
                sub_key = "artist";
                break;
            default:
                return;
        }
        formattedOut=Html.fromHtml("");
        updateBackground();
    }
    
	
	@Override
	public Spanned call() {
		if(formattedOut.length()>0)
            return formattedOut;
		parsed="Update in progress";
        try {
            RestClient.getSync(url, new JsonHttpResponseHandler(){
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, JSONObject j) {
                	parsed="error";
				}
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    rootJSON=response;
                }
            });
        }
        catch(Exception e){
            System.err.println(e.toString());
        }
		return toFormattedString();
	}
    public void setUser(String s){
        if(user.equals(s))
            return;
        user=s;
        url="http://ws.audioscrobbler.com/2.0/?method="+type+"&user="+user+"&api_key="+key+"&format=json&limit=20";
        formattedOut=Html.fromHtml("");
        parsed="";
    }
	public void updateBackground(){
        parsed="Update in progress";
        try {
            RestClient.get(url, new JsonHttpResponseHandler(){
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, JSONObject j) {
                	parsed="error";
				}
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    rootJSON=response;
                }
            });
        }
        catch(Exception e){
            System.err.println(e.toString());
        }
    }
	
	public Spanned toFormattedString(){
        if(formattedOut.length()>0)
            return formattedOut;
		String curr=""; StringBuilder fullText = new StringBuilder();
	    try {
		   JSONArray arr = rootJSON.getJSONObject(fetch_type).getJSONArray(sub_key);
		   for (int i = 0; i < arr.length(); i++) {
			   curr = "<b>"+arr.getJSONObject(i).getString("name")+"</b>";
			   curr += "<br /><small>" + arr.getJSONObject(i).getJSONObject("artist").getString("#text")+"</small>";

			   fullText.append(i + 1).append(".  ").append(curr).append("<br>");
		   }
		   formattedOut=Html.fromHtml(fullText.toString(), Html.FROM_HTML_OPTION_USE_CSS_COLORS);
	    }
	    catch(Exception e){
		   return toFormattedAlbumString();
	    }
        return formattedOut;
	}
	public Spanned toFormattedAlbumString(){
		String curr=""; StringBuilder fullText = new StringBuilder();
		try {
			JSONArray arr = rootJSON.getJSONObject(fetch_type).getJSONArray(sub_key);
			for (int i = 0; i < arr.length(); i++) {
				curr = "<b>" + arr.getJSONObject(i).getString("name")+"</b> - <small>"+arr.getJSONObject(i).getString("playcount")+"</small>";
				fullText.append(curr).append("<br>");
			}
			formattedOut=Html.fromHtml(fullText.toString(), Html.FROM_HTML_OPTION_USE_CSS_COLORS);
		}
		catch(Exception e){
			System.err.println("JSON parse error");
		}
        return formattedOut;
    }
	
    public boolean isEmpty(){
            return formattedOut.length()==0;
    }
    public void clear(){
        parsed="";
        formattedOut=Html.fromHtml("");
    }
    public String toString(){
        if(parsed.length()>5){
            String curr=""; StringBuilder fullText = new StringBuilder();
           try {
               JSONArray arr = rootJSON.getJSONObject(fetch_type).getJSONArray(sub_key);
               for (int i = 0; i < arr.length(); i++) {
                   curr = arr.getJSONObject(i).getJSONObject("artist").getString("#text");
                   curr += "\n" + arr.getJSONObject(i).getString("name");
                   fullText.append(i + 1).append(".").append(curr).append("\n");
               }
               parsed= fullText.toString();
           }
           catch(Exception e){
               return toAlbumsString();
           }
        }
        return parsed;
    }
    public String toAlbumsString(){
        if(parsed.length()>5){
            String curr=""; StringBuilder fullText = new StringBuilder();
            try {
                JSONArray arr = rootJSON.getJSONObject(fetch_type).getJSONArray(sub_key);
                for (int i = 0; i < arr.length(); i++) {
                    curr = arr.getJSONObject(i).getString("name")+"  "+arr.getJSONObject(i).getString("playcount");
                    fullText.append(curr).append("\n");
                }
                parsed= fullText.toString();
            }
            catch(Exception e){
                System.err.println("JSON parse error");
            }
        }
        return parsed;
    }

    public String toRawString(){
        return rootJSON.toString();
    }
}
