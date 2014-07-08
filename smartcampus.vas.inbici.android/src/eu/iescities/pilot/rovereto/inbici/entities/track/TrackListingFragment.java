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
package eu.iescities.pilot.rovereto.inbici.entities.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.inbici.custom.CategoryHelper.CategoryDescriptor;
import eu.iescities.pilot.rovereto.inbici.custom.ViewHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.map.MapManager;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.trentorise.smartcampus.android.common.SCAsyncTask.SCAsyncTaskProcessor;
import eu.trentorise.smartcampus.android.common.listing.AbstractLstingFragment;





public class TrackListingFragment extends AbstractLstingFragment<TrackObject> implements  Parcelable{

	public static final String ARG_CATEGORY = "category";
	public static final String ARG_LIST = "list";

	private ListView list;
	private Context context;
	private String category;
	private TrackAdapter trackAdapter;
	public static final String ARG_ID = "id_track";
	public static final String ARG_INDEX = "index_adapter";
	private String idTrack = "";
	private Integer indexAdapter;
	private Boolean reload = false;
	private Integer postitionSelected = 0;
	private int order_by=ListByOrder.ORDER_BY_ALPHABETICAL;
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ARG_ID, idTrack);
		if (indexAdapter != null)
			outState.putInt(ARG_INDEX, indexAdapter);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this.getActivity();
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		Cursor tracksCursor = null;
		super.onActivityCreated(arg0);
		list = (ListView) getActivity().findViewById(R.id.track_list);

		if (arg0 != null) {
			// Restore last state for checked position.
			idTrack = arg0.getString(ARG_ID);
			indexAdapter = arg0.getInt(ARG_INDEX);

		}
		if (trackAdapter == null) {
			trackAdapter = new TrackAdapter(context, R.layout.tracks_row);
		}
		// //check the db
		// if (true)
		// {
		// tracksCursor = getActivity().managedQuery(Tracks.CONTENT_URI, new
		// String[] { Tracks._ID, Tracks.NAME, Tracks.CREATION_TIME }, null,
		// null, Tracks.CREATION_TIME + " DESC");
		// }

		setAdapter(trackAdapter);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.trackslist, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!idTrack.equals("")) {
			try {

				// get info of the track
				TrackObject track;

				track = InBiciHelper.findTrackById(idTrack);

				if (track == null) {
					// cancellazione
					removeTrack(trackAdapter, indexAdapter);

				} else {
					if (track.getUpdateTime() == 0) {
						removeTrack(trackAdapter, indexAdapter);
						insertTrack(track);
					}
				}
				// notify
				trackAdapter.notifyDataSetChanged();
				idTrack = "";
				indexAdapter = 0;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * insert in the same adapter the new item
	 */
	private void insertTrack(TrackObject track) {

		// add in the right place
		int i = 0;
		boolean insert = false;
		while (i < trackAdapter.getCount()) {
			if (trackAdapter.getItem(i).getTitle() != null) {
				if (trackAdapter.getItem(i).getTitle().toLowerCase().compareTo(track.getTitle().toLowerCase()) <= 0) {
					i++;
				} else {
					trackAdapter.insert(track, i);
					insert = true;
					break;
				}
			}
		}

		if (!insert) {
			trackAdapter.insert(track, trackAdapter.getCount());
		}
	}

	/* clean the adapter from the items modified or erased */
	private void removeTrack(TrackAdapter trackAdapter, Integer indexAdapter) {
		TrackObject objectToRemove = trackAdapter.getItem(indexAdapter);
		int i = 0;
		while (i < trackAdapter.getCount()) {
			if (trackAdapter.getItem(i).getId() == objectToRemove.getId()) {
				trackAdapter.remove(trackAdapter.getItem(i));
			} else {
				i++;
			}
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {


		menu.clear();
		getActivity().getMenuInflater().inflate(R.menu.gripmenu, menu);
		MenuItem item = menu.add(1, R.id.map_view, Menu.NONE, R.string.action_poi);
		item.setIcon(R.drawable.ic_action_map);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		SubMenu submenu = menu.getItem(1).getSubMenu();
		submenu.clear();

		submenu.add(Menu.CATEGORY_SYSTEM, R.id.track_list_order, Menu.NONE, R.string.track_list_order);
		submenu.add(Menu.CATEGORY_SYSTEM, R.id.track_list_search, Menu.NONE, R.string.track_list_search);

		super.onPrepareOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.map_view) {
			category = (getArguments() != null) ? getArguments().getString(ARG_CATEGORY) : null;
			if (category != null) {
				MapManager.switchToMapView(category, Constants.ARG_TRACK_CATEGORY, this);
			} else {
				ArrayList<BaseDTObject> target = new ArrayList<BaseDTObject>();
				for (int i = 0; i < list.getAdapter().getCount(); i++) {
					BaseDTObject o = (BaseDTObject) list.getAdapter().getItem(i);
					target.add(o);
				}
				MapManager.switchToMapView(target, this);
			}
			return true;
		} else if (item.getItemId() == R.id.track_list_order) {
//			new OrderTracksDialogBox(getActivity(),order_by).chooseOrderBy(getActivity(),this);
//			new OrderTracksDialogBox().chooseOrderBy(getActivity(),this);
		    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
		    if (prev != null) {
		        ft.remove(prev);
		    }
		    ft.addToBackStack(null);

		    DialogFragment newFragment = OrderTracksDialogBox.newInstance(order_by,this);
		    newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
			return true;
		} else if (item.getItemId() == R.id.track_list_search) {
			return true;
		}
			else {
		
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onStart() {
		// hide keyboard if it is still open
		Utils.hideKeyboard(getActivity());

		if (reload) {
			trackAdapter = new TrackAdapter(context, R.layout.tracks_row);
			setAdapter(trackAdapter);
			reload = false;
		}

		Bundle bundle = this.getArguments();
		String category = (bundle != null) ? bundle.getString(ARG_CATEGORY) : null;
		CategoryDescriptor catDescriptor = CategoryHelper.getCategoryDescriptorByCategoryFiltered(
				CategoryHelper.CATEGORY_TYPE_TRACKS, category);
		String categoryString = (catDescriptor != null) ? context.getResources().getString(catDescriptor.description)
				: null;

		// set title
		TextView title = (TextView) getView().findViewById(R.id.list_title);
		if (categoryString != null) {
			title.setText(categoryString);
		}

		// close items menus if open
		((View) list.getParent()).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideListItemsMenu(v, false);
			}
		});
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				hideListItemsMenu(view, false);
				setStoredTrackId(view, position);

			}
		});
		super.onStart();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		super.onScrollStateChanged(view, scrollState);
		if ((postitionSelected != -1) && (scrollState == SCROLL_STATE_TOUCH_SCROLL)) {
			hideListItemsMenu(view, false);
		}
	}

	private void hideListItemsMenu(View v, boolean close) {
		boolean toBeHidden = false;
		for (int index = 0; index < list.getChildCount(); index++) {
			View view = list.getChildAt(index);
			if (view instanceof ViewSwitcher && ((ViewSwitcher) view).getDisplayedChild() == 1) {
				((ViewSwitcher) view).showPrevious();
				toBeHidden = true;
				trackAdapter.setElementSelected(-1);
				postitionSelected = -1;
			}
		}
		if (!toBeHidden && v != null && v.getTag() != null && !close) {
			// no items needed to be flipped, fill and open details page
			FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

			TrackContainerFragment fragment = TrackContainerFragment.newInstance(((TrackPlaceholder) v.getTag()).track
					.getId());

			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// fragmentTransaction.detach(this);
			fragmentTransaction.replace(R.id.content_frame, fragment, "tracks");
			fragmentTransaction.addToBackStack(fragment.getTag());
			fragmentTransaction.commit();
		}
	}

	private void setStoredTrackId(View v, int position) {
		final TrackObject track = ((TrackPlaceholder) v.getTag()).track;
		idTrack = track.getId();
		indexAdapter = position;
	}

	private List<TrackObject> getTracks(AbstractLstingFragment.ListingRequest... params) {
		try {
			Bundle bundle = getArguments();
			String categories = bundle.getString(ARG_CATEGORY);
			SortedMap<String, Integer> sort = new TreeMap<String, Integer>();
			sort.put("title", 1);

			if (categories != null) {
				return InBiciHelper.getTracksByCategory(categories,order_by, position, size);
			} else if (bundle.containsKey(ARG_LIST)) {
				return (List<TrackObject>) bundle.getSerializable(ARG_LIST);
			} else {
				return Collections.emptyList();
			}
		} catch (Exception e) {
			Log.e(TrackListingFragment.class.getName(), e.getMessage());
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private class TrackLoader extends
			AbstractAsyncTaskProcessor<AbstractLstingFragment.ListingRequest, List<TrackObject>> {

		public TrackLoader(Activity activity) {
			super(activity);
		}

		@Override
		public List<TrackObject> performAction(AbstractLstingFragment.ListingRequest... params)
				throws SecurityException, Exception {
			return getTracks(params);
		}

		@Override
		public void handleResult(List<TrackObject> result) {
			updateList(result == null || result.isEmpty());
		}

	}

	@Override
	protected SCAsyncTaskProcessor<AbstractLstingFragment.ListingRequest, List<TrackObject>> getLoader() {
		return new TrackLoader(getActivity());
	}

	@Override
	protected ListView getListView() {
		return list;
	}

	private void updateList(boolean empty) {
		if (getView() != null) {

			ViewHelper.removeEmptyListView((LinearLayout) getView().findViewById(R.id.tracklistcontainer));
			if (empty) {
				ViewHelper.addEmptyListView((LinearLayout) getView().findViewById(R.id.tracklistcontainer));
			}
			hideListItemsMenu(null, false);
		}
	}

	public void orderBy(int orderBy) {
		super.load();
		this.order_by = orderBy;

//		new TrackLoader(getActivity());
//		getTracks(new ListingRequest(position, size));
//		adapter.notifyDataSetChanged();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

}
