package eu.iescities.pilot.rovereto.inbici.entities.track;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.PagerSlidingTabStrip;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;
import eu.iescities.pilot.rovereto.inbici.entities.track.info.TrackDetailsFragment;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.map.LoggerMapHelper;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.map.MapQuestLoggerMap;
import eu.iescities.pilot.rovereto.inbici.entities.track.training.TrainingsFragment;
import eu.iescities.pilot.rovereto.inbici.map.MapManager;
import eu.iescities.pilot.rovereto.inbici.utils.Utils;

public class TrackContainerFragment extends Fragment {

	private PagerSlidingTabStrip tabs;
	private ViewPager mPager;
	private TrackPagerAdapter mPagerAdapter;
	private ActionBarActivity abActivity = null;
	private int tabColor;

	public TrackObject mTrack = null;
	private String mTrackId;

	public static TrackContainerFragment newInstance(String id) {
		TrackContainerFragment fragment = new TrackContainerFragment();
		Bundle args = new Bundle();
		args.putString(Constants.ARG_TRACK_ID, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Log.d("SCROLLTABS", "onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mTrackId = getArguments().getString(Constants.ARG_TRACK_ID);
				mTrack = InBiciHelper.getTrack(mTrackId);
				// mTrackImageUrl =
				// getArguments().getString(Utils.ARG_TRACK_IMAGE_URL);
			}

		} else {
			Log.d("SCROLLTABS", "onCreate SUBSEQUENT TIME");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("SCROLLTABS", "onCreateView");

		return inflater.inflate(R.layout.fragment_track_container, container, false);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onStart");

		tabColor = Color.parseColor(getResources().getString(R.color.blue));

		tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
		mPager = (ViewPager) getActivity().findViewById(R.id.pager);
		mPagerAdapter = new TrackPagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		mPager.setPageMargin(pageMargin);

		tabs.setViewPager(mPager);
		tabs.setIndicatorColor(tabColor);
		tabs.setUnderlineColor(tabColor);
		tabs.setDividerColor(tabColor);

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		// MenuItem item = menu.add(1, R.id.start_training, Menu.NONE,
		// R.string.action_training_start);
		// item.setIcon(android.R.drawable.ic_media_play);
		// item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		getActivity().getMenuInflater().inflate(R.menu.track_detail_menu, menu);

		if (mTrack == null || mTrack.getLocation() == null
				|| (mTrack.getLocation()[0] == 0 && mTrack.getLocation()[1] == 0)) {
			menu.findItem(R.id.map_view).setVisible(false);
			menu.findItem(R.id.direction_action).setVisible(false);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			abActivity.finish();
			return true;
		} else if (item.getItemId() == R.id.map_view) {
			// map
			ArrayList<BaseDTObject> list = new ArrayList<BaseDTObject>();
			list.add(mTrack);
			MapManager.switchToMapView(list, this);
			return true;
		} else if (item.getItemId() == R.id.direction_action) {
			// directions
			Address to = Utils.getTrackAsGoogleAddress(mTrack);
			Address from = null;
			GeoPoint mylocation = MapManager.requestMyLocation(getActivity());
			if (mylocation != null) {
				from = new Address(Locale.getDefault());
				from.setLatitude(mylocation.getLatitudeE6() / 1E6);
				from.setLongitude(mylocation.getLongitudeE6() / 1E6);
			}
			InBiciHelper.bringmethere(getActivity(), from, to);
			return true;
		} else if (item.getItemId() == R.id.start_training) {
			// if no previous log, new training on this path
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

			if (!InBiciHelper.isStartedANewTrack(sp)) {
				InBiciHelper.addTrackIdFromSP(LoggerMapHelper.getPreferences(),mTrackId);
			}
			Intent intent = new Intent(getActivity(), MapQuestLoggerMap.class);
			startActivity(intent);
			return true;
		}

		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onPause");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onSaveInstanceState");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onStop");
		// abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "TrackContainerFragment --> onDetach");
	}

	/**
	 * Adapter for the home viewPager
	 */
	public class TrackPagerAdapter extends FragmentStatePagerAdapter {
		private final String[] mPagerTitles = { getResources().getString(R.string.track_info),
				getResources().getString(R.string.track_trainings) };

		public TrackPagerAdapter(FragmentManager fm) {
			super(fm);

			if (getCount() <= 3) {
				tabs.setShouldExpand(true);
			}

		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mPagerTitles[position];
		}

		@Override
		public int getCount() {
			return mPagerTitles.length;
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
			case 0:
				return TrackDetailsFragment.newInstance(getArguments().getString(Constants.ARG_TRACK_ID));
			case 1:
				return TrainingsFragment.newInstance(getArguments().getString(Constants.ARG_TRACK_ID));
			default:
				return null;

			}
		}

	}

}
