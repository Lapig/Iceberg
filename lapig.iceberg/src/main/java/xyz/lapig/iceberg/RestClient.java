package xyz.lapig.iceberg;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Lapig on 4/23/2017.
 */

public class RestClient {
    //private static final String BASE_URL = "http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=lapigr&api_key="+""+"&format=json&limit=30";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

};
