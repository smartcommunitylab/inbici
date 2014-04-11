package eu.iescities.pilot.rovereto.inbici.map;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.entities.track.TrackListingFragment;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;


public class MapFragment extends Fragment implements MapItemsHandler, OnCameraChangeListener, OnMarkerClickListener,
		MapObjectContainer {

	protected GoogleMap mMap;
	
	private Collection<? extends BaseDTObject> objects;
	private String osmUrl = "http://otile1.mqcdn.com/tiles/1.0.0/osm/%d/%d/%d.jpg";

	private boolean loaded = false;
	private boolean listmenu = false;

	private static View view;
	float maxZoomOnMap = 19.0f;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_map, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		
		Log.i("MAP", "MapFragment --> onStart");
		
		// hide keyboard if it is still open
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.content_frame).getWindowToken(), 0);

		if (!loaded) {
			Log.i("MAP", "MapFragment --> onStar --> init view");
			initView();
			//loaded = true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("loaded", loaded);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected void initView() {
		if (getSupportMap() != null) {
			getSupportMap().clear();
			setUpMap();
			getSupportMap().getUiSettings().setRotateGesturesEnabled(false);
			getSupportMap().getUiSettings().setTiltGesturesEnabled(false);
		}

		if (getArguments() != null && getArguments().containsKey(Constants.ARG_OBJECTS)) {
			drawTracks((List<BaseDTObject>) getArguments().getSerializable(Constants.ARG_OBJECTS));
			Log.i("MENU", "ARG_OBJECTS");

		} else if (getArguments() != null && getArguments().containsKey(Constants.ARG_TRACK_CATEGORY)) {
			listmenu = true;
			Log.i("MENU", "LIST MENU in ARG_TRACK_CATEGORY");
			setTrackCategoriesToLoad(getArguments().getString(Constants.ARG_TRACK_CATEGORY));
		} else {
			Log.i("MENU", "ELSE");
			setTrackCategoriesToLoad();
			Log.i("MENU", "set track categories to load");
		}

		Log.i("MENU", "LIST MENU is" + listmenu);

	}

	// Mi dava fastidio il giallo u.u
	@SuppressWarnings("unchecked")
	private void drawTracks(List<? extends BaseDTObject> list) {

		new AsyncTask<List<? extends BaseDTObject>, Void, List<? extends BaseDTObject>>() {
			@Override
			protected List<? extends BaseDTObject> doInBackground(List<? extends BaseDTObject>... params) {
				return params[0];
			}

			@Override
			protected void onPostExecute(List<? extends BaseDTObject> result) {
				addObjects(result);
			}
		}.execute(list);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getSupportMap() != null) {
			getSupportMap().setMyLocationEnabled(true);
			getSupportMap().setOnCameraChangeListener(this);
			getSupportMap().setOnMarkerClickListener(this);
			// if (objects != null) {
			// render(objects);
			// }
		}
	}

	@Override
	public void onConfigurationChanged(Configuration arg0) {
		super.onConfigurationChanged(arg0);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (getSupportMap() != null) {
			getSupportMap().setMyLocationEnabled(false);
			getSupportMap().setOnCameraChangeListener(null);
			getSupportMap().setOnMarkerClickListener(null);
		}
	}

	private void onBaseDTObjectTap(BaseDTObject o) {
		Bundle args = new Bundle();
		args.putSerializable(InfoDialog.PARAM, o);
		InfoDialog dtoTap = new InfoDialog();
		dtoTap.setArguments(args);
		dtoTap.show(getActivity().getSupportFragmentManager(), "me");
	}

	private void onBaseDTObjectsTap(List<BaseDTObject> list) {
		if (list == null || list.size() == 0)
			return;
		if (list.size() == 1) {
			onBaseDTObjectTap(list.get(0));
			return;
		}
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		Fragment fragment = null;
		Bundle args = new Bundle();
		
		if (list.get(0) instanceof TrackObject) {
			fragment = new TrackListingFragment();
			args.putSerializable(TrackListingFragment.ARG_LIST, new ArrayList<BaseDTObject>(list));
		}
		
		if (fragment != null) {
			fragment.setArguments(args);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// fragmentTransaction.detach(this);
			fragmentTransaction.replace(R.id.content_frame, fragment, "me");
			fragmentTransaction.addToBackStack(fragment.getTag());
			fragmentTransaction.commit();
		}
	}

	@Override
	public void setTrackCategoriesToLoad(final String... categories) {
		getSupportMap().clear();
		setUpMap();
		new SCAsyncTask<Void, Void, Collection<? extends BaseDTObject>>(getActivity(), new MapLoadProcessor(
				getActivity(), this, getSupportMap()) {
			@Override
			protected Collection<? extends BaseDTObject> getObjects() {
				try {
					/*
					 * check if todays is checked and cat with searchTodayEvents
					 */
					//Collection<ExplorerObject> newList;
					Collection<TrackObject> newList = new ArrayList<TrackObject>();
					newList = DTHelper.getOfficialTracks(); 
					
//					if (isMyIncluded()) {
//						SortedMap<String, Integer> sort = new TreeMap<String, Integer>();
//						sort.put("fromTime", 1);
//						newList.addAll(DTHelper.searchInGeneral(0, -1, null, null, null, true, TrackObject.class,
//								sort, null));
//
//					}
//					if (eventsCleaned.length != 0 && !Arrays.asList(eventsCleaned).contains("Today")) {
//						SortedMap<String, Integer> sort = new TreeMap<String, Integer>();
//						sort.put("fromTime", 1);
//						newList.addAll(DTHelper.searchInGeneral(0, -1, null, null, null, false, TrackObject.class,
//								sort, eventsCleaned));
//					}
					Iterator<TrackObject> i = newList.iterator();
					while (i.hasNext()) {
						TrackObject obj = i.next();
						double[] loc = obj.getLocation();
						if (loc[0] == 0 && loc[1] == 0) {
							i.remove();
						}
					}
					return newList;
				} catch (Exception e) {
					e.printStackTrace();
					return Collections.emptyList();
				}
			}

		}).execute();
	}


//	private boolean isMyIncluded() {
//		List<String> categoriesCleaned = new ArrayList<String>();
//		boolean isMyincluded = false;
//		if (eventsCategories.length > 0)
//			for (int i = 0; i < eventsCategories.length; i++) {
//				if (eventsCategories[i].contains("My")) {
//
//					isMyincluded = true;
//				} else
//					categoriesCleaned.add(eventsCategories[i]);
//
//			}
//		eventsCleaned = categoriesCleaned.toArray(new String[categoriesCleaned.size()]);
//		return isMyincluded;
//	}

	private GoogleMap getSupportMap() {
		if (mMap == null) {
			if (getFragmentManager().findFragmentById(R.id.map) != null
					&& getFragmentManager().findFragmentById(R.id.map) instanceof SupportMapFragment) {
				mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			}
			if (mMap != null)
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapManager.DEFAULT_POINT, MapManager.ZOOM_DEFAULT));

		}
		return mMap;
	}

	private void setUpMap() {
		mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
		TileProvider tileProvider = new UrlTileProvider(256, 256) {
			@Override
			public URL getTileUrl(int x, int y, int z) {
				try {
					// if (z>17)
					// z=17;
					return new URL(String.format(osmUrl, z, x, y));
				} catch (MalformedURLException e) {
					return null;
				}
			}
		};

		mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		List<BaseDTObject> list = MapManager.ClusteringHelper.getFromGridId(marker.getTitle());
		if (list == null || list.isEmpty())
			return true;

		if (list.size() == 1) {
			onBaseDTObjectTap(list.get(0));
		} else if (getSupportMap().getCameraPosition().zoom >= maxZoomOnMap) {
			onBaseDTObjectsTap(list);
		} else {
			MapManager.fitMapWithOverlays(list, getSupportMap());
		}
		return true;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		manageMaxLevelOfZoom(position);
		render(objects);
	}

	private void manageMaxLevelOfZoom(CameraPosition position) {
		/* check if the zoom level is too high */
		if (position.zoom > maxZoomOnMap)
			getSupportMap().animateCamera(CameraUpdateFactory.zoomTo(maxZoomOnMap));
	}

	@Override
	public <T extends BaseDTObject> void addObjects(Collection<? extends BaseDTObject> objects) {
		if (getSupportMap() != null) {
			this.objects = objects;
			render(objects);
			MapManager.fitMapWithOverlays(objects, getSupportMap());
		}
	}

	private void render(Collection<? extends BaseDTObject> objects) {
		if (getSupportMap() != null) {
			// if (MapManager.getMapView()!=null)
			// {
			// MapManager.getMapView().getOverlays().clear();
			// MapManager.getMapView().invalidate();
			// }
			// getSupportMap().clear();
			// setUpMap();

			if (objects != null && getActivity() != null) {
				List<MarkerOptions> cluster = MapManager.ClusteringHelper.cluster(
						getActivity().getApplicationContext(), getSupportMap(), objects);
				MapManager.ClusteringHelper.removeAllMarkers();
				MapManager.ClusteringHelper.render(getActivity(), getSupportMap(), cluster, objects);
			}
		}

	}

}
