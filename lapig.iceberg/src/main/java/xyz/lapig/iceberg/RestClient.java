package xyz.lapig.iceberg;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

/**
 * Created by Lapig on 4/23/2017.
 */

public class RestClient {
    //private static final String BASE_URL = "http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=lapigr&api_key="+""+"&format=json&limit=30";

    private static final AsyncHttpClient client = new AsyncHttpClient();
    private static final SyncHttpClient syncClient = new SyncHttpClient();

    public static void get(String url, AsyncHttpResponseHandler responseHandler) {
        client.get(url, null, responseHandler);
    }

    public static void getSync(String url, AsyncHttpResponseHandler responseHandler) {
        syncClient.get(url, null, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

}
