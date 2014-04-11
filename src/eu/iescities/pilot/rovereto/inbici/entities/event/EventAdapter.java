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
package eu.iescities.pilot.rovereto.inbici.entities.event;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.InBiciApplication;
import eu.iescities.pilot.rovereto.inbici.custom.data.Address;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.R;
import android.view.MotionEvent;


// in EventsListingFragment
public class EventAdapter extends BaseExpandableListAdapter {

	private Context context;

	// for expandable list
	private Map<String, List<ExplorerObject>> eventCollections;
	private List<String> dateGroupList;
	private int layoutResourceId;

	private EventPlaceholder eventPlaceHolder = null;
	private View row = null;
	protected int visualizedGroup = 0;
	protected int visualizedItem = 0;
	
	//for loading images
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();



	EventsListingFragment fragment;

	private ReloadAdapter reloadAdapter;
	

	public EventAdapter(Context context, int layoutResourceId, List<String> events_dates,
			Map<String, List<ExplorerObject>> eventCollections) {
		this.context = context;
		this.eventCollections = eventCollections;
		this.dateGroupList = events_dates;
		this.layoutResourceId = layoutResourceId;

	}
	
	
	public EventAdapter(Context context, int layoutResourceId, EventsListingFragment fragment, List<String> events_dates,
			Map<String, List<ExplorerObject>> eventCollections) {
		this.context = context;
		this.eventCollections = eventCollections;
		this.dateGroupList = events_dates;
		this.layoutResourceId = layoutResourceId;
		this.fragment = fragment;
	}

	public Map<String, List<ExplorerObject>> getEventCollections() {
		return eventCollections;
	}


	public void setEventCollections(Map<String, List<ExplorerObject>> eventCollections) {
		this.eventCollections = eventCollections;
	}


	public List<String> getDateGroupList() {
		return dateGroupList;
	}


	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		
		final ExplorerObject event = (ExplorerObject) getChild(groupPosition, childPosition);

		row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
			eventPlaceHolder = new EventPlaceholder();
			eventPlaceHolder.title = (TextView) row.findViewById(R.id.event_placeholder_title);
			eventPlaceHolder.title.setTag(eventPlaceHolder.title);
			eventPlaceHolder.location = (TextView) row.findViewById(R.id.event_placeholder_loc);
			// e.hour = (TextView)
			// row.findViewById(R.id.event_placeholder_hour);
			eventPlaceHolder.icon = (ImageView) row.findViewById(R.id.event_placeholder_photo);
			eventPlaceHolder.attendees = (TextView) row.findViewById(R.id.event_placeholder_participants);
			eventPlaceHolder.rating = (RatingBar) row.findViewById(R.id.rating_bar);
			// e.dateSeparator = (TextView)
			// row.findViewById(R.id.date_separator);
			row.setTag(eventPlaceHolder);
		} else {
			eventPlaceHolder = (EventPlaceholder) row.getTag();
			 Log.i("BACKPRESSED", "EventAdapter -->get Tag: !!");

		}

		eventPlaceHolder.event = event;

		// **** EVENT INFO ***** //
		// Log.i("EVENT", "EventAdapter --> EVENT ID: " + eventPlaceHolder.event.getId() + "!!");
		 Log.i("BACKPRESSED", "EventAdapter --> title: " + eventPlaceHolder.event.getTitle() + "!!");
		// Log.i("EVENT", "rating: " +
		// eventPlaceHolder.event.getCommunityData().getAverageRating() + "!!");
		// Log.i("EVENT", "participants: " +
		// eventPlaceHolder.event.getCommunityData().getAttendees() + "!!");
		// Log.i("EVENT", "location: " + (String)
		// e.event.getCustomData().get("where_name") + "!!");
		// Log.i("EVENT", "when: " + e.event.eventDatesString() + "!!");
		// Log.i("EVENT", "image: " +
		// e.event.getCustomData().get("event_img").toString() + "!!");

		eventPlaceHolder.title.setText(eventPlaceHolder.event.getTitle());
		eventPlaceHolder.attendees.setText(eventPlaceHolder.event.getCommunityData().getAttendees().toString());

		Address address = eventPlaceHolder.event.getAddress();
		if (address != null) {

			String place = (address.getLuogo() != null) ? (String) address.getLuogo() : null;
			if ((place != null) && (!place.matches(""))){
				eventPlaceHolder.location.setText(place);
			}
			else 
				eventPlaceHolder.location.setText(context.getString(R.string.city_hint));
		}

		// load the event image
//		Log.i("IMAGES", "START ADAPTER, EVENT TITLE: " + eventPlaceHolder.event.getTitle() + "!!");
//
//		if (fragment.eventImageUrls!=null){
//			Log.i("IMAGES", "EventAdapter --> image array size: " + fragment.eventImageUrls.size() );
//			this.eventImageUrls = fragment.eventImageUrls.toArray(new String[fragment.eventImageUrls.size()]);
//		}
//
//		Log.i("IMAGES", "EventAdapter --> group position: " + groupPosition );
//		Log.i("IMAGES", "EventAdapter --> child position: " + childPosition );
//
//		
//		
		
		String imgUrl = null;
		try {
			imgUrl = fragment.eventImageUrls.get(dateGroupList.get(groupPosition)).get(childPosition);
		} catch (Exception e){
			e.printStackTrace();
		}
		Log.i("IMAGES", "EventAdapter --> image new url : " + imgUrl );
		
		InBiciApplication.imageLoader.displayImage(imgUrl, eventPlaceHolder.icon, fragment.imgOptions, animateFirstListener);
		
		
		
		//set the rating bar
		eventPlaceHolder.rating.setRating(eventPlaceHolder.event.getCommunityData().getAverageRating());
		
		eventPlaceHolder.rating.setOnTouchListener(new OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            return true;
	        }
	    });	
		
		
		eventPlaceHolder.rating.setFocusable(false);
	       
		
		Calendar previousEvent = null;
		Calendar currentEvent = Calendar.getInstance();
		

		if (event.getFromTime() != null)
			currentEvent.setTimeInMillis(event.getFromTime());
		visualizedGroup=groupPosition;
		visualizedItem = childPosition;
		return row;
	}

	/*
	 * public int getElementSelected() { return elementSelected; }
	 * 
	 * public void setElementSelected(int elementSelected) {
	 * this.elementSelected = elementSelected; }
	 */

	// Methods needed for the Expandable adapter
	public Object getChild(int groupPosition, int childPosition) {
		return eventCollections.get(dateGroupList.get(groupPosition)).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return eventCollections.get(dateGroupList.get(groupPosition)).size();
	}

	public Object getGroup(int groupPosition) {
		return dateGroupList.get(groupPosition);
	}

	public int getGroupCount() {
		if (dateGroupList != null)
			return dateGroupList.size();
		else return 0;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		String dateLabel = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.event_list_group_item, null);
		}

		convertView.setBackgroundResource(getBackgroundColor(groupPosition));

		TextView item = (TextView) convertView.findViewById(R.id.events_date);
		item.setTypeface(null, Typeface.BOLD);
		item.setText(dateLabel);

		return convertView;

	}

	public int getBackgroundColor(int groupPosition) {

		return R.color.app_green;

	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}


	public void setDateGroupList(List<String> dateGroupList2) {
		this.dateGroupList = dateGroupList2;
	}


	public void setEventCollection(Map<String, List<ExplorerObject>> eventCollection) {
		this.eventCollections = eventCollection;
	}
	
	
	public int getVisualizedGroup() {
		return visualizedGroup;
	}



	public int getVisualizedItem() {
		return visualizedItem;
	}
	

}
