package xyz.lapig.iceberg;

import java.lang.Runnable;
import android.content.Intent;
import android.content.Context;

public class BackgroundTasks implements Runnable {
	private Context context;
	private Intent intent;
	public BackgroundTasks(Context c, Intent i){
		context=c;
		intent=i;
	}

	public void run(){
		context.sendBroadcast(intent);
	}

}