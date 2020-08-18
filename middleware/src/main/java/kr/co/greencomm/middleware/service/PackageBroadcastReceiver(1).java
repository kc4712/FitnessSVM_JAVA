package kr.co.greencomm.middleware.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PackageBroadcastReceiver extends BroadcastReceiver {
	private static final String tag = PackageBroadcastReceiver.class.getSimpleName();
	
	/** actions list **/
	public static final String ACTION_SET_TODAY_ACTION = "kr.co.greencomm.ibody24.coachplus.Mw.setTodayAction";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(tag,"PackageBroadcastReceiver onReceive-> action : "+action);
		if(action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
			String packageName = intent.getData().getSchemeSpecificPart();
			if(packageName.equals("kr.co.greencomm.coachplus")) {
				Intent service = new Intent(context, MWService.class);
				service.setAction(ACTION_SET_TODAY_ACTION);
				context.startService(service);
			}
		}
	}
}