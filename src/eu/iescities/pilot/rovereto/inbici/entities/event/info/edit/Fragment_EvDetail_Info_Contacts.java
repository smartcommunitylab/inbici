package eu.iescities.pilot.rovereto.inbici.entities.event.info.edit;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.entities.event.edit.EditFieldListAdapter;
import eu.iescities.pilot.rovereto.inbici.entities.event.edit.EditedFieldListView;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;


public class Fragment_EvDetail_Info_Contacts extends Fragment {

	private Context mContext;


	public static final String ARG_EVENT_ID = "event_id";
	public static final String ARG_EVENT_PHONE = "event_phone";
	public static final String ARG_EVENT_EMAIL = "event_email";
	public static final String ARG_EVENT_WEBSITE = "event_website";


	private eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject mEvent = null;
	private String mEventId;

	TextView formLabel;
	EditText txtPhone;
	EditText txtEmail;
	EditText txtWebsite;
	EditText txtFacebook;
	EditText txtTwitter;


	private ArrayList<String> phone_list = null;
	EditFieldListAdapter phoneListAdapter = null;
	EditedFieldListView phoneList;

	private ArrayList<String> email_list = null;
	EditFieldListAdapter emailListAdapter = null;
	EditedFieldListView emailList;


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onCreate");

		this.mContext = this.getActivity();

		if(savedInstanceState==null)
		{
			Log.d("FRAGMENT LC","onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				Log.i("FRAGMENT LC", "EVENT ID: " + mEventId);

				mEvent = DTHelper.findEventById(mEventId);
				//List<ExplorerObject> eventList = Utils.getFakeExplorerObjects();
				//mEvent = Utils.getFakeLocalExplorerObject(Utils.appEvents,mEventId);
			}

		}
		else
		{
			Log.d("FRAGMENT LC","onCreate SUBSEQUENT TIME");
		}

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onCreateView");

		return inflater.inflate(R.layout.frag_ev_detail_info_edit_contacts, container, false);
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onActivityCreated");

		//getActivity().getActionBar().setTitle(mEvent.getTitle()); 
		//getActivity().getActionBar().setTitle("Modifica Contatti"); 
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.modify) + " " + getResources().getString(R.string.contacts_txt));

//		getActivity().getActionBar().setTitle(
//				getResources().getString(R.string.modify) + " " + getResources().getString(R.string.contacts_txt));

		formLabel = (TextView) getActivity().findViewById(R.id.title_contacts_label);

		txtPhone= (EditText) getActivity().findViewById(R.id.phone_text);
		txtEmail = (EditText) getActivity().findViewById(R.id.email_text);

		txtWebsite = (EditText) getActivity().findViewById(R.id.website_link);
		txtFacebook = (EditText) getActivity().findViewById(R.id.facebook_link);
		txtTwitter = (EditText) getActivity().findViewById(R.id.twitter_link);

		formLabel.setText("Evento: " + mEvent.getTitle());


		//get list of phone numbers
		phone_list =  (ArrayList<String>) mEvent.getPhoneEmailContacts(Utils.PHONE_CONTACT_TYPE);
		//get list view
		phoneList = (EditedFieldListView) getActivity().findViewById(R.id.phone_list);
		//set adapter if list not empty
		if ((phone_list!=null) && (phone_list.size()>0)){
			phoneListAdapter = new EditFieldListAdapter(mContext, R.layout.frag_ev_detail_info_edit_fields_list_row, phone_list, 
					Utils.EDIT_FIELD_PHONE_TYPE);
			phoneList.setAdapter(phoneListAdapter);
			phoneListAdapter.notifyDataSetChanged();
		}


		//handle insertion of a new phone 
		ImageView addPhoneField = (ImageView ) getActivity().findViewById(R.id.add_phone_icon);
		addPhoneField.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String addedPhone = txtPhone.getText().toString();
				if (addedPhone != null && addedPhone.trim().length() > 0 && PhoneNumberUtils.isGlobalPhoneNumber(addedPhone)) {

					if (phoneListAdapter==null){
						phone_list= new ArrayList<String>();
						phone_list.add(addedPhone);
						phoneListAdapter = new EditFieldListAdapter(mContext, R.layout.frag_ev_detail_info_edit_fields_list_row, phone_list, 
								Utils.EDIT_FIELD_PHONE_TYPE);
						phoneList.setAdapter(phoneListAdapter);
					}else
						phoneListAdapter.add(addedPhone);

					phoneListAdapter.notifyDataSetChanged();
					txtPhone.setText("");
				}else if (!PhoneNumberUtils.isGlobalPhoneNumber(addedPhone))
					Toast.makeText(getActivity(), getResources().getString(R.string.invalid_phone), Toast.LENGTH_SHORT).show();
			}
		});


		//get list of emails
		//email_list =  (ArrayList<String>) mEvent.getEmails();
		email_list =  (ArrayList<String>) mEvent.getPhoneEmailContacts(Utils.EMAIL_CONTACT_TYPE);
		
		
		//get list view
		emailList = (EditedFieldListView) getActivity().findViewById(R.id.email_list);
		//set adapter if list not empty
		if ((email_list!=null) && (email_list.size()>0)){
			emailListAdapter = new EditFieldListAdapter(mContext, R.layout.frag_ev_detail_info_edit_fields_list_row, email_list, 
					Utils.EDIT_FIELD_EMAIL_TYPE);
			emailList.setAdapter(emailListAdapter);
			emailListAdapter.notifyDataSetChanged();
		}

		//handle insertion of a new email
		ImageView addEmailField = (ImageView ) getActivity().findViewById(R.id.add_email_icon);
		addEmailField.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String addedEmail = txtEmail.getText().toString();

				if (addedEmail != null && addedEmail.trim().length() > 0 &&  Utils.isValidEmail(addedEmail) ) {

					if (emailListAdapter==null){
						email_list= new ArrayList<String>();
						email_list.add(addedEmail);
						emailListAdapter = new EditFieldListAdapter(mContext, R.layout.frag_ev_detail_info_edit_fields_list_row, email_list, 
								Utils.EDIT_FIELD_EMAIL_TYPE);
						emailList.setAdapter(emailListAdapter);
					}else
						emailListAdapter.add(addedEmail);

					emailListAdapter.notifyDataSetChanged();
					txtEmail.setText("");
				}else if (!Utils.isValidEmail(addedEmail))
					Toast.makeText(getActivity(), getResources().getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
			}
		});

		
		
		
		

		if (mEvent.getWebsiteUrl()!=null)
			txtWebsite.setText(mEvent.getWebsiteUrl());
		else 
			txtWebsite.setText( getResources().getString(R.string.start_url));
		txtWebsite.setSelection(txtWebsite.getText().length());


		if (mEvent.getFacebookUrl()!=null)
			txtFacebook.setText(mEvent.getFacebookUrl());
		else 
			txtFacebook.setText( getResources().getString(R.string.start_url));
		txtFacebook.setSelection(txtFacebook.getText().length());


		if (mEvent.getTwitterUrl()!=null)
			txtTwitter.setText(mEvent.getTwitterUrl());
		else 
			txtTwitter.setText( getResources().getString(R.string.start_url));
		txtTwitter.setSelection(txtTwitter.getText().length());



		Button modifyBtn = (Button) getView().findViewById(R.id.edit_contacts_modify_button);
		modifyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {


				List<String> phoneList = getEditFields(phoneListAdapter);
				List<String> emailList = getEditFields(emailListAdapter);


				if (isValidInput(phoneList, emailList)){

					mEvent.setPhoneEmailContacts(Utils.PHONE_CONTACT_TYPE, phoneList);


										if (mEvent.getContacts().containsKey("telefono")) {
											List<String> telephones = (List<String>) mEvent.getContacts().get("telefono");
											for (String tel : telephones) {
												if (!tel.matches("")) {
													Log.i("CONTACT", "Fragment_evDetail_Info_Contacts --> telefono: " + tel + "!!");
												}
					
											}
										}


					mEvent.setPhoneEmailContacts(Utils.EMAIL_CONTACT_TYPE, emailList);
					


					try {
						new URL(txtWebsite.getText().toString());
						mEvent.setWebsiteUrl(txtWebsite.getText().toString());
					} catch (MalformedURLException e) {
						mEvent.setWebsiteUrl(null);
					}


					try {
						new URL(txtFacebook.getText().toString());
						mEvent.setFacebookUrl(txtFacebook.getText().toString());
					} catch (MalformedURLException e) {
						mEvent.setFacebookUrl(null);
					}


					try {
						new URL(txtTwitter.getText().toString());
						mEvent.setTwitterUrl(txtTwitter.getText().toString());
					} catch (MalformedURLException e) {
						mEvent.setTwitterUrl(null);
					}


					//persist the new contacts
					new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
					.execute(mEvent);
					//Utils.appEvents.set(index2, mEvent);
				}else
					Log.i("CONTACT", "Fragment_evDetail_Info_Contacts --> INVALID INPUT!!");

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



	protected List<String> getEditFields(EditFieldListAdapter fieldListAdapter){
		//get values from edit fields
		if (fieldListAdapter!=null){
			List<String> fieldList = new ArrayList<String>();
			if (!fieldListAdapter.isEmpty()) {
				for (int i = 0; i < fieldListAdapter.getCount(); i++) {
					fieldList.add(fieldListAdapter.getItem(i));
				}
			}
			return fieldList; 		
		}else return null;
		
	}




	private  boolean isValidInput(List<String> phones, List<String> emails){
		boolean validEmail=true;
		boolean validPhone=true;
		boolean validWebUrl=false;
		boolean validFBUrl=false;
		boolean validTwitterUrl=false;
		List<String> invalidPhones = new ArrayList<String>(); 
		List<String> invalidEmails = new ArrayList<String>(); 


		//check the validity of phones
		if (phones!=null){
			for (String phone: phones){
				if (!((phone.matches(""))||(PhoneNumberUtils.isGlobalPhoneNumber(phone)))){
					validPhone=false;
					invalidPhones.add(phone); 
				}
			}
			if (!validPhone){
				String strInvalidPhones = (invalidPhones.size() > 1) ? 
						(String) getResources().getString(R.string.invalid_phones,invalidPhones) : getResources().getString(R.string.invalid_phone);
						Toast.makeText(getActivity(), strInvalidPhones, Toast.LENGTH_SHORT).show();
			}
		}

		//check the validity of emails
		if (emails!=null){
			for (String email: emails){
				if (!((email.matches(""))||(Utils.isValidEmail(email)))){
					validEmail=false;
					invalidEmails.add(email); 
				}
			}
			if (!validEmail){
				String strInvalidEmails = (invalidEmails.size() > 1) ? 
						(String) getResources().getString(R.string.invalid_emails,invalidEmails) : getResources().getString(R.string.invalid_email);
						Toast.makeText(getActivity(), strInvalidEmails, Toast.LENGTH_SHORT).show();
			}
		}


		if ((txtWebsite.getText().toString().matches("")) || (txtWebsite.getText().toString().matches(getResources().getString(R.string.start_url))) || (Utils.isValidUrl(txtWebsite.getText().toString()))){
			validWebUrl=true;	
		}
		else
			Toast.makeText(getActivity(), getResources().getString(R.string.invalid_weburl), Toast.LENGTH_SHORT).show();


		if ((txtFacebook.getText().toString().matches("")) || (txtFacebook.getText().toString().matches(getResources().getString(R.string.start_url))) || (Utils.isValidUrl(txtFacebook.getText().toString()))){
			validFBUrl=true;	
		}
		else
			Toast.makeText(getActivity(), getResources().getString(R.string.invalid_fburl), Toast.LENGTH_SHORT).show();



		if ((txtTwitter.getText().toString().matches("")) || (txtTwitter.getText().toString().matches(getResources().getString(R.string.start_url))) || (Utils.isValidUrl(txtTwitter.getText().toString()))){
			validTwitterUrl=true;	
		}
		else
			Toast.makeText(getActivity(), getResources().getString(R.string.invalid_twitterurl), Toast.LENGTH_SHORT).show();




		if ((validEmail)&&(validPhone)&&(validWebUrl)&&(validFBUrl)&&(validTwitterUrl))
			return true;
		else return false;


	}



	//to be deleted when there will be the call to the server
	public void setNewEventContacts(String eventID, String[] tel, String[] email, String website){

		//		//set the new fields	
		//		Map<String,Object> contacts = new HashMap<String, Object>();
		//		contacts.put("telefono", tel);
		//		contacts.put("email", email);
		//		mEvent.getContacts().clear();
		//		mEvent.setContacts(contacts);
		//		mEvent.setWebsiteUrl(website);

	}




	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onStart");


	}



	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onResume");

	}



	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onPause");

	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onSaveInstanceState");

	}



	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onStop");

	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onDestroyView");

	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onDestroy");

	}



	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onDetach");

	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		Log.i("FRAGMENT LC", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());

		menu.clear();

		//getActivity().getMenuInflater().inflate(R.menu.event_detail_menu, menu);

		/*if (category == null) {
			category = (getArguments() != null) ? getArguments().getString(SearchFragment.ARG_CATEGORY) : null;
		}
		 */
		super.onPrepareOptionsMenu(menu);
	}    


	private class UpdateEventProcessor extends AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {

		public UpdateEventProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public Boolean performAction(ExplorerObject... params) throws SecurityException, Exception {

			return DTHelper.saveEvent(params[0]);

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
