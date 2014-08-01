package eu.iescities.pilot.rovereto.inbici.entities.track;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import eu.iescities.pilot.rovereto.inbici.R;

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

public class OrderTracksDialogBox extends DialogFragment {

	int actual = 0;
	int idx = 0;

	TrackListingFragment order = null;

	static OrderTracksDialogBox newInstance(int num, TrackListingFragment order) {
		OrderTracksDialogBox f = new OrderTracksDialogBox();

		Bundle args = new Bundle();
		args.putInt(ListByOrder.ACTUAL_ORDER, num);
		args.putParcelable(ListByOrder.NEW_ORDER, order);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actual = getArguments().getInt(ListByOrder.ACTUAL_ORDER);
		order = getArguments().getParcelable(ListByOrder.NEW_ORDER);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.order_tracks__dialog, container, false);
		getDialog().setTitle(R.string.track_list_order);
		setCheckedButton(v);
		setListenerForChanging(v);
		// Watch for button clicks.
		Button button_save = (Button) v.findViewById(R.id.save_ok);
		button_save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				order.orderBy(idx);
				OrderTracksDialogBox.this.dismiss();
			}
		});

		// Watch for button clicks.
		Button button_cancel = (Button) v.findViewById(R.id.save_cancel);
		button_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				OrderTracksDialogBox.this.dismiss();
			}
		});
		return v;
	}

	private void setListenerForChanging(View v) {
		RadioGroup radioButtonGroup = (RadioGroup) v.findViewById(R.id.order_group);
		radioButtonGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId is the RadioButton selected
				int radioButtonID = group.getCheckedRadioButtonId();
				switch (radioButtonID) {
				case R.id.order_alpha:
					idx = ListByOrder.ORDER_BY_ALPHABETICAL;
					break;
				case R.id.order_distance:
					idx = ListByOrder.ORDER_BY_DISTANCE;
					break;
				case R.id.order_length:
					idx = ListByOrder.ORDER_BY_LENGHT;
					break;
				case R.id.order_altitude_gap:
					idx = ListByOrder.ORDER_BY_ALTITUDE_GAP;
					break;
				case R.id.order_avg_time:
					idx = ListByOrder.ORDER_BY_AVG_TIME;
					break;
				default:
					idx = ListByOrder.ORDER_BY_ALPHABETICAL;

				}
			}
		});
	}

	private void setCheckedButton(View v) {
		RadioButton button;
		if (actual == ListByOrder.ORDER_BY_ALPHABETICAL) {
			button = (RadioButton) v.findViewById(R.id.order_alpha);
			button.setChecked(true);
		}
		if (actual == ListByOrder.ORDER_BY_DISTANCE) {
			button = (RadioButton) v.findViewById(R.id.order_distance);
			button.setChecked(true);
		}
		if (actual == ListByOrder.ORDER_BY_LENGHT) {
			button = (RadioButton) v.findViewById(R.id.order_length);
			button.setChecked(true);
		}
		if (actual == ListByOrder.ORDER_BY_ALTITUDE_GAP) {
			button = (RadioButton) v.findViewById(R.id.order_altitude_gap);
			button.setChecked(true);
		}
		if (actual == ListByOrder.ORDER_BY_AVG_TIME) {
			button = (RadioButton) v.findViewById(R.id.order_avg_time);
			button.setChecked(true);
		}
	}
}
