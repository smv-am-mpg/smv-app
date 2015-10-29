package de.smv_am_mpg.smvapp;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

public class NotificationReciever extends ParsePushBroadcastReceiver {

	@Override
	protected void onPushOpen(Context context, Intent intent) {
		
		super.onPushOpen(context, intent);
		//getActivity(context, intent).
	}

}
