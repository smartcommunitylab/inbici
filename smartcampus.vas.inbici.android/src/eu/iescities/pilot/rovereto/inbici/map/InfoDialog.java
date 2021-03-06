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
package eu.iescities.pilot.rovereto.inbici.map;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.entities.track.TrackContainerFragment;

public class InfoDialog extends DialogFragment {
	public static final String PARAM = "DTO_OBJECT";
	private BaseDTObject data;

	public InfoDialog() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (this.data == null) {
			this.data = (BaseDTObject) getArguments().getSerializable(PARAM);
		}
		if (data instanceof TrackObject) {
			getDialog().setTitle(R.string.info_dialog_title_event);
		}
		return inflater.inflate(R.layout.mapdialog, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		TextView msg = (TextView) getDialog().findViewById(R.id.mapdialog_msg);

		if (data instanceof TrackObject) {
			TrackObject event = (TrackObject) data;
			String msgText = "";
			msgText += "<h2>";
			msgText += event.getTitle();
			msgText += "</h2><br/><p>";
			msg.setText(Html.fromHtml(msgText));
		}

		msg.setMovementMethod(new ScrollingMovementMethod());

		Button b = (Button) getDialog().findViewById(R.id.mapdialog_cancel);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getDialog().dismiss();
			}
		});

		b = (Button) getDialog().findViewById(R.id.mapdialog_ok);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				if (data instanceof TrackObject) {
					TrackContainerFragment fragment = TrackContainerFragment.newInstance(data.getId());
					fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					fragmentTransaction.replace(R.id.content_frame, fragment, "me");
					fragmentTransaction.addToBackStack(fragment.getTag());
				}
				fragmentTransaction.commit();
				getDialog().dismiss();
			}
		});

	}
}
