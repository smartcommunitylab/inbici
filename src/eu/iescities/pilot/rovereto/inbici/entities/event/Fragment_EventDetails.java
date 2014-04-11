package eu.iescities.pilot.rovereto.inbici.entities.event;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.inbici.custom.PagerSlidingTabStrip;
import eu.iescities.pilot.rovereto.inbici.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.inbici.entities.event.community.Fragment_EvDetail_Community;
import eu.iescities.pilot.rovereto.inbici.entities.event.dasapere.Fragment_EvDetail_DaSapere;
import eu.iescities.pilot.rovereto.inbici.entities.event.info.Fragment_EvDetail_Info;
import eu.iescities.pilot.rovereto.inbici.map.MapManager;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;
import eu.iescities.pilot.rovereto.inbici.R;

public class Fragment_EventDetails extends Fragment {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	// private int currentColor = R.color.jungle_green;
	private int currentColor = 0xFF96AA39;
	public ExplorerObject mEvent = null;
	private String mEventId;
	private String mEventImageUrl;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Log.d("SCROLLTABS", "onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
				mEvent = DTHelper.findEventById(mEventId);
				mEventImageUrl = getArguments().getString(Utils.ARG_EVENT_IMAGE_URL);
			}
		} else {
			Log.d("SCROLLTABS", "onCreate SUBSEQUENT TIME");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("SCROLLTABS", "onCreateView");

		return inflater.inflate(R.layout.frag_ev_detail_scrolltabs, container, false);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onStart");

		// getActivity().getActionBar().setTitle(mEvent.getTitle());

		// Set up the action bar.
//		final ActionBar actionBar = getActivity().getActionBar();
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(mEvent.getTitle());
//		actionBar.setTitle(mEvent.getTitle());
		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.

		// getFragmentManager().addOnBackStackChangedListener(getListener());

		tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
		pager = (ViewPager) getActivity().findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getChildFragmentManager());
		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		tabs.setIndicatorColor(currentColor);
		tabs.setUnderlineColor(currentColor);
		tabs.setDividerColor(currentColor);


	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onPause");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onSaveInstanceState");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onDetach");
	}

	/* Pager Adapter */
	public class MyPagerAdapter extends FragmentStatePagerAdapter {
		//private final String[] TITLES = { "Info", "Da Sapere", "Multimedia", "Comunita" };
		private final String[] TITLES = { "Info", "Da Sapere", "Comunita" };
		//private final String[] TITLES = { "Info", "Eventi"};
		
		
		private Fragment mPrimaryItem;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			
			if (getCount()<=3){
				tabs.setShouldExpand(true);
			}
			
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			
			
			switch (position) {
			case 0:
				return Fragment_EvDetail_Info.newInstance(mEventId, mEventImageUrl);
			case 1:
				return Fragment_EvDetail_DaSapere.newInstance(mEventId);
//			case 2:
//				return Fragment_EvDetail_Multimedia.newInstance(mEventId);
//			case 3:
			case 2:
				return Fragment_EvDetail_Community.newInstance(mEventId);
			default:
				return null;
			}
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			mPrimaryItem = (Fragment) object;
		}

		public Fragment getPrimaryItem() {
			return mPrimaryItem;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 if (item.getItemId() == R.id.map_view) {
		 ArrayList<BaseDTObject> list = new ArrayList<BaseDTObject>();
		 getEvent().setLocation(mEvent.getLocation());
		 list.add(getEvent());
		 MapManager.switchToMapView(list, this);
		 return true;
		 } else if (item.getItemId() == R.id.direction_action) {
		 callBringMeThere();
		
		 return true;
		 }
		return true;
	}

	protected void callBringMeThere() {
		 Address to = new Address(Locale.getDefault());
		 to.setLatitude(mEvent.getLocation()[0]);
		 to.setLongitude(mEvent.getLocation()[1]);
		 Address from = null;
		 GeoPoint mylocation = MapManager.requestMyLocation(getActivity());
		 if (mylocation != null) {
		 from = new Address(Locale.getDefault());
		 from.setLatitude(mylocation.getLatitudeE6() / 1E6);
		 from.setLongitude(mylocation.getLongitudeE6() / 1E6);
		 }
		 DTHelper.bringmethere(getActivity(), from, to);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// Log.i("MENU", "start on Prepare Options Menu EVENT LISTING frag: " +
		// menu.toString());
		//
		// // menu.clear();
		//
		 getActivity().getMenuInflater().inflate(R.menu.event_detail_menu,
		 menu);
		 if (getEvent()== null || getEvent().getLocation() == null ||
		 (getEvent().getLocation()[0] == 0 && getEvent().getLocation()[1] ==
		 0)) {
		 menu.findItem(R.id.map_view).setVisible(false);
		 menu.findItem(R.id.direction_action).setVisible(false);
		 }
		// /*
		// * if (category == null) { category = (getArguments() != null) ?
		// * getArguments().getString(SearchFragment.ARG_CATEGORY) : null; }
		// */
		super.onPrepareOptionsMenu(menu);
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
