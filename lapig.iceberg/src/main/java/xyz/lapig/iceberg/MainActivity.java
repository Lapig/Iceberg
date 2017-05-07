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

import xyz.lapig.iceberg.handlers.LastFMContainer;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout homeView;
    //private HashMap<String, LastFMContainer> parsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        homeView = (CoordinatorLayout) findViewById(R.id.home_view);
        //parsers=new HashMap<>();

        final LastFMContainer recent=new LastFMContainer(getString(R.string.recent),"lapigr",getString(R.string.api_key));
        final LastFMContainer albums=new LastFMContainer(getString(R.string.albums),"lapigr",getString(R.string.api_key));
        final LastFMContainer artists=new LastFMContainer(getString(R.string.artists),"lapigr",getString(R.string.api_key));


        //parsers.put("recentTracks", recent);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                snackAttack("Attempting");
                //((TextView)findViewById(R.id.textView)).setText(recent.toString());
            }}
        );

        final TabLayout tab_holder= (TabLayout) findViewById(R.id.tab_bar);
        tab_holder.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        ((TextView)findViewById(R.id.textView)).setText(recent.toString());
                        Globals.setWidgetText(recent.toString());
                        Intent intent = new Intent(IcebergWidget.ACTION_TEXT_CHANGED);
                        intent.putExtra("NewString", recent.toString());
                        getApplicationContext().sendBroadcast(intent);
                        break;
                    case 1:
                        ((TextView)findViewById(R.id.textView)).setText(albums.toString());
                        break;
                    case 2:
                        ((TextView)findViewById(R.id.textView)).setText(artists.toString());
                        break;
                    default:
                        snackAttack("default");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        ((TextView)findViewById(R.id.textView)).setText(recent.toString());
                        break;
                    case 1:
                        ((TextView)findViewById(R.id.textView)).setText(albums.toString());
                        break;
                    case 2:
                        ((TextView)findViewById(R.id.textView)).setText(artists.toString());
                        break;
                    default:
                        snackAttack("default");
                        break;
                }
            }
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
