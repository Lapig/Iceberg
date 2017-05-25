package xyz.lapig.iceberg.handlers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import xyz.lapig.iceberg.RestClient;

public class LastFMContainer{

    public enum State { INIT, FAILURE, UNPARSED, PARSED}

    private JSONObject rootJSON;
    private int limit;
    private String url, type, user, key;
    private String fetch_type, sub_key;
	private Spanned formattedOut;
    private State state;
    private static Context mainContext;

    public LastFMContainer(String type, String user, String key, Context c){
        this.user=user; this.type=type; this.key = key;
        mainContext=c;
        limit=20;
        state=State.INIT;
        url="http://ws.audioscrobbler.com/2.0/?method="+type+"&user="+user+"&api_key="+key+"&format=json&limit="+Integer.toString(limit);
        
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
    }
    
    @Override
    public Spanned doInBackground(Void... v){
        if(state==State.PARSED)
            return formattedOut;
        else{
            Intent i = new Intent("android.intent.action.DATA_UPDATE").putExtra("htmlResponse", Html.fromHtml("Updating.."));
		    mainContext.sendBroadcast(i);
        }
        try {
            RestClient.getSync(url, new JsonHttpResponseHandler(){
                @Override
                public void onFailure(int i, Header[] headers, Throwable throwable, JSONObject j) {
                    state=State.FAILURE;
				}
                @Override
                public void onFailure(int i, Header[] h, String s, Throwable t){
                    state=State.FAILURE;
                }
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    rootJSON=response;
                    state=State.UNPARSED;
                } 
            });
        }
        catch(Exception e){
            System.err.println(e.toString());
        }
        if(state==State.FAILURE)
            return (Html.fromHtml("<b>No Connection !</b>"));

		return toSpanned();
    }
    @Override
    public void onProgressUpdate(Integer... progress) {
    }

    @Override
    public void onPostExecute(Spanned result){
        Intent i = new Intent("android.intent.action.DATA_UPDATE").putExtra("htmlResponse", result);
		mainContext.sendBroadcast(i);
    }

	
    public void setUser(String s){
        if(user.equals(s))
            return;
        user=s;
        url="http://ws.audioscrobbler.com/2.0/?method="+type+"&user="+user+"&api_key="+key+"&format=json&limit="+Integer.toString(limit);

        formattedOut=Html.fromHtml("");
    }
    public String getState(){
        switch(state){
            case INIT:
                return "INIT";
            case FAILURE:
                return "FAILURE";
            case UNPARSED:
                return "UNPARSED";
            case PARSED:
                return "PARSED";
            default:
                return "SEND HELP";
        }
    }
    public boolean isEmpty(){
        return formattedOut.length()==0;
    }
    public void clear(){
        formattedOut=Html.fromHtml("");
        state=State.INIT;
    }

    //really cant explain this one
    public Spanned toSpanned(){
            return toFormattedString();
    }
    @TargetApi(24)
	public Spanned toFormattedString(){
        if(state==State.FAILURE)
            return Html.fromHtml("<b>No Connection !</b>");
        else if (state==State.PARSED)
            return formattedOut;
		String curr=""; 
        StringBuilder fullText = new StringBuilder();
	    try {
		   JSONArray arr = rootJSON.getJSONObject(fetch_type).getJSONArray(sub_key);
		   for (int i = 0; i < arr.length(); i++) {
			   curr = "<b>"+arr.getJSONObject(i).getString("name")+"</b>";
			   curr += "<br /><small>" + arr.getJSONObject(i).getJSONObject("artist").getString("#text")+"</small>";

			   fullText.append(i + 1).append(".  ").append(curr).append("<br>");
		   }
		   formattedOut=Html.fromHtml(fullText.toString());
	    }
	    catch(Exception e){
		   return toFormattedAlbumString();
	    }
        state=State.PARSED;
        return formattedOut;
	}
    @TargetApi(24)
	public Spanned toFormattedAlbumString(){
		String curr=""; StringBuilder fullText = new StringBuilder();
		try {
			JSONArray arr = rootJSON.getJSONObject(fetch_type).getJSONArray(sub_key);
			for (int i = 0; i < arr.length(); i++) {
				curr = "<b>" + arr.getJSONObject(i).getString("name")+"</b> - <small>"+arr.getJSONObject(i).getString("playcount")+"</small>";
				fullText.append(curr).append("<br>");
			}
			formattedOut=Html.fromHtml(fullText.toString());
		}
		catch(Exception e){
			System.err.println("JSON parse error");
		}
        state=State.PARSED;
        return formattedOut;
    }
}
