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
import android.text.Html;
import android.text.Spanned;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import xyz.lapig.iceberg.handlers.LastFMContainer;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout homeView;
    private int activeTab;
    private LastFMContainer recent;
    private LastFMContainer albums;
    private LastFMContainer artists;
	private static final ExecutorService threadpool = Executors.newFixedThreadPool(3);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        homeView = (CoordinatorLayout) findViewById(R.id.home_view);
        activeTab=0;
        recent=new LastFMContainer(getString(R.string.recent),"lapigr",getString(R.string.api_key));
        albums=new LastFMContainer(getString(R.string.albums),"lapigr",getString(R.string.api_key));
        artists=new LastFMContainer(getString(R.string.artists),"lapigr",getString(R.string.api_key));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                snackAttack("Clearing stored data");
                recent.updateBackground();albums.updateBackground();artists.updateBackground();
                ((TextView)findViewById(R.id.textView)).setText(Html.fromHtml("<b>"+"Title"+"</b>" +  "<br />" + 
					"<small>" + "description" + "</small>" + "<br />" + 
					"<small>" + "DateAdded" + "</small>"+"<br /><font color='#ff0000'>COLORED</font>"));
            }}
        );

        final TabLayout tab_holder= (TabLayout) findViewById(R.id.tab_bar);
        tab_holder.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
			  try{
                switch(tab.getPosition()){
                    case 0:
                        Future<Spanned> fRecent = threadpool.submit(recent);
						while (!fRecent.isDone()) {
							Thread.sleep(500); 
						}
						Spanned responseRecent=fRecent.get();
						((TextView)findViewById(R.id.textView)).setText(responseRecent);
						
                        Intent intent = new Intent(IcebergWidget.ACTION_TEXT_CHANGED);
                        intent.putExtra("updatedWidgetText", Html.toHtml(recent.toFormattedString())); //haha epic
                        getApplicationContext().sendBroadcast(intent);
                        activeTab=0;
                        break;
                    case 1:
                        ((TextView)findViewById(R.id.textView)).setText(albums.toFormattedString());
                        activeTab=1;
                        break;
                    case 2:
                        ((TextView)findViewById(R.id.textView)).setText(artists.toFormattedString());
                        activeTab=2;
                        break;
                    default:
                        snackAttack("default");
                        break;
                }
			  }
			  catch(Exception e){
				  e.printStackTrace();
			  }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        ((TextView)findViewById(R.id.textView)).setText(recent.toFormattedString());
                        activeTab=0;
                        break;
                    case 1:
                        ((TextView)findViewById(R.id.textView)).setText(albums.toFormattedString());
                        activeTab=1;
                        break;
                    case 2:
                        ((TextView)findViewById(R.id.textView)).setText(artists.toFormattedString());
                        activeTab=2;
                        break;
                    default:
                        snackAttack("default");
                        break;
                }
            }
        });
		
		//set text for recent tracks on first launch
		//
		//starting to worry that i really have no idea what im doing with this futures business
		//	yep nvm i have no idea what im doing
		/*try{
			Future<Spanned> fRecent = threadpool.submit(recent);
			while (!fRecent.isDone()) {
				Thread.sleep(500); 
			}
			Spanned responseRecent=fRecent.get();
			((TextView)findViewById(R.id.textView)).setText(responseRecent);
		}
		catch(Exception e){
			e.printStackTrace();
		}*/
    }
	public void updateComplete(String msg)
	{
		((TextView)findViewById(R.id.textView)).setText(msg);
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
    public void onResume() {
        super.onResume();  // Always call the superclass method first
	    try{
	    snackAttack("Active tab: "+activeTab);} catch(Exception e){}
		try{	
		switch(activeTab){
            case 0:
                Future<Spanned> fRecent = threadpool.submit(recent);
				snackAttack("Updating..");
				while (!fRecent.isDone()) {
					Thread.sleep(500); 
				}
				Spanned responseRecent=fRecent.get();
                ((TextView)findViewById(R.id.textView)).setText(responseRecent);
				
				Intent intent = new Intent(IcebergWidget.ACTION_TEXT_CHANGED);
				intent.putExtra("updatedWidgetText", Html.toHtml(recent.toFormattedString())); 
				getApplicationContext().sendBroadcast(intent);
                break;
            case 1:
                Future<Spanned> fAlbums = threadpool.submit(albums);
				snackAttack("Updating..");
				while (!fAlbums.isDone()) {
					Thread.sleep(500); 
				}
				Spanned responseAlbums=fAlbums.get();
                ((TextView)findViewById(R.id.textView)).setText(responseAlbums);
                break;
            case 2:
                Future<Spanned> fArtists = threadpool.submit(artists);
				snackAttack("Updating..");
				while (!fArtists.isDone()) {
					Thread.sleep(500); 
				}
				Spanned responseArtists=fArtists.get();
                ((TextView)findViewById(R.id.textView)).setText(responseArtists);
                break;
            default:
			    ((TextView)findViewById(R.id.textView)).setText(recent.toString());  
                activeTab=0;
				break;
        }
		}
		catch(Exception e){
			((TextView)findViewById(R.id.textView)).setText("Interruption error");
		}
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
