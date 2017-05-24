package xyz.lapig.iceberg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import xyz.lapig.iceberg.handlers.LastFMContainer;

import static xyz.lapig.iceberg.IcebergWidget.ACTION_TEXT_CHANGED;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout homeLayout;
    private int activeTab;
    private LastFMContainer recent;
    private LastFMContainer albums;
    private LastFMContainer artists;
    private LastFMContainer lastFMLookups[] = new LastFMContainer[3];
    private String user="";
    private TextView homeView;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        homeLayout = (CoordinatorLayout) findViewById(R.id.home_view);
        homeView = (TextView)findViewById(R.id.textView);
        
        activeTab=-1;

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                snackAttack("Clearing stored data");
                recent.clear();
                albums.clear();
                artists.clear();
            }}
        );

        final TabLayout tab_holder= (TabLayout) findViewById(R.id.tab_bar);
        tab_holder.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
			  try{
                switch(tab.getPosition()){
                    case 0:
                        homeView.setText("Updating..");
                        viewUpdateAsync(recent);
         //               widgetUpdate(recent);
                        activeTab=0;
                        break;
                    case 1:
                        homeView.setText("Updating..");
                        viewUpdateAsync(albums);
                        activeTab=1;
                        break;
                    case 2:
                        homeView.setText("Updating..");
                        viewUpdateAsync(artists);
                        activeTab=2;
                        break;
                    default:
                        break;
                }
			  }
			  catch(Exception e){
				  e.printStackTrace();
			  }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try{
                switch(tab.getPosition()){
                    case 0:
                        homeView.setText("Updating..");
                        viewUpdateAsync(recent);
                        activeTab=0;
                        break;
                    case 1:
                        homeView.setText("Updating..");
                        viewUpdateAsync(albums);
                        activeTab=1;
                        break;
                    case 2:
                        homeView.setText("Updating..");
                        viewUpdateAsync(artists);
                        activeTab=2;
                        break;
                    default:
                        snackAttack("Invalid tab");
                        break;
                }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        user = sharedPref.getString(getString(R.string.user), "lapigr");
        Globals.setUser(user);

        recent=new LastFMContainer(getString(R.string.recent),user,getString(R.string.api_key), getApplicationContext()); lastFMLookups[0]=recent;
        albums=new LastFMContainer(getString(R.string.albums),user,getString(R.string.api_key), getApplicationContext()); lastFMLookups[1]=albums;
        artists=new LastFMContainer(getString(R.string.artists),user,getString(R.string.api_key), getApplicationContext()); lastFMLookups[2]=artists;
       
        if(homeView.getText().length()==0){
            homeView.setText("Empty cache, select a tab or reselect active tab");
        }
        else{
            homeView.setText(homeView.getText().length());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void widgetUpdate(LastFMContainer target){
        Intent intent = new Intent(ACTION_TEXT_CHANGED);
        intent.putExtra("updatedWidgetText", Html.toHtml(target.toSpanned())); //haha epic
        this.sendBroadcast(intent);
    }

	public void viewUpdateAsync(final LastFMContainer target)
	{
        target.execute();    
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onPause() {
        super.onPause(); 
        this.unregisterReceiver(this.mReceiver);
    }
    @Override
    public void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("android.intent.action.DATA_UPDATE");
        mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				//extract our message from intent
				String responseMsg = intent.getStringExtra("htmlResponse");
				//log our message value
				homeView.setText(Html.fromHtml(responseMsg));
 
			}
		};
        this.registerReceiver(mReceiver, intentFilter);

        snackAttack("on resume - user: "+Globals.getUser());
        if(!user.equals(Globals.getUser())){
            user=Globals.getUser();
            updateContainers(user);
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.user), user);
            editor.commit();
        }
		try{
		switch(activeTab){
            case 0:
                viewUpdateAsync(recent);
                break;
            case 1:
                viewUpdateAsync(albums);
                break;
            case 2:
                viewUpdateAsync(artists);
                break;
            default:
                activeTab=0;
				break;
        }
		}
		catch(Exception e){
	        e.printStackTrace();
			snackAttack("Interruption error");
		}
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.user), user);
        editor.commit();
    }
    ///utils
    private void updateContainers(String s){
        for(LastFMContainer l : lastFMLookups){
            l.setUser(s);
        }
    }
    public void snackAttack(String msg){
        Snackbar.make(homeLayout, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
    /////
    
}
