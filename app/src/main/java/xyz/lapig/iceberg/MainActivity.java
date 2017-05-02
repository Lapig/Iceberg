package xyz.lapig.iceberg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                try {
                    Snackbar.make(view, "Attempting", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    RestClient.get("", null, new JsonHttpResponseHandler(){
                        @Override
                        public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                            Snackbar.make(view, "FAILURE", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        }

                        @Override
                        public void onSuccess(int i, Header[] headers, JSONArray  response) {
                            Snackbar.make(view, "SUCCESS", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            ((TextView)findViewById(R.id.textView)).setText(response.toString());
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                            Snackbar.make(view, "SUCCESS", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            //((TextView)findViewById(R.id.textView)).setText(response.toString());

                            String curr="";
                            String fullText="";
                            JSONArray arr;
                            try {
                                arr=response.getJSONObject("recenttracks").getJSONArray("track");
                                for (int i = 0; i < arr.length(); i++){
                                    curr = arr.getJSONObject(i).getJSONObject("artist").getString("#text");
                                    curr+="\n" + arr.getJSONObject(i).getString("name");
                                    fullText=(arr.length()-i) + "." + curr + "\n\n"+fullText;

                                    //response.remove("track");
                                }
                                ((TextView)findViewById(R.id.textView)).setText(fullText);
                            }
                            catch(Exception e){
                               ((TextView)findViewById(R.id.textView)).setText("exception hell yeah");
                            }
                        }
                    });
                }
                catch(Exception e){

                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("text", "@app_name");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
