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
package eu.iescities.pilot.rovereto.inbici.entities.track.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.res.Resources;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.map.MapManager;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;


public class TrackDetailsFragment extends ListFragment {

	TrackObject mTrack = null;
	String mTrackId;
	private TrackDetailsInfoAdapter adapter;


	private Fragment mFragment = this;

	public static TrackDetailsFragment newInstance(String id) {
		TrackDetailsFragment fragment = new TrackDetailsFragment();
		Bundle args = new Bundle();
		args.putString(Constants.ARG_TRACK_ID, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onAttach");
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if (savedInstanceState == null) {
			Log.d("SCROLLTABS", "onCreate FIRST TIME");
			if (getArguments() != null) {
				mTrackId = getArguments().getString(Constants.ARG_TRACK_ID);
				mTrack = InBiciHelper.getTrack(mTrackId);
			} else {
				Log.d("SCROLLTABS", "onCreate SUBSEQUENT TIME");
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tracks_info_details_list, container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onActivityCreated");

		mTrack = InBiciHelper.getTrack(mTrackId);


		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(mTrack.getTitle());


		//adapter = new EventDetailToKnowAdapter(getActivity(), R.layout.event_toknow_row_item, getTag(), mEventId);
		adapter = new TrackDetailsInfoAdapter(getActivity(), R.layout.tracks_info_details_row, getTag(), mTrackId);


		//getListView().setDivider(null);
		//getListView().setDivider(getResources().getDrawable(R.color.transparent));
		setListAdapter(adapter);


		//List<ToKnow> toKnowList = Utils.toKnowMapToList(getTrackData());
		List<TrackInfo> trackInfoList = getTrackData();

		adapter.addAll(trackInfoList);
		adapter.notifyDataSetChanged();


	}


	/*@Override
	public void onStart() {
		super.onStart();
		if (getTrack() != null) {
			// title
			TextView tv = (TextView) this.getView().findViewById(R.id.track_details_title);
			tv.setText(mTrack.getTitle());

	 * BUTTONS


			// map
			ImageButton mapBtn = (ImageButton) getView().findViewById(R.id.trackdetails_map);
			mapBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ArrayList<BaseDTObject> list = new ArrayList<BaseDTObject>();
					list.add(mTrack);
					MapManager.switchToMapView(list, mFragment);
				}
			});

			// directions
			ImageButton directionsBtn = (ImageButton) getView().findViewById(R.id.trackdetails_directions);
			directionsBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Address to = Utils.getTrackAsGoogleAddress(mTrack);
					Address from = null;
					GeoPoint mylocation = MapManager.requestMyLocation(getActivity());
					if (mylocation != null) {
						from = new Address(Locale.getDefault());
						from.setLatitude(mylocation.getLatitudeE6() / 1E6);
						from.setLongitude(mylocation.getLongitudeE6() / 1E6);
					}
					InBiciHelper.bringmethere(getActivity(), from, to);
				}
			});

	 * END BUTTONS


			// description, optional
			tv = (TextView) this.getView().findViewById(R.id.track_details_descr);
			String customDescr = mTrack.customDescription(getActivity());
			if (customDescr != null && customDescr.length() > 0) {
				tv.setText(Html.fromHtml(customDescr));
			} else {
				((LinearLayout) this.getView().findViewById(R.id.trackdetails)).removeView(tv);
			}
		}
	}*/




	private List<TrackInfo> getTrackData(){

		List<TrackInfo> list = new ArrayList<TrackInfo>();
		Resources res = getResources();

		if (mTrack != null) {

			//			list.add(ToKnow.newIstance(String.format(res.getString(R.string.average_travel_time)),
			//					mTrack.getAverage_travel_time()));


			list.add(new TrackInfo(String.format(res.getString(R.string.track_name)),
					mTrack.getTitle(), false));
			
			
			String customDescr = mTrack.customDescription(getActivity());
			Log.d("FRAGMENT LC", "TrackDetailsFragment --> customDescr: " + customDescr);
			if (customDescr != null && customDescr.length() > 0) {
				Log.d("FRAGMENT LC", "TrackDetailsFragment --> customDescr if: " + customDescr);
				list.add(new TrackInfo(String.format(res.getString(R.string.description)),customDescr, false));
			} 			


			list.add(new TrackInfo(String.format(res.getString(R.string.average_travel_time)),
					mTrack.getAverage_travel_time(), false, R.drawable.duration));

//			list.add(new TrackInfo(String.format(res.getString(R.string.track_lenght_descriptive)),
//					mTrack.getTrack_lenght_descriptive(), false, R.drawable.length));
		
			list.add(new TrackInfo(String.format(res.getString(R.string.track_lenght_descriptive)),
					mTrack.lengthString(getActivity()), false, R.drawable.length));
		
			


			list.add(new TrackInfo(String.format(res.getString(R.string.wind)),
					mTrack.getWind(), false));

			list.add(new TrackInfo(String.format(res.getString(R.string.altitude_gap)),
					mTrack.getAltitude_gap(), false, R.drawable.altitude));


			list.add(new TrackInfo(String.format(res.getString(R.string.type_of_surface)),
					mTrack.getType_of_surface(),false));

			list.add(new TrackInfo(String.format(res.getString(R.string.crossing_with_other_paths)),
					mTrack.getCrossing_with_other_paths(),false));

			list.add(new TrackInfo(String.format(res.getString(R.string.advised_season)),
					mTrack.getAdvised_season(), false));

			list.add(new TrackInfo(String.format(res.getString(R.string.traffic)),
					mTrack.getTraffic(), false));

			list.add(new TrackInfo(String.format(res.getString(R.string.number_of_registered_uses)),
					Integer.toString(mTrack.getNumber_of_registered_uses()), true, R.drawable.users_b));

			list.add(new TrackInfo(String.format(res.getString(R.string.last_training_date)),
					Long.toString(mTrack.getLast_training_date()), false));

			list.add(new TrackInfo(String.format(res.getString(R.string.elapsed_time)),
					Long.toString(mTrack.getElapsed_time()), false));


			list.add(new TrackInfo(String.format(res.getString(R.string.traveled_distance)),
					Double.toString(mTrack.getTraveled_distance()), true));


			list.add(new TrackInfo(String.format(res.getString(R.string.avg_speed)),
					Double.toString(mTrack.getAvg_speed()), true));

			list.add(new TrackInfo(String.format(res.getString(R.string.max_speed)),
					Double.toString(mTrack.getMax_speed()), true));

			list.add(new TrackInfo(String.format(res.getString(R.string.total_elevation)),
					Double.toString(mTrack.getTotal_elevation()), true));

		}

		return list;		
	}





	@Override
	public void onStart() {
		super.onStart();
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onStart");
	}


	@Override
	public void onResume() {
		super.onResume();
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onPause");

	}

	@Override
	public void onStop() {
		super.onStop();
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onStop");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onSaveInstanceState");

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onDestroy");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		//Log.d("FRAGMENT LC", "TrackDetailsFragment --> onDetach");
	}

}
