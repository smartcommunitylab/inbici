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
package eu.iescities.pilot.rovereto.inbici.entities.track.logger;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.trentorise.smartcampus.profileservice.model.BasicProfile;

/**
 * Dialog for user registration request
 * 
 * @author m.chini
 * 
 * 
 * 
 */

public class DifferentTrackDialogBox {

	static AlertDialog.Builder builder;
	public static void newtrackfound(final Activity activity, final SharedPreferences preferences,
			final TrackObject mTrack, final AddTrack addtrack, final GPSLoggerServiceManager mLoggerServiceManager,
			final BasicProfile bp, final List<LatLng> decodedLine) {

		builder = new AlertDialog.Builder(activity);
		
		//
		// dialogbox for registration
		DialogInterface.OnClickListener updateDialogClickListener;

		updateDialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Dialog f = (Dialog) dialog;
				double[] initPosition = new double[2];
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
//					List<LatLng> line = mTrack.decodedLine();
					TrackObject mTrack = new TrackObject();
					// TrainingObject mTraining = new TrainingObject();
					EditText trackName = (EditText) f.findViewById(R.id.track_name);
					mTrack.setTitle(trackName.getText().toString());
					if (bp != null){
						
						mTrack.setCreator(bp.getUserId());
					}
					if (decodedLine!=null){
						initPosition[0]=decodedLine.get(0).latitude;
						initPosition[1]=decodedLine.get(0).longitude;
						mTrack.setLocation(initPosition);
						mTrack.encodedLine(decodedLine);
					}
					//se come parametro trovo mTrack lo aggiungo (nuovo tracciato)
					if (mLoggerServiceManager.getLoggingState() == Constants.PAUSED
							|| mLoggerServiceManager.getLoggingState() == Constants.LOGGING) {
						// I am at the beginning

						mLoggerServiceManager.resumeGPSLogging();
						addtrack.changeTrack(mTrack);
						addtrack.addNewTrack(mTrack);
						// addtrack.changeTraining(mTraining);

					}
					if (mLoggerServiceManager.getLoggingState() == Constants.STOPPED || mLoggerServiceManager.getLoggingState() == Constants.UNKNOWN) {
						// I am at the end
						activity.finish();
						addtrack.addNewTrack(mTrack);
						dialog.dismiss();
					}
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					mLoggerServiceManager.stopGPSLogging();
					InBiciHelper.removeTrackIdFromSP(preferences);
					activity.finish();

					break;

				}

			}
		};

		builder.setCancelable(false).setMessage(activity.getString(R.string.new_track_found))
				.setPositiveButton(android.R.string.yes, updateDialogClickListener)
				.setNegativeButton(android.R.string.no, updateDialogClickListener)
				.setView(activity.getLayoutInflater().inflate(R.layout.save_track_dialog, null)).show();

	}

	public interface AddTrack {

		public void addNewTrack(TrackObject mTrack);

		// public void changeTraining(TrainingObject mTraining);

		public void changeTrack(TrackObject mTrack);
	}
}
