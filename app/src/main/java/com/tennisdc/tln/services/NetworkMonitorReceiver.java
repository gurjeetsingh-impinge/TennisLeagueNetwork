package com.tennisdc.tln.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tennisdc.tln.common.App;

/**
 * Created  on 2015-05-05.
 */
public class NetworkMonitorReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (App.isNetworkOnline(context)) {
			context.startService(new Intent(context, SubmitPaymentService.class));
			context.startService(new Intent(context, ErrorReportingService.class));
		}
	}

}
