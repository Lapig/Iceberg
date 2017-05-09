package xyz.lapig.iceberg;

import android.content.Intent;
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
	private ExecutorService updateExecuter;


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

        updateExecuter = Executors.newCachedThreadPool();

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
                        Future<Spanned> fRecent = updateExecuter.submit(recent);
						Spanned responseRecent=fRecent.get();
						((TextView)findViewById(R.id.textView)).setText(responseRecent);
						
                        Intent intent = new Intent(IcebergWidget.ACTION_TEXT_CHANGED);
                        intent.putExtra("updatedWidgetText", Html.toHtml(recent.toFormattedString(), Html.FROM_HTML_OPTION_USE_CSS_COLORS)); //haha epic
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
                try{
                switch(tab.getPosition()){
                    case 0:
                        Future<Spanned> fRecent = updateExecuter.submit(recent);
                        Spanned responseRecent=fRecent.get();
                        ((TextView)findViewById(R.id.textView)).setText(responseRecent);
                        activeTab=0;
                        break;
                    case 1:
                        Future<Spanned> fAlbums = updateExecuter.submit(albums);
                        Spanned responseAlbums=fAlbums.get();
                        ((TextView)findViewById(R.id.textView)).setText(responseAlbums);
                        activeTab=1;
                        break;
                    case 2:
                        Future<Spanned> fArtists = updateExecuter.submit(artists);
                        Spanned responseArtists=fArtists.get();
                        ((TextView)findViewById(R.id.textView)).setText(responseArtists);
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
        });
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
        super.onResume();
        updateExecuter = Executors.newCachedThreadPool();
		try{
		switch(activeTab){
            case 0:
                Future<Spanned> fRecent = updateExecuter.submit(recent);
				Spanned responseRecent=fRecent.get();
                ((TextView)findViewById(R.id.textView)).setText(responseRecent);
				
				Intent intent = new Intent(IcebergWidget.ACTION_TEXT_CHANGED);
				intent.putExtra("updatedWidgetText", Html.toHtml(recent.toFormattedString(), Html.FROM_HTML_OPTION_USE_CSS_COLORS));
				getApplicationContext().sendBroadcast(intent);
                break;
            case 1:
                Future<Spanned> fAlbums = updateExecuter.submit(albums);
				Spanned responseAlbums=fAlbums.get();
                ((TextView)findViewById(R.id.textView)).setText(responseAlbums);
                break;
            case 2:
                Future<Spanned> fArtists = updateExecuter.submit(artists);
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
	        e.printStackTrace();
			((TextView)findViewById(R.id.textView)).setText("Interruption error");
		}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            intent.putExtra("text", "@app_name");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        updateExecuter.shutdown();
    }
}
