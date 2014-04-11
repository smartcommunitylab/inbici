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
package eu.iescities.pilot.rovereto.inbici.entities.event.info.edit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.DTParamsHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.android.common.geo.OSMAddress;
import eu.trentorise.smartcampus.android.common.geo.OSMGeocoder;
import eu.trentorise.smartcampus.android.map.InfoDialog;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class AddressSelectActivity extends ActionBarActivity implements OnMapLongClickListener {

	private GoogleMap mMap = null;
	private String url = "https://vas.smartcampuslab.it";
	private OSMAddress osmAddress = null;
	private String osmUrl = "http://otile1.mqcdn.com/tiles/1.0.0/osm/%d/%d/%d.jpg";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapcontainer);

		// getActionBar().setDisplayShowTitleEnabled(false);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if (getSupportActionBar().getNavigationMode() != ActionBar.NAVIGATION_MODE_STANDARD) {
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}
		if (((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap() != null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.setOnMapLongClickListener(this);
			mMap.setMyLocationEnabled(true);
			setUpMap();
			LatLng centerLatLng = null;
			if (DTParamsHelper.getCenterMap() != null) {
				centerLatLng = new LatLng(DTParamsHelper.getCenterMap().get(0), DTParamsHelper.getCenterMap().get(1));
			} else if (DTHelper.getLocationHelper().getLocation() != null) {
				centerLatLng = new LatLng(DTHelper.getLocationHelper().getLocation().getLatitudeE6() / 1e6, DTHelper
						.getLocationHelper().getLocation().getLongitudeE6() / 1e6);
			}
			if (centerLatLng != null) {
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, DTParamsHelper.getZoomLevelMap()));
			} else {
				mMap.moveCamera(CameraUpdateFactory.zoomTo(DTParamsHelper.getZoomLevelMap()));
			}

			Toast.makeText(this, R.string.address_select_toast, Toast.LENGTH_LONG).show();

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onMapLongClick(LatLng point) {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(100);

		// GeoPoint p = new GeoPoint((int) (point.latitude * 1e6), (int)
		// (point.longitude * 1e6));
		GeoPoint p = new GeoPoint((int) (point.latitude * 1e6), (int) (point.longitude * 1e6));
		// List<OSMAddress> addresses = new
		// SCGeocoder(getApplicationContext(),url).findAddressesAsync(p);
		// addresses = new
		// OSMGeocoder(getApplicationContext(),url).getFromLocation(point.latitude
		// , point.longitude, null);
		new SCAsyncTask<Void, Void, List<OSMAddress>>(this, new GetAddressProcessor((Activity) this, p)).execute();

	}
	
	@Override
	public void finish() {
	  //Prepare data intent 
	  Intent data = new Intent();
	  data.putExtra(Utils.ADDRESS, osmAddress);
	  // Activity finished ok, return the data
	  setResult(RESULT_OK, data);
	  super.finish();
	} 
	

	private class GetAddressProcessor extends AbstractAsyncTaskProcessor<Void, List<OSMAddress>> {

		List<OSMAddress> addresses = null;
		private GeoPoint point;

		public GetAddressProcessor(Activity activity, GeoPoint p) {
			super(activity);
			this.point = p;
		}

		@Override
		public List<OSMAddress> performAction(Void... params) throws SecurityException, Exception {
			return addresses = new OSMGeocoder(getApplicationContext(), url).getFromLocation(
					point.getLatitudeE6() / 1e6, point.getLongitudeE6() / 1e6, null);

		}

		@Override
		public void handleResult(List<OSMAddress> result) {
			Address address = new Address(Locale.getDefault());
			OSMAddress myAddress = new OSMAddress();
			// get first wit street
			
			for (OSMAddress osmAddress : result) {
				
				Log.i("ADDRESS", "AddressSelectActivity --> osmAddress: " +  osmAddress.toString());
				if (osmAddress.getStreet() != null) {
					myAddress = osmAddress;
					break;
				}
			}
			
			//store the osm address so that it is returned as intent result
			osmAddress = myAddress;
			
			address.setAddressLine(0, myAddress.formattedAddress());
			address.setCountryName(myAddress.country());
			address.setLocality(myAddress.getCity().get(""));
			address.setLatitude(myAddress.getLocation()[0]);
			address.setLongitude(myAddress.getLocation()[1]);

			if (addresses != null && !addresses.isEmpty()) {
				new InfoDialog(AddressSelectActivity.this, address).show(getSupportFragmentManager(), "me");
			} else {
				address.setLatitude(point.getLatitudeE6());
				address.setLongitude(point.getLongitudeE6());
				String addressLine = "LON " + Double.toString(address.getLongitude()) + ", LAT "
						+ Double.toString(address.getLatitude());
				address.setAddressLine(0, addressLine);
				new InfoDialog(AddressSelectActivity.this, address).show(getSupportFragmentManager(), "me");
			}
		}
	}

	
	 private void setUpMap() {
	        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
	        TileProvider tileProvider = new UrlTileProvider(256, 256) {
			    @Override
			    public URL getTileUrl(int x, int y, int z) {
			        try {
			        	if (z>17) 
			        		z=17;
			            return new URL(String.format(osmUrl, z, x, y));
			        }
			        catch (MalformedURLException e) {
			            return null;
			        }
			    }
	        };

	        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
	    }
	
}
