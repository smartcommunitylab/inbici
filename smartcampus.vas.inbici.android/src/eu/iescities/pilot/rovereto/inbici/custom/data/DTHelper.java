/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.iescities.pilot.rovereto.inbici.custom.data;

import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.inbici.custom.DTParamsHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.map.MapManager;
import eu.trentorise.smartcampus.ac.AACException;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.android.common.GlobalConfig;
import eu.trentorise.smartcampus.android.common.LocationHelper;
import eu.trentorise.smartcampus.android.common.navigation.NavigationHelper;
import eu.trentorise.smartcampus.network.RemoteConnector;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;
import eu.trentorise.smartcampus.storage.db.StorageConfiguration;
import eu.trentorise.smartcampus.storage.sync.Utils;

public class DTHelper {

	/**
	 * 
	 */

	public static final String EVENTS = "events";

	public static final String SYNC_SERVICE = "sync";

	public static final int SYNC_REQUIRED = 2;
	public static final int SYNC_NOT_REQUIRED = 0;
	public static final int SYNC_REQUIRED_FIRST_TIME = 3;
	private static final int CURR_DB = 4;

	private static SCAccessProvider accessProvider = null;
	private static String serviceUrl;


	private static DTHelper instance = null;


	private static Context mContext;
	private StorageConfiguration config = null;
	private DTSyncStorage storage = null;


	private static LocationHelper mLocationHelper;

	private boolean syncInProgress = false;
	private FragmentActivity rootActivity = null;


	public static void init(final Context mContext) {
		if (instance == null)
			instance = new DTHelper(mContext);

		serviceUrl = getAppUrl();
		if (!serviceUrl.endsWith("/")) {
			serviceUrl += '/';
		}

		Log.d("MAP", "DTHelper --> init --> serviceURL: " + serviceUrl);
	}

	
	private static String getAppUrl() {
		String returnAppUrl = "";
		try {
			returnAppUrl = GlobalConfig.getAppUrl(mContext);
			if (!returnAppUrl.endsWith("/"))
				returnAppUrl = returnAppUrl.concat("/");
		} catch (Exception e) { // protocolexception
			e.printStackTrace();
		}
		return returnAppUrl;
	}

	
	private static String getAuthToken() {
		String mToken = null;
		try {
			mToken = getAccessProvider().readToken(mContext);
		} catch (AACException e) {
			Log.e(DTHelper.class.getName(), "No token data");
		}
		return mToken;
	}

	
	public static SCAccessProvider getAccessProvider() {
		if (accessProvider == null)
			accessProvider = SCAccessProvider.getInstance(mContext);
		return accessProvider;
	}


	private static DTHelper getInstance() throws DataException {
		if (instance == null)
			throw new DataException("DTHelper is not initialized");
		return instance;
	}

	protected DTHelper(Context mContext) {
		super();

		DTHelper.mContext = mContext;
		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.FROYO) {
			RemoteConnector.setClientType(RemoteConnector.CLIENT_TYPE.CLIENT_WILDCARD);
		}
		accessProvider = SCAccessProvider.getInstance(mContext);
		DTParamsHelper.init(mContext);
		MapManager.initWithParam();

		config = new DTStorageConfiguration();

		if (Utils.getDBVersion(mContext, DTParamsHelper.getAppToken(), Constants.SYNC_DB_NAME) != CURR_DB) {
			Utils.writeObjectVersion(mContext, DTParamsHelper.getAppToken(), Constants.SYNC_DB_NAME, 0);
		}
		this.storage = new DTSyncStorage(mContext, DTParamsHelper.getAppToken(), Constants.SYNC_DB_NAME, CURR_DB,
				config);

		setLocationHelper(new LocationHelper(mContext));
	}



	public static FragmentActivity start(FragmentActivity activity) throws RemoteException, DataException,
	StorageConfigurationException, SecurityException, ConnectionException, ProtocolException,
	NameNotFoundException, AACException {
		getInstance().rootActivity = activity;
		try {
			if (getInstance().syncInProgress)
				return null;

			if (Utils.getObjectVersion(mContext, DTParamsHelper.getAppToken(), Constants.SYNC_DB_NAME) <= 0) {

				Log.d("MAP", "DTHelper --> start --> appToken: " + DTParamsHelper.getAppToken());


				Utils.writeObjectVersion(mContext, DTParamsHelper.getAppToken(), Constants.SYNC_DB_NAME, 1L);
			}

			getInstance().syncInProgress = true;

			Log.d("MAP", "DTHelper --> start --> authToken: " + getAuthToken());

			// TO DO
			getInstance().storage.synchronize(getAuthToken(), getAppUrl(), SYNC_SERVICE);

		} finally {
			getInstance().syncInProgress = false;
		}
		return getInstance().rootActivity;
	}

	public static void synchronize() throws RemoteException, DataException, StorageConfigurationException,
	SecurityException, ConnectionException, ProtocolException, AACException {
		// TO DO
		getInstance().storage.synchronize(getAuthToken(), getAppUrl(), SYNC_SERVICE);

	}
	public static void destroy() {
	}

	public static void endAppFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_LONG).show();
		activity.finish();
	}

	public static void showFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_LONG).show();
	}

	/**
	 * @param trackId
	 * @return
	 */

	//check if it is correct!!!
	public static TrackObject findTrackById(String id) throws DataException, StorageConfigurationException {
		return findDTObjectById(TrackObject.class, id);
	}

	private static <T extends BaseDTObject> T findDTObjectById(Class<T> cls, String id) throws DataException, StorageConfigurationException {
		T returnObject = getInstance().storage.getObjectById(id, cls);
		return returnObject;

	}

	public static LocationHelper getLocationHelper() {
		return mLocationHelper;
	}

	public static void setLocationHelper(LocationHelper mLocationHelper) {
		DTHelper.mLocationHelper = mLocationHelper;
	}

	public class DTLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	//methods called by on Resume() method in SearchFragment
	public static boolean checkInternetConnection(Context context) {

		ConnectivityManager con_manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (con_manager.getActiveNetworkInfo() != null && con_manager.getActiveNetworkInfo().isAvailable()
				&& con_manager.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}
	
	public static void bringmethere(FragmentActivity activity, Address from, Address to) {
		Intent intent = activity.getPackageManager().getLaunchIntentForPackage(
				"eu.trentorise.smartcampus.viaggiatrento");
		if (intent == null) {
			intent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=eu.trentorise.smartcampus.viaggiatrento"));
			activity.startActivity(intent);
		} else
			NavigationHelper.bringMeThere(activity, from, to);
	}


	public static Collection<TrackObject> getOfficialTracks() throws DataException, StorageConfigurationException {
		String where = "creator IS NULL";
		return getInstance().storage.query(TrackObject.class, where, null);
	}



}
