package net.antoniy.gidder.beta.dns;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.antoniy.gidder.beta.ui.util.GidderCommons;
import net.antoniy.gidder.beta.ui.util.PrefsConstants;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DynamicDNSManager {

	private final static String TAG = DynamicDNSManager.class.getSimpleName();

	private final static int PROVIDER_INDEX_DYNDNS = 0;
	private final static int PROVIDER_INDEX_NOIP = 1;
	
	private final Context context;
	private final SharedPreferences prefs;
	
	public DynamicDNSManager(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void update() {
		final boolean active = prefs.getBoolean(PrefsConstants.DYNDNS_ACTIVE.getKey(), false);
		final String hostname = prefs.getString(PrefsConstants.DYNDNS_DOMAIN.getKey(), "");
		final int providerIndex = prefs.getInt(PrefsConstants.DYNDNS_PROVIDER_INDEX.getKey(), -1);
		final String username = prefs.getString(PrefsConstants.DYNDNS_USERNAME.getKey(), "");
		final String password = prefs.getString(PrefsConstants.DYNDNS_PASSWORD.getKey(), "");
		
		if(!active) {
			return;
		} 

		if("".equals(username.trim()) || "".equals(password.trim()) || "".equals(hostname.trim())) {
			Toast.makeText(context, "Dynamic DNS information is NOT valid!", Toast.LENGTH_SHORT).show();
		}
		
		final String address = GidderCommons.getCurrentWifiIpAddress(context);
		
		if(providerIndex == PROVIDER_INDEX_NOIP) {
			if(context instanceof Activity) {
				updateOnNewThread(DynamicDNSFactory.createNoIpStrategy(context), hostname, address, username, password);
			} else {
				updataOnSameThread(DynamicDNSFactory.createNoIpStrategy(context), hostname, address, username, password);
			}
		} else if(providerIndex == PROVIDER_INDEX_DYNDNS) {
			if(context instanceof Activity) {
				updateOnNewThread(DynamicDNSFactory.createDynDNSStrategy(context), hostname, address, username, password);
			} else {
				updataOnSameThread(DynamicDNSFactory.createDynDNSStrategy(context), hostname, address, username, password);
			}
		}
	}
	
	private void updateOnNewThread(final DynamicDNS strategy, final String hostname, final String address, final String username, final String password) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				try {
					strategy.update(
							URLEncoder.encode(hostname, "UTF-8"), 
							URLEncoder.encode(address, "UTF-8"), 
							URLEncoder.encode(username, "UTF-8"),
							URLEncoder.encode(password, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "Problem using UTF-8 encoding.", e);
				}
			}
		}).start();
	}
	
	private void updataOnSameThread(final DynamicDNS strategy, final String hostname, final String address, final String username, final String password) {
		try {
			strategy.update(
					URLEncoder.encode(hostname, "UTF-8"), 
					URLEncoder.encode(address, "UTF-8"), 
					URLEncoder.encode(username, "UTF-8"), 
					URLEncoder.encode(password, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Problem using UTF-8 encoding.", e);
		}
	}
}
 