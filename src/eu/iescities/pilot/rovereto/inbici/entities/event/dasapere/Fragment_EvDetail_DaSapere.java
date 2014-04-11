package eu.iescities.pilot.rovereto.inbici.entities.event.dasapere;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.event.ToKnow;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_DaSapere extends ListFragment {

	private static final List<String> CUSTOM_TOKNOW_FIELDS = Arrays.asList(Constants.CUSTOM_TOKNOW_PLACE_TYPE,
			Constants.CUSTOM_TOKNOW_ACCESS, Constants.CUSTOM_TOKNOW_CHANCE, Constants.CUSTOM_TOKNOW_LANGUAGE_MAIN,
			Constants.CUSTOM_TOKNOW_CLOTHING, Constants.CUSTOM_TOKNOW_TO_BRING);

	protected Context mContext;
	protected String mEventId;
	protected ExplorerObject mEvent = null;
	private EventDetailToKnowAdapter adapter;

	public static Fragment_EvDetail_DaSapere newInstance(String event_id) {
		Fragment_EvDetail_DaSapere f = new Fragment_EvDetail_DaSapere();
		Bundle b = new Bundle();
		b.putString(Utils.ARG_EVENT_ID, event_id);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onCreate");

		this.mContext = this.getActivity();

		if (savedInstanceState == null) {
			Log.d("SCROLLTABS", "onCreate FIRST TIME");
			if (getArguments() != null) {
				mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
				mEvent = DTHelper.findEventById(mEventId);
			} else {
				Log.d("SCROLLTABS", "onCreate SUBSEQUENT TIME");
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_dasapere, container, false);
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onActivityCreated");

		mEvent = getEvent();

		//adapter = new EventDetailToKnowAdapter(getActivity(), R.layout.event_toknow_row_item, getTag(), mEventId);
		adapter = new EventDetailToKnowAdapter(getActivity(), R.layout.event_info_child_item, getTag(), mEventId);


		getListView().setDivider(null);
		getListView().setDivider(getResources().getDrawable(R.color.transparent));
		setListAdapter(adapter);

		//List<ToKnow> toKnowList = Utils.toKnowMapToList(getToKnowEventData());

		List<ToKnow> toKnowList = Utils.toKnowMapToList(getToKnowEventData());

		adapter.addAll(toKnowList);
		adapter.notifyDataSetChanged();



		//handle the creation of new type of information by the user
		Button toKnowAddButton = (Button) getActivity().findViewById(R.id.toKnowAddButton);
		toKnowAddButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				Bundle args = new Bundle();
				String frag_description = null;

				Fragment editFragment = new Fragment_EvDetail_AddNew_FieldType();
				Log.i("CONTACTS", "EventDetailInfoAdapter --> event selected ID: " + mEventId + "!!");
				args.putString(Utils.ARG_EVENT_ID, mEventId);
				frag_description = "event_details_custom_addnew_fieldtype";

				editFragment.setArguments(args);
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// fragmentTransaction.detach(this);
				fragmentTransaction.replace(R.id.content_frame, editFragment, frag_description);
				fragmentTransaction.addToBackStack(getTag());
				fragmentTransaction.commit();




				// reset event and event id
				// mEvent = null;
				// mEventId = null;
			}
		});
	}




	private Map<String, List<String>> getToKnowEventData(){


		if (mEvent.getCustomData() == null) {
			mEvent.setCustomData(new HashMap<String, Object>());
		}

		Map<String, List<String>> toKnowMap = Utils.getCustomToKnowDataFromEvent(mEvent);


		if (toKnowMap == null) {


			Map<String, Object> customData = mEvent.getCustomData();


			customData.put(Constants.CUSTOM_TOKNOW, new LinkedHashMap<String, List<String>>());
			mEvent.setCustomData(customData);
			toKnowMap = (Map<String, List<String>>) mEvent.getCustomData().get(Constants.CUSTOM_TOKNOW);

		}

		if (toKnowMap.isEmpty()) {

			Log.i("DASAPERE", "DaSapere--> toKnowMap EMPTY");



			try {

				List<ToKnow> toKnowList = new ArrayList<ToKnow>();
				for (String field : CUSTOM_TOKNOW_FIELDS) {
					toKnowList.add(ToKnow.newCustomDataAttributeField(field, false, 3));
				}

				Map<String, Object> customData = new HashMap<String, Object>();
				toKnowMap = Utils.toKnowListToMap(toKnowList);
				customData.put(Constants.CUSTOM_TOKNOW, toKnowMap);
				mEvent.setCustomData(customData);

				// persistence
				new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
				.execute(mEvent);
			} catch (Exception e) {
				Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : "");
			}
		}



		return toKnowMap;		
	}



	@Override
	public void onStart() {
		super.onStart();
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onStart");
	}

	@Override
	public void onResume() {
		super.onResume();
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onPause");

	}

	@Override
	public void onStop() {
		super.onStop();
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onStop");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onSaveInstanceState");

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onDestroy");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onDetach");
	}

	private ExplorerObject getEvent() {
		if (mEventId == null) {
			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
		}
		mEvent = DTHelper.findEventById(mEventId);
		return mEvent;
	}

	private class UpdateEventProcessor extends AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {

		public UpdateEventProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public Boolean performAction(ExplorerObject... params) throws SecurityException, Exception {
			// to be enabled when the connection with the server is ok
			return DTHelper.saveEvent(params[0]);
		}

		@Override
		public void handleResult(Boolean result) {
			if (getActivity() != null) {
				// getActivity().getSupportFragmentManager().popBackStack();

				// if (result) {
				// Toast.makeText(getActivity(), R.string.event_create_success,
				// Toast.LENGTH_SHORT).show();
				// } else {
				// Toast.makeText(getActivity(), R.string.update_success,
				// Toast.LENGTH_SHORT).show();
				// }
			}
		}
	}

}
