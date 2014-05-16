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

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrainingObject;
import eu.iescities.pilot.rovereto.inbici.entities.track.TrackPlaceholder;
import eu.iescities.pilot.rovereto.inbici.entities.training.TrainingPlaceholder;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;

public class TrainingAdapter extends ArrayAdapter<TrainingObject> {

	private Context context;
	private int layoutResourceId;
	private int elementSelected = -1;
	private String mTag;
	private String mTrainingId;

	public TrainingAdapter(Context context, int layoutResourceId, String string, String mTrackId) {
		super(context, layoutResourceId);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.mTag = mTag;
		this.mTrainingId = mTrackId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		TrainingPlaceholder p = null;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
			p = new TrainingPlaceholder();
			p.numberOfTraining = (TextView) row.findViewById(R.id.number_of_training);
			p.startTime = (TextView) row.findViewById(R.id.start_time_training);
			p.durationTime = (TextView) row.findViewById(R.id.stop_time_training);
			p.totalDistance = (TextView) row.findViewById(R.id.total_distance_training);
			p.totalDifference = (TextView) row.findViewById(R.id.total_difference_training);
			row.setTag(p);
		} else
			p = (TrainingPlaceholder) row.getTag();

		p.numberOfTraining.setText(String.valueOf(position));
		p.training = getItem(position);// data[position];
		//convert to time
		p.startTime.setText(Utils.setDateString(p.training.getStartTime()));
		p.durationTime.setText(Utils.getTimeTrainingFormatted((p.training.getRunningTime().longValue())));
		p.totalDistance.setText(String.valueOf(p.training.getDistance()));
		p.totalDifference.setText(String.valueOf(p.training.getElevation()));
		return row;
	}

	public int getElementSelected() {
		return elementSelected;
	}

	public void setElementSelected(int elementSelected) {
		this.elementSelected = elementSelected;
	}

}
