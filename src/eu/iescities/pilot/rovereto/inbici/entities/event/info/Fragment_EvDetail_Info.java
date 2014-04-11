package eu.iescities.pilot.rovereto.inbici.entities.event.info;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.inbici.InBiciApplication;
import eu.iescities.pilot.rovereto.inbici.custom.data.Address;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
 

public class Fragment_EvDetail_Info extends Fragment {

	protected Context context;

	// For the expandable list view
	List<String> attributeGroupList;
	// private List<LocalExplorerObject> listEvents = new
	// ArrayList<LocalExplorerObject>();
	Map<String, List<String>> eventAttributeCollection;
	ExpandableListView expListView;
	protected ExplorerObject mEvent = null;
	private EventDetailInfoAdapter eventDetailInfoAdapter;
	View header;

	public static final String ARG_INDEX = "index_adapter";

	private Integer indexAdapter;
	protected String mEventId;
	private String mEventImageUrl;


	// Initialize variables
	protected int ParentClickStatus = -1;
	protected int childClickStatus = -1;
	protected ArrayList<EventInfoParent> parents;
	protected List<Integer> groupImages;
	// private HashMap<Integer, List<Integer>> childImages;
	protected HashMap<Integer, List<Integer>> childType2Images;
	protected HashMap<Integer, Integer> childType1Images;


	Activity infoActivity = null;

	public static Fragment_EvDetail_Info newInstance(String event_id) {
		Fragment_EvDetail_Info f = new Fragment_EvDetail_Info();
		Bundle b = new Bundle();
		b.putString(Utils.ARG_EVENT_ID, event_id);
		f.setArguments(b);
		return f;
	}

	public static Fragment_EvDetail_Info newInstance(String event_id, String event_img_url) {
		Fragment_EvDetail_Info f = new Fragment_EvDetail_Info();
		Bundle b = new Bundle();
		b.putString(Utils.ARG_EVENT_ID, event_id);
		b.putString(Utils.ARG_EVENT_IMAGE_URL, event_img_url);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onAttach: " + activity.toString());
		infoActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onCreate");

		this.context = this.getActivity();

		if (savedInstanceState == null) {
			Log.d("SCROLLTABS", "onCreate FIRST TIME");
			if (getArguments() != null) {
				mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
			}
		} else {
			Log.d("SCROLLTABS", "onCreate SUBSEQUENT TIME");
			mEventId = savedInstanceState.getString(Utils.ARG_EVENT_ID);
		}

		Log.d("IMAGES", "Fragment_evDetail_Info --> image url:" + mEventImageUrl + "!");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_info_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onActivityCreated");

	}

//	private void editField(String field_type) {
//
//		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> INFO ACTIVITY: " + infoActivity.toString());
//		Log.i("CONTACTS", "Fragment_EvDetail_Info --> event selected ID: " + mEventId + "!!");
//
//		String frag_description = "event_details_info_edit_" + field_type;
//		Bundle args = new Bundle();
//		args.putString(Utils.ARG_EVENT_ID, mEventId);
//		args.putString(Utils.ARG_EVENT_FIELD_TYPE, field_type);
//
//		Fragment edit_fragment = new Fragment_EvDetail_Info_What();
//
//		// FragmentTransaction transaction =
//		// getChildFragmentManager().beginTransaction();
//		// if (edit_fragment != null) {
//		// edit_fragment.setArguments(args);
//		// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//		// // fragmentTransaction.detach(this);
//		// //transaction.replace(R.id.content_frame, edit_fragment,
//		// frag_description);
//		// transaction.add(edit_fragment, frag_description);
//		// //transaction.addToBackStack(edit_fragment.getTag());
//		// transaction.commit();
//		// // reset event and event id
//		// mEvent = null;
//		// mEventId = null;
//		// }
//
//		FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//		if (edit_fragment != null) {
//			// reset event and event id
//			mEvent = null;
//			mEventId = null;
//			edit_fragment.setArguments(args);
//			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//			// fragmentTransaction.detach(this);
//			fragmentTransaction.replace(R.id.content_frame, edit_fragment, frag_description);
//			fragmentTransaction.addToBackStack(getTag());
//			fragmentTransaction.commit();
//
//		}
//
//	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onStart");
		mEvent = getEvent();
		if (mEvent != null)
			mEventImageUrl = mEvent.getImage();

		// set expandable list view
		setExpandableListView();

		// display the event title
		TextView titleTextView = (TextView) getActivity().findViewById(R.id.event_info_placeholder_title);
		titleTextView.setText(mEvent.getTitle());

		//add the edit icon besides the text
		//		String text = mEvent.getTitle() + " ";
		//		SpannableString ss = new SpannableString(text);
		//		Drawable d = getResources().getDrawable(R.drawable.ic_action_edit);
		//		d.setBounds(0, 0, 35, 35);
		//		ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
		//		int start = text.length() - 1;
		//		ss.setSpan(span, start, start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		//		ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, start + 1,
		//				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//		ss.setSpan(new ClickableSpan() {
		//			@Override
		//			public void onClick(View v) {
		//				Log.d("main", "link clicked");
		//				// Toast.makeText(context, "modify event title",
		//				// Toast.LENGTH_SHORT).show();
		//				editField("title");
		//			}
		//		}, start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//		titleTextView.setText(ss);
		//		titleTextView.setMovementMethod(LinkMovementMethod.getInstance());


		// display the event image
		ImageView imgView = (ImageView) getActivity().findViewById(R.id.event_placeholder_photo);

		if ((mEventImageUrl != null) && (!mEventImageUrl.matches(""))) {
			InBiciApplication.imageLoader.displayImage(mEventImageUrl, imgView);
		}

		// display the event category plus the "promoted by" attribute
		TextView categoryTextView = (TextView) getActivity().findViewById(R.id.event_placeholder_category);
		String category = mEvent.categoryString(getActivity());
		
		
		if (mEvent.getOrigin() != null && !mEvent.getOrigin().matches("")) {
			String origin = mEvent.getOrigin().substring(0, 1).toLowerCase() + mEvent.getOrigin().substring(1);
			String text = getResources().getString(R.string.event_category, category, 
					Utils.removeWords(Arrays.asList(Utils.stopWordsForOrigin), origin));
			categoryTextView.setText(text);
			//add the edit icon besides the text
			//			text += " ";
			//			ss = new SpannableString(text);
			//			start = text.length() - 1;
			//			ss.setSpan(span, start, start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			//			ss.setSpan(new ClickableSpan() {
			//				@Override
			//				public void onClick(View v) {
			//					Log.d("main", "link clicked");
			//					// Toast.makeText(context, "modify promoted by",
			//					// Toast.LENGTH_SHORT).show();
			//					editField("origin");
			//				}
			//			}, start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			//			categoryTextView.setText(ss);
			//			categoryTextView.setMovementMethod(LinkMovementMethod.getInstance());
		} else
			categoryTextView.setText("Evento " + category + ".");

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onPause");

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onSaveInstanceState");
		outState.putString(Utils.ARG_EVENT_ID, mEventId);
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onDestroy");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onDetach");
	}

	private void setExpandableListView() {

		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> setExpandableListView");

		// Creating static data in arraylist
		ArrayList<EventInfoParent> eventInfoList = getEventDetailData(mEvent);
		setGroupImages();

		// attributeGroupList = createAttributeGroupList();
		// //eventAttributeCollection =
		// createFakeEventDetailCollection(attributeGroupList);
		// eventAttributeCollection =
		// getEventDetailCollection(attributeGroupList, mEvent);

		expListView = (ExpandableListView) getActivity().findViewById(R.id.event_details_info);

		expListView.setChildDivider(null);
		expListView.setChildDivider(getResources().getDrawable(R.color.transparent));

		// header part
		header = getActivity().getLayoutInflater().inflate(R.layout.frag_ev_detail_info_header, null);

		// adding header to the list
		if (expListView.getHeaderViewsCount() == 0) {
			expListView.addHeaderView(header);
			expListView.setHeaderDividersEnabled(true);
		}

		if (getArguments() != null) {
			// Restore last state for checked position.
			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
			indexAdapter = getArguments().getInt(ARG_INDEX);
		}

		/* create the adapter is it is the first time you load */
		// Adding ArrayList data to ExpandableListView values
		parents = eventInfoList;

		if (eventDetailInfoAdapter == null) {
			eventDetailInfoAdapter = new EventDetailInfoAdapter(Fragment_EvDetail_Info.this);

		}
		expListView.invalidateViews();
		expListView.setAdapter(eventDetailInfoAdapter);

		eventDetailInfoAdapter.notifyDataSetChanged();

		expListView.expandGroup(0);
		expListView.expandGroup(1);

		
		
		//this is not useful now
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				// if(_lastColored != null)
				// {
				// _lastColored.setBackgroundColor(Color.WHITE);
				// _lastColored.invalidate();
				// }
				// _lastColored = v;
				//
				// v.setBackgroundColor(Color.BLUE);

				Log.i("GROUPVIEW", "parent == " + groupPosition + "=  child : ==" + childPosition);

				if (childClickStatus != childPosition) {
					childClickStatus = childPosition;

					// Toast.makeText(context, "Parent :" + groupPosition +
					// " Child :" + childPosition, Toast.LENGTH_LONG).show();
				}

				int iCount;
				int iIdx;
				EventInfoChild childItem;

				EventDetailInfoAdapter adapter = (EventDetailInfoAdapter) parent.getExpandableListAdapter();

				iCount = adapter.getChildrenCount(groupPosition);
				for (iIdx = 0; iIdx < iCount; ++iIdx) {
					childItem = (EventInfoChild) adapter.getChild(groupPosition, iIdx);
					if (childItem != null) {
						Log.i("GROUPVIEW", "child item not null");

						if (iIdx == childPosition) {
							// Here you would toggle checked state in the data
							// for this item
						} else {
							// Here you would clear checked state in the data
							// for this item
						}
					}
				}

				parent.invalidateViews(); // Update the list view

				return false;

			}
		});

	}

	private void setGroupImages() {

		groupImages = new ArrayList<Integer>();
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);

	}


	protected void resetEvent() {
		mEvent = null;
		mEventId = null;
	}

	/**
	 * here should come your data service implementation
	 * 
	 * @return
	 */
	private ArrayList<EventInfoParent> getEventDetailData(ExplorerObject event) {

		// Creating ArrayList of type parent class to store parent class objects
		ArrayList<EventInfoParent> list = new ArrayList<EventInfoParent>();

		for (int i = 1; i < 5; i++) {
			// Create parent class object
			EventInfoParent parent = new EventInfoParent();

			 if (event.getImage() != null) {
			 Log.i("EVENT", "Fragment_EvDetail_Info --> image: " +
			 event.getImage() + "!!");
			 }
			//
			//
			// if (event.getCategory() != null) {
			// Log.i("EVENT", "Fragment_EvDetail_Info --> Category: " +
			// event.getCategory() + "!!");
			// }
			//
			// if (event.getOrigin() != null) {
			// Log.i("EVENT", "Fragment_EvDetail_Info --> Origin: " +
			// event.getOrigin() + "!!");
			// }
			//
			// if (event.getLocation() != null) {
			// Log.i("EVENT", "Fragment_EvDetail_Info --> Location: " +
			// event.getLocation() + "!!");
			// }


			if (event.getCommunityData().getTags() != null) {
				Log.i("EVENT", "Fragment_EvDetail_Info --> TAGS: " + event.getCommunityData().getTags() + "!!");
			} else
				Log.i("EVENT", "Fragment_EvDetail_Info --> TAGS NULL!!");

			// Set values in parent class object
			if (i == 1) { // field DOVE e QUANDO
				parent.setName("" + i);
				parent.setText1("Dove e quando");
				parent.setChildren(new ArrayList<EventInfoChild>());

				if (event.getWhenWhere() != null) {
					Log.i("EVENT", "Fragment_EvDetail_Info --> WhenWhere: " + event.getWhenWhere() + "!!");
					EventInfoChild child = new EventInfoChild();
					child.setName("whenwhere");
					child.setText(event.getWhenWhere());
					child.setType(0);
					parent.getChildren().add(child);
				}

				Address address = event.getAddress();
				if (address != null) {

					String place = (address.getLuogo() != null) ? (String) address.getLuogo() : null;

					String street = (address.getVia() != null) ? (String) address.getVia() : null;

					String city = (address.getCitta() != null) ? (String) address.getCitta() : null;

					String addressStr = "";

					if ((place != null) && (!place.matches("")))
						addressStr = addressStr + place;

					if ((street != null) && (!street.matches("")))
						addressStr = addressStr + ", " +  street;

					if ((city != null) && (!city.matches("")))
						addressStr = addressStr + ", "  + city;
					//to be enabled again when on the server side there will be distinction between street and city
//					else
//						addressStr = addressStr + context.getString(R.string.city_hint);

					
					if (addressStr.startsWith(","))
						addressStr = addressStr.substring(1);
					
					if (addressStr.length()==0)
						addressStr = context.getString(R.string.city_hint);
	 				
					
					
					
					// Create Child class object
					EventInfoChild child = new EventInfoChild();
					child.setName("address");
					//child.setText(getResources().getString(R.string.address) + ": " + addressStr);
					child.setText(addressStr);

					child.setType(0);
					child.setLeftIconId(R.drawable.ic_action_place);
					parent.getChildren().add(child);
				}

				if ((event.getFromTime() != null) && (event.getFromTime() != 0)) {
					String[] fromDateTime = Utils.getDateTimeString(this.context, event.getFromTime(),
							Utils.DATETIME_FORMAT, false, true);
					EventInfoChild child = new EventInfoChild();
					child.setName(getResources().getString(R.string.start_date));
					if (!fromDateTime[1].matches("")) {
						child.setText(getResources().getString(R.string.date_with_time,fromDateTime[0],fromDateTime[1]));
					} else
						child.setText(fromDateTime[0]);

					child.setType(0);
					child.setLeftIconId(R.drawable.ic_start);
					parent.getChildren().add(child);
				}

				if ((event.getToTime() != null) && (event.getToTime() != 0)) {
					String[] toDateTime = Utils.getDateTimeString(this.context, event.getToTime(),
							Utils.DATETIME_FORMAT, false, true);
					EventInfoChild child = new EventInfoChild();
					child.setName(getResources().getString(R.string.end_date));

					if (!toDateTime[1].matches("")) {
						child.setText(getResources().getString(R.string.date_with_time,toDateTime[0],toDateTime[1]));

					} else
						child.setText(toDateTime[0]);

					Log.i("EVENT", "Fragment_EvDetail_Info --> toTime: " + child.getText() + "!!");
					child.setType(0);
					child.setLeftIconId(R.drawable.ic_end);

					parent.getChildren().add(child);

					// compute duration or get in from the Explorer Object when
					// there will be such info!!!
//					String duration = null;
//					if (duration != null) {
//						child = new EventInfoChild();
//						child.setName(getResources().getString(R.string.duration));
//						child.setText("Durata: " + duration);
//						child.setType(0);
//						parent.getChildren().add(child);
//					}
				}
			} else if (i == 2) { // field COSA
				parent.setName("" + i);
				parent.setText1("Cosa");
				parent.setChildren(new ArrayList<EventInfoChild>());
				if (event.getDescription() != null) {
					Log.i("EVENT", "Fragment_EvDetail_Info --> description: " + event.getDescription() + "!!");
					String desc = event.getDescription();
					EventInfoChild child = new EventInfoChild();
					child.setName("Description");
					child.setText(desc);
					child.setType(0);
					
					parent.getChildren().add(child);
				}
			} else if (i == 3) { // field CONTATTI
				parent.setName("" + i);
				parent.setText1("Contatti");
				parent.setChildren(new ArrayList<EventInfoChild>());
				// String[] telList = null;

				// set the Phone item of type 1
				EventInfoChild telChildLabel = new EventInfoChild();
				telChildLabel.setName("Phones");
				telChildLabel.setText("Telefono");
				telChildLabel.setType(1);
				telChildLabel.setTextInBold(true);
				telChildLabel.setLeftIconId(R.drawable.ic_action_phone);

				// to be added again when it will be possible to add more
				// numbers
				// int[] rightIconIds = new int[]{R.drawable.ic_action_new};
				// telChildLabel.setRightIconIds(rightIconIds);

				parent.getChildren().add(telChildLabel);


				// set the list of phone numbers
				List<String> telephones = event.getPhoneEmailContacts(Utils.PHONE_CONTACT_TYPE);
				if (telephones!=null){
					telChildLabel.setDividerHeight(0);
					for (String tel : telephones) {
						if (!tel.matches("")) {
							EventInfoChild child1 = new EventInfoChild();
							child1.setName("tel");
							child1.setText(tel);
							child1.setType(0);
							// to be added when it will be possible to
							// cancel/edit the single item
							// int[] rightIconIds1 = new
							// int[]{R.drawable.ic_action_edit,
							// R.drawable.ic_action_cancel,
							// R.drawable.ic_action_call};
							int[] rightIconIds1 = new int[] { R.drawable.ic_action_call };

							child1.setRightIconIds(rightIconIds1);
							child1.setDividerHeight(0);

							if (tel==telephones.get(telephones.size()-1))
								child1.setDividerHeight(1);

							parent.getChildren().add(child1);


						}
					}
				}

				// set the Email item of type 1
				EventInfoChild emailChildLabel = new EventInfoChild();
				emailChildLabel.setName("Emails");
				emailChildLabel.setText("Email");
				emailChildLabel.setType(1);
				emailChildLabel.setTextInBold(true);
				emailChildLabel.setLeftIconId(R.drawable.ic_action_email);

				// to be added again when it will be possible to add more emails
				// int[] rightIconIdsEmail = new
				// int[]{R.drawable.ic_action_new_email};
				// emailChildLabel.setRightIconIds(rightIconIdsEmail);

				parent.getChildren().add(emailChildLabel);

				List<String> emails = event.getPhoneEmailContacts(Utils.EMAIL_CONTACT_TYPE);

				if (emails != null) {
					emailChildLabel.setDividerHeight(0);
					for (String email : emails) {
						if (!email.matches("")) {
							EventInfoChild child = new EventInfoChild();
							child.setName("email");
							child.setText(email);
							child.setType(0);
							// to be added when it will be possible to
							// cancel/edit the single item
							// int[] rightIconIds2 = new
							// int[]{R.drawable.ic_action_edit,
							// R.drawable.ic_action_cancel,
							// R.drawable.ic_action_email};
							int[] rightIconIds2 = new int[] { R.drawable.ic_compose_email };
							child.setRightIconIds(rightIconIds2);

							child.setDividerHeight(0);

							if (email==emails.get(emails.size()-1))
								child.setDividerHeight(1);



							parent.getChildren().add(child);
						}

					}
				}

				// set the Web Site item of type 0
				EventInfoChild siteChildLabel = new EventInfoChild();
				siteChildLabel.setName("Website ");
				if (event.getWebsiteUrl() != null) {
					siteChildLabel.setText(event.getWebsiteUrl());
				} else
					siteChildLabel.setText("Web Site");
				siteChildLabel.setType(0);
				siteChildLabel.setTextInBold(true);
				siteChildLabel.setLeftIconId(R.drawable.ic_action_web_site);
				Log.i("EVENT", "Fragment_EvDetail_Info --> website: " + siteChildLabel.getText() + "!!");

				parent.getChildren().add(siteChildLabel);

				// set Facebook item of type 0
				EventInfoChild fbChildLabel = new EventInfoChild();
				fbChildLabel.setName("Facebook ");
				if (event.getFacebookUrl() != null) {
					// fbChildLabel.setText("<a href=\"" +
					// event.getFacebookUrl() + "\">Facebook</a>");
					fbChildLabel.setText(event.getFacebookUrl());
				} else
					fbChildLabel.setText("Facebook");
				fbChildLabel.setType(0);
				fbChildLabel.setTextInBold(true);
				fbChildLabel.setLeftIconId(R.drawable.ic_facebook);
				Log.i("EVENT", "Fragment_EvDetail_Info --> facebook: " + fbChildLabel.getText() + "!!");
				parent.getChildren().add(fbChildLabel);

				// set Twitter item of type 0
				EventInfoChild twitterChildLabel = new EventInfoChild();
				twitterChildLabel.setName("Twitter ");
				if (event.getTwitterUrl() != null) {
					// twitterChildLabel.setText("<a href=\"" +
					// event.getTwitterUrl() + "\">Twitter</a>");
					twitterChildLabel.setText(event.getTwitterUrl());

				} else
					twitterChildLabel.setText("Twitter ");
				twitterChildLabel.setType(0);
				twitterChildLabel.setTextInBold(true);
				twitterChildLabel.setLeftIconId(R.drawable.ic_twitter);
				Log.i("EVENT", "Fragment_EvDetail_Info --> twitter: " + twitterChildLabel.getText() + "!!");
				parent.getChildren().add(twitterChildLabel);
			} else if (i == 4) { // field TAGS
				parent.setName("" + i);
				parent.setText1("Tags");
				parent.setChildren(new ArrayList<EventInfoChild>());
				List<String> tags = null;
				if (event.getCommunityData().getTags() != null) {
					tags = event.getCommunityData().getTags();
					for (String tag : tags) {
						EventInfoChild child = new EventInfoChild();
						child.setName("tag");
						child.setText(tag);
						child.setType(0);
						child.setLeftIconId(R.drawable.ic_action_labels_dark);
						parent.getChildren().add(child);
					}
				}
			}


			//delete the divider line for the last item of a group

			if (parent.getChildren().size()!=0){
				EventInfoChild lastChild = parent.getChildren().get(parent.getChildren().size()-1);
				lastChild.setDividerHeight(0);
				parent.getChildren().remove(parent.getChildren().size()-1);
				parent.getChildren().add(lastChild);
			}


			// Adding Parent class object to ArrayList
			list.add(parent);
		}
		return list;
	}

	

	/**
	 * Get the ExpandableListAdapter associated with this activity's
	 * ExpandableListView.
	 */
	public EventDetailInfoAdapter getExpandableListAdapter() {
		return eventDetailInfoAdapter;
	}

	public void setListAdapter(EventDetailInfoAdapter adapter) {
		eventDetailInfoAdapter = adapter;
	}

	private ExplorerObject getEvent() {
		if (mEventId == null) {
			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
		}

		// if (mEvent == null) {
		mEvent = DTHelper.findEventById(mEventId);
		// }

		return mEvent;
	}

}
