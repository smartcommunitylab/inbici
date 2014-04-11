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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.entities.search.SearchFragment;
import eu.iescities.pilot.rovereto.inbici.entities.search.WhenForSearch;
import eu.iescities.pilot.rovereto.inbici.entities.search.WhereForSearch;
import eu.iescities.pilot.rovereto.inbici.map.MapFragment;
import eu.iescities.pilot.rovereto.inbici.map.MapManager;
import eu.iescities.pilot.rovereto.inbici.utils.TimeUtils;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.android.common.SCAsyncTask.SCAsyncTaskProcessor;
import eu.trentorise.smartcampus.android.common.listing.AbstractLstingFragment;
import eu.trentorise.smartcampus.android.common.listing.AbstractLstingFragment.ListingRequest;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

// to be used for event listing both in categories and in My Events
public class EventsListingFragment extends Fragment implements OnScrollListener, ReloadAdapter {
	private ListView list;
	private Context context;

	public static final String ARG_CATEGORY = "event_category";
	public static final String ARG_POI = "event_poiId";
	public static final String ARG_POI_NAME = "event_poi_title";
	public static final String ARG_QUERY = "event_query";
	public static final String ARG_QUERY_TODAY = "event_query_today";
	public static final String ARG_MY = "event_my";
	public static final String ARG_CATEGORY_SEARCH = "category_search";
	public static final String ARG_MY_EVENTS_SEARCH = "my_events_search";
	public static final String ARG_LIST = "event_list";
	public static final String ARG_ID = "id_event";
	public static final String ARG_INDEX = "index_adapter";
	public static final int DEFAULT_ELEMENTS_NUMBER = 20;

	private String category;
	private EventAdapter eventsAdapter;
	private boolean mFollowByIntent;
	private String idEvent = "";
	private Integer indexAdapter;
	private Boolean reload = false;
	private Integer postitionSelected = -1;
	private boolean postProcAndHeader = true;
	private String event_id_selected = null;
	protected int lastSize = 0;
	protected int position = 0;
	protected int size = DEFAULT_ELEMENTS_NUMBER;
	private Long oldFromTime = null;
	private Long oldToTime = null;
	private boolean today = false;
	// For the expandable list view
	List<String> dateGroupList = new ArrayList<String>();

	private List<ExplorerObject> listEvents = new ArrayList<ExplorerObject>();
	Map<String, List<ExplorerObject>> eventCollection = new LinkedHashMap<String, List<ExplorerObject>>();
	ExpandableListView expListView;

	// for loading the images
	protected DisplayImageOptions imgOptions;
	private int firstVis;
	private int lastVis;;

	protected Map<String, List<String>> eventImageUrls = new LinkedHashMap<String, List<String>>();
	private int previousGroup;
	private int previousItem;;

	// protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public void onResume() {
		super.onResume();
		if (!idEvent.equals("")) {
			// get info of the event
			ExplorerObject event = DTHelper.findEventById(idEvent);
			// notify
			// eventsAdapter.notifyDataSetInvalidated();

			eventsAdapter.notifyDataSetChanged();
			idEvent = "";
			indexAdapter = 0;
		}

		try {
			expListView.setSelectedGroup(previousGroup);
			expListView.setSelectedChild(previousGroup, previousItem, true);
			expListView.expandGroup(previousGroup);
		} catch (IndexOutOfBoundsException e) {
			// the changes modify the order of the group, so by default open
			// the first group
			if (eventsAdapter.getGroupCount() > 0) {
				expListView.setSelectedGroup(0);
				expListView.setSelectedChild(0, 0, true);
				expListView.expandGroup(0);
			}
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ARG_ID, idEvent);
		if (indexAdapter != null)
			outState.putInt(ARG_INDEX, indexAdapter);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this.getActivity();
		setHasOptionsMenu(true);
		setFollowByIntent();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.eventslist, container, false);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);

		imgOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty).showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				// .displayer(new RoundedBitmapDisplayer(20))
				.build();

		list = (ListView) getActivity().findViewById(R.id.events_list);
		if (arg0 != null) {
			// Restore last state for checked position.
			idEvent = arg0.getString(ARG_ID);
			indexAdapter = arg0.getInt(ARG_INDEX);

		}

		if (eventsAdapter == null) {
			eventsAdapter = new EventAdapter(context, R.layout.event_list_child_item, EventsListingFragment.this,
					dateGroupList, eventCollection);

		}
		expListView = (ExpandableListView) getActivity().findViewById(R.id.events_list);
		setListenerOnEvent();
		list.setOnScrollListener(this);
		expListView.setAdapter(eventsAdapter);
		if (eventsAdapter.getGroupCount() > 0)
			expListView.expandGroup(0);

	}

	private void setListenerOnEvent() {
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

				Log.i("LISTENER", "I should toast 1 ");

				final ExplorerObject selected = (ExplorerObject) eventsAdapter.getChild(groupPosition, childPosition);

				Log.i("SCROLLTABS", "Load the scroll tabs!!");
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				Fragment_EventDetails fragment = new Fragment_EventDetails();

				Bundle args = new Bundle();

				Log.i("SCROLLTABS", "event selected ID: " + ((EventPlaceholder) v.getTag()).event.getId() + "!!");
				event_id_selected = ((EventPlaceholder) v.getTag()).event.getId();
				oldFromTime = ((EventPlaceholder) v.getTag()).event.getFromTime();
				oldToTime = ((EventPlaceholder) v.getTag()).event.getToTime();
				previousGroup = groupPosition;
				previousItem = childPosition;
				args.putString(Utils.ARG_EVENT_ID, ((EventPlaceholder) v.getTag()).event.getId());
				try {
					args.putString(Utils.ARG_EVENT_IMAGE_URL,
							eventImageUrls.get(dateGroupList.get(groupPosition)).get(childPosition));
				} catch (Exception e) {
				}

				fragment.setArguments(args);

				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// fragmentTransaction.detach(this);
				fragmentTransaction.replace(R.id.content_frame, fragment, "event_details");
				fragmentTransaction.addToBackStack(fragment.getTag());
				fragmentTransaction.commit();

				return true;
			}
		});
	}

	@Override
	public void onStart() {
		Bundle bundle = this.getArguments();

		// I need to pass the interface to the fragment whenwhere. Now reloading
		// the adapter everytime is too slow
		// if (reload){
		eventsAdapter = new EventAdapter(context, R.layout.event_list_child_item, EventsListingFragment.this,
				dateGroupList, eventCollection);

		expListView.setAdapter(eventsAdapter);
		setListenerOnEvent();
		reload = false;
		// }
		if (event_id_selected != null) {
			// get event's info
			ExplorerObject new_event = null;
			try {
				new_event = DTHelper.findEventById(event_id_selected);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// change info in the adapter collection
			cahngeNewEventinCollection(new_event);
			eventsAdapter.setDateGroupList(dateGroupList);
			eventsAdapter.setEventCollection(eventCollection);
			// eventsAdapter.notifyDataSetInvalidated();
			eventsAdapter.notifyDataSetChanged();

		} else
			initData();
		super.onStart();

	}

	private void cahngeNewEventinCollection(ExplorerObject new_event) {
		removeOldSingleEvent(new_event);
		updateSingleEvent(new_event);
	}

	private void removeOldSingleEvent(ExplorerObject new_event) {
		String date_with_day;
		date_with_day = Utils.getDateTimeString(context, new_event.getFromTime(), Utils.DATE_FORMAT_2, true, true)[0];

		if ((new_event.getToTime() == null) || ((new_event.getToTime() == 0))) {
			if (getArguments().getBoolean(SearchFragment.ARG_MY)
					|| (new_event.getFromTime() >= TimeUtils.getCurrentDateTimeForSearching())) {
				// get event-dates
				removeEvent(new_event, date_with_day);

			}
		} else {
			// get the list of dates
			List<Date> listOfDate = Utils.getDatesBetweenInterval(new Date(oldFromTime), new Date(oldToTime));
			// get event-dates
			for (Date date : listOfDate) {
				date_with_day = Utils.getDateTimeString(context, date.getTime(), Utils.DATE_FORMAT_2, true, true)[0];
				if (getArguments().getBoolean(SearchFragment.ARG_MY)
						|| (date.getTime() >= TimeUtils.getCurrentDateTimeForSearching())) {
					removeEvent(new_event, date_with_day);

				}
			}

		}
		// clean empty date

		List<Date> listOfDate = Utils.getDatesBetweenInterval(new Date(oldFromTime), new Date(oldToTime));
		// get event-dates
		for (Date date : listOfDate) {
			date_with_day = Utils.getDateTimeString(context, date.getTime(), Utils.DATE_FORMAT_2, true, true)[0];
			if (eventCollection.get(date_with_day) != null && eventCollection.get(date_with_day).size() == 0) {
				dateGroupList.remove(date_with_day);
			}
		}
	}

	private void removeEvent(ExplorerObject new_event, String date_with_day) {
		if (dateGroupList.contains(date_with_day)) {
			int index = 0;
			boolean found = false;
			for (ExplorerObject e : eventCollection.get(date_with_day)) {
				if (e.getId().equals(new_event.getId())) {
					found = true;
					break;
				}
				index++;
			}
			if (found) {
				eventCollection.get(date_with_day).remove(index);
				eventImageUrls.get(date_with_day).remove(index);
			}

		}
	}

	protected void initData() {

		if (eventsAdapter != null && eventsAdapter.getGroupCount() == 0) {
			position = 0;
			lastSize = 0;

			if (loadOnStart())
				load();

		}

	}

	protected boolean loadOnStart() {
		return true;
	}

	private void setFollowByIntent() {
		try {
			ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(),
					PackageManager.GET_META_DATA);
			Bundle aBundle = ai.metaData;
			mFollowByIntent = aBundle.getBoolean("follow-by-intent");
		} catch (NameNotFoundException e) {
			mFollowByIntent = false;
			Log.e(EventsListingFragment.class.getName(), "you should set the follow-by-intent metadata in app manifest");
		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	protected void load() {
		if (position == 0) {
			eventCollection.clear();
			eventImageUrls.clear();
		}
		new SCListingFragmentTask<ListingRequest, Void>(getActivity(), getLoader()).execute(new ListingRequest(
				position, size));
	}

	protected SCAsyncTaskProcessor<AbstractLstingFragment.ListingRequest, List<ExplorerObject>> getLoader() {
		return new EventLoader(getActivity());
	}

	private class EventLoader extends
			AbstractAsyncTaskProcessor<AbstractLstingFragment.ListingRequest, List<ExplorerObject>> {

		private FragmentActivity currentRootActivity = null;

		public EventLoader(Activity activity) {
			super(activity);
		}

		@Override
		public List<ExplorerObject> performAction(AbstractLstingFragment.ListingRequest... params)
				throws SecurityException, Exception {
			return getEvents(params);
		}

		@Override
		public void handleResult(List<ExplorerObject> result) {
			if (!result.isEmpty()) {

				// order data by date
				updateCollectionAndGetImages(result);
				eventsAdapter.setDateGroupList(dateGroupList);
				eventsAdapter.setEventCollection(eventCollection);
				// eventsAdapter.notifyDataSetInvalidated();

				eventsAdapter.notifyDataSetChanged();
				if (expListView.getExpandableListAdapter().getGroupCount() > 0)
					expListView.expandGroup(0);

			} else {
				TextView no_result = (TextView) getActivity().findViewById(R.id.events_no_results);
				no_result.setVisibility(View.VISIBLE);
				expListView.setVisibility(View.GONE);
			}

		}

	}

	private void updateCollectionAndGetImages(List<ExplorerObject> result) {
		String date_with_day = null;
		dateGroupList = new ArrayList<String>();
		for (ExplorerObject expObj : result) {
			updateSingleEvent(expObj);

		}
	}

	private void updateSingleEvent(ExplorerObject expObj) {
		String date_with_day;
		date_with_day = Utils.getDateTimeString(context, expObj.getFromTime(), Utils.DATE_FORMAT_2, true, true)[0];

		if ((expObj.getToTime() == null) || ((expObj.getToTime() == 0))) {
			if (getArguments().getBoolean(SearchFragment.ARG_MY)
					|| (expObj.getFromTime() >= TimeUtils.getCurrentDateTimeForSearching())) {
				// get event-dates
				addEvent(expObj, date_with_day);
			}
		} else {
			List<Date> listOfDate = null;
			// get the list of dates
			if (!today)
				listOfDate = Utils
						.getDatesBetweenInterval(new Date(expObj.getFromTime()), new Date(expObj.getToTime()));
			else {
				// get only today
				listOfDate = new ArrayList<Date>() {
					{
						add(new Date());

					}
				};
			}
			// get event-dates
			for (Date date : listOfDate) {
				date_with_day = Utils.getDateTimeString(context, date.getTime(), Utils.DATE_FORMAT_2, true, true)[0];
				if (getArguments().getBoolean(SearchFragment.ARG_MY)
						|| (date.getTime() >= TimeUtils.getCurrentDateTimeForSearching())) {
					addEvent(expObj, date_with_day);
				}
			}

		}
	}

	private void addEvent(ExplorerObject expObj, String date_with_day) {
		if (!dateGroupList.contains(date_with_day)) {
			dateGroupList.add(date_with_day);
			eventCollection.put(date_with_day, new ArrayList<ExplorerObject>());
			eventImageUrls.put(date_with_day, new ArrayList<String>());

		}
		// insert se precedente era presente
		if (previousItem != -1 && previousGroup == dateGroupList.indexOf(date_with_day))
			eventCollection.get(date_with_day).add(previousItem, expObj);
		else
			eventCollection.get(date_with_day).add(expObj);

		// get event image urls
		String eventImg = expObj.getImage();
		if (previousItem != -1 && previousGroup == dateGroupList.indexOf(date_with_day))
			eventImageUrls.get(date_with_day).add(previousItem, eventImg);
		else
			eventImageUrls.get(date_with_day).add(eventImg);
	}

	// private void addEvent(ExplorerObject expObj, String date_with_day) {
	// if (!dateGroupList.contains(date_with_day)) {
	//
	// dateGroupList.add(date_with_day);
	// eventCollection.put(date_with_day, new ArrayList<ExplorerObject>());
	// eventImageUrls.put(date_with_day, new ArrayList<String>());
	//
	// }
	// eventCollection.get(date_with_day).add(expObj);
	//
	// // get event image urls
	// String eventImg = expObj.getImage();
	// eventImageUrls.get(date_with_day).add(eventImg);
	// }

	private List<ExplorerObject> getEvents(AbstractLstingFragment.ListingRequest... params) {
		try {
			Collection<ExplorerObject> result = null;

			Bundle bundle = getArguments();
			boolean my = false;

			if (bundle == null) {
				return Collections.emptyList();
			}
			if (bundle.getBoolean(SearchFragment.ARG_MY))
				my = true;
			String categories = bundle.getString(SearchFragment.ARG_CATEGORY);
			SortedMap<String, Integer> sort = new TreeMap<String, Integer>();
			sort.put("fromTime", 1);
			if (bundle.containsKey(SearchFragment.ARG_CATEGORY)
					&& (bundle.getString(SearchFragment.ARG_CATEGORY) != null)) {

				result = DTHelper.searchInGeneral(0, -1, bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my, ExplorerObject.class,
						sort, categories);

			} else if (bundle.containsKey(ARG_POI) && (bundle.getString(ARG_POI) != null)) {
				result = DTHelper.getEventsByPOI(0, -1, bundle.getString(ARG_POI));
			} else if (bundle.containsKey(SearchFragment.ARG_MY) && (bundle.getBoolean(SearchFragment.ARG_MY))) {

				result = DTHelper.searchInGeneral(0, -1, bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my, ExplorerObject.class,
						sort, categories);

			} else if (bundle.containsKey(SearchFragment.ARG_QUERY)) {

				result = DTHelper.searchInGeneral(0, -1, bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my, ExplorerObject.class,
						sort, categories);
			} else if (bundle.containsKey(ARG_QUERY_TODAY)) {
				today = true;
				result = DTHelper.searchTodayEvents(0, -1, bundle.getString(SearchFragment.ARG_QUERY));
			} else if (bundle.containsKey(SearchFragment.ARG_LIST)) {
				result = (Collection<ExplorerObject>) bundle.get(SearchFragment.ARG_LIST);
			} else {
				return Collections.emptyList();
			}

			listEvents.addAll(result);

			List<ExplorerObject> sorted = new ArrayList<ExplorerObject>(listEvents);

			return sorted;
		} catch (Exception e) {
			Log.e(EventsListingFragment.class.getName(), e.getMessage());
			e.printStackTrace();
			listEvents = Collections.emptyList();
			return listEvents;
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		Log.i("MENU", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());

		// menu.clear();

		getActivity().getMenuInflater().inflate(R.menu.list_menu, menu);

		if (category == null) {
			category = (getArguments() != null) ? getArguments().getString(SearchFragment.ARG_CATEGORY) : null;
		}

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.map_view) {
			category = (getArguments() != null) ? getArguments().getString(SearchFragment.ARG_CATEGORY) : null;
			if (category == null && (getArguments() != null) && getArguments().containsKey(SearchFragment.ARG_MY))
				category = CategoryHelper.EVENTS_MY.category;
			if (category == null && (getArguments() != null) && getArguments().getString(ARG_QUERY_TODAY) != null)
				category = CategoryHelper.EVENTS_TODAY.category;
			boolean query = getArguments().containsKey(SearchFragment.ARG_QUERY);

			if (category != null && !query) {
				Log.i("AB TITLE", "switchToMapView category:" + category);
				MapManager.switchToMapView(category, Constants.ARG_EVENT_CATEGORY, this);
			} else {
				ArrayList<BaseDTObject> target = new ArrayList<BaseDTObject>();
				if (list != null) {
					for (int i = 0; i < listEvents.size(); i++) {
						ExplorerObject o = listEvents.get(i);
						if (o.getLocation() != null && o.getLocation()[0] != 0 && o.getLocation()[1] != 0) {
							target.add(o);
						}
					}
				}

				Log.i("AB TITLE", "switchToMapView BaseDTOObjects:" + target.toString());
				MapManager.switchToMapView(target, this);
			}
			return true;
		}

		else if (item.getItemId() == R.id.search_action) {
			FragmentTransaction fragmentTransaction;
			Fragment fragment;
			fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			fragment = new SearchFragment();
			Bundle args = new Bundle();
			args.putString(SearchFragment.ARG_CATEGORY, category);
			args.putString(CategoryHelper.CATEGORY_TYPE_EVENTS, CategoryHelper.CATEGORY_TYPE_EVENTS);
			if (getArguments() != null && getArguments().containsKey(SearchFragment.ARG_MY)
					&& getArguments().getBoolean(SearchFragment.ARG_MY))
				args.putBoolean(SearchFragment.ARG_MY, getArguments().getBoolean(SearchFragment.ARG_MY));
			fragment.setArguments(args);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.replace(R.id.content_frame, fragment, "events");
			fragmentTransaction.addToBackStack(fragment.getTag());
			fragmentTransaction.commit();
			/* add category to bundle */
			return true;

		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	protected class SCListingFragmentTask<Params, Progress> extends SCAsyncTask<Params, Progress, List<ExplorerObject>> {

		public SCListingFragmentTask(Activity activity,
				SCAsyncTask.SCAsyncTaskProcessor<Params, List<ExplorerObject>> processor) {
			super(activity, processor);
		}

		@Override
		protected void handleSuccess(List<ExplorerObject> result) {
			super.handleSuccess(result);
			// eventsAdapter.notifyDataSetInvalidated();
			if (!result.isEmpty()) {

				// order data by date
				updateCollectionAndGetImages(result);
				eventsAdapter.setDateGroupList(dateGroupList);
				eventsAdapter.setEventCollection(eventCollection);
				// eventsAdapter.notifyDataSetInvalidated();

				eventsAdapter.notifyDataSetChanged();
				if (expListView.getExpandableListAdapter().getGroupCount() > 0)
					expListView.expandGroup(0);

			} else {
				TextView no_result = (TextView) getActivity().findViewById(R.id.events_no_results);
				no_result.setVisibility(View.VISIBLE);
				expListView.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void reload() {
		reload = true;
	}

}
