/*------------------------------------------------------------------------------
 **     Ident: Delivery Center Java
 **    Author: rene
 ** Copyright: (c) Feb 26, 2012 Sogeti Nederland B.V. All Rights Reserved.
 **------------------------------------------------------------------------------
 ** Sogeti Nederland B.V.            |  No part of this file may be reproduced  
 ** Distributed Software Engineering |  or transmitted in any form or by any        
 ** Lange Dreef 17                   |  means, electronic or mechanical, for the      
 ** 4131 NJ Vianen                   |  purpose, without the express written    
 ** The Netherlands                  |  permission of the copyright holder.
 *------------------------------------------------------------------------------
 */
package eu.iescities.pilot.rovereto.inbici.entities.track.logger.map;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Gallery;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrainingObject;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.Constants;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.DifferentTrackDialogBox;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.DifferentTrackDialogBox.AddTrack;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.GPSLoggerServiceManager;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.GPStracking.Media;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.GPStracking.Segments;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.GPStracking.Tracks;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.GPStracking.Waypoints;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.NewTrackDialogBox;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.SegmentRendering;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.SlidingIndicatorView;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.UnitsI18n;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.Overlay.BitmapSegmentsOverlay;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.statistics.StatisticsCalulator;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.statistics.StatisticsDelegate;
import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.FrechetDistance;
import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.implementations.polyhedral.PolyhedralFrechetDistance;
import eu.iescities.pilot.rovereto.inbici.utils.frechetdistance.algo.util.PolyhedralDistanceFunction;
import eu.trentorise.smartcampus.profileservice.BasicProfileService;
import eu.trentorise.smartcampus.profileservice.model.BasicProfile;
import eu.trentorise.smartcampus.storage.DataException;
import eu.trentorise.smartcampus.storage.StorageConfigurationException;

/**
 * ????
 * 
 * @version $Id:$
 * @author rene (c) Feb 26, 2012, Sogeti B.V.
 */
public class LoggerMapHelper implements StatisticsDelegate, AddTrack {

	public static final String OSM_PROVIDER = "OSM";
	public static final String GOOGLE_PROVIDER = "GOOGLE";
	public static final String MAPQUEST_PROVIDER = "MAPQUEST";

	private static final String INSTANCE_E6LONG = "e6long";
	private static final String INSTANCE_E6LAT = "e6lat";
	private static final String INSTANCE_ZOOM = "zoom";
	private static final String INSTANCE_AVGSPEED = "averagespeed";
	private static final String INSTANCE_HEIGHT = "averageheight";
	private static final String INSTANCE_TRACK = "track";
	private static final String INSTANCE_SPEED = "speed";
	private static final String INSTANCE_ALTITUDE = "altitude";
	private static final String INSTANCE_DISTANCE = "distance";

	private static final int ZOOM_LEVEL = 16;
	// MENU'S
	private static final int MENU_SETTINGS = 1;
	private static final int MENU_TRACKING = 2;
	private static final int MENU_TRACKLIST = 3;
	private static final int MENU_STATS = 4;
	private static final int MENU_ABOUT = 5;
	private static final int MENU_LAYERS = 6;
	private static final int MENU_NOTE = 7;
	private static final int MENU_SHARE = 13;
	private static final int MENU_CONTRIB = 14;
	private static final int MENU_TRACKING_START = 15;
	private static final int MENU_TRACKING_STOP = 16;
	private static final int MENU_TRACKING_PAUSE = 17;
	private static final int MENU_TRACKING_RESUME = 18;

	private static final int DIALOG_NOTRACK = 24;
	private static final int DIALOG_LAYERS = 31;
	private static final int DIALOG_URIS = 34;
	private static final int DIALOG_CONTRIB = 35;
	private static final String TAG = "OGT.LoggerMap";
	private static final double MINIMAL_EQUAL_DISTANCE = 0.002;

	private double mAverageSpeed = 33.33d / 3d;
	private double mAverageHeight = 33.33d / 3d;
	private long mTrackId = -1;
	private long mLastSegment = -1;
	private UnitsI18n mUnits;
	private WakeLock mWakeLock = null;
	private static SharedPreferences mSharedPreferences;
	private GPSLoggerServiceManager mLoggerServiceManager;
	private SegmentRendering mLastSegmentOverlay;
	private BaseAdapter mMediaAdapter;

	private Handler mHandler;

	private ContentObserver mTrackSegmentsObserver;
	private ContentObserver mSegmentWaypointsObserver;
	private ContentObserver mTrackMediasObserver;
	private DialogInterface.OnClickListener mNoTrackDialogListener;
	private OnItemSelectedListener mGalerySelectListener;
	private Uri mSelected;
	private OnClickListener mNoteSelectDialogListener;
	private OnCheckedChangeListener mCheckedChangeListener;
	private android.widget.RadioGroup.OnCheckedChangeListener mGroupCheckedChangeListener;
	private OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
	private UnitsI18n.UnitsChangeListener mUnitsChangeListener;

	/**
	 * Run after the ServiceManager completes the binding to the remote service
	 */
	private Runnable mServiceConnected;
	private Runnable speedCalculator;
	private Runnable heightCalculator;

	private LoggerMap mLoggerMap;
	private BitmapSegmentsOverlay mBitmapSegmentsOverlay;
	private float mSpeed;
	private double mAltitude;
	private float mDistance;
	// private BasicProfile mBp = null;
	private TrackObject mTrack = null;
	private TrainingObject mTraining = null;

	// private TrackObject track = null;

	public LoggerMapHelper(LoggerMap loggerMap) {
		mLoggerMap = loggerMap;
	}

	/**
	 * Called when the activity is first created.
	 */
	protected void onCreate(Bundle load) {

		mLoggerMap.setDrawingCacheEnabled(true);
		mUnits = new UnitsI18n(mLoggerMap.getActivity());
		mLoggerServiceManager = new GPSLoggerServiceManager(mLoggerMap.getActivity());

		final Semaphore calulatorSemaphore = new Semaphore(0);
		Thread calulator = new Thread("OverlayCalculator") {
			@Override
			public void run() {
				Looper.prepare();
				mHandler = new Handler();
				calulatorSemaphore.release();
				Looper.loop();
			}
		};
		calulator.start();
		try {
			calulatorSemaphore.acquire();
		} catch (InterruptedException e) {
			Log.e(TAG, "Failed waiting for a semaphore", e);
		}
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mLoggerMap.getActivity());

		mBitmapSegmentsOverlay = new BitmapSegmentsOverlay(mLoggerMap, mHandler);
		createListeners();
		onRestoreInstanceState(load);
		mLoggerMap.updateOverlays();

	}

	protected void onResume() {

		updateMapProvider();

		mLoggerServiceManager.startup(mLoggerMap.getActivity(), mServiceConnected, mLoggerMap.getActivity(), true);

		mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener);
		mUnits.setUnitsChangeListener(mUnitsChangeListener);
		updateTitleBar();
		updateBlankingBehavior();

		if (mTrackId >= 0) {
			ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
			Uri trackUri = Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId + "/segments");
			Uri lastSegmentUri = Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId + "/segments/" + mLastSegment
					+ "/waypoints");
			Uri mediaUri = ContentUris.withAppendedId(Media.CONTENT_URI, mTrackId);

			resolver.unregisterContentObserver(this.mTrackSegmentsObserver);
			resolver.unregisterContentObserver(this.mSegmentWaypointsObserver);
			resolver.unregisterContentObserver(this.mTrackMediasObserver);
			resolver.registerContentObserver(trackUri, false, this.mTrackSegmentsObserver);
			resolver.registerContentObserver(lastSegmentUri, true, this.mSegmentWaypointsObserver);
			resolver.registerContentObserver(mediaUri, true, this.mTrackMediasObserver);
			// getmTrack();
		}
		updateDataOverlays();
		updateSpeedColoring();
		updateSpeedDisplayVisibility();
		updateAltitudeDisplayVisibility();
		updateDistanceDisplayVisibility();
		updateCompassDisplayVisibility();
		updateLocationDisplayVisibility();

		updateTrackNumbers();

		mLoggerMap.executePostponedActions();
	}

	private void getmTrack() {
		if (mTrack == null)
			try {
				mTrack = InBiciHelper.findTrackById(String.valueOf(mTrackId));
			} catch (DataException e) {
				e.printStackTrace();
			} catch (StorageConfigurationException e) {
				e.printStackTrace();
			}
	}

	protected void onPause() {
		if (this.mWakeLock != null && this.mWakeLock.isHeld()) {
			this.mWakeLock.release();
			Log.w(TAG, "onPause(): Released lock to keep screen on!");
		}
		mLoggerMap.clearOverlays();
		mBitmapSegmentsOverlay.clearSegments();
		mLastSegmentOverlay = null;
		ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
		resolver.unregisterContentObserver(this.mTrackSegmentsObserver);
		resolver.unregisterContentObserver(this.mSegmentWaypointsObserver);
		resolver.unregisterContentObserver(this.mTrackMediasObserver);
		mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this.mSharedPreferenceChangeListener);
		mUnits.setUnitsChangeListener(null);
		mLoggerMap.disableMyLocation();
		mLoggerMap.disableCompass();
		this.mLoggerServiceManager.shutdown(mLoggerMap.getActivity());
	}

	protected void onDestroy() {
		mLoggerMap.clearOverlays();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Looper.myLooper().quit();
			}
		});

		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			Log.w(TAG, "onDestroy(): Released lock to keep screen on!");
		}
		if (mLoggerServiceManager.getLoggingState() == Constants.STOPPED) {
			mLoggerMap.getActivity().stopService(new Intent(Constants.SERVICENAME));
		}
		mUnits = null;
	}

	public void onNewIntent(Intent newIntent) {
		Uri data = newIntent.getData();
		if (data != null) {
			moveToTrack(Long.parseLong(data.getLastPathSegment()), true);
		}
	}

	protected void onRestoreInstanceState(Bundle load) {
		Uri data = mLoggerMap.getActivity().getIntent().getData();
		if (load != null && load.containsKey(INSTANCE_TRACK)) // 1st method:
																// track from a
																// previous
																// instance of
																// this activity
		{
			long loadTrackId = load.getLong(INSTANCE_TRACK);
			moveToTrack(loadTrackId, false);
			if (load.containsKey(INSTANCE_AVGSPEED)) {
				mAverageSpeed = load.getDouble(INSTANCE_AVGSPEED);
			}
			if (load.containsKey(INSTANCE_HEIGHT)) {
				mAverageHeight = load.getDouble(INSTANCE_HEIGHT);
			}
			if (load.containsKey(INSTANCE_SPEED)) {
				mSpeed = load.getFloat(INSTANCE_SPEED);
			}
			if (load.containsKey(INSTANCE_ALTITUDE)) {
				mAltitude = load.getDouble(INSTANCE_HEIGHT);
			}
			if (load.containsKey(INSTANCE_DISTANCE)) {
				mDistance = load.getFloat(INSTANCE_DISTANCE);
			}
		} else if (data != null) // 2nd method: track ordered to make
		{
			long loadTrackId = Long.parseLong(data.getLastPathSegment());
			moveToTrack(loadTrackId, true);
		}
		// else
		// 3rd method: just try the last track
		// {
		// // check if it is a new start then move to the next
		// if ((mLoggerServiceManager.getLoggingState() == Constants.STOPPED
		// || mLoggerServiceManager.getLoggingState() ==
		// Constants.UNKNOWN)){
		// moveToTheNewTrack();
		// }
		// else{
		// moveToLastTrack();
		// }
		// }

		if (load != null && load.containsKey(INSTANCE_ZOOM)) {
			mLoggerMap.setZoom(load.getInt(INSTANCE_ZOOM));
		} else {
			mLoggerMap.setZoom(ZOOM_LEVEL);
		}

		if (load != null && load.containsKey(INSTANCE_E6LAT) && load.containsKey(INSTANCE_E6LONG)) {
			GeoPoint storedPoint = new GeoPoint(load.getInt(INSTANCE_E6LAT), load.getInt(INSTANCE_E6LONG));
			mLoggerMap.animateTo(storedPoint);
		} else {
			GeoPoint lastPoint = getLastTrackPoint();
			mLoggerMap.animateTo(lastPoint);
		}
	}

	protected void onSaveInstanceState(Bundle save) {
		save.putLong(INSTANCE_TRACK, this.mTrackId);
		save.putDouble(INSTANCE_AVGSPEED, mAverageSpeed);
		save.putDouble(INSTANCE_HEIGHT, mAverageHeight);
		save.putInt(INSTANCE_ZOOM, mLoggerMap.getZoomLevel());
		save.putFloat(INSTANCE_SPEED, mSpeed);
		save.putDouble(INSTANCE_ALTITUDE, mAltitude);
		save.putFloat(INSTANCE_DISTANCE, mDistance);
		GeoPoint point = mLoggerMap.getMapCenter();
		save.putInt(INSTANCE_E6LAT, point.getLatitudeE6());
		save.putInt(INSTANCE_E6LONG, point.getLongitudeE6());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean propagate = true;
		switch (keyCode) {
		case KeyEvent.KEYCODE_T:
			propagate = mLoggerMap.zoomIn();
			propagate = false;
			break;
		case KeyEvent.KEYCODE_G:
			propagate = mLoggerMap.zoomOut();
			propagate = false;
			break;
		case KeyEvent.KEYCODE_F:
			moveToTrack(this.mTrackId - 1, true);
			propagate = false;
			break;
		case KeyEvent.KEYCODE_H:
			moveToTrack(this.mTrackId + 1, true);
			propagate = false;
			break;
		}
		return propagate;
	}

	private void setSpeedOverlay(boolean b) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(Constants.SPEED, b);
		editor.commit();
	}

	private void setAltitudeOverlay(boolean b) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(Constants.ALTITUDE, b);
		editor.commit();
	}

	private void setDistanceOverlay(boolean b) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(Constants.DISTANCE, b);
		editor.commit();
	}

	private void setCompassOverlay(boolean b) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(Constants.COMPASS, b);
		editor.commit();
	}

	private void setLocationOverlay(boolean b) {
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(Constants.LOCATION, b);
		editor.commit();
	}

	private void setOsmBaseOverlay(int b) {
		Editor editor = mSharedPreferences.edit();
		editor.putInt(Constants.OSMBASEOVERLAY, b);
		editor.commit();
	}

	private void createListeners() {
		/*******************************************************
		 * 8 Runnable listener actions
		 */
		speedCalculator = new Runnable() {
			@Override
			public void run() {
				double avgspeed = 0.0;
				ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
				Cursor waypointsCursor = null;
				try {
					waypointsCursor = resolver.query(
							Uri.withAppendedPath(Tracks.CONTENT_URI, LoggerMapHelper.this.mTrackId + "/waypoints"),
							new String[] { "avg(" + Waypoints.SPEED + ")", "max(" + Waypoints.SPEED + ")" }, null,
							null, null);

					if (waypointsCursor != null && waypointsCursor.moveToLast()) {
						double average = waypointsCursor.getDouble(0);
						double maxBasedAverage = waypointsCursor.getDouble(1) / 2;
						avgspeed = Math.min(average, maxBasedAverage);
					}
					if (avgspeed < 2) {
						avgspeed = 5.55d / 2;
					}
				} finally {
					if (waypointsCursor != null) {
						waypointsCursor.close();
					}
				}
				mAverageSpeed = avgspeed;
				mLoggerMap.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateSpeedColoring();
					}
				});
			}
		};
		heightCalculator = new Runnable() {
			@Override
			public void run() {
				double avgHeight = 0.0;
				ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
				Cursor waypointsCursor = null;
				try {
					waypointsCursor = resolver.query(
							Uri.withAppendedPath(Tracks.CONTENT_URI, LoggerMapHelper.this.mTrackId + "/waypoints"),
							new String[] { "avg(" + Waypoints.ALTITUDE + ")", "max(" + Waypoints.ALTITUDE + ")" },
							null, null, null);

					if (waypointsCursor != null && waypointsCursor.moveToLast()) {
						double average = waypointsCursor.getDouble(0);
						double maxBasedAverage = waypointsCursor.getDouble(1) / 2;
						avgHeight = Math.min(average, maxBasedAverage);
					}
				} finally {
					if (waypointsCursor != null) {
						waypointsCursor.close();
					}
				}
				mAverageHeight = avgHeight;
				mLoggerMap.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateSpeedColoring();
					}
				});
			}
		};
		mServiceConnected = new Runnable() {
			@Override
			public void run() {
				updateBlankingBehavior();
			}
		};
		/*******************************************************
		 * 8 Various dialog listeners
		 */
		mGalerySelectListener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				mSelected = (Uri) parent.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				mSelected = null;
			}
		};
		mNoteSelectDialogListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				SegmentRendering.handleMedia(mLoggerMap.getActivity(), mSelected);
				mSelected = null;
			}
		};
		mGroupCheckedChangeListener = new android.widget.RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.layer_osm_cloudmade:
					setOsmBaseOverlay(Constants.OSM_CLOUDMADE);
					break;
				case R.id.layer_osm_maknik:
					setOsmBaseOverlay(Constants.OSM_MAKNIK);
					break;
				case R.id.layer_osm_bicycle:
					setOsmBaseOverlay(Constants.OSM_CYCLE);
					break;
				default:
					mLoggerMap.onLayerCheckedChanged(checkedId, true);
					break;
				}
			}
		};
		mCheckedChangeListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int checkedId;
				checkedId = buttonView.getId();
				switch (checkedId) {
				case R.id.layer_speed:
					setSpeedOverlay(isChecked);
					break;
				case R.id.layer_altitude:
					setAltitudeOverlay(isChecked);
					break;
				case R.id.layer_distance:
					setDistanceOverlay(isChecked);
					break;
				case R.id.layer_compass:
					setCompassOverlay(isChecked);
					break;
				case R.id.layer_location:
					setLocationOverlay(isChecked);
					break;
				default:
					mLoggerMap.onLayerCheckedChanged(checkedId, isChecked);
					break;
				}
			}
		};
		mNoTrackDialogListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Log.d( TAG, "mNoTrackDialogListener" + which);
				// Intent tracklistIntent = new Intent(mLoggerMap.getActivity(),
				// TrackList.class);
				// tracklistIntent.putExtra(Tracks._ID, mTrackId);
				// mLoggerMap.getActivity().startActivityForResult(tracklistIntent,
				// MENU_TRACKLIST);
			}
		};
		/**
		 * Listeners to events outside this mapview
		 */
		mSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.equals(Constants.TRACKCOLORING)) {
					mAverageSpeed = 0.0;
					mAverageHeight = 0.0;
					updateSpeedColoring();
				} else if (key.equals(Constants.DISABLEBLANKING) || key.equals(Constants.DISABLEDIMMING)) {
					updateBlankingBehavior();
				} else if (key.equals(Constants.SPEED)) {
					updateSpeedDisplayVisibility();
				} else if (key.equals(Constants.ALTITUDE)) {
					updateAltitudeDisplayVisibility();
				} else if (key.equals(Constants.DISTANCE)) {
					updateDistanceDisplayVisibility();
				} else if (key.equals(Constants.COMPASS)) {
					updateCompassDisplayVisibility();
				} else if (key.equals(Constants.LOCATION)) {
					updateLocationDisplayVisibility();
				} else if (key.equals(Constants.MAPPROVIDER)) {
					updateMapProvider();
				} else if (key.equals(Constants.OSMBASEOVERLAY)) {
					mLoggerMap.updateOverlays();
				} else {
					mLoggerMap.onSharedPreferenceChanged(sharedPreferences, key);
				}
			}
		};
		mTrackMediasObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfUpdate) {
				if (!selfUpdate) {
					if (mLastSegmentOverlay != null) {
						mLastSegmentOverlay.calculateMedia();
					}
				} else {
					Log.w(TAG, "mTrackMediasObserver skipping change on " + mLastSegment);
				}
			}
		};
		mTrackSegmentsObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfUpdate) {
				if (!selfUpdate) {
					updateDataOverlays();
				} else {
					Log.w(TAG, "mTrackSegmentsObserver skipping change on " + mLastSegment);
				}
			}
		};
		mSegmentWaypointsObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfUpdate) {
				if (!selfUpdate) {
					updateTrackNumbers();
					if (mLastSegmentOverlay != null) {
						moveActiveViewWindow();
						updateMapProviderAdministration(mLoggerMap.getDataSourceId());
					} else {
						Log.e(TAG, "Error the last segment changed but it is not on screen! " + mLastSegment);
					}
				} else {
					Log.w(TAG, "mSegmentWaypointsObserver skipping change on " + mLastSegment);
				}
			}
		};
		mUnitsChangeListener = new UnitsI18n.UnitsChangeListener() {
			@Override
			public void onUnitsChange() {
				mAverageSpeed = 0.0;
				mAverageHeight = 0.0;
				updateTrackNumbers();
				updateSpeedColoring();
			}
		};
	}

	public void onCreateOptionsMenu(Menu menu, GPSLoggerServiceManager mLoggerServiceManager) {
		menu.clear();

		// called also if new service is running
		refreshNewTrack();

		// check if it is running or not
		switch (mLoggerServiceManager.getLoggingState()) {
		case Constants.STOPPED:
			// i need only start
			menu.add(ContextMenu.NONE, MENU_TRACKING_START, ContextMenu.NONE, R.string.menu_start)
					.setIcon(android.R.drawable.ic_media_play).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			break;
		case Constants.LOGGING:
			// pause and stop

			menu.add(ContextMenu.NONE, MENU_TRACKING_STOP, ContextMenu.NONE, R.string.menu_stop)
					.setIcon(R.drawable.ic_action_stop).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(ContextMenu.NONE, MENU_TRACKING_PAUSE, ContextMenu.NONE, R.string.menu_pause)
					.setIcon(android.R.drawable.ic_media_pause).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			break;
		case Constants.PAUSED:
			// stop or start
			menu.add(ContextMenu.NONE, MENU_TRACKING_STOP, ContextMenu.NONE, R.string.menu_stop)
					.setIcon(R.drawable.ic_action_stop).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(ContextMenu.NONE, MENU_TRACKING_RESUME, ContextMenu.NONE, R.string.menu_start)
					.setIcon(android.R.drawable.ic_media_play).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			break;
		default:
			break;

		}

	}

	public void onPrepareOptionsMenu(Menu menu, GPSLoggerServiceManager mLoggerServiceManager) {
		menu.clear();
		// check if it is running or not
		switch (mLoggerServiceManager.getLoggingState()) {
		case Constants.STOPPED:
			// i need only start
			menu.add(ContextMenu.NONE, MENU_TRACKING_START, ContextMenu.NONE, R.string.menu_start)
					.setIcon(android.R.drawable.ic_media_play).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			break;
		case Constants.LOGGING:
			// pause and stop
			menu.add(ContextMenu.NONE, MENU_TRACKING_STOP, ContextMenu.NONE, R.string.menu_stop)
					.setIcon(R.drawable.ic_action_stop).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(ContextMenu.NONE, MENU_TRACKING_PAUSE, ContextMenu.NONE, R.string.menu_pause)
					.setIcon(android.R.drawable.ic_media_pause).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			break;
		case Constants.PAUSED:
			// stop or start
			menu.add(ContextMenu.NONE, MENU_TRACKING_STOP, ContextMenu.NONE, R.string.menu_stop)
					.setIcon(R.drawable.ic_action_stop).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(ContextMenu.NONE, MENU_TRACKING_RESUME, ContextMenu.NONE, R.string.menu_start)
					.setIcon(android.R.drawable.ic_media_play).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			break;
		default:
			break;
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = false;

		switch (item.getItemId()) {
		case MENU_TRACKING_START:
			mLoggerServiceManager.startGPSLogging(null);
			break;
		case MENU_TRACKING_RESUME:
			mLoggerServiceManager.resumeGPSLogging();
			break;
		case MENU_TRACKING_STOP:
			mLoggerServiceManager.stopGPSLogging();
			mLoggerServiceManager.shutdown(mLoggerMap.getActivity());

			// start the saving training and check if also it is a new track
			// check stop point

			saveTraining();
			break;
		case MENU_TRACKING_PAUSE:
			mLoggerServiceManager.pauseGPSLogging();
			break;
		}
		mLoggerMap.getActivity().invalidateOptionsMenu();
		return handled;
	}

	public static String queryForTrackName(ContentResolver resolver, Uri trackUri) {
		Cursor trackCursor = null;
		String name = null;

		try {
			trackCursor = resolver.query(trackUri, new String[] { Tracks.NAME }, null, null, null);
			if (trackCursor.moveToFirst()) {
				name = trackCursor.getString(0);
			}
		} finally {
			if (trackCursor != null) {
				trackCursor.close();
			}
		}
		return name;
	}

	// get track from service
	Cursor tracksCursor = null;

	private void saveTraining() {
		// Intent intent = new Intent();
		// get last
		tracksCursor = mLoggerMap.getActivity().managedQuery(Tracks.CONTENT_URI,
				new String[] { Tracks._ID, Tracks.NAME, Tracks.CREATION_TIME }, null, null,
				Tracks.CREATION_TIME + " DESC");
		Uri trackUri = ContentUris.withAppendedId(Tracks.CONTENT_URI, tracksCursor.getCount());
		// intent.setData(trackUri);
		mUnits = new UnitsI18n(mLoggerMap.getActivity(), null);
		StatisticsCalulator calculator = new StatisticsCalulator(mLoggerMap.getActivity(), mUnits, this);
		calculator.execute(trackUri);

	}

	@Override
	public void finishedCalculations(StatisticsCalulator calculated) {
		TrackObject oldTrack = null;
		if (mTrack == null)
			mTrack = new TrackObject();
		else
			oldTrack = mTrack;

		if (mTraining == null)
			mTraining = new TrainingObject();
		Uri segmentsUri = Uri.withAppendedPath(Tracks.CONTENT_URI, tracksCursor.getCount() + "/segments");
		Cursor segmentsCursor = mLoggerMap.getActivity().getContentResolver()
				.query(segmentsUri, new String[] { Segments._ID }, null, null, null);
		// i have segments, I need waypoints
		List<LatLng> decodedLine = new ArrayList<LatLng>();
		LatLng firstPosition = null;
		if (segmentsCursor != null && segmentsCursor.moveToFirst()) {
			do {
				long segmentsId = segmentsCursor.getLong(0);
				Uri segmentUri = ContentUris.withAppendedId(segmentsUri, segmentsId);
				Uri mWaypointsUri = Uri.withAppendedPath(segmentUri, "waypoints");

				Cursor mWaypointsCursor = mLoggerMap
						.getActivity()
						.getContentResolver()
						.query(mWaypointsUri,
								new String[] { Waypoints.LATITUDE, Waypoints.LONGITUDE, Waypoints.SPEED,
										Waypoints.TIME, Waypoints.ACCURACY, Waypoints.ALTITUDE }, null, null, null);
				if (mWaypointsCursor.moveToFirst())
					do {

						LatLng waypoint = new LatLng(mWaypointsCursor.getDouble(0), mWaypointsCursor.getDouble(1));
						if (firstPosition == null)
							firstPosition = waypoint;
						decodedLine.add(waypoint);
					} while (mWaypointsCursor.moveToNext());
			} while (segmentsCursor.moveToNext());
		}
		// set points for track
		mTrack.encodedLine(decodedLine);
		mTrack.setCreator(InBiciHelper.getBasicProfile().getUserId());
		String idTrack = null;
		// set training;
		mTraining.setStartTime(calculated.getStarttime());
		mTraining.setEndTime(calculated.getEndtime());
		mTraining.setElevation(calculated.getAscension());
		mTraining.setMaxSpeed(calculated.getMaxSpeed());
		mTraining.setRunningTime((double) calculated.getDuration());
		mTraining.setDistance(calculated.getDistanceTraveled());
		mTraining.setAvgSpeed(calculated.getAverageStatisicsSpeed());

		// new track and add training

		if ((idTrack = checkNewTrack()) == null) {
			InBiciHelper.removeNewTrackStart(getPreferences());
			mTrack.setTotal_elevation(mTraining.getElevation());
			NewTrackDialogBox.newtrackfound(mLoggerMap.getActivity(), mTrack, this);
		} else if (InBiciHelper.hadDifferentStartPlace(getPreferences())
				|| (oldTrack != null && !isSameTrack(oldTrack.decodedLine(), decodedLine)) || !isSameEndPlace()) {
			InBiciHelper.removeDifferentStartPlace(getPreferences());
			mTrack.setTotal_elevation(mTraining.getElevation());
			DifferentTrackDialogBox.newtrackfound(mLoggerMap.getActivity(), getPreferences(), mTrack, this,
					mLoggerServiceManager, InBiciHelper.getBasicProfile(), decodedLine);

			// showSaveDialog();
			InBiciHelper.removeTrackIdFromSP(getPreferences());

		}
		// otherwise -> add a new training on it
		else {
			if (InBiciHelper.hadDifferentStartPlace(getPreferences())) {
				mTrack.setId(idTrack);
				InBiciHelper.removeTrackIdFromSP(getPreferences());
				updateOldTrack(mTrack);
			}
			mTraining.setTrackId(idTrack);
			addNewTraining(mTraining);
		}

	}

	private void updateOldTrack(TrackObject mTrack2) {
		new UpdateOldTrackAsyncTask().execute(mTrack2);

	}

	private void addNewTraining(TrainingObject training) {
		new SaveTrainingAsyncTask().execute(training);

	}

	private class SaveTrainingAsyncTask extends AsyncTask<TrainingObject, Void, Void> {
		private Exception e = null;

		@Override
		protected Void doInBackground(TrainingObject... params) {
			InBiciHelper.saveNewTraining(params[0]);

			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mLoggerMap.getActivity().finish();
		}

	}

	private class UpdateOldTrackAsyncTask extends AsyncTask<TrackObject, Void, Void> {
		private Exception e = null;

		@Override
		protected Void doInBackground(TrackObject... params) {
			InBiciHelper.modifyTrack(params[0]);
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}

	}

	@Override
	public void addNewTrack(TrackObject track) {
		new SaveTrackAsyncTask().execute(track);

	}

	private void clearLogFromMap() {
		mLoggerMap.clearOverlays();
		mBitmapSegmentsOverlay.clearSegments();
		((MapQuestLoggerMap) mLoggerMap).onDateOverlayChanged();
	}

	private String checkNewTrack() {
		// check if shared preferences is present
		return InBiciHelper.getTrackIdFromSP(getPreferences());

	}

	private class SaveTrackAsyncTask extends AsyncTask<TrackObject, Void, TrackObject> {
		private TrackObject returnTrack = null;
		private ProgressDialog progress = null;
		private Exception e = null;

		@Override
		protected TrackObject doInBackground(TrackObject... params) {
			returnTrack = InBiciHelper.saveNewTrack(params[0]);
			return returnTrack;

		}

		protected void onPostExecute(TrackObject result) {
			if (progress != null) {
				try {
					progress.dismiss();
				} catch (Exception e) {
					Log.w(getClass().getName(), "Problem closing progress dialog: " + e.getMessage());
				}
			}
			if (result != null && mTraining != null) {
				mTraining.setTrackId(result.getId());
				addNewTraining(mTraining);
			}
			if (mTraining == null) {
				// in the case I created a new track because it is different
				// from what I have choose
				mTraining = new TrainingObject();
				InBiciHelper.addTrackIdFromSP(getPreferences(), result.getId());
			}
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(mLoggerMap.getActivity(), "",
					mLoggerMap.getActivity().getString(R.string.progress_loading), true);
			if (mTrack != null)
				check_location(mTrack);

			super.onPreExecute();
		}

		private void check_location(TrackObject track) {
			if (track.getLocation() == null) {
				// get Start Position
				double[] locations = new double[2];

				if (track.decodedLine() != null && track.decodedLine().size() > 0 && track.decodedLine().get(0) != null) {
					// GeoPoint lastloc = getLastKnowGeopointLocation();
					locations[0] = track.decodedLine().get(0).latitude;
					locations[1] = track.decodedLine().get(0).longitude;
				} else {
					// it is the beginning of the track
					GeoPoint lastloc = getLastKnowGeopointLocation();
					locations[0] = lastloc.getLatitudeE6();
					locations[1] = lastloc.getLongitudeE6();
				}
				track.setLocation(locations);
			}
		}
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		LayoutInflater factory = null;
		View view = null;
		Builder builder = null;
		switch (id) {
		case DIALOG_LAYERS:
			builder = new AlertDialog.Builder(mLoggerMap.getActivity());
			factory = LayoutInflater.from(mLoggerMap.getActivity());
			view = factory.inflate(R.layout.layerdialog, null);

			CheckBox traffic = (CheckBox) view.findViewById(R.id.layer_traffic);
			CheckBox speed = (CheckBox) view.findViewById(R.id.layer_speed);
			CheckBox altitude = (CheckBox) view.findViewById(R.id.layer_altitude);
			CheckBox distance = (CheckBox) view.findViewById(R.id.layer_distance);
			CheckBox compass = (CheckBox) view.findViewById(R.id.layer_compass);
			CheckBox location = (CheckBox) view.findViewById(R.id.layer_location);

			((RadioGroup) view.findViewById(R.id.google_backgrounds))
					.setOnCheckedChangeListener(mGroupCheckedChangeListener);
			((RadioGroup) view.findViewById(R.id.osm_backgrounds))
					.setOnCheckedChangeListener(mGroupCheckedChangeListener);

			traffic.setOnCheckedChangeListener(mCheckedChangeListener);
			speed.setOnCheckedChangeListener(mCheckedChangeListener);
			altitude.setOnCheckedChangeListener(mCheckedChangeListener);
			distance.setOnCheckedChangeListener(mCheckedChangeListener);
			compass.setOnCheckedChangeListener(mCheckedChangeListener);
			location.setOnCheckedChangeListener(mCheckedChangeListener);

			builder.setTitle(R.string.dialog_layer_title).setIcon(android.R.drawable.ic_dialog_map)
					.setPositiveButton(R.string.btn_okay, null).setView(view);
			dialog = builder.create();
			return dialog;
		case DIALOG_NOTRACK:
			builder = new AlertDialog.Builder(mLoggerMap.getActivity());
			builder.setTitle(R.string.dialog_notrack_title).setMessage(R.string.dialog_notrack_message)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(R.string.btn_selecttrack, mNoTrackDialogListener)
					.setNegativeButton(R.string.btn_cancel, null);
			dialog = builder.create();
			return dialog;
		case DIALOG_URIS:
			builder = new AlertDialog.Builder(mLoggerMap.getActivity());
			factory = LayoutInflater.from(mLoggerMap.getActivity());
			view = factory.inflate(R.layout.mediachooser, null);
			builder.setTitle(R.string.dialog_select_media_title).setMessage(R.string.dialog_select_media_message)
					.setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton(R.string.btn_cancel, null)
					.setPositiveButton(R.string.btn_okay, mNoteSelectDialogListener).setView(view);
			dialog = builder.create();
			return dialog;
		case DIALOG_CONTRIB:
			builder = new AlertDialog.Builder(mLoggerMap.getActivity());
			factory = LayoutInflater.from(mLoggerMap.getActivity());
			view = factory.inflate(R.layout.contrib, null);
			TextView contribView = (TextView) view.findViewById(R.id.contrib_view);
			contribView.setText(R.string.dialog_contrib_message);
			builder.setTitle(R.string.dialog_contrib_title).setView(view).setIcon(android.R.drawable.ic_dialog_email)
					.setPositiveButton(R.string.btn_okay, null);
			dialog = builder.create();
			return dialog;
		default:
			return null;
		}
	}

	protected void onPrepareDialog(int id, Dialog dialog) {
		RadioButton satellite;
		RadioButton regular;
		RadioButton cloudmade;
		RadioButton mapnik;
		RadioButton cycle;
		switch (id) {
		case DIALOG_LAYERS:
			satellite = (RadioButton) dialog.findViewById(R.id.layer_google_satellite);
			regular = (RadioButton) dialog.findViewById(R.id.layer_google_regular);
			satellite.setChecked(mSharedPreferences.getBoolean(Constants.SATELLITE, false));
			regular.setChecked(!mSharedPreferences.getBoolean(Constants.SATELLITE, false));

			int osmbase = mSharedPreferences.getInt(Constants.OSMBASEOVERLAY, 0);
			cloudmade = (RadioButton) dialog.findViewById(R.id.layer_osm_cloudmade);
			mapnik = (RadioButton) dialog.findViewById(R.id.layer_osm_maknik);
			cycle = (RadioButton) dialog.findViewById(R.id.layer_osm_bicycle);
			cloudmade.setChecked(osmbase == Constants.OSM_CLOUDMADE);
			mapnik.setChecked(osmbase == Constants.OSM_MAKNIK);
			cycle.setChecked(osmbase == Constants.OSM_CYCLE);

			((CheckBox) dialog.findViewById(R.id.layer_traffic)).setChecked(mSharedPreferences.getBoolean(
					Constants.TRAFFIC, false));
			((CheckBox) dialog.findViewById(R.id.layer_speed)).setChecked(mSharedPreferences.getBoolean(
					Constants.SPEED, false));
			((CheckBox) dialog.findViewById(R.id.layer_altitude)).setChecked(mSharedPreferences.getBoolean(
					Constants.ALTITUDE, false));
			((CheckBox) dialog.findViewById(R.id.layer_distance)).setChecked(mSharedPreferences.getBoolean(
					Constants.DISTANCE, false));
			((CheckBox) dialog.findViewById(R.id.layer_compass)).setChecked(mSharedPreferences.getBoolean(
					Constants.COMPASS, false));
			((CheckBox) dialog.findViewById(R.id.layer_location)).setChecked(mSharedPreferences.getBoolean(
					Constants.LOCATION, false));
			int provider = Integer.valueOf(mSharedPreferences.getString(Constants.MAPPROVIDER, "" + Constants.OSM))
					.intValue();
			switch (provider) {
			case Constants.GOOGLE:
				dialog.findViewById(R.id.google_backgrounds).setVisibility(View.VISIBLE);
				dialog.findViewById(R.id.osm_backgrounds).setVisibility(View.GONE);
				dialog.findViewById(R.id.shared_layers).setVisibility(View.VISIBLE);
				dialog.findViewById(R.id.google_overlays).setVisibility(View.VISIBLE);
				break;
			case Constants.OSM:
				dialog.findViewById(R.id.osm_backgrounds).setVisibility(View.VISIBLE);
				dialog.findViewById(R.id.google_backgrounds).setVisibility(View.GONE);
				dialog.findViewById(R.id.shared_layers).setVisibility(View.VISIBLE);
				dialog.findViewById(R.id.google_overlays).setVisibility(View.GONE);
				break;
			default:
				dialog.findViewById(R.id.osm_backgrounds).setVisibility(View.GONE);
				dialog.findViewById(R.id.google_backgrounds).setVisibility(View.GONE);
				dialog.findViewById(R.id.shared_layers).setVisibility(View.VISIBLE);
				dialog.findViewById(R.id.google_overlays).setVisibility(View.GONE);
				break;
			}
			break;
		case DIALOG_URIS:
			Gallery gallery = (Gallery) dialog.findViewById(R.id.gallery);
			gallery.setAdapter(mMediaAdapter);
			gallery.setOnItemSelectedListener(mGalerySelectListener);
		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Uri trackUri;
		long trackId;
		switch (requestCode) {
		case MENU_TRACKLIST:
			if (resultCode == Activity.RESULT_OK) {
				trackUri = intent.getData();
				trackId = Long.parseLong(trackUri.getLastPathSegment());
				moveToTrack(trackId, true);
			}
			break;
		case MENU_TRACKING:
			if (resultCode == Activity.RESULT_OK) {
				trackUri = intent.getData();
				if (trackUri != null) {
					trackId = Long.parseLong(trackUri.getLastPathSegment());
					moveToTrack(trackId, true);
				}
			}
			break;
		// case MENU_SHARE:
		// ShareTrack.clearScreenBitmap();
		// break;
		default:
			Log.e(TAG, "Returned form unknow activity: " + requestCode);
			break;
		}
	}

	private void updateTitleBar() {
		ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
		Cursor trackCursor = null;
		try {
			trackCursor = resolver.query(ContentUris.withAppendedId(Tracks.CONTENT_URI, this.mTrackId),
					new String[] { Tracks.NAME }, null, null, null);
			if (trackCursor != null && trackCursor.moveToLast()) {
				String trackName = trackCursor.getString(0);
				if (trackName == null)
					mLoggerMap.getActivity().setTitle(mLoggerMap.getActivity().getString(R.string.app_name));
				else
					mLoggerMap.getActivity().setTitle(
							mLoggerMap.getActivity().getString(R.string.app_name) + ": " + trackName);
			}
		} finally {
			if (trackCursor != null) {
				trackCursor.close();
			}
		}
	}

	private void updateMapProvider() {
		Class<?> mapClass = null;
		int provider = Integer.valueOf(mSharedPreferences.getString(Constants.MAPPROVIDER, "" + Constants.MAPQUEST))
				.intValue();
		switch (provider) {

		case Constants.MAPQUEST:
			mapClass = MapQuestLoggerMap.class;
			break;
		default:
			mapClass = MapQuestLoggerMap.class;
			Log.e(TAG, "Fault in value " + provider + " as MapProvider, defaulting to Google Maps.");
			break;
		}
		if (mapClass != mLoggerMap.getActivity().getClass()) {
			Intent myIntent = mLoggerMap.getActivity().getIntent();
			Intent realIntent;
			if (myIntent != null) {
				realIntent = new Intent(myIntent.getAction(), myIntent.getData(), mLoggerMap.getActivity(), mapClass);
				realIntent.putExtras(myIntent);
			} else {
				realIntent = new Intent(mLoggerMap.getActivity(), mapClass);
				realIntent.putExtras(myIntent);
			}
			mLoggerMap.getActivity().startActivity(realIntent);
			mLoggerMap.getActivity().finish();
		}
	}

	protected void updateMapProviderAdministration(String provider) {
		mLoggerServiceManager.storeDerivedDataSource(provider);
	}

	private void updateBlankingBehavior() {
		boolean disableblanking = mSharedPreferences.getBoolean(Constants.DISABLEBLANKING, false);
		boolean disabledimming = mSharedPreferences.getBoolean(Constants.DISABLEDIMMING, false);
		if (disableblanking) {
			if (mWakeLock == null) {
				PowerManager pm = (PowerManager) mLoggerMap.getActivity().getSystemService(Context.POWER_SERVICE);
				if (disabledimming) {
					mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
				} else {
					mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
				}
			}
			if (mLoggerServiceManager.getLoggingState() == Constants.LOGGING && !mWakeLock.isHeld()) {
				mWakeLock.acquire();
				Log.w(TAG, "Acquired lock to keep screen on!");
			}
		}
	}

	private void updateSpeedColoring() {
		int trackColoringMethod = Integer.valueOf(mSharedPreferences.getString(Constants.TRACKCOLORING, "3"))
				.intValue();
		View speedbar = mLoggerMap.getActivity().findViewById(R.id.speedbar);
		SlidingIndicatorView scaleIndicator = mLoggerMap.getScaleIndicatorView();

		TextView[] speedtexts = mLoggerMap.getSpeedTextViews();
		switch (trackColoringMethod) {
		case SegmentRendering.DRAW_MEASURED:
		case SegmentRendering.DRAW_CALCULATED:
			// mAverageSpeed is set to 0 if unknown or to trigger an
			// recalculation here
			if (mAverageSpeed == 0.0) {
				mHandler.removeCallbacks(speedCalculator);
				mHandler.post(speedCalculator);
			} else {
				drawSpeedTexts();
				speedtexts = mLoggerMap.getSpeedTextViews();
				speedbar.setVisibility(View.VISIBLE);
				scaleIndicator.setVisibility(View.VISIBLE);
				for (int i = 0; i < speedtexts.length; i++) {
					speedtexts[i].setVisibility(View.VISIBLE);
				}
			}
			break;
		case SegmentRendering.DRAW_DOTS:
		case SegmentRendering.DRAW_GREEN:
		case SegmentRendering.DRAW_RED:
			speedbar.setVisibility(View.INVISIBLE);
			scaleIndicator.setVisibility(View.INVISIBLE);
			for (int i = 0; i < speedtexts.length; i++) {
				speedtexts[i].setVisibility(View.INVISIBLE);
			}
			break;
		case SegmentRendering.DRAW_HEIGHT:
			if (mAverageHeight == 0.0) {
				mHandler.removeCallbacks(heightCalculator);
				mHandler.post(heightCalculator);
			} else {
				drawHeightTexts();
				speedtexts = mLoggerMap.getSpeedTextViews();
				speedbar.setVisibility(View.VISIBLE);
				scaleIndicator.setVisibility(View.VISIBLE);
				for (int i = 0; i < speedtexts.length; i++) {
					speedtexts[i].setVisibility(View.VISIBLE);
				}
			}
			break;
		default:
			break;
		}
		mBitmapSegmentsOverlay.setTrackColoringMethod(trackColoringMethod, mAverageSpeed, mAverageHeight);
	}

	private void updateSpeedDisplayVisibility() {
		boolean showspeed = mSharedPreferences.getBoolean(Constants.SPEED, false);
		TextView lastGPSSpeedView = mLoggerMap.getSpeedTextView();
		if (showspeed) {
			lastGPSSpeedView.setVisibility(View.VISIBLE);
		} else {
			lastGPSSpeedView.setVisibility(View.GONE);
		}
		updateScaleDisplayVisibility();
	}

	private void updateAltitudeDisplayVisibility() {
		boolean showaltitude = mSharedPreferences.getBoolean(Constants.ALTITUDE, false);
		TextView lastGPSAltitudeView = mLoggerMap.getAltitideTextView();
		if (showaltitude) {
			lastGPSAltitudeView.setVisibility(View.VISIBLE);
		} else {
			lastGPSAltitudeView.setVisibility(View.GONE);
		}
		updateScaleDisplayVisibility();
	}

	private void updateScaleDisplayVisibility() {
		SlidingIndicatorView scaleIndicator = mLoggerMap.getScaleIndicatorView();
		boolean showspeed = mSharedPreferences.getBoolean(Constants.SPEED, false);
		boolean showaltitude = mSharedPreferences.getBoolean(Constants.ALTITUDE, false);
		int trackColoringMethod = Integer.valueOf(mSharedPreferences.getString(Constants.TRACKCOLORING, "3"))
				.intValue();
		switch (trackColoringMethod) {
		case SegmentRendering.DRAW_MEASURED:
		case SegmentRendering.DRAW_CALCULATED:
			if (showspeed) {
				scaleIndicator.setVisibility(View.VISIBLE);
			} else {
				scaleIndicator.setVisibility(View.GONE);
			}
			break;
		case SegmentRendering.DRAW_HEIGHT:
		default:
			if (showaltitude) {
				scaleIndicator.setVisibility(View.VISIBLE);
			} else {
				scaleIndicator.setVisibility(View.GONE);
			}
			break;
		}
	}

	private void updateDistanceDisplayVisibility() {
		boolean showdistance = mSharedPreferences.getBoolean(Constants.DISTANCE, false);
		TextView distanceView = mLoggerMap.getDistanceTextView();
		if (showdistance) {
			distanceView.setVisibility(View.VISIBLE);
		} else {
			distanceView.setVisibility(View.GONE);
		}
	}

	private void updateCompassDisplayVisibility() {
		boolean compass = mSharedPreferences.getBoolean(Constants.COMPASS, false);
		if (compass) {
			mLoggerMap.enableCompass();
		} else {
			mLoggerMap.disableCompass();
		}
	}

	private void updateLocationDisplayVisibility() {
		boolean location = mSharedPreferences.getBoolean(Constants.LOCATION, false);
		if (location) {
			mLoggerMap.enableMyLocation();
		} else {
			mLoggerMap.disableMyLocation();
		}
	}

	/**
	 * Retrieves the numbers of the measured speed and altitude from the most
	 * recent waypoint and updates UI components with this latest bit of
	 * information.
	 */
	private void updateTrackNumbers() {
		Location lastWaypoint = mLoggerServiceManager.getLastWaypoint();
		UnitsI18n units = mUnits;
		if (lastWaypoint != null && units != null) {
			// Speed number
			mSpeed = lastWaypoint.getSpeed();
			mAltitude = lastWaypoint.getAltitude();
			mDistance = mLoggerServiceManager.getTrackedDistance();
		}

		// Distance number
		double distance = units.conversionFromMeter(mDistance);
		String distanceText = String.format("%.2f %s", distance, units.getDistanceUnit());
		TextView mDistanceView = mLoggerMap.getDistanceTextView();
		mDistanceView.setText(distanceText);

		// Speed number
		double speed = units.conversionFromMetersPerSecond(mSpeed);
		String speedText = units.formatSpeed(speed, false);
		TextView lastGPSSpeedView = mLoggerMap.getSpeedTextView();
		lastGPSSpeedView.setText(speedText);

		// Altitude number
		double altitude = units.conversionFromMeterToHeight(mAltitude);
		String altitudeText = String.format("%.0f %s", altitude, units.getHeightUnit());
		TextView mLastGPSAltitudeView = mLoggerMap.getAltitideTextView();
		mLastGPSAltitudeView.setText(altitudeText);

		// Slider indicator
		SlidingIndicatorView currentScaleIndicator = mLoggerMap.getScaleIndicatorView();
		int trackColoringMethod = Integer.valueOf(mSharedPreferences.getString(Constants.TRACKCOLORING, "3"))
				.intValue();
		if (trackColoringMethod == SegmentRendering.DRAW_MEASURED
				|| trackColoringMethod == SegmentRendering.DRAW_CALCULATED) {
			currentScaleIndicator.setValue((float) speed);
			// Speed color bar and reference numbers
			if (speed > 2 * mAverageSpeed) {
				mAverageSpeed = 0.0;
				updateSpeedColoring();
				mBitmapSegmentsOverlay.scheduleRecalculation();
			}
		} else if (trackColoringMethod == SegmentRendering.DRAW_HEIGHT) {
			currentScaleIndicator.setValue((float) altitude);
			// Speed color bar and reference numbers
			if (altitude > 2 * mAverageHeight) {
				mAverageHeight = 0.0;
				updateSpeedColoring();
				mLoggerMap.postInvalidate();
			}
		}

	}

	/**
	 * For the current track identifier the route of that track is drawn by
	 * adding a OverLay for each segments in the track
	 * 
	 * @param trackId
	 * @see SegmentRendering
	 */
	private void createDataOverlays() {
		mLastSegmentOverlay = null;
		mBitmapSegmentsOverlay.clearSegments();
		mLoggerMap.clearOverlays();
		mLoggerMap.addOverlay(mBitmapSegmentsOverlay);

		ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
		Cursor segments = null;
		int trackColoringMethod = Integer.valueOf(mSharedPreferences.getString(Constants.TRACKCOLORING, "2"))
				.intValue();

		try {
			Uri segmentsUri = Uri.withAppendedPath(Tracks.CONTENT_URI, this.mTrackId + "/segments");
			segments = resolver.query(segmentsUri, new String[] { Segments._ID }, null, null, null);
			if (segments != null && segments.moveToFirst()) {
				do {
					long segmentsId = segments.getLong(0);
					Uri segmentUri = ContentUris.withAppendedId(segmentsUri, segmentsId);
					SegmentRendering segmentOverlay = new SegmentRendering(mLoggerMap, segmentUri, trackColoringMethod,
							mAverageSpeed, mAverageHeight, mHandler);
					mBitmapSegmentsOverlay.addSegment(segmentOverlay);
					mLastSegmentOverlay = segmentOverlay;
					if (segments.isFirst()) {
						segmentOverlay.addPlacement(SegmentRendering.FIRST_SEGMENT);
					}
					if (segments.isLast()) {
						segmentOverlay.addPlacement(SegmentRendering.LAST_SEGMENT);
					}
					mLastSegment = segmentsId;
				} while (segments.moveToNext());
			}
		} finally {
			if (segments != null) {
				segments.close();
			}
		}

		Uri lastSegmentUri = Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId + "/segments/" + mLastSegment
				+ "/waypoints");
		resolver.unregisterContentObserver(this.mSegmentWaypointsObserver);
		resolver.registerContentObserver(lastSegmentUri, false, this.mSegmentWaypointsObserver);
	}

	private void updateDataOverlays() {
		ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
		Uri segmentsUri = Uri.withAppendedPath(Tracks.CONTENT_URI, this.mTrackId + "/segments");
		Cursor segmentsCursor = null;
		int segmentOverlaysCount = mBitmapSegmentsOverlay.size();
		try {
			segmentsCursor = resolver.query(segmentsUri, new String[] { Segments._ID }, null, null, null);
			if (segmentsCursor != null && segmentsCursor.getCount() == segmentOverlaysCount) {
				// Log.d( TAG, "Alignment of segments" );
			} else {
				createDataOverlays();
			}
		} finally {
			if (segmentsCursor != null) {
				segmentsCursor.close();
			}
		}
	}

	/**
	 * Call when an overlay has recalulated and has new information to be
	 * redrawn
	 */

	private void moveActiveViewWindow() {
		GeoPoint lastPoint = getLastTrackPoint();
		if (lastPoint != null && mLoggerServiceManager.getLoggingState() == Constants.LOGGING) {
			if (mLoggerMap.isOutsideScreen(lastPoint)) {
				mLoggerMap.clearAnimation();
				mLoggerMap.setCenter(lastPoint);
			} else if (mLoggerMap.isNearScreenEdge(lastPoint)) {
				mLoggerMap.clearAnimation();
				mLoggerMap.animateTo(lastPoint);
			}
		}
	}

	/**
	 * Updates the labels next to the color bar with speeds
	 */
	private void drawSpeedTexts() {
		UnitsI18n units = mUnits;
		if (units != null) {
			double avgSpeed = units.conversionFromMetersPerSecond(mAverageSpeed);
			TextView[] mSpeedtexts = mLoggerMap.getSpeedTextViews();
			SlidingIndicatorView currentScaleIndicator = mLoggerMap.getScaleIndicatorView();
			for (int i = 0; i < mSpeedtexts.length; i++) {
				mSpeedtexts[i].setVisibility(View.VISIBLE);
				double speed;
				if (mUnits.isUnitFlipped()) {
					speed = ((avgSpeed * 2d) / 5d) * (mSpeedtexts.length - i - 1);
				} else {
					speed = ((avgSpeed * 2d) / 5d) * i;
				}
				if (i == 0) {
					currentScaleIndicator.setMin((float) speed);
				} else {
					currentScaleIndicator.setMax((float) speed);
				}
				String speedText = units.formatSpeed(speed, false);
				mSpeedtexts[i].setText(speedText);
			}
		}
	}

	/**
	 * Updates the labels next to the color bar with heights
	 */
	private void drawHeightTexts() {
		UnitsI18n units = mUnits;
		if (units != null) {
			double avgHeight = units.conversionFromMeterToHeight(mAverageHeight);
			TextView[] mSpeedtexts = mLoggerMap.getSpeedTextViews();
			SlidingIndicatorView currentScaleIndicator = mLoggerMap.getScaleIndicatorView();
			for (int i = 0; i < mSpeedtexts.length; i++) {
				mSpeedtexts[i].setVisibility(View.VISIBLE);
				double height = ((avgHeight * 2d) / 5d) * i;
				String heightText = String.format("%d %s", (int) height, units.getHeightUnit());
				mSpeedtexts[i].setText(heightText);
				if (i == 0) {
					currentScaleIndicator.setMin((float) height);
				} else {
					currentScaleIndicator.setMax((float) height);
				}
			}
		}
	}

	/**
	 * Alter this to set a new track as current.
	 * 
	 * @param trackId
	 * @param center
	 *            center on the end of the track
	 */
	private void moveToTrack(long trackId, boolean center) {
		if (trackId == mTrackId) {
			return;
		}

		Cursor track = null;
		try {
			ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
			Uri trackUri = ContentUris.withAppendedId(Tracks.CONTENT_URI, trackId);
			track = resolver.query(trackUri, new String[] { Tracks.NAME }, null, null, null);
			if (track != null && track.moveToFirst()) {
				this.mTrackId = trackId;
				mLastSegment = -1;
				resolver.unregisterContentObserver(this.mTrackSegmentsObserver);
				resolver.unregisterContentObserver(this.mTrackMediasObserver);
				Uri tracksegmentsUri = Uri.withAppendedPath(Tracks.CONTENT_URI, trackId + "/segments");

				resolver.registerContentObserver(tracksegmentsUri, false, this.mTrackSegmentsObserver);
				resolver.registerContentObserver(Media.CONTENT_URI, true, this.mTrackMediasObserver);

				mLoggerMap.clearOverlays();
				mBitmapSegmentsOverlay.clearSegments();
				mAverageSpeed = 0.0;
				mAverageHeight = 0.0;

				updateTitleBar();
				updateDataOverlays();
				updateSpeedColoring();

				if (center) {
					GeoPoint lastPoint = getLastTrackPoint();
					mLoggerMap.animateTo(lastPoint);
				}

				if (!InBiciHelper.isStartedANewTrack(getPreferences())) {
					String mStringTrackId = InBiciHelper.getTrackIdFromSP(getPreferences());
					if (mTrack == null) {
						try {
							mTrack = InBiciHelper.findTrackById(mStringTrackId);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						mTrack = new TrackObject();
					}
					if (!isSameStartPlace()) {
						// showDialogBox because they are different
						mLoggerServiceManager.pauseGPSLogging();
						DifferentTrackDialogBox.newtrackfound(mLoggerMap.getActivity(), getPreferences(), mTrack, this,
								mLoggerServiceManager, InBiciHelper.getBasicProfile(), null);
					}
				}
			}
		} finally {
			if (track != null) {
				track.close();
			}
		}
	}

	/**
	 * Get the last know position from the GPS provider and return that
	 * information wrapped in a GeoPoint to which the Map can navigate.
	 * 
	 * @see GeoPoint
	 * @return
	 */
	private GeoPoint getLastKnowGeopointLocation() {
		int microLatitude = 0;
		int microLongitude = 0;
		LocationManager locationManager = (LocationManager) mLoggerMap.getActivity().getApplication()
				.getSystemService(Context.LOCATION_SERVICE);
		Location locationFine = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (locationFine != null) {
			microLatitude = (int) (locationFine.getLatitude() * 1E6d);
			microLongitude = (int) (locationFine.getLongitude() * 1E6d);
		}
		if (locationFine == null || microLatitude == 0 || microLongitude == 0) {
			Location locationCoarse = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (locationCoarse != null) {
				microLatitude = (int) (locationCoarse.getLatitude() * 1E6d);
				microLongitude = (int) (locationCoarse.getLongitude() * 1E6d);
			}
			if (locationCoarse == null || microLatitude == 0 || microLongitude == 0) {
				microLatitude = 51985105;
				microLongitude = 5106132;
			}
		}
		GeoPoint geoPoint = new GeoPoint(microLatitude, microLongitude);
		return geoPoint;
	}

	/**
	 * Retrieve the last point of the current track
	 * 
	 * @param context
	 */
	private GeoPoint getLastTrackPoint() {
		Cursor waypoint = null;
		GeoPoint lastPoint = null;
		// First try the service which might have a cached version
		Location lastLoc = mLoggerServiceManager.getLastWaypoint();
		if (lastLoc != null) {
			int microLatitude = (int) (lastLoc.getLatitude() * 1E6d);
			int microLongitude = (int) (lastLoc.getLongitude() * 1E6d);
			lastPoint = new GeoPoint(microLatitude, microLongitude);
		}

		// If nothing yet, try the content resolver and query the track
		if (lastPoint == null || lastPoint.getLatitudeE6() == 0 || lastPoint.getLongitudeE6() == 0) {
			try {
				ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
				waypoint = resolver.query(Uri.withAppendedPath(Tracks.CONTENT_URI, mTrackId + "/waypoints"),
						new String[] { Waypoints.LATITUDE, Waypoints.LONGITUDE,
								"max(" + Waypoints.TABLE + "." + Waypoints._ID + ")" }, null, null, null);
				if (waypoint != null && waypoint.moveToLast()) {
					int microLatitude = (int) (waypoint.getDouble(0) * 1E6d);
					int microLongitude = (int) (waypoint.getDouble(1) * 1E6d);
					lastPoint = new GeoPoint(microLatitude, microLongitude);
				}
			} finally {
				if (waypoint != null) {
					waypoint.close();
				}
			}
		}

		// If nothing yet, try the last generally known location
		if (lastPoint == null || lastPoint.getLatitudeE6() == 0 || lastPoint.getLongitudeE6() == 0) {
			lastPoint = getLastKnowGeopointLocation();
		}
		return lastPoint;
	}

	private void moveToLastTrack() {
		int trackId = -1;
		Cursor track = null;
		try {
			ContentResolver resolver = mLoggerMap.getActivity().getContentResolver();
			track = resolver.query(Tracks.CONTENT_URI, new String[] { "max(" + Tracks._ID + ")", Tracks.NAME, }, null,
					null, null);
			if (track != null && track.moveToLast()) {
				trackId = track.getInt(0);
				moveToTrack(trackId, false);
			}
		} finally {
			if (track != null) {
				track.close();
			}
		}
	}

	/**
	 * Enables a SegmentOverlay to call back to the MapActivity to show a dialog
	 * with choices of media
	 * 
	 * @param mediaAdapter
	 */
	public void showMediaDialog(BaseAdapter mediaAdapter) {
		mMediaAdapter = mediaAdapter;
		mLoggerMap.getActivity().showDialog(DIALOG_URIS);
	}

	public static void setPreferences(Context ctx) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

	public static SharedPreferences getPreferences() {
		return mSharedPreferences;
	}

	public boolean isLogging() {
		return mLoggerServiceManager.getLoggingState() == Constants.LOGGING;
	}

	private void refreshNewTrack() {
		if ((InBiciHelper.isStartedANewTrack(getPreferences()) && mLoggerServiceManager.getLoggingState() == Constants.LOGGING)) {
			// InBiciHelper.removeNewTrackStart(getPreferences());
			// check start point
			// if (!isSameStartPlace()) {
			// // dialog "is not the same starting place, add new tracks?"
			// if (mTrack == null) {
			// mTrack = new TrackObject();
			// }
			// mLoggerServiceManager.pauseGPSLogging();
			// DifferentTrackDialogBox.newtrackfound(mLoggerMap.getActivity(),
			// getPreferences(), mTrack, this,mLoggerServiceManager);
			//
			// }
		} else {
			moveToLastTrack();
		}
	}

	private boolean isSameStartPlace() {
		GeoPoint lastGeoPoint = getLastKnowGeopointLocation();
		GeoPoint startGeoPoint = null;
		// LatLng startPoint =null;
		// LatLng lastPoint = new LatLng(lastGeoPoint.getLatitudeE6(),
		// lastGeoPoint.getLongitudeE6());
		if (mTrack != null && mTrack.decodedLine() != null && mTrack.decodedLine().size() > 0) {
			// startPoint = mTrack.decodedLine().get(0);

			startGeoPoint = new GeoPoint((int) (mTrack.decodedLine().get(0).latitude * 1e6), (int) (mTrack
					.decodedLine().get(0).longitude * 1e6));

		} else
			return false;
		if (pointsNear(startGeoPoint, lastGeoPoint))

			return true;
		InBiciHelper.setDifferentStartPlace(getPreferences());
		return false;
	}

	private boolean pointsNear(GeoPoint startPoint, GeoPoint lastPoint) {

		// Location loc1 = new Location("");
		// loc1.setLatitude(startPoint.getLatitudeE6());
		// loc1.setLongitude(startPoint.getLongitudeE6());
		//
		// Location loc2 = new Location("");
		// loc2.setLatitude(lastPoint.getLatitudeE6());
		// loc2.setLongitude(lastPoint.getLongitudeE6());
		//
		// float distanceInMeters = loc1.distanceTo(loc2);
		// if (Math.abs(distanceInMeters)<100)
		// return true;
		// return false;
		float[] distance = new float[2];
		Location.distanceBetween(startPoint.getLatitudeE6() / 1e6, startPoint.getLongitudeE6() / 1e6,
				lastPoint.getLatitudeE6() / 1e6, lastPoint.getLongitudeE6() / 1e6, distance);
		if (Math.abs(distance[0]) < 100)
			return true;
		return false;
	}

	private boolean isSameEndPlace() {

		// to be done
		return true;
	}

	private boolean isSameTrack(List<LatLng> newLine, List<LatLng> oldLine) {
		double fDistance = calculateFrechetDistance(newLine, oldLine);
		// if (true)
		if (fDistance <= MINIMAL_EQUAL_DISTANCE)
			return true;
		else
			return false;
	}

	private double calculateFrechetDistance(List<LatLng> oldLine, List<LatLng> newLine) {
		FrechetDistance frechet;
		double[][] curveA, curveB;
		double dist;

		// calculate distance between two track
		List<LatLng> oldCurv = oldLine;
		ArrayList<double[]> oldPoints = new ArrayList<double[]>();
		for (LatLng oldPoint : oldCurv) {
			double[] single_point = new double[] { oldPoint.latitude, oldPoint.longitude };
			oldPoints.add(single_point);
		}
		double[][] curvRif = oldPoints.toArray(new double[oldPoints.size()][]);

		List<LatLng> newCurv = newLine;
		ArrayList<double[]> newPoints = new ArrayList<double[]>();
		for (LatLng newPoint : newCurv) {
			double[] single_point = new double[] { newPoint.latitude, newPoint.longitude };
			newPoints.add(single_point);
		}
		double[][] newRif = newPoints.toArray(new double[newPoints.size()][]);

		frechet = new PolyhedralFrechetDistance(PolyhedralDistanceFunction.epsApproximation2D(1.1));
		if (curvRif.length < 2 || newRif.length < 2)
			return Double.POSITIVE_INFINITY; // now points in the training
		dist = frechet.computeDistance(curvRif, newRif);
		return dist;
	}

	@Override
	public void changeTrack(TrackObject mTrack) {
		this.mTrack = mTrack;
	}

}
