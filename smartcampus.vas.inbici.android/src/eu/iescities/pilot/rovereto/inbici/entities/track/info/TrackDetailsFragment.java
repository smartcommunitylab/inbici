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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;

public class TrackDetailsFragment extends Fragment {

	TrackObject mTrack = null;
	String mTrackId;
	// private TrackDetailsInfoAdapter adapter;

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

		mTrack = InBiciHelper.getTrack(mTrackId);

		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(mTrack.getTitle());

	}

	@Override
	public void onStart() {
		super.onStart();
		// Log.d("FRAGMENT LC", "TrackDetailsFragment --> onStart");
		if (mTrack != null) {
			TextView mTrackName = (TextView) getActivity().findViewById(R.id.track_name);
			mTrackName.setText(mTrack.getTitle());
			TextView mAverageTravelTime = (TextView) getActivity().findViewById(R.id.track_average_travel_time);

			mAverageTravelTime.setText(mTrack.getFormattedTravelTime());
			TextView mLength = (TextView) getActivity().findViewById(R.id.track_length);
			mLength.setText(mTrack.getFormattedTraveledDistance());
			TextView mNumberOfRegisteredUsed = (TextView) getActivity().findViewById(
					R.id.track_number_of_registered_uses);
			mNumberOfRegisteredUsed.setText(String.valueOf(mTrack.getNumber_of_registered_uses()));
			TextView mAverageSpeed = (TextView) getActivity().findViewById(R.id.track_avg_speed);
			mAverageSpeed.setText(String.valueOf(mTrack.getAvg_speed()));
			TextView mMaxSpeed = (TextView) getActivity().findViewById(R.id.track_max_speed);
			mMaxSpeed.setText(String.valueOf(mTrack.getMax_speed()));
			TextView mElapsedTime = (TextView) getActivity().findViewById(R.id.track_elapsed_time);
			mElapsedTime.setText(Utils.getTimeTrainingFormatted(mTrack.getElapsed_time()));
			TextView mTotalElevation = (TextView) getActivity().findViewById(R.id.track_total_elevation);
			mTotalElevation.setText(String.valueOf(mTrack.getTotal_elevation()));
			TextView mTraveledDistance = (TextView) getActivity().findViewById(R.id.track_traveled_distance);
			mTraveledDistance.setText(String.valueOf(mTrack.getTraveled_distance() / 1000.0)); // km
			if (mTrack.getWind() == null) {
				// hide wind
				hide_statistics(R.id.layouttrack_wind, R.id.layouttrack_wind_separator);

			} else {
				TextView mWind = (TextView) getActivity().findViewById(R.id.track_wind);
				mWind.setText(String.valueOf(mTrack.getWind()));

			}

			if (mTrack.getAltitude_gap() == null) {
				// hide altitude gap
				hide_statistics(R.id.layouttrack_altitude_gap, R.id.layouttrack_altitude_gap_separator);

			} else {
				TextView mAltitudeGap = (TextView) getActivity().findViewById(R.id.track_altitude_gap);
				mAltitudeGap.setText(String.valueOf(mTrack.getAltitude_gap()));
			}
			if (mTrack.getType_of_surface() == null) {
				// hide type of surface
				hide_statistics(R.id.layouttrack_type_of_surface, R.id.layouttrack_type_of_surface);

			} else {
				TextView mTypeOfSurface = (TextView) getActivity().findViewById(R.id.track_type_of_surface);
				mTypeOfSurface.setText(String.valueOf(mTrack.getType_of_surface()));
			}
			if (mTrack.getCrossing_with_other_paths() == null) {
				// hide crossing
				hide_statistics(R.id.layouttrack_crossing_with_other_path,R.id.layouttrack_crossing_with_other_path_separator);

			} else {
				TextView mCrossing = (TextView) getActivity().findViewById(R.id.track_crossing_with_other_paths);
				mCrossing.setText(String.valueOf(mTrack.getCrossing_with_other_paths()));
			}
			if (mTrack.getAdvised_season() == null) {
				hide_statistics(R.id.layouttrack_advised_season, R.id.layouttrack_advised_season_separator);
			} else {
				TextView mAdvisedSeason = (TextView) getActivity().findViewById(R.id.track_advised_season);
				mAdvisedSeason.setText(String.valueOf(mTrack.getAdvised_season()));
			}
			if (mTrack.getTraffic() == null) {
				// hide traffic
				hide_statistics(R.id.layouttrack_traffic, R.id.layouttrack_traffic_separator);
			} else {
				TextView mTraffic = (TextView) getActivity().findViewById(R.id.track_traffic);
				mTraffic.setText(String.valueOf(mTrack.getTraffic()));
			}
		}
	}

	private void hide_statistics(int linearLayout, int separator) {
		LinearLayout ll = (LinearLayout) getActivity().findViewById(linearLayout);
		ll.setVisibility(View.GONE);
		LinearLayout lls = (LinearLayout) getActivity().findViewById(separator);
		lls.setVisibility(View.GONE);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.d("FRAGMENT LC", "TrackDetailsFragment --> onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		// Log.d("FRAGMENT LC", "TrackDetailsFragment --> onPause");

	}

	@Override
	public void onStop() {
		super.onStop();
		// Log.d("FRAGMENT LC", "TrackDetailsFragment --> onStop");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Log.d("FRAGMENT LC", "TrackDetailsFragment --> onSaveInstanceState");

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// Log.d("FRAGMENT LC", "TrackDetailsFragment --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Log.d("FRAGMENT LC", "TrackDetailsFragment --> onDestroy");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		// Log.d("FRAGMENT LC", "TrackDetailsFragment --> onDetach");
	}

}
