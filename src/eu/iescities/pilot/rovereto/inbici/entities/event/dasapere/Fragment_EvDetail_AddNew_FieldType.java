package eu.iescities.pilot.rovereto.inbici.entities.event.dasapere;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.event.ToKnow;
import eu.iescities.pilot.rovereto.inbici.entities.event.edit.EditFieldListAdapter;
import eu.iescities.pilot.rovereto.inbici.entities.event.info.Fragment_EvDetail_Info;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_AddNew_FieldType extends Fragment {

	private Context mContext;

	public static final String ARG_EVENT_ID = "event_id";

	private ExplorerObject mEvent = null;
	private String mEventId;

	private Map<String,List<String>> toKnowMap=null; 


	TextView formLabel;


	EditText newFieldTypeText;



	EditText valueToAdd;
	ImageView iconImage;
	TextView eventFieldLabel;



	private ArrayList<String> value_list = new ArrayList<String>();
	EditFieldListAdapter valueListAdapter;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onCreate");

		this.mContext = this.getActivity();

		if (savedInstanceState == null) {
			Log.d("FRAGMENT LC", "onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> EVENT ID: " + mEventId);
				mEvent = DTHelper.findEventById(mEventId);

				// get event "toknow" custom  data
				toKnowMap = Utils.getCustomToKnowDataFromEvent(mEvent);


			}
		} else {
			Log.d("FRAGMENT LC", "Fragment_EvDetail_Edit_MultiValueField --> onCreate SUBSEQUENT TIME");
		}

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_EvDetail_Edit_MultiValueField --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_addnew_fieldtype, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (mEvent == null) {

			mEvent = DTHelper.findEventById(mEventId);

		}


		iconImage = (ImageView) getActivity().findViewById(R.id.add_icon);
		eventFieldLabel = (TextView) getActivity().findViewById(R.id.event_field_label);


		//set the action bar title
//		getActivity().getActionBar().setTitle(getResources().getString(R.string.addnewinfo));
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.addnewinfo));

		//set event name
		formLabel = (TextView) getActivity().findViewById(R.id.form_label);
		formLabel.setText("Evento: " + mEvent.getTitle());


		eventFieldLabel.setText(getResources().getString(R.string.insert_info));

		newFieldTypeText = (EditText) getActivity().findViewById(R.id.new_field_type_text);
		newFieldTypeText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (newFieldTypeText.getText().length() > 0) {
					eventFieldLabel.setText(getResources().getString(R.string.insert_info) + " su " + newFieldTypeText.getText().toString());
					// formLabel.setText(txtEventField.getText().toString());
				}else if (newFieldTypeText.getText().length() == 0) {
					eventFieldLabel.setText(getResources().getString(R.string.insert_info));
				}



			}
		});





		valueToAdd = (EditText) getActivity().findViewById(R.id.field_values_tv);

		//valueListAdapter = new EditFieldListAdapter(mContext, R.layout.frag_ev_detail_info_edit_fields_list_row, value_list, Utils.EDIT_FIELD_TEXT_TYPE);
		valueListAdapter = new EditFieldListAdapter(mContext, R.layout.frag_ev_detail_info_edit_fields_list_row, value_list,
				Utils.EDIT_FIELD_TEXT_TYPE, newFieldTypeText.getText().toString());


		ListView list = (ListView) getActivity().findViewById(R.id.field_value_list);
		list.setAdapter(valueListAdapter);
		valueListAdapter.notifyDataSetChanged();
		//list.setOnItemClickListener(this);


		Button cancel = (Button) getActivity().findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});

		ImageView addValue = (ImageView) getActivity().findViewById(R.id.add_icon);
		addValue.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String addedValue = valueToAdd.getText().toString();
				if (addedValue != null && addedValue.trim().length() > 0) {
					valueListAdapter.add(addedValue);
					valueListAdapter.notifyDataSetChanged();
					valueToAdd.setText("");
				}
			}
		});

		Button ok = (Button) getActivity().findViewById(R.id.btn_ok);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				List<String> list = new ArrayList<String>();
				if (!valueListAdapter.isEmpty()) {
					for (int i = 0; i < valueListAdapter.getCount(); i++) {
						list.add(valueListAdapter.getItem(i));
					}
				}

//				for (String value : list){
//					Log.d("DASAPERE", "Fragment_EvDetail_AddNew_FieldType --> value: " + value);
//				}
				if (newFieldTypeText.getText().length() == 0){
					missingFieldTypeAlert();
				}else{
					
					String key = (newFieldTypeText.getText().length()!=1) ? 
							newFieldTypeText.getText().toString().substring(0, 1).toUpperCase() + newFieldTypeText.getText().toString().substring(1) : 
								newFieldTypeText.getText().toString().toUpperCase(); 

					if (list.isEmpty()){
						//the "addedbyuser" attribute has no values hence can be deleted
						//alert user first
						missingFieldTypeValuesAlert();
					}else{
						//set the modified fields
						//set edited "toknow" custom field

						toKnowMap.put(key, list);
						Map<String, Object> customData = mEvent.getCustomData();
						customData.put(Constants.CUSTOM_TOKNOW, toKnowMap);
						mEvent.setCustomData(customData);

						// persist the modified field
						new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
						.execute(mEvent);
					}
				}
			}
		});		
	}



	public void missingFieldTypeValuesAlert(){


		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		//set msg to show
		builder.setMessage(	getResources().getString(R.string.missing_fieldtype_values_alert, newFieldTypeText.getText().toString()));
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				dialog.cancel();
			}
		});

		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				getActivity().getSupportFragmentManager().popBackStack();

			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}





	public void missingFieldTypeAlert(){

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		//set msg to show
		builder.setMessage(	getResources().getString(R.string.missing_fieldtype_alert, newFieldTypeText.getText().toString()));
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				dialog.cancel();
			}
		});

		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				getActivity().getSupportFragmentManager().popBackStack();

			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}










	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onStart");

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onResume");

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onPause");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onSaveInstanceState");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onDetach");
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
