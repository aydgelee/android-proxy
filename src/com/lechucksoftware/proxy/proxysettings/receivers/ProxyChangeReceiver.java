package com.lechucksoftware.proxy.proxysettings.receivers;

import com.lechucksoftware.proxy.proxysettings.ApplicationGlobals;
import com.lechucksoftware.proxy.proxysettings.Constants;
import com.lechucksoftware.proxy.proxysettings.services.ProxySettingsCheckerService;
import com.lechucksoftware.proxy.proxysettings.utils.LogWrapper;
import com.lechucksoftware.proxy.proxysettings.utils.UIUtils;
import com.shouldit.proxy.lib.APLConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class ProxyChangeReceiver extends BroadcastReceiver
{
	public static String TAG = "ProxyChangeReceiver";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (	   intent.getAction().equals(APLConstants.APL_UPDATED_PROXY_CONFIGURATION) 		// INTERNAL (APL): Called when a proxy configuration is written by APL
				|| intent.getAction().equals(Constants.PROXY_SETTINGS_STARTED) 					// INTERNAL (PS) : Called when Proxy Settings is started
				|| intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) 			// Connection type change (switch between 3G/WiFi)
				|| intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) 		// Scan restults available information
//		   	    || intent.getAction().equals(Proxy.PROXY_CHANGE_ACTION) 			 			// Called when a Proxy Configuration is changed
//				|| intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) 	// Changed wifi supplicant connection state (connected/disconnected)
		{
			//LogWrapper.logIntent(TAG, intent, Log.DEBUG);
			callProxySettingsChecker(context, intent);
		}
		else if (	intent.getAction().equals(Constants.PROXY_REFRESH_UI)						// INTERNAL (PS) : Called to refresh the UI of Proxy Settings
				 || intent.getAction().equals(APLConstants.APL_UPDATED_PROXY_STATUS_CHECK))		// INTERNAL (APL): Called when an updated status on the check of a configuration is available 
		{
			//LogWrapper.logIntent(TAG, intent, Log.DEBUG);
			UIUtils.UpdateStatusBarNotification(ApplicationGlobals.getCachedConfiguration(), context);
		}
		else
		{
			LogWrapper.logIntent(TAG, intent, Log.ERROR);
			LogWrapper.e(TAG, "Intent not found into handled list!");
		}
	}

	private void callProxySettingsChecker(Context context, Intent intent)
	{
		//Call the ProxySettingsCheckerService for update the network status
		Intent serviceIntent = new Intent(context, ProxySettingsCheckerService.class);
		serviceIntent.putExtra(ProxySettingsCheckerService.CALLER_INTENT, intent);
		context.startService(serviceIntent);
	}
}
