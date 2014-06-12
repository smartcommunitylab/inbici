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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.EditText;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.DifferentTrackDialogBox.AddTrack;
import eu.trentorise.smartcampus.ac.SCAccessProvider;

/**
 * Dialog for user registration request
 * @author m.chini
 *
 */
public class NewTrackDialogBox {
	static AlertDialog.Builder builder;
	public static void newtrackfound(final Activity activity,final TrackObject mTrack, final AddTrack addtrack) {
			builder = new AlertDialog.Builder(activity);
			SCAccessProvider accessprovider =  SCAccessProvider.getInstance(activity);

			//
				// dialogbox for registration
				DialogInterface.OnClickListener updateDialogClickListener;

				updateDialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Dialog f = (Dialog) dialog;


							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								EditText trackName = (EditText) f.findViewById(R.id.track_name);
								mTrack.setTitle(trackName.getText().toString());
								addtrack.addNewTrack(mTrack);
								dialog.dismiss();
//								if (mLoggerServiceManager.getLoggingState() == Constants.PAUSED)
//									mLoggerServiceManager.resumeGPSLogging();
//								if (mLoggerServiceManager.getLoggingState() == Constants.STOPPED)
//									activity.finish();
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								dialog.dismiss();
//								mLoggerServiceManager.stopGPSLogging();
//								InBiciHelper.removeTrackIdFromSP(preferences);
								activity.finish();

								break;
							
							}

					}
				};
				
				builder.setCancelable(false).setMessage(activity.getString(R.string.name_of_track))
						.setPositiveButton(android.R.string.yes, updateDialogClickListener)
						.setNegativeButton(android.R.string.no, updateDialogClickListener)
						.setView(activity.getLayoutInflater().inflate(R.layout.save_track_dialog, null)).show();
			
		
}
}
