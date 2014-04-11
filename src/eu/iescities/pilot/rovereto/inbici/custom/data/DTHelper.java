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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

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

import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.inbici.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.inbici.custom.DTParamsHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.CommunityData;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ObjectFilter;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.Review;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ReviewObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.entities.search.WhenForSearch;
import eu.iescities.pilot.rovereto.inbici.entities.search.WhereForSearch;
import eu.iescities.pilot.rovereto.inbici.map.MapManager;
import eu.iescities.pilot.rovereto.inbici.utils.TimeUtils;
import eu.trentorise.smartcampus.ac.AACException;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.android.common.GlobalConfig;
import eu.trentorise.smartcampus.android.common.LocationHelper;
import eu.trentorise.smartcampus.android.common.navigation.NavigationHelper;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion;
import eu.trentorise.smartcampus.android.common.tagging.SuggestionHelper;
import eu.trentorise.smartcampus.network.JsonUtils;
import eu.trentorise.smartcampus.network.RemoteConnector;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ConnectionException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.ProtocolException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.storage.BasicObject;
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


	private static List<ExplorerObject> getEventsRemote(ObjectFilter filter, String authToken) {

		Log.i("MAP", "DTHelper --> getEventsRemote");

		try {
			Map<String, Object> params = null;
			if (filter == null)
				params = Collections.<String, Object> emptyMap();
			else
				params = Collections.<String, Object> singletonMap("filter", JsonUtils.toJSON(filter));
			String json = RemoteConnector.getJSON(serviceUrl, EVENTS, authToken, params);
			return JsonUtils.toObjectList(json, ExplorerObject.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}




	public static List<SemanticSuggestion> getSuggestions(CharSequence suggest) throws ConnectionException,
	ProtocolException, SecurityException, DataException, AACException {
		return SuggestionHelper.getSuggestions(suggest, mContext, GlobalConfig.getAppUrl(mContext), getAuthToken(),
				DTParamsHelper.getAppToken());
	}



	public static void endAppFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_LONG).show();
		activity.finish();
	}

	public static void showFailure(Activity activity, int id) {
		Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_LONG).show();
	}







	public static CommunityData writeReview(BaseDTObject object, Review review) throws ConnectionException,
	ProtocolException, SecurityException, DataException, RemoteException, StorageConfigurationException,
	AACException, java.lang.SecurityException, eu.trentorise.smartcampus.network.RemoteException {

		String string = RemoteConnector.postJSON(getAppUrl(), "social/review/" + object.getId(),
				JsonUtils.toJSON(review), getAuthToken());
		synchronize();

		return JsonUtils.toObject(string, CommunityData.class);
	}

	public static List<Review> loadReviews(String id) throws java.lang.SecurityException,
	eu.trentorise.smartcampus.network.RemoteException {
		String string = RemoteConnector.getJSON(getAppUrl(), "social/readReviews/" + id, getAuthToken());

		return JsonUtils.toObject(string, ReviewObject.class).getReviews();
	}

















	/**
	 * @param trackId
	 * @return
	 */

	//check if it is correct!!!
	public static TrackObject findTrackByEntityId(String entityId) throws DataException,
	StorageConfigurationException, ConnectionException, ProtocolException, SecurityException {
		return findDTObjectByEntityId(TrackObject.class, entityId);
	}






	@SuppressWarnings("rawtypes")
	private static <T extends BaseDTObject> T findDTObjectByEntityId(Class<T> cls, String entityId)
			throws DataException, StorageConfigurationException, ConnectionException, ProtocolException,
			SecurityException {
		T returnObject = null;
		String where = "entityId = '" + entityId + "'";
		Collection<T> coll = getInstance().storage.query(cls, where, null);
		if (coll != null && coll.size() == 1)
			returnObject = coll.iterator().next();
		if (returnObject == null)
			returnObject = findLocalDTOObjectByEntityId(cls, entityId);
		return returnObject;

	}

	@SuppressWarnings("rawtypes")
	private static <T extends BaseDTObject> T findLocalDTOObjectByEntityId(Class<T> cls, String entityId) {
		try {
			DTHelper.synchronize();
			T returnObject = null;
			String where = "entityId = '" + entityId + "'";
			Collection<T> coll = getInstance().storage.query(cls, where, null);
			if (coll != null && coll.size() == 1)
				returnObject = coll.iterator().next();
			return returnObject;
		} catch (Exception e) {
			return null;
		}

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

	

	
	public static <T extends BaseDTObject> Collection<T> searchInGeneral(int position, int size, String what,
			WhereForSearch distance, WhenForSearch when, boolean my, Class<T> cls, SortedMap<String, Integer> sort,
			String... inCategories) throws DataException, StorageConfigurationException, ConnectionException,
			ProtocolException, SecurityException {


		Log.i("MAP", "DTHelper --> searchInGeneral");


		/* calcola when */
		String[] argsArray = null;
		ArrayList<String> args = null;

		if (distance != null) {
			/* search online */

			Log.i("MAP", "search online");

			return getObjectsFromServer(position, size, what, distance, when, my, cls, inCategories, sort);
		} else {
			/* search offline */

	
			/* if sync create the query */
			String where = "";
			if (inCategories != null && inCategories[0] != null) {
				args = new ArrayList<String>();
				where = addCategoriesToWhere(where, inCategories, args);
			}
			if (what != null && what.length() > 0) {
				where = addWhatToWhere(cls, where, what);
				if (args == null)
					args = new ArrayList<String>(Arrays.asList(what));
				else
					args.add(what);
			}

			if (ExplorerObject.class.getCanonicalName().equals(cls.getCanonicalName())) {
				if (when != null)
					where = addWhenToWhere(where, when.getFrom(), when.getTo());

				/* se sono con gli eventi setto la data a oggi */
				else
					where = addWhenToWhere(where, TimeUtils.getCurrentDateTimeForSearching(), 0);
			}
			if (my)
				where = addMyEventToWhere(where);
			if (args != null)
				argsArray = args.toArray(new String[args.size()]);
			/*
			 * se evento metti in ordine di data ma se place metti in ordine
			 * alfabetico
			 */
			if (ExplorerObject.class.getCanonicalName().equals(cls.getCanonicalName())) {
				return getInstance().storage.query(cls, where, argsArray, position, size, "fromTime ASC");
			} else {
				return getInstance().storage.query(cls, where, argsArray, position, size, "title ASC");
			}

		}

	}
	
	
	
	//methods called by searchInGeneral
	@SuppressWarnings("unchecked")
	private static <T extends BasicObject> Collection<T> getObjectsFromServer(int position, int size, String what,
			WhereForSearch distance, WhenForSearch when, boolean myevent, Class<T> cls, String[] inCategories,
			SortedMap<String, Integer> sort) {

		Log.i("MAP", "DTHelper --> getObjectsFromServer");

		try {

			ObjectFilter filter = new ObjectFilter();

			/* get position */

			if (when != null)
				filter.setFromTime(when.getFrom());
			if ((when != null) && (when.getTo() != 0))
				filter.setToTime(when.getTo());

			if (distance != null) {
				GeoPoint mypos = MapManager.requestMyLocation(getInstance().mContext);
				filter.setCenter(new double[] { (double) mypos.getLatitudeE6() / 1000000,
						(double) mypos.getLongitudeE6() / 1000000 });
				filter.setRadius(distance.getFilter());
			}
			if (what != null && what.length() > 0) {
				filter.setText(what);
			}
			if (inCategories[0] != null) {
				filter.setTypes(Arrays.asList(CategoryHelper.getAllCategories(new HashSet<String>(Arrays
						.asList(inCategories)))));
			}
			filter.setSkip(position);
			filter.setLimit(size);
			filter.setClassName(cls.getCanonicalName());
			if (sort != null)
				filter.setSort(sort);
			// TO DO
			Collection<T> result = new ArrayList<T>();
			Collection<ExplorerObject> events = null;
			events = getEventsRemote(filter, null);

		
			if (result != null) {
				synchronize();
			}
			return result;

		} catch (Exception e) {
			return null;
		}
	}


	//methods called by searchInGeneral
	private static String addMyEventToWhere(String where) {
		
		
		Log.i("MAP", "DTHelper --> addMyEventToWhere");
		
		String whereReturns = new String(" attending IS NOT NULL ");
		if (where.length() > 0) {
			return where += " and (" + whereReturns + ")";
		} else
			return where += whereReturns;
	}


	//methods called by searchInGeneral
	private static String addWhenToWhere(String where, long whenFrom, long whenTo) {
		
		Log.i("MAP", "DTHelper --> addWhenToWhere");

		String whereReturns = null;
		if ((whenTo != 0)) {
			whereReturns = new String("( fromTime > " + whenFrom + " AND fromTime < " + whenTo + " ) OR (  toTime < "
					+ whenTo + " AND toTime > " + whenFrom + " )");
		} else
			whereReturns = new String(" ( fromTime > " + whenFrom + "  ) OR ( toTime > " + whenFrom + " )");

		if (where.length() > 0) {
			return where += " and (" + whereReturns + ")";
		} else
			return "(" + whereReturns + ")";

	}
	

	//methods called by searchInGeneral
	@SuppressWarnings("rawtypes")
	private static <T extends BaseDTObject> String addWhatToWhere(Class<T> cls, String where, String what)
			throws StorageConfigurationException, DataException {
		
		Log.i("MAP", "DTHelper --> addWhatToWhere");

		
		String whereReturns = "";

		whereReturns = " " + getInstance().config.getTableName(cls) + " MATCH ? ";
		if (where.length() > 0) {
			return where += " and (" + whereReturns + ")";
		} else
			return where += whereReturns;

	}


	//methods called by searchInGeneral
	private static String addCategoriesToWhere(String where, String[] inCategories, List<String> nonNullCategories) {
		String whereReturns = new String();
		String[] categories = CategoryHelper.getAllCategories(new HashSet<String>(Arrays.asList(inCategories)));

		for (int i = 0; i < categories.length; i++) {
			if (whereReturns.length() > 0)
				whereReturns += " or ";
			if (categories[i] != null) {
				//				nonNullCategories.add(categories[i]);
				whereReturns += " category like '%\""+categories[i]+"\"%'";
			} else {
				whereReturns += " category is null";
			}
		}
		if (whereReturns.length() > 0) {
			if (where.length() > 0) {
				return where += " and (" + whereReturns + ")";
			} else
				return where += "( " + whereReturns + " ) ";
		}
		else return "";

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

	

	// Methods using the ExplorerObject class in packages different from custom.data, 
	//that is, in packages that are not related to the initialization of the data (data transfer from the server)  


	public static ExplorerObject attend(BaseDTObject event) throws ConnectionException, ProtocolException,
	SecurityException, DataException, RemoteException, StorageConfigurationException, AACException {
		// TO DO
		ExplorerObject newEvent = myEvent(event.getId(), true, getAuthToken());

		synchronize();
		return newEvent;
	}

	public static ExplorerObject notAttend(BaseDTObject event) throws ConnectionException, ProtocolException,
	SecurityException, DataException, RemoteException, StorageConfigurationException, AACException {
		// TO DO

		ExplorerObject newEvent = myEvent(event.getId(), false, getAuthToken());
		synchronize();

		return newEvent;
	}


	private static ExplorerObject myEvent(String id, boolean add, String authToken) {
		if (id != null) {
			try {
				String json = RemoteConnector.postJSON(getAppUrl(),
						"social/attend/" + id + "/" + JsonUtils.toJSON(add), "", getAuthToken());

				return JsonUtils.toObject(json, ExplorerObject.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	/**
	 * return true in case of create and false in case of update
	 * 
	 * @param event
	 * @return
	 * @throws RemoteException
	 * @throws DataException
	 * @throws StorageConfigurationException
	 * @throws ConnectionException
	 * @throws ProtocolException
	 * @throws SecurityException
	 * @throws TerritoryServiceException
	 * @throws AACException
	 */
	public static Boolean saveEvent(ExplorerObject event) throws RemoteException, DataException,
	StorageConfigurationException, ConnectionException, ProtocolException, SecurityException, AACException,
	eu.trentorise.smartcampus.network.RemoteException {
		Boolean result = null;
		if (event.getId() == null) {
			// TO DO
			event = createEvent(event, getAuthToken());
			result = true;
		} else {
			// TO DO
			event = updateEvent(event.getId(), event);
			result = false;
		}
		synchronize();
		return result;
	}

	private static ExplorerObject updateEvent(String id, ExplorerObject event) throws SecurityException,
	eu.trentorise.smartcampus.network.RemoteException {
		if (event != null) {

			Log.i("POST EDIT", JsonUtils.toJSON(event));
			String string = RemoteConnector.postJSON(getAppUrl(), "/social/edit", JsonUtils.toJSON(event),
					getAuthToken());
			return JsonUtils.toObject(string, ExplorerObject.class);
		}
		return null;
	}



	private static ExplorerObject createEvent(ExplorerObject event, String authToken) {
		if (event != null) {
			try {
				String json = RemoteConnector.postJSON(serviceUrl, EVENTS, JsonUtils.toJSON(event).toString(),
						authToken);
				return JsonUtils.toObject(json, ExplorerObject.class);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	
	
	public static ExplorerObject findEventById(String eventId) {

		try {
			ExplorerObject event = getInstance().storage.getObjectById(eventId, ExplorerObject.class);
			return event;
		} catch (Exception e) {
			return null;
		}
	}




	//called in setMiscellaneousListToLoad of MapFragment 
	public static Collection<ExplorerObject> getEventsByCategories(int position, int size, String... inCategories)
			throws DataException, StorageConfigurationException, ConnectionException, ProtocolException,
			SecurityException {

		if (inCategories == null || inCategories.length == 0)
			return Collections.emptyList();

		String[] categories = CategoryHelper.getAllCategories(new HashSet<String>(Arrays.asList(inCategories)));

		if (Utils.getObjectVersion(instance.mContext, DTParamsHelper.getAppToken(), Constants.SYNC_DB_NAME) > 0) {
			List<String> nonNullCategories = new ArrayList<String>();
			String where = "";
			for (int i = 0; i < categories.length; i++) {
				if (where.length() > 0)
					where += " or ";
				if (categories[i] != null) {
					where += " category like '%\""+categories[i]+"\"%'";
				} else {
					where += " category is null";
				}
			}
			if (where.length() > 0) {
				where = "(" + where + ")";
			}
			where += " AND fromTime > " + TimeUtils.getCurrentDateTimeForSearching();
			return getInstance().storage.query(ExplorerObject.class, where,
					nonNullCategories.toArray(new String[nonNullCategories.size()]), position, size, "fromTime ASC");
		} else {
			List<ExplorerObject> result = null;
			for (int c = 0; c < categories.length; c++) {
				ObjectFilter filter = new ObjectFilter();
				filter.setTypes(Arrays.asList(categories));
				filter.setSkip(position);
				filter.setLimit(size);
				// TO DO
				result = getEventsRemote(filter, getAuthToken());
			}
			return result;
		}
	}


	public static Collection<ExplorerObject> getEventsByPOI(int position, int size, String poiId) throws DataException,
	StorageConfigurationException, ConnectionException, ProtocolException, SecurityException, AACException {
		ArrayList<ExplorerObject> returnlist = new ArrayList<ExplorerObject>();

		if (Utils.getObjectVersion(mContext, DTParamsHelper.getAppToken(), Constants.SYNC_DB_NAME) > 0) {
			Collection<ExplorerObject> events = getInstance().storage.query(ExplorerObject.class,
					"poiId = ? AND fromTime > " + TimeUtils.getCurrentDateTimeForSearching(), new String[] { poiId }, position,
					size, "fromTime ASC");
			for (ExplorerObject event : events) {
				returnlist.add(event);
			}
			return returnlist;
		} else {
			ObjectFilter filter = new ObjectFilter();
			Map<String, Object> criteria = new HashMap<String, Object>(1);
			criteria.put("poiId", poiId);
			filter.setCriteria(criteria);
			filter.setSkip(position);
			filter.setLimit(size);
			// TO DO
			return getEventsRemote(filter, getAuthToken());

		}
	}


	public static Collection<ExplorerObject> searchTodayEvents(int position, int size, String text)
			throws DataException, StorageConfigurationException, ConnectionException, ProtocolException,
			SecurityException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date today = cal.getTime();

		cal.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = cal.getTime();

		if (Utils.getObjectVersion(instance.mContext, DTParamsHelper.getAppToken(), Constants.SYNC_DB_NAME) > 0) {
			return getInstance().storage.query(ExplorerObject.class, "( toTime > " + TimeUtils.getCurrentDateTimeForSearching()
					+ " AND (fromTime < " + tomorrow.getTime() + " ) OR (fromTime < " + tomorrow.getTime()
					+ " AND fromTime >" + today.getTime() + " )) ", null, position, size, "fromTime ASC");
		} else {
			ObjectFilter filter = new ObjectFilter();
			Map<String, Object> criteria = new HashMap<String, Object>(1);
			criteria.put("text", text);
			filter.setCriteria(criteria);
			filter.setSkip(position);
			filter.setLimit(size);
			// TO DO
			List<ExplorerObject> events = getEventsRemote(filter, getAuthToken());
			return events;
		}
	}



	public static int rate(BaseDTObject event, int rating) throws ConnectionException, ProtocolException,
	SecurityException, DataException, RemoteException, StorageConfigurationException, AACException {
		// TO DO
		int returnValue = rate(event.getId(), rating, getAuthToken());
		synchronize();
		return returnValue;
	}

	
	
	
	private static int rate(String id, int rating, String authToken) {
		if (id != null) {
			try {
				Map<String, Object> params = Collections.<String, Object> singletonMap("rating", rating);

				String json = RemoteConnector.putJSON(getAppUrl(), "social/rate/" + id, null, authToken, params);
				return Integer.parseInt(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 3;
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


	public static Collection<TrackObject> getTracks() throws DataException, StorageConfigurationException {
		String where = "";
		return getInstance().storage.query(TrackObject.class, null, null);
	}



}
