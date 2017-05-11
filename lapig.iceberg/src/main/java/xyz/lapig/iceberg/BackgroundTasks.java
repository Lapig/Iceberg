package xyz.lapig.iceberg;

import java.lang.Runnable;
import android.content.Intent;
import android.content.Context;

public class BackgroundTasks implements Runnable {
	private Context context;
	private Intent intent;
	private int task;
	public BackgroundTasks(int task, Context c, Intent i){
		this.task=task;
		context=c;
		intent=i;
	}

	public void run(){
		switch(task){
			case 0:
				context.sendBroadcast(intent);
				return;
			default:
				return;
		}
	}

}