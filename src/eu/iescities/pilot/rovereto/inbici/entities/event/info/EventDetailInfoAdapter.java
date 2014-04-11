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
package eu.iescities.pilot.rovereto.inbici.entities.event.info;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.entities.event.edit.Fragment_EvDetail_Edit_MultiValueField;
import eu.iescities.pilot.rovereto.inbici.entities.event.edit.Fragment_EvDetail_Edit_SingleValueField;
import eu.iescities.pilot.rovereto.inbici.entities.event.info.edit.Fragment_EvDetail_Info_Contacts;
import eu.iescities.pilot.rovereto.inbici.entities.event.info.edit.Fragment_EvDetail_Info_WhenWhere;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.tagging.TaggingDialog;

//in Fragment_EvDetail_Info
public class EventDetailInfoAdapter extends BaseExpandableListAdapter {

	private static final DateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");
	private static final DateFormat extDateFormat = new SimpleDateFormat(
			"EEEEEE dd/MM/yyyy");
	private int elementSelected = -1;
	private boolean postProcAndHeader = true;

	// for expandable list
	private Map<String, List<String>> attrValuesCollections;
	private List<String> attrNameGroupList;
	private int layoutResourceId;

	private ExplorerObject event = null;
	// private EventPlaceholder eventPlaceHolder = null;
	private TextView txtView = null;

	private View row = null;

	String attrName = null;
	Long groupPos;
	String dateLabel = null;

	// Initialize variables
	private boolean tagGroupLabelSet = false;
	private boolean tagChildLabelSet = false;
	private EventInfoChildViewHolder eventChildViewHolder = null;
	private int countGroupViewCall = 1;
	private int countChildViewCall = 1;

	Fragment_EvDetail_Info fragment;

	// public EventDetailInfoAdapter(Context context, int layoutResourceId,
	// ArrayList<EventInfoParent> parents) {
	// this.context = context;
	// this.layoutResourceId = layoutResourceId;
	// this.parents = parents;
	//
	// }

	// public EventDetailInfoAdapter(Context context, int layoutResourceId,
	// ArrayList<EventInfoParent> parents, Fragment_EvDetail_Info fragment) {
	// this.context = context;
	// this.layoutResourceId = layoutResourceId;
	// this.parents = parents;
	// this.fragment = fragment;
	//
	// }

	// public EventDetailInfoAdapter(Context context, ArrayList<EventInfoParent>
	// parents, Fragment_EvDetail_Info fragment) {
	// this.context = context;
	// this.parents = parents;
	// this.fragment = fragment;
	//
	// }


	public EventDetailInfoAdapter(Fragment_EvDetail_Info fragment) {
		this.fragment = fragment;

	}


	// public EventDetailInfoAdapter(Context context, int layoutResourceId,
	// List<String> events_attr_names,
	// Map<String, List<String>> eventAttrValuesCollections) {
	// this.context = context;
	// this.attrValuesCollections = eventAttrValuesCollections;
	// this.attrNameGroupList = events_attr_names;
	// this.layoutResourceId = layoutResourceId;
	// }

	// This Function used to inflate child rows view

	@SuppressWarnings("deprecation")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parentView) {
		final EventInfoParent parent = this.fragment.parents.get(groupPosition);
		final EventInfoChild child = parent.getChildren().get(childPosition);
		int itemType = getChildType(groupPosition, childPosition);

		Log.i("GROUPVIEW", "************ init child view!! ************ ");
		Log.i("GROUPVIEW", "COUNT: " + countChildViewCall);
		Log.i("GROUPVIEW", "CHILD TEXT: " + child.getText());
		Log.i("GROUPVIEW", "CHILD TYPE: " + child.getType());

		row = convertView;
		if (row == null) {
			// Inflate event_info_child_item.xml file for child rows
			LayoutInflater inflater = (LayoutInflater) this.fragment.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 		row = inflater.inflate(R.layout.event_info_child_item, parentView,
					false);
			eventChildViewHolder = new EventInfoChildViewHolder();
			eventChildViewHolder.text = (TextView) row
					.findViewById(R.id.event_info_attribute_values);
//			eventChildViewHolder.imgSx = (ImageView) row
//					.findViewById(R.id.event_info_attribute_value_icon);
			eventChildViewHolder.imgsDx1 = (ImageView) row
					.findViewById(R.id.event_info_action1);

			eventChildViewHolder.divider = (View) row
					.findViewById(R.id.event_info_item_divider);


			//this will be added again when it will be possible to cancel/edit the single items
			//			eventChildViewHolder.imgsDx2 = (ImageView) row
			//					.findViewById(R.id.event_info_action2);
			//			eventChildViewHolder.imgsDx3 = (ImageView) row
			//					.findViewById(R.id.event_info_action3);

			row.setTag(eventChildViewHolder);
		} else {
			eventChildViewHolder = (EventInfoChildViewHolder) row.getTag();
		}

		// Get event_info_child_item.xml file elements and set values


		if (child.getTextInBold())
			eventChildViewHolder.text.setTypeface(null, Typeface.BOLD);
		else
			eventChildViewHolder.text.setTypeface(null, Typeface.NORMAL);


		if (!child.getText().contains("http")){
			eventChildViewHolder.text.setText(child.getText());
		}
		else{

			if(!child.getText().matches(fragment.getString(R.string.start_url))){

				Log.i("GROUPVIEW", "make the text part clickable!!!");


				//make the text part clickable
				int i1 = 0;
				int i2 = child.getName().length()-1;
				eventChildViewHolder.text.setMovementMethod(LinkMovementMethod.getInstance());
				eventChildViewHolder.text.setText(child.getName(), BufferType.SPANNABLE);
				//eventChildViewHolder.text.setAutoLinkMask(Linkify.WEB_URLS);
				//Linkify.addLinks(eventChildViewHolder.text, Linkify.WEB_URLS);
				//String s = "<a href=\" + child.getText() + \">Website</a>";
				//eventChildViewHolder.text.setText(Html.fromHtml(s));

				Spannable mySpannable = (Spannable)eventChildViewHolder.text.getText();
				ClickableSpan myClickableSpan = new ClickableSpan()
				{
					@Override
					public void onClick(View widget) { 
						//					Toast.makeText(fragment.context,
						//							"Open browser ofr url: " + child.getText(),
						//							Toast.LENGTH_LONG).show(); 
						String url = child.getText();
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(url)); 
						fragment.context.startActivity(i); 

					}
				};

				//			row.setFocusable(true);
				//			row.setFocusableInTouchMode(true);

				mySpannable.setSpan(myClickableSpan, i1, i2 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


				//			eventChildViewHolder.text.setText(Html.fromHtml(child.getText()));
				//			eventChildViewHolder.text.setMovementMethod(LinkMovementMethod.getInstance());
			}
			else
				eventChildViewHolder.text.setText(child.getName());
		}

		
		if (child.getName().equals("Description")){
			//Log.i("EVENT", "EventDetailInfoAdapter --> set description to html!!");
			eventChildViewHolder.text.setText(Html.fromHtml(child.getText()));
			eventChildViewHolder.text.setPadding(10,10,0,0);
		}


		// set icon on the left side
//		if (child.getLeftIconId() != -1) {
//			Log.i("GROUPVIEW", "CHILD SX ICON ID: " + child.getLeftIconId());
//			eventChildViewHolder.imgSx.setVisibility(View.VISIBLE);
//			eventChildViewHolder.imgSx.setImageResource(child.getLeftIconId());
//
//			Log.i("IMAGE", "IMG CHILD NAME: "
//					+ child.getText());
//
//			Log.i("IMAGE", "IMG ICON WIDTH: "
//					+ eventChildViewHolder.imgSx.getWidth());
//			
//		} else {
//			Log.i("GROUPVIEW", "CHILD SX ICON -1");
//			if ( (child.getName().equals("email")) || (child.getName().equals("tel")) ){
//				Log.i("GROUPVIEW", "CHILD NAME: " + child.getName());
//				eventChildViewHolder.imgSx.setVisibility(View.INVISIBLE);
//				eventChildViewHolder.text.setPadding(10, 0, 0, 0);
//			}
//			else{
//				eventChildViewHolder.imgSx.setVisibility(View.GONE);
//				eventChildViewHolder.text.setPadding(10, 10, 0, 0);
//			}
//			
//		}
		
		// set icon on the left side
		if (child.getLeftIconId() != -1) {
			Log.i("GROUPVIEW", "CHILD SX ICON ID: " + child.getLeftIconId());
			eventChildViewHolder.text.setCompoundDrawablesWithIntrinsicBounds(child.getLeftIconId(), 0, 0, 0);
		}else
			eventChildViewHolder.text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		
		
		
		
		
		// set icons on the right side for the items of type 1 (telefono, email)
		if ((child.getRightIconIds() != null) && (child.getType() == 1)) {
			Log.i("GROUPVIEW", "CHILD DX1 ICON ID: "
					+ child.getRightIconIds()[0]);

			eventChildViewHolder.text.setTypeface(null, Typeface.BOLD);

			eventChildViewHolder.imgsDx1.setVisibility(View.VISIBLE);
			eventChildViewHolder.imgsDx1.setImageResource(child
					.getRightIconIds()[0]);
			eventChildViewHolder.imgsDx1
			.setOnClickListener(new ChildAddIconClickListener(
					this.fragment.context, child));
		} else {
			Log.i("GROUPVIEW", "CHILD DX1 ICON NULL");
			eventChildViewHolder.imgsDx1.setVisibility(View.INVISIBLE);
		}

	
		//set divider line height and color
		eventChildViewHolder.divider.setBackgroundColor(fragment.getResources().getColor(child.getDividerColor()));
		eventChildViewHolder.divider.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				child.getDividerHeight()));



		// set icons on the right side for the items of type 0 (single values)
		if ((child.getRightIconIds() != null) && (child.getType() == 0)) {
			Log.i("GROUPVIEW", "CHILD DX1 ICON ID: "
					+ child.getRightIconIds()[0]);
			int iconsNumb = child.getRightIconIds().length;
			Log.i("GROUPVIEW", "ICON NUMMBER: " + iconsNumb);
			eventChildViewHolder.imgsDx1.setVisibility(View.VISIBLE);
			eventChildViewHolder.imgsDx1.setImageResource(child
					.getRightIconIds()[0]);
			eventChildViewHolder.imgsDx1
			.setOnClickListener(new ChildActionIconClickListener(
					this.fragment.context, child));
			//this will be added when cancel/edit for single item will be possible
			//			eventChildViewHolder.imgsDx2.setVisibility(View.VISIBLE);
			//			eventChildViewHolder.imgsDx2.setImageResource(child
			//					.getRightIconIds()[1]);
			//			if (iconsNumb == 3)
			//				eventChildViewHolder.imgsDx3.setVisibility(View.VISIBLE);
			//			eventChildViewHolder.imgsDx3.setImageResource(child
			//					.getRightIconIds()[2]);

		} else {
			Log.i("GROUPVIEW", "CHILD DX1 ICON NULL");
			eventChildViewHolder.imgsDx1.setVisibility(View.INVISIBLE);

			//this will be added when cancel/edit for single item will be possible
			//			eventChildViewHolder.imgsDx2.setVisibility(View.INVISIBLE);
			//			eventChildViewHolder.imgsDx3.setVisibility(View.INVISIBLE);
		}

		//		Log.i("GROUPVIEW", "child view: group  POS: " + groupPosition + "!!");
		//		Log.i("GROUPVIEW", "child view: child POS: " + childPosition + "!!");

		countChildViewCall++;


		return row;
	}

	// @Override
	// public View getChildView(final int groupPosition, final int
	// childPosition,
	// boolean isLastChild, View convertView, ViewGroup parent) {
	//
	// final String eventAttributeValue = (String) getChild(groupPosition,
	// childPosition);
	//
	// row = convertView;
	// if (row == null) {
	// LayoutInflater inflater = (LayoutInflater)
	// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// row = inflater.inflate(layoutResourceId, parent, false);
	// //eventPlaceHolder = new EventPlaceholder();
	// txtView = (TextView) row.findViewById(R.id.event_info_attribute_values);
	// //eventPlaceHolder.title.setTag(eventPlaceHolder.title);
	// //eventPlaceHolder.location = (TextView)
	// row.findViewById(R.id.event_placeholder_loc);
	// //e.hour = (TextView) row.findViewById(R.id.event_placeholder_hour);
	// //eventPlaceHolder.icon = (ImageView)
	// row.findViewById(R.id.event_placeholder_photo);
	// //eventPlaceHolder.attendees = (TextView)
	// row.findViewById(R.id.event_placeholder_participants);
	// //eventPlaceHolder.rating = (RatingBar)
	// row.findViewById(R.id.rating_bar);
	// //e.dateSeparator = (TextView) row.findViewById(R.id.date_separator);
	// row.setTag(txtView);
	// } else
	// {
	// txtView = (TextView) row.getTag();
	// }
	//
	// //eventPlaceHolder.event = event;
	//
	// //**** EVENT INFO ***** //
	// //Log.i("SCROLLTABS", "ATTR: " + txtView.getText() + "!!");
	// //Log.i("EVENT", "title: " + e.event.getTitle() + "!!");
	// //Log.i("EVENT", "rating: " +
	// e.event.getCommunityData().getAverageRating() + "!!");
	// //Log.i("EVENT", "participants: " + e.event.getAttendees() + "!!");
	// //Log.i("EVENT", "location: " + (String)
	// e.event.getCustomData().get("where_name") + "!!");
	// //Log.i("EVENT", "when: " + e.event.eventDatesString() + "!!");
	// //Log.i("EVENT", "image: " +
	// e.event.getCustomData().get("event_img").toString() + "!!");
	//
	// txtView.setText(eventAttributeValue);
	//
	// return row;
	// }

	/*
	 * public int getElementSelected() { return elementSelected; }
	 * 
	 * public void setElementSelected(int elementSelected) {
	 * this.elementSelected = elementSelected; }
	 */

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		Log.i("Childs", groupPosition + "=  getChild ==" + childPosition);
		return this.fragment.parents.get(groupPosition).getChildren()
				.get(childPosition);
	}

	// Call when child row clicked
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		/****** When Child row clicked then this function call *******/

		Log.i("Noise", "parent == " + groupPosition + "=  child : =="
				+ childPosition);



		//		if (fragment.ChildClickStatus != childPosition) {
		//			fragment.ChildClickStatus = childPosition;
		//
		//			Toast.makeText(this.fragment.context,
		//					"Parent :" + groupPosition + " Child :" + childPosition,
		//					Toast.LENGTH_LONG).show();
		//		}

		return childPosition;
	}

	// public int getChildrenCount(int groupPosition) {
	// return
	// attrValuesCollections.get(attrNameGroupList.get(groupPosition)).size();
	// }

	@Override
	public int getChildrenCount(int groupPosition) {
		int size = 0;
		if (this.fragment.parents.get(groupPosition).getChildren() != null)
			size = this.fragment.parents.get(groupPosition).getChildren()
			.size();
		return size;
	}

	// public Object getGroup(int groupPosition) {
	// return attrNameGroupList.get(groupPosition);
	// }

	@Override
	public Object getGroup(int groupPosition) {
		Log.i("Parent", groupPosition + "=  getGroup ");

		return this.fragment.parents.get(groupPosition);
	}

	// public int getGroupCount() {
	// return attrNameGroupList.size();
	// }

	@Override
	public int getGroupCount() {
		return this.fragment.parents.size();
	}

	// Call when parent row clicked
	// public long getGroupId(int groupPosition) {
	//
	// Log.i("Parent", groupPosition+"=  getGroupId ");
	//
	// return groupPosition;
	// }

	// Call when parent row clicked
	@Override
	public long getGroupId(int groupPosition) {
		Log.i("Parent", groupPosition + "=  getGroupId "
				+ fragment.ParentClickStatus);

		if (groupPosition == 2 && fragment.ParentClickStatus != groupPosition) {

			// Alert to user
			//			Toast.makeText(this.fragment.context, "Parent :" + groupPosition,
			//					Toast.LENGTH_LONG).show();
		}

		fragment.ParentClickStatus = groupPosition;
		if (fragment.ParentClickStatus == 0)
			fragment.ParentClickStatus = -1;

		return groupPosition;
	}



	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parentView) {

		EventInfoParent parent = this.fragment.parents.get(groupPosition);
		//
		// Log.i("GROUPVIEW", "************ init group view!! ************ ");
		// Log.i("GROUPVIEW", "COUNT: " + countGroupViewCall);
		// Log.i("GROUPVIEW", "GROUP POSITION: " + groupPosition);
		// Log.i("GROUPVIEW", "tag group set label: " + tagGroupLabelSet);

		// Inflate event_info_group_item.xml file for parent rows
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.fragment.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.event_info_group_item,
					null);
		}


		convertView.setBackgroundResource(getBackgroundColor(groupPosition));

		// Get grouprow.xml file elements and set values
		TextView item = (TextView) convertView
				.findViewById(R.id.event_info_attribute_names);
		item.setTypeface(null, Typeface.BOLD);
		item.setText(parent.getText1());

		ImageView image = (ImageView) convertView
				.findViewById(R.id.event_info_action);
		int imageId = fragment.groupImages.get(groupPosition);
		image.setImageResource(imageId);

		image.setOnClickListener(new GroupIconClickListener(
				this.fragment.context, parent));

		countGroupViewCall++;
		return convertView;

	}

	public int getBackgroundColor(int groupPosition) {

		//return R.color.orange_rovereto;
		return R.color.app_green;

	}

	@Override
	public void notifyDataSetChanged() {
		// Refresh List rows
		super.notifyDataSetChanged();
	}

	@Override
	public boolean isEmpty() {
		return ((this.fragment.parents == null) || this.fragment.parents
				.isEmpty());
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	/******************* Checkbox Checked Change Listener ********************/

	private final class GroupIconClickListener implements OnClickListener {
		private final EventInfoParent parent;
		private Context context;

		private GroupIconClickListener(Context context, EventInfoParent parent) {
			this.parent = parent;
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			Log.i("FRAGMENT LC", "EventDetailInfoAdapter --> Edit button pressed!");
			parent.setChecked(true);

			((EventDetailInfoAdapter) fragment.getExpandableListAdapter())
			.notifyDataSetChanged();

			// final Boolean checked = parent.isChecked();
			//			Toast.makeText(context, "Parent : " + parent.getText1(),
			//					Toast.LENGTH_LONG).show();

			FragmentTransaction fragmentTransaction = fragment.getActivity().getSupportFragmentManager().beginTransaction();
			
			
			Fragment edit_fragment=null;
			Bundle args = new Bundle();
			String frag_description=null;

			if (parent.getText1()=="Contatti"){
				edit_fragment = new Fragment_EvDetail_Info_Contacts();
				Log.i("CONTACTS", "EventDetailInfoAdapter --> event selected ID: " + fragment.mEventId + "!!");
				args.putString(Utils.ARG_EVENT_ID, fragment.mEventId);
				frag_description = "event_details_info_edit_contacts";
			} else if(parent.getText1()=="Dove e quando"){
				edit_fragment = new Fragment_EvDetail_Info_WhenWhere();
				Log.i("CONTACTS", "EventDetailInfoAdapter --> event selected ID: " + fragment.mEventId + "!!");
				args.putString(Utils.ARG_EVENT_ID, fragment.mEventId);
				frag_description = "event_details_info_edit_where";
			}else if(parent.getText1()=="Cosa"){
				//edit_fragment = new Fragment_EvDetail_Info_What();
				edit_fragment = new Fragment_EvDetail_Edit_SingleValueField();
				Log.i("CONTACTS", "EventDetailInfoAdapter --> event selected ID: " + fragment.mEventId + "!!");
				args.putString(Utils.ARG_EVENT_ID, fragment.mEventId);
				args.putString(Utils.ARG_EVENT_FIELD_TYPE, "description");
				frag_description = "event_details_info_edit_what";
			}else if(parent.getText1()=="Tags"){
				edit_fragment = new Fragment_EvDetail_Edit_MultiValueField();
				Log.i("CONTACTS", "EventDetailInfoAdapter --> event selected ID: " + fragment.mEventId + "!!");
				args.putString(Utils.ARG_EVENT_ID, fragment.mEventId);
				args.putString(Utils.ARG_EVENT_FIELD_TYPE, "Tags");
				args.putBoolean(Utils.ARG_EVENT_FIELD_TYPE_IS_MANDATORY, true);
				frag_description = "event_details_info_edit_tags";
			}




			if (edit_fragment!=null){
				edit_fragment.setArguments(args);
				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// fragmentTransaction.detach(this);
				fragmentTransaction.replace(R.id.content_frame, edit_fragment, frag_description);
				fragmentTransaction.addToBackStack(edit_fragment.getTag());
				fragmentTransaction.commit();
				//reset event and event id
				fragment.mEvent=null;
				fragment.mEventId=null;
			}



		}

	}

	/***********************************************************************/

	/******************* Checkbox Checked Change Listener ********************/

	private final class ChildAddIconClickListener implements OnClickListener {
		private final EventInfoChild child;
		private Context context;

		private ChildAddIconClickListener(Context context, EventInfoChild child) {
			this.child = child;
			this.context = context;
		}

		@Override
		public void onClick(View v) {
			Log.i("GROUPVIEW", "Right Icon Pressed!");

			((EventDetailInfoAdapter) fragment.getExpandableListAdapter())
			.notifyDataSetChanged();

			//			Toast.makeText(context,
			//					"Add a new child of type: " + child.getText(),
			//					Toast.LENGTH_LONG).show();

		}

	}

	/***********************************************************************/


	/******************* Checkbox Checked Change Listener ********************/

	private final class ChildActionIconClickListener implements OnClickListener {
		private final EventInfoChild child;
		private Context context;

		private ChildActionIconClickListener(Context context, EventInfoChild child) {
			this.child = child;
			this.context = context;
		}

		@Override
		public void onClick(View v) {

			Log.i("GROUPVIEW", "Right Icon Pressed!");

			((EventDetailInfoAdapter) fragment.getExpandableListAdapter())
			.notifyDataSetChanged();

			if (child.getName()=="tel") {
				Log.i("GROUPVIEW", "Call number:" + child.getText());
				//				Toast.makeText(context,
				//						"Call number: " + child.getText(),
				//						Toast.LENGTH_LONG).show();
				try {
					Intent my_callIntent = new Intent(Intent.ACTION_CALL);
					my_callIntent.setData(Uri.parse("tel:" + child.getText()));
					//here the word 'tel' is important for making a call...
					this.context.startActivity(my_callIntent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(this.context, "Error in your phone call"+e.getMessage(), Toast.LENGTH_LONG).show();
				}

			}else if (child.getName()=="email"){
				Log.i("GROUPVIEW", "Write email to:" + child.getText());
				//				Toast.makeText(this.context,
				//						"Write email to: " + child.getText(),
				//						Toast.LENGTH_LONG).show();
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{child.getText()});
				i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
				i.putExtra(Intent.EXTRA_TEXT   , "body of email");
				try {
					this.context.startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(this.context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	/***********************************************************************/


	
	private static class EventInfoChildViewHolder {
		TextView text;
		//ImageView imgSx;
		ImageView imgsDx1;
		ImageView imgsDx2;
		ImageView imgsDx3;
		View divider;
		int position;
	}

	@Override
	public int getChildTypeCount() {
		return 2;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		int childtype = 0;
		// to be done automatically!!
		int countPhoneNumbers = 2;
		if ((groupPosition == 3)
				&& ((childPosition == 0) || (childPosition == countPhoneNumbers + 1))) {
			childtype = 1;
		}
		// if (childPosition == getChildrenCount(groupPosition) - 1)
		// childtype = 1;

		Log.i("GROUPVIEW", "CHILD TYPE IS: " + childtype);

		return childtype;
	}

}
