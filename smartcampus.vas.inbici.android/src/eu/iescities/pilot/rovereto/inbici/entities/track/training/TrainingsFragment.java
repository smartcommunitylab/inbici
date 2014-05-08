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
package eu.iescities.pilot.rovereto.inbici.entities.track.training;

import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrainingObject;



public class TrainingsFragment extends Fragment {

	public static final String ARG_TRACK_ID = "track_id";

	TrackObject mTrack = null;
	String mTrackId;
	List<TrainingObject> mTrainings = null;

	private Fragment mFragment = this;

	public static TrainingsFragment newInstance(String id) {
		TrainingsFragment fragment = new TrainingsFragment();
		Bundle args = new Bundle();
		args.putString(TrainingsFragment.ARG_TRACK_ID, id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setHasOptionsMenu(true);

		if (getArguments() != null) {
			mTrackId = getArguments().getString(ARG_TRACK_ID);
			mTrack = getTrack();
		}
	}

	private TrackObject getTrack() {
		if (mTrackId == null) {
			mTrackId = getArguments().getString(ARG_TRACK_ID);
		}

		try {
			mTrack = InBiciHelper.findTrackById(mTrackId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mTrack;
	}

	private List<TrainingObject> getTrainings() {
		if (mTrackId == null) {
			mTrackId = getArguments().getString(ARG_TRACK_ID);
		}
		if (mTrainings == null) {
			try {
				mTrainings = InBiciHelper.getTrainings(mTrackId);
			} catch (Exception e) {
				e.printStackTrace();
				mTrainings = Collections.emptyList();
			}
		}
		return mTrainings;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.trainings, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (getTrack() != null) {
		}
	}
}
