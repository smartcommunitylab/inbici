package eu.iescities.pilot.rovereto.inbici.entities.event.info.edit;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.AutoCompleteTextView.Validator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.DTParamsHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.Address;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.GeocodingAutocompletionHelper;
import eu.trentorise.smartcampus.android.common.GeocodingAutocompletionHelper.OnAddressSelectedListener;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.android.common.geo.OSMAddress;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_Info_WhenWhere extends Fragment {

	private Context context;

	public final static int REQUEST_CODE= 10;


	private ExplorerObject mEvent = null;
	private String mEventId;
	
	protected TextView txtWhenWhere;
	protected TextView formLabel;
	protected EditText txtStartDay;
	protected EditText txtStartTime;
	protected EditText txtEndDay;
	protected EditText txtEndTime;

	//this edit field is currently disabled
	//protected EditText txtDuration;

	protected Date fromDate;
	protected Date fromTime;
	protected EditText txtPlaceName;
	protected EditText txtCity;
	protected AutoCompleteTextView txtStreet;
	protected Position where;


	OSMAddress selectedAddress = null;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onCreate");

		this.context = this.getActivity();

		if (savedInstanceState == null) {
			Log.d("FRAGMENT LC", "onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
				Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Where --> EVENT ID: " + mEventId);
				mEvent = DTHelper.findEventById(mEventId);
			}

		} else {
			Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onCreate SUBSEQUENT TIME");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onCreateView");

		return inflater.inflate(R.layout.frag_ev_detail_info_edit_when_where, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<Double> mapcenter = DTParamsHelper.getCenterMap();
		double[] refLoc = mapcenter == null ? null : new double[] { mapcenter.get(0), mapcenter.get(1) };

		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onActivityCreated");

		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.modify) + " " + getResources().getString(R.string.date_place_name));

//		getActivity().getActionBar().setTitle(
//				getResources().getString(R.string.modify) + " " + getResources().getString(R.string.date_place_name));

		txtWhenWhere= (EditText) getActivity().findViewById(R.id.when_where_text);
		txtStartDay = (EditText) getActivity().findViewById(R.id.start_day_text);
		txtStartTime = (EditText) getActivity().findViewById(R.id.start_time_text);
		txtEndDay = (EditText) getActivity().findViewById(R.id.end_day_text);
		txtEndTime = (EditText) getActivity().findViewById(R.id.end_time_text);
		//txtDuration = (EditText) getActivity().findViewById(R.id.duration_text);

		formLabel = (TextView) getActivity().findViewById(R.id.title_when_where_label);
		txtPlaceName = (EditText) getActivity().findViewById(R.id.place_name_text);
		txtCity = (EditText) getActivity().findViewById(R.id.city_text);
		//txtStreet = (EditText) getActivity().findViewById(R.id.street_text);

		txtStreet = (AutoCompleteTextView) getView().findViewById(R.id.street_text);


		GeocodingAutocompletionHelper fromAutocompletionHelper = new GeocodingAutocompletionHelper(getActivity(),
				txtStreet, Utils.ROVERETO_REGION, Utils.ROVERETO_COUNTRY, Utils.ROVERETO_ADM_AREA, refLoc);
		fromAutocompletionHelper.setOnAddressSelectedListener(new OnAddressSelectedListener() {
			@Override
			public void onAddressSelected(android.location.Address address) {

				Log.i("ADDRESS", "Fragment_EvDetail_Info_WhenWhere --> onAddressSelected");

				//convert from address to OsmAddress
				
				savePosition(Utils.getOsmAddressFromAddress(address));

			}

		});
		ImageView imgBtn = (ImageView) getView().findViewById(R.id.select_where_map);

		if (imgBtn != null) {
			imgBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), AddressSelectActivity.class);
					//not useful but necessary because otherwise the app crashes 
					//in that it is dependent on line 57 of the InfoDialog class of the  
					//package eu.trentorise.smartcampus.android.map;
					intent.putExtra("field", Utils.ADDRESS);
					//launch the sub-activity to locate an address in the map
					startActivityForResult(intent, REQUEST_CODE);
				}
			});

		}
		formLabel.setText("Evento: " + mEvent.getTitle());

		if (mEvent.getWhenWhere() != null) {

			txtWhenWhere.setText(mEvent.getWhenWhere());
		} else {
			txtWhenWhere.setText("");
		}
		
		if (mEvent.getFromTime() != null) {
			String[] fromDateTime = Utils.getDateTimeString(this.context, mEvent.getFromTime(), Utils.DATETIME_FORMAT,
					false, false);
			Log.d("FRAGMENT LC", "Fragment_evDetail_Info_When --> from Time: " + fromTime);
			txtStartDay.setText(fromDateTime[0]);
			if (!fromDateTime[1].matches(""))
				txtStartTime.setText(fromDateTime[1]);
		} else {
			txtStartDay.setText(getResources().getString(R.string.day_hint));
			txtStartTime.setText(getResources().getString(R.string.time_hint));
		}

		txtStartDay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// DialogFragment f =
				// DatePickerDialogFragment.newInstance((EditText) v);
				DialogFragment f = DatePickerDialogFragment.newInstance((EditText) v);
				f.setArguments(DatePickerDialogFragment.prepareData(txtStartDay.getText().toString()));
				f.show(getActivity().getSupportFragmentManager(), "datePicker");
			}
		});

		txtStartTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment f = TimePickerDialogFragment.newInstance((EditText) v);
				f.setArguments(TimePickerDialogFragment.prepareData(txtStartTime.getText().toString()));
				f.show(getActivity().getSupportFragmentManager(), "timePicker");

			}
		});

		if ((mEvent.getToTime() != null) && (mEvent.getToTime() != 0)) {
			String[] toDateTime = Utils.getDateTimeString(this.context, mEvent.getToTime(), Utils.DATETIME_FORMAT,
					false, false);
			Log.d("FRAGMENT LC", "Fragment_evDetail_Info_When --> to Time: " + toDateTime);
			txtEndDay.setText(toDateTime[0]);
			if (!toDateTime[1].matches(""))
				txtEndTime.setText(toDateTime[1]);
			// compute duration, currently duration edit field is disabled
//			String duration = "3 ore";
//			txtDuration.setText(duration);
		} else {
//			txtEndDay.setText(getResources().getString(R.string.day_hint));
//			txtEndTime.setText(getResources().getString(R.string.time_hint));
			txtEndDay.setText("");
			txtEndTime.setText("");
		}

		txtEndDay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment f = DatePickerDialogFragment.newInstance((EditText) v);
				f.setArguments(DatePickerDialogFragment.prepareData(txtEndDay.getText().toString()));
				f.show(getActivity().getSupportFragmentManager(), "datePicker");
			}
		});

		txtEndTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment f = TimePickerDialogFragment.newInstance((EditText) v);
				f.setArguments(TimePickerDialogFragment.prepareData(txtEndTime.getText().toString()));
				f.show(getActivity().getSupportFragmentManager(), "timePicker");

			}
		});

		Address address = mEvent.getAddress();

		if (address != null) {

			String place = (address.getLuogo() != null) ? (String) address.getLuogo() : "";
			String street = (address.getVia() != null) ? (String) address.getVia() : "";
			String city = (address.getCitta() != null) ? (String) address.getCitta() : "";
			txtPlaceName.setText(place);
			txtCity.setText(city);
			txtStreet.setText(street);
		}

		Button modifyBtn = (Button) getView().findViewById(R.id.edit_whenwhere_modify_button);
		modifyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Address modifiedAddress = new Address();
				modifiedAddress.setLuogo(txtPlaceName.getText().toString());
				modifiedAddress.setVia(txtStreet.getText().toString());
				modifiedAddress.setCitta(txtCity.getText().toString());
				mEvent.setAddress(modifiedAddress);

				// persist the new contacts
				Log.i("FRAGMENT LC", "Edited Fields: " + txtStartDay.getText() + ", " + txtStartTime.getText() + ", "
						+ txtEndDay.getText() + ", " + txtEndTime.getText());

				// Toast.makeText(
				// context,
				// "Edited Fields: " + txtStartDay.getText() + ", " +
				// txtStartTime.getText() + ", "
				// + txtEndDay.getText() + ", " + txtEndTime.getText(),
				// Toast.LENGTH_SHORT).show();

				if (!txtStartDay.getText().toString().matches("")) {
					String start_datetime;
					if (!txtStartTime.getText().toString().matches("")) {
						start_datetime = txtStartDay.getText().toString() + " " + txtStartTime.getText().toString();
						Log.i("FRAGMENT LC", "datatime inizio string: " + start_datetime);
						Log.i("FRAGMENT LC",
								"datatime inizio long: " + Utils.toDateTimeLong(Utils.DATETIME_FORMAT, start_datetime));
						String[] fromTime = Utils.getDateTimeString(context,
								Utils.toDateTimeLong(Utils.DATETIME_FORMAT, start_datetime), Utils.DATETIME_FORMAT,
								false, false);
						Log.i("FRAGMENT LC", "datatime inizio string converted: " + fromTime);
						mEvent.setFromTime(Utils.toDateTimeLong(Utils.DATETIME_FORMAT, start_datetime));
					} else {
						start_datetime = txtStartDay.getText().toString();
						Log.i("FRAGMENT LC", "data inizio string: " + start_datetime);
						Log.i("FRAGMENT LC",
								"data inizio long: " + Utils.toDateTimeLong(Utils.DATE_FORMAT, start_datetime));
						String[] fromTime = Utils.getDateTimeString(context,
								Utils.toDateTimeLong(Utils.DATE_FORMAT, start_datetime), Utils.DATE_FORMAT, false,
								false);
						Log.i("FRAGMENT LC", "data inizio string converted: " + fromTime);
						mEvent.setFromTime(Utils.toDateTimeLong(Utils.DATE_FORMAT, start_datetime));
					}
				}

				if (!txtEndDay.getText().toString().matches("")) {
					String end_datetime;
					if (!txtEndTime.getText().toString().matches("")) {
						end_datetime = txtEndDay.getText().toString() + " " + txtEndTime.getText().toString();
						Log.i("FRAGMENT LC", "datatime fine string: " + end_datetime);
						Log.i("FRAGMENT LC",
								"datatime fine long: " + Utils.toDateTimeLong(Utils.DATETIME_FORMAT, end_datetime));
						String[] toTime = Utils.getDateTimeString(context,
								Utils.toDateTimeLong(Utils.DATETIME_FORMAT, end_datetime), Utils.DATETIME_FORMAT,
								false, false);
						Log.i("FRAGMENT LC", "datatime fine string converted: " + toTime);
						mEvent.setToTime(Utils.toDateTimeLong(Utils.DATETIME_FORMAT, end_datetime));
					} else {
						end_datetime = txtEndDay.getText().toString();
						Log.i("FRAGMENT LC", "data fine string: " + end_datetime);
						Log.i("FRAGMENT LC", "data fine long: " + Utils.toDateTimeLong(Utils.DATE_FORMAT, end_datetime));
						String[] toTime = Utils.getDateTimeString(context,
								Utils.toDateTimeLong(Utils.DATE_FORMAT, end_datetime), Utils.DATE_FORMAT, false, false);
						Log.i("FRAGMENT LC", "data fine string converted: " + toTime);
						mEvent.setToTime(Utils.toDateTimeLong(Utils.DATE_FORMAT, end_datetime));
					}
				}
				if (!checkDateTime()) {
					Toast.makeText(getActivity(), getActivity().getString(R.string.toast_time_wrong),
							Toast.LENGTH_SHORT).show();
					return;

				}

				new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
				.execute(mEvent);
				// Utils.appEvents.set(index, mEvent);

			}
		});

		Button cancelBtn = (Button) getView().findViewById(R.id.edit_contacts_cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});

	}

	protected boolean checkDateTime() {
		Date fromDate = null;
		Date toDate = null;
		Date fromTime = null;
		Date toTime = null;

		try {
			fromDate = Utils.FORMAT_DATE_UI_LONG.parse(txtStartDay.getText().toString());
			toDate = Utils.FORMAT_DATE_UI_LONG.parse(txtEndDay.getText().toString());
			fromTime = Utils.FORMAT_TIME_UI.parse(txtStartTime.getText().toString());
			toTime = Utils.FORMAT_TIME_UI.parse(txtEndTime.getText().toString());

		} catch (ParseException e) {
		}
		if (toDate!=null ){
		if (fromDate.after(toDate))
			return false;
		if (fromDate.equals(toDate))
			if (fromTime.after(toTime))
				return false;
		}
		return true;
	}

	//	private void savePosition(android.location.Address address) {
	//
	//		Log.i("ADDRESS", "Fragment_EvDetail_Info_WhenWhere --> saveAddress");
	//
	//		EditText street = null;
	//		EditText city = null;
	//
	//		String s = "";
	//		for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
	//
	//			Log.i("ADDRESS", "AddressLine: " +  address.getAddressLine(i));
	//			s += address.getAddressLine(i) + " ";
	//		}
	//		s = s.trim();
	//
	//		Log.i("ADDRESS", "Fragment_EvDetail_Info_WhenWhere --> AddressLine0: " +  address.getAddressLine(0));
	//		Log.i("ADDRESS", "Fragment_EvDetail_Info_WhenWhere --> Address Country: " +  address.getCountryName());
	//		Log.i("ADDRESS", "Fragment_EvDetail_Info_WhenWhere --> Address Locality: " +  address.getLocality());
	//
	//
	//		where = new Position(address.getAddressLine(0), address.getCountryName(), address.getLocality(),
	//				address.getLongitude(), address.getLatitude());
	//
	//		if (getView() != null) {
	//			street = (EditText) getView().findViewById(R.id.street_text);
	//			city = (EditText) getView().findViewById(R.id.city_text);
	//		}
	//
	//		if (street != null) {
	//			street.setFocusable(false);
	//			street.setFocusableInTouchMode(false);
	//			street.setText(s);
	//			street.setFocusable(true);
	//			street.setFocusableInTouchMode(true);
	//		}
	//
	//		if (city != null) {
	//			city.setFocusable(false);
	//			city.setFocusableInTouchMode(false);
	//			city.setText(address.getLocality());
	//			city.setFocusable(true);
	//			city.setFocusableInTouchMode(true);
	//		}
	//
	//	}


	private void savePosition(OSMAddress address) {
		if (address!=null){
			if (address.getName()!=null && !address.getName().matches(address.getStreet()))
				txtPlaceName.setText(address.getName());
			txtCity.setText(address.city());
			txtStreet.setText(address.getStreet());
			mEvent.setLocation(address.getLocation());
		}
	}



	// to be deleted when there will be the call to the server
	public void setNewEventContacts(String eventID, String[] tel, String[] email, String website) {

		// //set the new fields
		// Map<String,Object> contacts = new HashMap<String, Object>();
		// contacts.put("telefono", tel);
		// contacts.put("email", email);
		// mEvent.getContacts().clear();
		// mEvent.setContacts(contacts);
		// mEvent.setWebsiteUrl(website);

	}

	//	@Override
	//	public void onActivityResult(int requestCode, int resultCode, Intent result) {
	//
	//		if (resultCode == RESULT_SELECTED) {
	//			android.location.Address address = result.getParcelableExtra("address");
	//			String field = result.getExtras().getString("field");
	//			savePosition(address);
	//		}
	//	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result_data) {

		if (resultCode == android.app.Activity.RESULT_OK && requestCode == REQUEST_CODE) {
			selectedAddress = (OSMAddress) result_data.getSerializableExtra(Utils.ADDRESS);
			savePosition(selectedAddress);
		}
	} 



	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onStart");

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onResume");

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onPause");

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onSaveInstanceState");

	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onStop");

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onDestroyView");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onDestroy");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Where --> onDetach");

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		Log.i("FRAGMENT LC", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());

		menu.clear();

		// getActivity().getMenuInflater().inflate(R.menu.event_detail_menu,
		// menu);

		/*
		 * if (category == null) { category = (getArguments() != null) ?
		 * getArguments().getString(SearchFragment.ARG_CATEGORY) : null; }
		 */
		super.onPrepareOptionsMenu(menu);
	}

	private class UpdateEventProcessor extends AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {

		public UpdateEventProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public Boolean performAction(ExplorerObject... params) throws SecurityException, Exception {

			// to be enabled when the connection with the server is ok
			return DTHelper.saveEvent(params[0]);
			// store the modified event
			// int index = Utils.appEvents.indexOf(params[0]);
			// Utils.appEvents.set(index, params[0]);
			// ExplorerObject mNewEvent = Utils.appEvents.get(index);
			// return true;
		}

		@Override
		public void handleResult(Boolean result) {
			if (getActivity() != null) {
				getActivity().getSupportFragmentManager().popBackStack();

				if (result) {
					Toast.makeText(getActivity(), R.string.event_create_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

}