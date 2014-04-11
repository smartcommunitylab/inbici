package eu.iescities.pilot.rovereto.inbici.entities.event.edit;

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
import eu.iescities.pilot.rovereto.inbici.entities.event.info.Fragment_EvDetail_Info;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_Edit_MultiValueField extends Fragment {

	private Context mContext;

	public static final String ARG_EVENT_ID = "event_id";

	private ExplorerObject mEvent = null;
	private String mEventId;

	private String mEventFieldType;
	private Boolean mEventFieldTypeMandatory;
	private Map<String,List<String>> toKnowMap=null; 


	TextView formLabel;
	EditText valueToAdd;
	ImageView iconImage;
	TextView eventFieldLabel;



	private ArrayList<String> value_list = null;
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

				//handle field type mandatoriness
				mEventFieldTypeMandatory = getArguments().getBoolean(Utils.ARG_EVENT_FIELD_TYPE_IS_MANDATORY);

				//handle field type
				mEventFieldType = getArguments().getString(Utils.ARG_EVENT_FIELD_TYPE);
				if (mEventFieldType.equals("Tags")) {
					// get event tags data
					if (mEvent.getCommunityData().getTags()!=null)
						value_list = new ArrayList<String>(mEvent.getCommunityData().getTags());
				}
				else 
				{
					//edit a custom field
					// get event "toknow" custom  data
					toKnowMap = Utils.getCustomToKnowDataFromEvent(mEvent);
					//List<String> values = (List<String>) toKnowMap.get(mEventFieldType);
					value_list = new ArrayList<String>();
					for (String value : (List<String>) toKnowMap.get(mEventFieldType)){
						if (!value.matches("")){
							value_list.add(value);
						}
					}
				}


				for (String value : value_list ){
					Log.i("DASAPERE", "Fragment_EvDetail_Edit_MultiValueField --> FIRST ORIGINAL LIST value:" + value + "!");
				}





			}
		} else {
			Log.d("FRAGMENT LC", "Fragment_EvDetail_Edit_MultiValueField --> onCreate SUBSEQUENT TIME");
		}

	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_EvDetail_Edit_MultiValueField --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_edit_multivalue_field, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_EvDetail_Edit_MultiValueField --> onActivityCreated");
		Log.i("FRAGMENT LC", "Fragment_EvDetail_Edit_MultiValueField --> EVENT ID activity created: " + mEventId);

		if (mEvent == null) {
			Log.i("FRAGMENT LC", "Fragment_EvDetail_Edit_MultiValueField --> MY EVENT null");

			mEvent = DTHelper.findEventById(mEventId);

		}


		iconImage = (ImageView) getActivity().findViewById(R.id.add_icon);
		eventFieldLabel = (TextView) getActivity().findViewById(R.id.event_field_label);


		if (mEventFieldType.equals("Tags")) {
			//set the action bar title
//			GETACTIVITY().GETACTIONBAR().SETTITLE(
//					getResources().getString(R.string.modify) + " " + getResources().getString(R.string.create_tags));
			((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.modify) + " " + getResources().getString(R.string.create_tags));

			iconImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_new_label));
			eventFieldLabel.setText(mEventFieldType);

		}else{
			//set the action bar title
//			getActivity().getActionBar().setTitle(
//					getResources().getString(R.string.modify) + " " + getResources().getString(R.string.info_txt));
			((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.modify) + " " + getResources().getString(R.string.info_txt));

			if (mEventFieldType.startsWith("_toknow_")) {
				//edit a custom "toknow" field
				Integer resId = getResources().getIdentifier(mEventFieldType, "string",
						"eu.iescities.pilot.rovereto.inbici");
				if (resId != null && resId != 0) {
					String mandatoryTitle =getResources().getString(resId);
					eventFieldLabel.setText(mandatoryTitle);
				}
			}else
				eventFieldLabel.setText(mEventFieldType);
		}


		Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> EVENT title  activity created: " + mEvent.getTitle());
		Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> TAG LIST: " + value_list);

		formLabel = (TextView) getActivity().findViewById(R.id.form_label);
		formLabel.setText("Evento: " + mEvent.getTitle());


		valueToAdd = (EditText) getActivity().findViewById(R.id.field_values_tv);

		//valueListAdapter = new EditFieldListAdapter(mContext, R.layout.frag_ev_detail_info_edit_fields_list_row, value_list, Utils.EDIT_FIELD_TEXT_TYPE);
		valueListAdapter = new EditFieldListAdapter(mContext, R.layout.frag_ev_detail_info_edit_fields_list_row, value_list, Utils.EDIT_FIELD_TEXT_TYPE, mEventFieldType);


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

				//set the modified fields
				if (mEventFieldType.equals("Tags")) {
					mEvent.getCommunityData().setTags(list);
					// persist the modified field
					new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
					.execute(mEvent);
				}else{
					
					Map<String, Object> customData = mEvent.getCustomData();
					
//					Log.i("DASAPERE", "MultivALues--> FILED TYPE : " + mEventFieldType);
//					Log.i("DASAPERE", "MultivALues--> LIST EMPTY : " + list.isEmpty());
//					Log.i("DASAPERE", "MultivALues--> MANDATORY FIELd : " + mEventFieldTypeMandatory);

					
					if (list.isEmpty() && !mEventFieldTypeMandatory ){
						//the "addedbyuser" attribute has no values hence can be deleted
						//alert user first
						//Log.i("DASAPERE", "MultivALues--> MANDATORY FIELd : " + mEventFieldTypeMandatory);
						askUserForFieldRemotion();
					}else{
						//set edited "toknow" custom field
						toKnowMap.put(mEventFieldType, list);
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
	
	
	public void askUserForFieldRemotion(){
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		//set msg to show
		builder.setMessage(	getResources().getString(R.string.fieldtype_remove_alert, mEventFieldType));
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
				//remove the field added by the user
				toKnowMap.remove(mEventFieldType);

				//update the event 
				Map<String, Object> customData = mEvent.getCustomData();
				customData.put(Constants.CUSTOM_TOKNOW, toKnowMap);
				mEvent.setCustomData(customData);

				// persist the modified field
				new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
				.execute(mEvent);

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
