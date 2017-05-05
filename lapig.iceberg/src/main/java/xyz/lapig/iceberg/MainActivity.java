package xyz.lapig.iceberg;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import xyz.lapig.iceberg.handlers.LastFMContainer;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout homeView;
    private HashMap<String, LastFMContainer> parsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        homeView = (CoordinatorLayout) findViewById(R.id.home_view);
        parsers=new HashMap<>();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            try {
                Snackbar.make(view, "Attempting", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                RestClient.get("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=lapigr&api_key="+getString(R.string.api_key)+"&format=json&limit=30\"", null, new JsonHttpResponseHandler(){
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        Snackbar.make(view, "FAILURE", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, Throwable throwable, JSONObject j) {
                        Snackbar.make(view, "FAILURE", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, JSONArray response) {
                        Snackbar.make(view, "unexpected jsonarray response", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        ((TextView)findViewById(R.id.textView)).setText(response.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                        Snackbar.make(view, "SUCCESS", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        LastFMContainer recent=new LastFMContainer(response, "http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=lapigr&api_key="+getString(R.string.api_key)+"&format=json&limit=30");
                        parsers.put("recentTracks", recent);
                        ((TextView) findViewById(R.id.textView)).setText(recent.toString());
                    }
                });
            }
            catch(Exception e){
            }
        }});

        final TabLayout tab_holder= (TabLayout) findViewById(R.id.tab_bar);
        tab_holder.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        snackAttack("one");
                        break;
                    case 1:
                        snackAttack("two");
                        break;
                    case 2:
                        snackAttack("three");
                        break;
                    default:
                        snackAttack("default");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
    public void snackAttack(String msg){
         Snackbar.make(homeView, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
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

            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtra("text", "@app_name");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
