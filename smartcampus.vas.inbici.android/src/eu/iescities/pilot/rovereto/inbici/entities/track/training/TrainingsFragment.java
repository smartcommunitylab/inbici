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
import android.widget.ListView;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrainingObject;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.StatisticsCalulator;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;

public class TrainingsFragment extends Fragment {

	public static final String ARG_TRACK_ID = "track_id";
	private TrainingAdapter trainingAdapter;
	private TrackObject mTrack = null;
	private String mTrackId;
	private List<TrainingObject> mTrainings = null;

	private Fragment mFragment = this;
	private double averageDistanceTrack;
	private double averageTimeTrack;
	private double averageDifferenceTrack;

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
			if (mTrack != null)
				mTrainings = getTrainings();
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
		View returnView = inflater.inflate(R.layout.trainings, container, false);
		ListView listView = (ListView) returnView.findViewById(R.id.trainings_track);
		View header = inflater.inflate(R.layout.trainings_header, null);
		listView.addHeaderView(header);

		return returnView;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (getTrainings() != null) {
			// number of trainings
			TextView mNumberOfTrainingTrack = (TextView) getActivity().findViewById(R.id.number_of_trainings_track);
			mNumberOfTrainingTrack.setText(String.valueOf(mTrainings.size()));
			TextView mAverageDistanceTrack = (TextView) getActivity().findViewById(R.id.average_distance_track);
			averageDistanceTrack = StatisticsCalulator.getAvgDistanceTrack(mTrack,mTrainings.size());
			mAverageDistanceTrack.setText(String.valueOf(averageDistanceTrack));
			TextView mAverageSpeedTrack = (TextView) getActivity().findViewById(R.id.average_speed_track);
			mAverageSpeedTrack.setText(String.valueOf(mTrack.getAvg_speed()));
			TextView mMaximumSpeed = (TextView) getActivity().findViewById(R.id.maximum_speed_track);
			mMaximumSpeed.setText(String.valueOf(mTrack.getMax_speed()));
			TextView mAverageTime = (TextView) getActivity().findViewById(R.id.average_time_track);
			averageTimeTrack = StatisticsCalulator.getAvgTimeTrack(mTrainings);
			mAverageTime.setText(Utils.getTimeTrainingFormatted((long) averageTimeTrack));
			TextView mDifferenceTrack = (TextView) getActivity().findViewById(R.id.average_difference_track);
			averageDifferenceTrack = StatisticsCalulator.getAvgDifferenceTrack(mTrack,mTrainings.size());

			mDifferenceTrack.setText(String.valueOf(averageDifferenceTrack));

			TextView mLastTrainingDay = (TextView) getActivity().findViewById(R.id.last_training_day_track);
			mLastTrainingDay.setText(String.valueOf(mTrack.getLast_training_date()));

			trainingAdapter = new TrainingAdapter(getActivity(), R.layout.training_row, getTag(), mTrackId);
			ListView listView = (ListView) getActivity().findViewById(R.id.trainings_track);
			listView.setAdapter(trainingAdapter);
			List<TrainingObject> trainings = getTrainings();
			trainingAdapter.addAll(trainings);
			trainingAdapter.notifyDataSetChanged();

		}

	}



}
