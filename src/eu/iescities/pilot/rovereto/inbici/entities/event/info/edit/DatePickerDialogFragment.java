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
package eu.iescities.pilot.rovereto.inbici.entities.event.info.edit;

import java.util.Calendar;
import java.util.Date;

import eu.iescities.pilot.rovereto.inbici.utils.Utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	private static final String DATA = "data";
	private EditText dateEditText;

	private Context context;

	public static Bundle prepareData(String date) {
		Bundle b = new Bundle();
		b.putString(DATA, date);
		return b;
	}

	static DatePickerDialogFragment newInstance(EditText dateEditText) {
		DatePickerDialogFragment f = new DatePickerDialogFragment();
		f.setDateEditText(dateEditText);
		return f;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		if (getArguments() != null && getArguments().containsKey(DATA)) {
			try {
				Date d = Utils.FORMAT_DATE_UI_LONG.parse((String) getArguments().getString(DATA));
				c.setTime(d);
			} catch (Exception e) {
			}
		}

		if (getDateEditText().getTag() != null) {
			c.setTime((Date) getDateEditText().getTag());
		}

		// Use the current date as the default date in the picker
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		Date date = calendar.getTime();
		String formattedDate = Utils.FORMAT_DATE_UI_LONG.format(date);
		getDateEditText().setTag(date);
		getDateEditText().setText(formattedDate);
		getDialog().dismiss();
	}

	public EditText getDateEditText() {
		return dateEditText;
	}

	public void setDateEditText(EditText dateEditText) {
		this.dateEditText = dateEditText;
	}

}
