package xyz.lapig.iceberg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                        viewUpdate(recent);
                        activeTab=0;
                        break;
                    case 1:
                        viewUpdate(albums);
                        activeTab=1;
                        break;
                    case 2:
                        viewUpdate(artists);
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
                        viewUpdate(recent);
                        activeTab=0;
                        break;
                    case 1:
                        homeView.setText("Updating..");
                        viewUpdate(albums);
                        activeTab=1;
                        break;
                    case 2:
                        homeView.setText("Updating..");
                        viewUpdate(artists);
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
        String s=Html.toHtml(target.getFormattedOut());

        intent.putExtra("updatedWidgetText", s); //haha epic
        this.sendBroadcast(intent);
    }

	public void viewUpdate(final LastFMContainer target)
	{
        new SimplyEpic(target).execute(); 
    }
    static class SimplyEpic extends AsyncTask<Void,Integer,Spanned> {
        private LastFMContainer node;
        public SimplyEpic(LastFMContainer fm){
            node=fm;
        }
        @Override
        public Spanned doInBackground(Void... v){
            return node.doInBackground();
        }
        @Override
        public void onProgressUpdate(Integer... progress) {
        }

        @Override
        public void onPostExecute(Spanned result){
            node.onPostExecute(result);
        }
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
				Spanned responseMsg = (Spanned)(intent.getExtras().get("htmlResponse"));
				homeView.setText((responseMsg));
				widgetUpdate(recent);
			}
		};
        this.registerReceiver(mReceiver, intentFilter);

        snackAttack("on resume - user: "+Globals.getUser());

        if(Globals.isUpdateNeeded()){
            updateContainers();
        }

		try{
		switch(activeTab){
            case 0:
                viewUpdate(recent);
                break;
            case 1:
                viewUpdate(albums);
                break;
            case 2:
                viewUpdate(artists);
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
        editor.apply();
    }
    ///utils
    private void updateContainers(){
        for(LastFMContainer l : lastFMLookups){
            l.updateUrl();
            l.clear();
        }
    }
    private LastFMContainer activeContainer(){
        return lastFMLookups[activeTab];
    }
    public void snackAttack(String msg){
        Snackbar.make(homeLayout, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
    }
    /////
    
}
