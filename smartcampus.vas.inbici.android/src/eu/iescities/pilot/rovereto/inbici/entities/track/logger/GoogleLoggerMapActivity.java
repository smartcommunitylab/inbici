/*------------------------------------------------------------------------------
 **     Ident: Sogeti Smart Mobile Solutions
 **    Author: rene
 ** Copyright: (c) Apr 24, 2011 Sogeti Nederland B.V. All Rights Reserved.
 **------------------------------------------------------------------------------
 ** Sogeti Nederland B.V.            |  No part of this file may be reproduced  
 ** Distributed Software Engineering |  or transmitted in any form or by any        
 ** Lange Dreef 17                   |  means, electronic or mechanical, for the      
 ** 4131 NJ Vianen                   |  purpose, without the express written    
 ** The Netherlands                  |  permission of the copyright holder.
 *------------------------------------------------------------------------------
 *
 *   This file is part of OpenGPSTracker.
 *
 *   OpenGPSTracker is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   OpenGPSTracker is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with OpenGPSTracker.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package eu.iescities.pilot.rovereto.inbici.entities.track.logger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.Overlay.FixedMyLocationOverlay;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.Overlay.OverlayProvider;
import eu.iescities.pilot.rovereto.inbici.map.MapManager;

/**
 * Main activity showing a track and allowing logging control
 * 
 * @version $Id$
 * @author rene (c) Jan 18, 2009, Sogeti B.V.
 */
public class GoogleLoggerMapActivity extends FragmentActivity implements LoggerMap {

	private GPSLoggerServiceManager mLoggerServiceManager;
	private LoggerMapHelper mHelper;
	private GoogleMap myMap;
	// private MapView mMapView;
	private TextView[] mSpeedtexts;
	private TextView mLastGPSSpeedView;
	private TextView mLastGPSAltitudeView;
	private TextView mDistanceView;
	private FixedMyLocationOverlay mMylocation;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle load) {
		super.onCreate(load);
		setContentView(R.layout.map_google);

		mHelper = new LoggerMapHelper(this);
		GoogleMap myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMapView)).getMap();
		// mMapView = (MapView) findViewById(R.id.myMapView);
		myMap.setMyLocationEnabled(true);
		// mMylocation = new FixedMyLocationOverlay(this, myMap);
		// mMapView.setBuiltInZoomControls(true);
		TextView[] speeds = { (TextView) findViewById(R.id.speedview05), (TextView) findViewById(R.id.speedview04),
				(TextView) findViewById(R.id.speedview03), (TextView) findViewById(R.id.speedview02),
				(TextView) findViewById(R.id.speedview01), (TextView) findViewById(R.id.speedview00) };
		mSpeedtexts = speeds;
		mLastGPSSpeedView = (TextView) findViewById(R.id.currentSpeed);
		mLastGPSAltitudeView = (TextView) findViewById(R.id.currentAltitude);
		mDistanceView = (TextView) findViewById(R.id.currentDistance);

		mHelper.onCreate(load);
        mLoggerServiceManager = new GPSLoggerServiceManager( this );

	}

	@Override
	protected void onResume() {
		super.onResume();
		mHelper.onResume();
	}

	@Override
	protected void onPause() {
		mHelper.onPause();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		mHelper.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onNewIntent(Intent newIntent) {
		mHelper.onNewIntent(newIntent);
	}

	@Override
	protected void onRestoreInstanceState(Bundle load) {
		if (load != null) {
			super.onRestoreInstanceState(load);
		}
		mHelper.onRestoreInstanceState(load);
	}

	@Override
	protected void onSaveInstanceState(Bundle save) {
		super.onSaveInstanceState(save);
		mHelper.onSaveInstanceState(save);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		mHelper.onCreateOptionsMenu(menu,mLoggerServiceManager);
		return result;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mHelper.onPrepareOptionsMenu(menu,mLoggerServiceManager);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean handled = mHelper.onOptionsItemSelected(item);
		if (!handled) {
			handled = super.onOptionsItemSelected(item);
		}
		return handled;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		mHelper.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean propagate = true;
		switch (keyCode) {
		case KeyEvent.KEYCODE_S:
			setSatelliteOverlay(!(getSupportMap().getMapType() == GoogleMap.MAP_TYPE_SATELLITE));
			propagate = false;
			break;
		case KeyEvent.KEYCODE_A:
			setTrafficOverlay(!getSupportMap().isTrafficEnabled());
			propagate = false;
			break;
		default:
			propagate = mHelper.onKeyDown(keyCode, event);
			if (propagate) {
				propagate = super.onKeyDown(keyCode, event);
			}
			break;
		}
		return propagate;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = mHelper.onCreateDialog(id);
		if (dialog == null) {
			dialog = super.onCreateDialog(id);
		}
		return dialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		mHelper.onPrepareDialog(id, dialog);
		super.onPrepareDialog(id, dialog);
	}

	/******************************/
	/** Own methods **/
	/******************************/

	private void setTrafficOverlay(boolean b) {
		SharedPreferences sharedPreferences = mHelper.getPreferences();
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(Constants.TRAFFIC, b);
		editor.commit();
	}

	private void setSatelliteOverlay(boolean b) {
		SharedPreferences sharedPreferences = mHelper.getPreferences();
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(Constants.SATELLITE, b);
		editor.commit();
	}

	// @Override
	// protected boolean isRouteDisplayed() {
	// return true;
	// }
	//
	// @Override
	// protected boolean isLocationDisplayed() {
	// SharedPreferences sharedPreferences = mHelper.getPreferences();
	// return sharedPreferences.getBoolean(Constants.LOCATION, false) ||
	// mHelper.isLogging();
	// }

	/******************************/
	/** Loggermap methods **/
	/******************************/

	@Override
	public void updateOverlays() {
		SharedPreferences sharedPreferences = mHelper.getPreferences();
		if (sharedPreferences.getBoolean(Constants.SATELLITE, false))
			getSupportMap().setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		else
			getSupportMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);

		getSupportMap().setTrafficEnabled(sharedPreferences.getBoolean(Constants.TRAFFIC, false));
		// GoogleLoggerMapFragment.this.mMapView.setSatellite(sharedPreferences.getBoolean(Constants.SATELLITE,
		// false));
		// GoogleLoggerMapFragment.this.mMapView.setTraffic(sharedPreferences.getBoolean(Constants.TRAFFIC,
		// false));
	}

	@Override
	public void setDrawingCacheEnabled(boolean b) {
		findViewById(R.id.mapScreen).setDrawingCacheEnabled(true);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void onLayerCheckedChanged(int checkedId, boolean isChecked) {
		switch (checkedId) {
		case R.id.layer_google_satellite:
			setSatelliteOverlay(true);
			break;
		case R.id.layer_google_regular:
			setSatelliteOverlay(false);
			break;
		case R.id.layer_traffic:
			setTrafficOverlay(isChecked);
			break;
		default:
			break;
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals(Constants.TRAFFIC)) {
			updateOverlays();
		} else if (key.equals(Constants.SATELLITE)) {
			updateOverlays();
		}
	}

	@Override
	public Bitmap getDrawingCache() {
		return findViewById(R.id.mapScreen).getDrawingCache();
	}

	@Override
	public void showMediaDialog(BaseAdapter mediaAdapter) {
		mHelper.showMediaDialog(mediaAdapter);
	}

	public void onDateOverlayChanged() {
		// mMapView.postInvalidate();
	}

	@Override
	public String getDataSourceId() {
		return LoggerMapHelper.GOOGLE_PROVIDER;
	}

	@Override
	public boolean isOutsideScreen(GeoPoint lastPoint) {
		Point out = new Point();
		// out = getSupportMap().getProjection().toScreenLocation(new
		// LatLng(lastPoint.getLatitudeE6(), lastPoint.getLongitudeE6()));
		// this.mMapView.getProjection().toPixels(lastPoint, out);
		// int height = this.mMapView.getHeight();
		// int width = this.mMapView.getWidth();
		// return (out.x < 0 || out.y < 0 || out.y > height || out.x > width);
		return false;
	}

	@Override
	public boolean isNearScreenEdge(GeoPoint lastPoint) {
		// Point out = new Point();
		// this.mMapView.getProjection().toPixels(lastPoint, out);
		// int height = this.mMapView.getHeight();
		// int width = this.mMapView.getWidth();
		// return (out.x < width / 4 || out.y < height / 4 || out.x > (width /
		// 4) * 3 || out.y > (height / 4) * 3);
		return false;
	}

	@Override
	public void executePostponedActions() {
		// NOOP for Google Maps
	}

	@Override
	public void enableCompass() {
		getSupportMap().getUiSettings().setCompassEnabled(true);
		// mMylocation.enableCompass();
	}

	@Override
	public void enableMyLocation() {
		getSupportMap().setMyLocationEnabled(true);
		// mMylocation.enableMyLocation();
	}

	@Override
	public void disableMyLocation() {
		getSupportMap().setMyLocationEnabled(false);
		// mMylocation.disableMyLocation();
	}

	@Override
	public void disableCompass() {
		getSupportMap().getUiSettings().setCompassEnabled(false);
		// mMylocation.disableCompass();
	}

	@Override
	public void setZoom(int zoom) {
		getSupportMap().animateCamera(CameraUpdateFactory.zoomTo(zoom));
		// mMapView.getController().setZoom(zoom);
	}

	@Override
	public void animateTo(GeoPoint storedPoint) {
		getSupportMap().animateCamera(
				CameraUpdateFactory.newLatLngZoom(
						new LatLng(storedPoint.getLatitudeE6(), storedPoint.getLongitudeE6()), getSupportMap()
								.getCameraPosition().zoom));

		// mMapView.getController().animateTo(storedPoint);
	}

	@Override
	public int getZoomLevel() {
		return (int) getSupportMap().getCameraPosition().zoom;
		// return mMapView.getZoomLevel();
	}

	@Override
	public GeoPoint getMapCenter() {
		return new GeoPoint((int) getSupportMap().getCameraPosition().target.latitude, (int) getSupportMap()
				.getCameraPosition().target.longitude);

		// return mMapView.getMapCenter();
	}

	@Override
	public boolean zoomOut() {
		getSupportMap().animateCamera(CameraUpdateFactory.zoomTo(getZoomLevel() - 1));

		return true;
	}

	@Override
	public boolean zoomIn() {
		getSupportMap().animateCamera(CameraUpdateFactory.zoomTo(getZoomLevel() + 1));

		return true;
	}

	@Override
	public void postInvalidate() {
		// mMapView.postInvalidate();
	}

	@Override
	public void clearAnimation() {
		// mMapView.clearAnimation();
	}

	@Override
	public void setCenter(GeoPoint lastPoint) {

		// mMapView.getController().setCenter(lastPoint);
	}

	@Override
	public int getMaxZoomLevel() {
		return (int) getSupportMap().getMaxZoomLevel();
		// return mMapView.getMaxZoomLevel();
	}

	@Override
	public GeoPoint fromPixels(int x, int y) {
		LatLng latlng = getSupportMap().getProjection().fromScreenLocation(new Point(x, y));
		return new GeoPoint((int) (latlng.latitude * 1E6), (int) (latlng.longitude * 1E6));
		// return new GeoPoint(46071709, 11119326);
	}

	@Override
	public boolean hasProjection() {
		// return true;
		return getSupportMap().getProjection() != null;
		// return mMapView.getProjection() != null;
	}

	@Override
	public float metersToEquatorPixels(float float1) {
		return metersToEquatorPixels(getSupportMap(), null, float1);
		// return (float) 46.071709;
	}

	public static int metersToEquatorPixels(GoogleMap map, LatLng base, float meters) {
		final double OFFSET_LON = 0.5d;

		Location baseLoc = new Location("");
		baseLoc.setLatitude(base.latitude);
		baseLoc.setLongitude(base.longitude);

		Location dest = new Location("");
		dest.setLatitude(base.latitude);
		dest.setLongitude(base.longitude + OFFSET_LON);

		double degPerMeter = OFFSET_LON / baseLoc.distanceTo(dest);
		double lonDistance = meters * degPerMeter;

		Projection proj = map.getProjection();
		Point basePt = proj.toScreenLocation(base);
		Point destPt = proj.toScreenLocation(new LatLng(base.latitude, base.longitude + lonDistance));

		return Math.abs(destPt.x - basePt.x);
	}

	@Override
	public void toPixels(GeoPoint geoPoint, Point screenPoint) {
		// mMapView.getProjection().toPixels(geoPoint, screenPoint);
	}

	@Override
	public TextView[] getSpeedTextViews() {
		return mSpeedtexts;
	}

	@Override
	public TextView getAltitideTextView() {
		return mLastGPSAltitudeView;
	}

	@Override
	public TextView getSpeedTextView() {
		return mLastGPSSpeedView;
	}

	@Override
	public TextView getDistanceTextView() {
		return mDistanceView;
	}

	@Override
	public void addOverlay(OverlayProvider overlay) {
		// mMapView.getOverlays().add(overlay.getGoogleOverlay());
	}

	@Override
	public void clearOverlays() {
		getSupportMap().clear();
		// mMapView.getOverlays().clear();
	}

	@Override
	public SlidingIndicatorView getScaleIndicatorView() {
		return (SlidingIndicatorView) findViewById(R.id.scaleindicator);
	}

	private GoogleMap getSupportMap() {
		if (myMap == null) {
			if (getSupportFragmentManager().findFragmentById(R.id.myMapView) != null
					&& getSupportFragmentManager().findFragmentById(R.id.myMapView) instanceof SupportMapFragment) {
				myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMapView)).getMap();
			}
			if (myMap != null)
				myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapManager.DEFAULT_POINT, MapManager.ZOOM_DEFAULT));

		}
		return myMap;
	}
}