package eu.iescities.pilot.rovereto.inbici.entities.track;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

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

/**
 * Dialog for user registration request
 * 
 * @author m.chini
 * 
 * 
 * 
 * 
 */

public class SearchTracksDialogBox extends DialogFragment {

	int actual = 0;
	int idx = 0;
	public static final String ALPHABETICAL = "alpha";
	public static final String DISTANCE = "distance";
	public static final String ADAPTER = "adapter";
	private String order_by = ALPHABETICAL;
	private EditText searchEditText = null;
	private SeekBar searchSeekBar = null;
	private TextView seekBarValueText = null;
	private TrackListingFragment order = null;
	TrackAdapter trackAdapter =null;
	final SearchListInterface searchInterfaceLocal =null;
	
	static SearchTracksDialogBox newInstance(int num, TrackListingFragment order, TrackAdapter trackAdapter) {
		SearchTracksDialogBox f = new SearchTracksDialogBox();

		Bundle args = new Bundle();
		args.putInt(ListByOrder.ACTUAL_ORDER, num);
		args.putParcelable(ListByOrder.NEW_ORDER, order);
		args.putParcelable(ADAPTER, trackAdapter);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// actual = getArguments().getInt(ListByOrder.ACTUAL_ORDER);
		// order = getArguments().getParcelable(ListByOrder.NEW_ORDER);
		trackAdapter =getArguments().getParcelable(ADAPTER);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View view = inflater.inflate(R.layout.search_tracks__dialog, container, false);
		((RadioButton) view.findViewById(R.id.search_alpha)).setChecked(true);
		searchEditText = ((EditText) view.findViewById(R.id.search_alpha_text));
		((RadioButton) view.findViewById(R.id.search_distance)).setChecked(false);
		seekBarValueText = ((TextView) view.findViewById(R.id.search_seek_by_value));

		((SeekBar) view.findViewById(R.id.search_distance_bar)).setEnabled(false);
		searchSeekBar = ((SeekBar) view.findViewById(R.id.search_distance_bar));
		searchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				seekBarValueText.setText(String.valueOf(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
		});

		getDialog().setTitle(R.string.track_list_search);
		RadioButton alpha_button = (RadioButton) view.findViewById(R.id.search_alpha);
		alpha_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((RadioButton) view.findViewById(R.id.search_distance)).setChecked(false);
				((SeekBar) view.findViewById(R.id.search_distance_bar)).setEnabled(false);
				((EditText) view.findViewById(R.id.search_alpha_text)).setEnabled(true);
				order_by = ALPHABETICAL;

			}
		});
		RadioButton distance_button = (RadioButton) view.findViewById(R.id.search_distance);
		distance_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((RadioButton) view.findViewById(R.id.search_alpha)).setChecked(false);
				((EditText) view.findViewById(R.id.search_alpha_text)).setEnabled(false);
				((SeekBar) view.findViewById(R.id.search_distance_bar)).setEnabled(true);
				order_by = DISTANCE;

			}
		});

		Button ok_button = (Button) view.findViewById(R.id.search_ok);
		ok_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (ALPHABETICAL.equals(order_by)) {
					// get text from editText
					String searchText = searchEditText.getText().toString();
					new SCAsyncTask<String, Void, List<TrackObject>>(getActivity(), new TrackSearchProcessor(
							getActivity())).execute(searchText);
				} else if (DISTANCE.equals(order_by)) {
					// get text from bar
					String searchText = seekBarValueText.getText().toString();

					searchBY(searchText);

				}
				SearchTracksDialogBox.this.dismiss();
			}
		});
		Button canel_button = (Button) view.findViewById(R.id.search_cancel);
		canel_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SearchTracksDialogBox.this.dismiss();
			}
		});

		return view;
	}

	protected List<TrackObject> searchBY(String searchText) {
		// search by parameters using
		if (ALPHABETICAL.equals(order_by)) {
			return new ArrayList<TrackObject>(InBiciHelper.searchTrackByName(searchText));
		} else if (DISTANCE.equals(order_by)) {
			return new ArrayList<TrackObject>(InBiciHelper.searchByDistance(searchText));

		}
		return null;
	}

	private class TrackSearchProcessor extends AbstractAsyncTaskProcessor<String, List<TrackObject>> {

		public TrackSearchProcessor(Activity activity) {
			super(activity);
		}

		// fetches the events
		@Override
		public List<TrackObject> performAction(String... params) throws SecurityException, Exception {
			return searchBY(params[0]);
		}

		// populates the listview with the events
		@Override
		public void handleResult(List<TrackObject> result) {
			//close the dialog and show on the list result;
			trackAdapter.clear();
			if (result!=null)
			{
				trackAdapter.addAll(result);
				trackAdapter.notifyDataSetChanged();

			}
		}
	}
}
