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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import eu.iescities.pilot.rovereto.inbici.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrainingObject;
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

public class InBiciHelper {

	/**
	 * 
	 */
	private static final String APP_INBICI = "inbici";

	/**
	 * 
	 */
	public static final String TRACK_IDENTIFICATOR = "TRACK_ID";

	public static final String EVENTS = "events";

	public static final String SYNC_SERVICE = "sync";

	public static final String NEW_TRACK_STARTED = "NEW_TRACK_STARTED";
	public static final int SYNC_REQUIRED = 2;
	public static final int SYNC_NOT_REQUIRED = 0;
	public static final int SYNC_REQUIRED_FIRST_TIME = 3;
	private static final int CURR_DB = 4;

	private static final String DIFFERENT_START_PLACE = "DIFFERENT_START_PLACE";

	private static SCAccessProvider accessProvider = null;
	private static String serviceUrl;

	private static InBiciHelper instance = null;

	private static Context mContext;
	private StorageConfiguration config = null;
	private DTSyncStorage storage = null;

	private static LocationHelper mLocationHelper;

	private boolean syncInProgress = false;
	private FragmentActivity rootActivity = null;

	public static void init(final Context mContext) {
		if (instance == null)
			instance = new InBiciHelper(mContext);

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

	public static String getAuthToken() {

		String mToken = null;
		try {
			mToken = getAccessProvider().readToken(mContext);
		} catch (AACException e) {
			Log.e(InBiciHelper.class.getName(), "No token data");
		}
		return mToken;
	}

	public static SCAccessProvider getAccessProvider() {
		if (accessProvider == null)
			accessProvider = SCAccessProvider.getInstance(mContext);
		return accessProvider;
	}

	private static InBiciHelper getInstance() throws DataException {
		if (instance == null)
			throw new DataException("DTHelper is not initialized");
		return instance;
	}

	protected InBiciHelper(Context mContext) {
		super();

		InBiciHelper.mContext = mContext;
		if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.FROYO) {
			RemoteConnector.setClientType(RemoteConnector.CLIENT_TYPE.CLIENT_WILDCARD);
		}
		accessProvider = SCAccessProvider.getInstance(mContext);

		config = new DTStorageConfiguration();

		if (Utils.getDBVersion(mContext, APP_INBICI, Constants.SYNC_DB_NAME) != CURR_DB) {
			Utils.writeObjectVersion(mContext, APP_INBICI, Constants.SYNC_DB_NAME, 0);
		}
		this.storage = new DTSyncStorage(mContext, APP_INBICI, Constants.SYNC_DB_NAME, CURR_DB, config);

		setLocationHelper(new LocationHelper(mContext));
	}

	public static FragmentActivity start(FragmentActivity activity) throws RemoteException, DataException,
			StorageConfigurationException, SecurityException, ConnectionException, ProtocolException,
			NameNotFoundException, AACException {
		getInstance().rootActivity = activity;
		try {
			if (getInstance().syncInProgress)
				return null;

			if (Utils.getObjectVersion(mContext, APP_INBICI, Constants.SYNC_DB_NAME) <= 0) {

				Log.d("MAP", "DTHelper --> start --> appToken: " + APP_INBICI);

				Utils.writeObjectVersion(mContext, APP_INBICI, Constants.SYNC_DB_NAME, 1L);
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

	// check if it is correct!!!
	public static TrackObject findTrackById(String id) throws DataException, StorageConfigurationException {
		return findDTObjectById(TrackObject.class, id);
	}

	public static TrackObject getTrack(String trackId) {

		TrackObject trackObj = null;

		if (trackId == null) {
			trackId = Constants.ARG_TRACK_ID;
		}

		try {
			trackObj = InBiciHelper.findTrackById(trackId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trackObj;
	}

	private static <T extends BaseDTObject> T findDTObjectById(Class<T> cls, String id) throws DataException,
			StorageConfigurationException {
		T returnObject = getInstance().storage.getObjectById(id, cls);
		return returnObject;

	}

	public static LocationHelper getLocationHelper() {
		return mLocationHelper;
	}

	public static void setLocationHelper(LocationHelper mLocationHelper) {
		InBiciHelper.mLocationHelper = mLocationHelper;
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

	// methods called by on Resume() method in SearchFragment
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

	
	public static void modifyTrack(TrackObject track){
		try {
			getInstance().storage.update(track, false);
//			getInstance().storage.synchronize(getAuthToken(), getAppUrl(), SYNC_SERVICE);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveNewTraining(TrainingObject training) {
		try {
			getInstance().storage.create(training);
			getInstance().storage.synchronize(getAuthToken(), getAppUrl(), SYNC_SERVICE);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static TrackObject saveNewTrack(TrackObject track) {
		// devo scrivere direttamente nel db -> DtSynchStorage?
		try {
			TrackObject newTrack = getInstance().storage.create(track);
			getInstance().storage.synchronize(getAuthToken(), getAppUrl(), SYNC_SERVICE);
			return newTrack;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean sameTrack() {
		return true;
	}

	public static Collection<TrackObject> getHomeTracks() throws DataException, StorageConfigurationException {
		return getOfficialTracks();
	}

	public static Collection<TrackObject> getOfficialTracks() throws DataException, StorageConfigurationException {
		String where = "creator IS NULL";
		return getInstance().storage.query(TrackObject.class, where, null);
	}

	public static Collection<TrackObject> getUserTracks() throws DataException, StorageConfigurationException {
		String where = "creator IS NOT NULL";
		return getInstance().storage.query(TrackObject.class, where, null);
	}

	public static Collection<TrackObject> getMyTracks() throws DataException, StorageConfigurationException {
		String where = "id in (SELECT DISTINCT trackid FROM trainings)";
		return getInstance().storage.query(TrackObject.class, where, null);
	}

	public static List<TrackObject> getTracksByCategory(String categories) throws DataException,
			StorageConfigurationException {
		if (CategoryHelper.TYPE_MY.equals(categories)) {
			return new ArrayList<TrackObject>(getMyTracks());
		}
		if (CategoryHelper.TYPE_OFFICIAL.equals(categories)) {
			return new ArrayList<TrackObject>(getOfficialTracks());
		}
		if (CategoryHelper.TYPE_USER.equals(categories)) {
			return new ArrayList<TrackObject>(getUserTracks());
		}
		return Collections.emptyList();
	}

	
	public static Collection<TrackObject> getHomeTracks(int position, int size) throws DataException, StorageConfigurationException {
		return getOfficialTracks(position, size);
	}

	public static Collection<TrackObject> getOfficialTracks(int position, int size) throws DataException, StorageConfigurationException {
		String where = "creator IS NULL";
		return getInstance().storage.query(TrackObject.class, where, null, position, size);
	}

	public static Collection<TrackObject> getUserTracks(int position, int size) throws DataException, StorageConfigurationException {
		String where = "creator IS NOT NULL";
		return getInstance().storage.query(TrackObject.class, where, null, position, size);
	}

	public static Collection<TrackObject> getMyTracks(int position, int size) throws DataException, StorageConfigurationException {
		String where = "id in (SELECT DISTINCT trackid FROM trainings)";
		return getInstance().storage.query(TrackObject.class, where, null, position, size);
	}

	public static List<TrackObject> getTracksByCategory(String categories, int position, int size) throws DataException,
			StorageConfigurationException {
		if (CategoryHelper.TYPE_MY.equals(categories)) {
			return new ArrayList<TrackObject>(getMyTracks(position, size));
		}
		if (CategoryHelper.TYPE_OFFICIAL.equals(categories)) {
			return new ArrayList<TrackObject>(getOfficialTracks(position, size));
		}
		if (CategoryHelper.TYPE_USER.equals(categories)) {
			return new ArrayList<TrackObject>(getUserTracks(position, size));
		}
		return Collections.emptyList();
	}
	/**
	 * @param mTrackId
	 * @return
	 * @throws StorageConfigurationException
	 * @throws DataException
	 */
	public static List<TrainingObject> getTrainings(String mTrackId) throws DataException,
			StorageConfigurationException {
		Collection<TrainingObject> trainings = getInstance().storage.query(TrainingObject.class, "trackId = ?",
				new String[] { mTrackId });
		return new ArrayList<TrainingObject>(trainings);
	}

	public static String getTrackIdFromSP(SharedPreferences sharedPreferences2) {
		SharedPreferences sharedPreferences = sharedPreferences2;
		if (sharedPreferences.contains(TRACK_IDENTIFICATOR)) {
			String returnID = sharedPreferences.getString(TRACK_IDENTIFICATOR, null);
			return returnID;
		} else {
			return null;
		}
	}

	public static void addTrackIdFromSP(SharedPreferences sharedPreferences2, String trackId) {
		SharedPreferences sharedPreferences = sharedPreferences2;
		Editor editor = sharedPreferences.edit();
		editor.putString(TRACK_IDENTIFICATOR, trackId);
		editor.commit();
	}

	public static void removeTrackIdFromSP(SharedPreferences sharedPreferences2) {
		SharedPreferences sharedPreferences = sharedPreferences2;
		if (sharedPreferences.contains(TRACK_IDENTIFICATOR)) {
			Editor editor = sharedPreferences.edit();
			editor.remove(TRACK_IDENTIFICATOR);
			editor.commit();
		}
	}

	public static void startANewTrack(SharedPreferences sharedPreferences2) {
		SharedPreferences sharedPreferences = sharedPreferences2;
		if (!sharedPreferences.contains(NEW_TRACK_STARTED)) {
			Editor editor = sharedPreferences.edit();
			editor.putBoolean(NEW_TRACK_STARTED, true);
			editor.commit();
		}
	}

	public static boolean isStartedANewTrack(SharedPreferences sharedPreferences2) {
		SharedPreferences sharedPreferences = sharedPreferences2;
		if (sharedPreferences.contains(NEW_TRACK_STARTED)) {
			boolean started = sharedPreferences.getBoolean(NEW_TRACK_STARTED, false);
			return started;
		} else {
			return false;
		}
	}

	public static void removeNewTrackStart(SharedPreferences sharedPreferences2) {
		SharedPreferences sharedPreferences = sharedPreferences2;
		if (sharedPreferences.contains(NEW_TRACK_STARTED)) {
			Editor editor = sharedPreferences.edit();
			editor.remove(NEW_TRACK_STARTED);
			editor.commit();
		}
	}

	public static void setDifferentStartPlace(SharedPreferences preferences) {
		SharedPreferences sharedPreferences = preferences;
		if (!sharedPreferences.contains(DIFFERENT_START_PLACE)) {
			Editor editor = sharedPreferences.edit();
			editor.putBoolean(DIFFERENT_START_PLACE, true);
			editor.commit();
		}

	}

	public static boolean hadDifferentStartPlace(SharedPreferences preferences) {
		SharedPreferences sharedPreferences = preferences;
		if (sharedPreferences.contains(DIFFERENT_START_PLACE)) {
			boolean started = sharedPreferences.getBoolean(DIFFERENT_START_PLACE, false);
			return started;
		} else {
			return false;
		}
	}

	public static void removeDifferentStartPlace(SharedPreferences preferences) {
		SharedPreferences sharedPreferences = preferences;
		if (sharedPreferences.contains(DIFFERENT_START_PLACE)) {
			Editor editor = sharedPreferences.edit();
			editor.remove(DIFFERENT_START_PLACE);
			editor.commit();
		}
	}
}
