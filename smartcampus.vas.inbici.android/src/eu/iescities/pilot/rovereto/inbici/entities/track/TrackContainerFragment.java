package eu.iescities.pilot.rovereto.inbici.entities.track;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.MapQuestLoggerMap;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.OsmLoggerMap;

public class TrackContainerFragment extends Fragment {

	private String[] mPagerTitles;
	private ViewPager mPager;
	private TrackPagerAdapter mPagerAdapter;
	private ActionBarActivity abActivity = null;
	private static String trackId = null;
	
	public static TrackContainerFragment newInstance(String id) {
		TrackContainerFragment fragment = new TrackContainerFragment();
		Bundle args = new Bundle();
		args.putString(TrackDetailsFragment.ARG_TRACK_ID, id);
		fragment.setArguments(args);
		trackId = id;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mPagerTitles = new String[] { getResources().getString(R.string.track_info), getResources().getString(R.string.track_trainings)};
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		abActivity = (ActionBarActivity) getActivity();
		abActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		abActivity.getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		MenuItem item = menu.add(1, R.id.start_training, Menu.NONE, R.string.action_training_start);
		item.setIcon(android.R.drawable.ic_media_play);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			abActivity.finish();
			return true;
		} else if (item.getItemId() == R.id.start_training) {
//			Object[] out = new Object[2];
//			String cat = null;
//			Bundle args = new Bundle();
//			Fragment f = null;
//			String tag = null;
			//write on sharedpreferences
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			Editor editor = sp.edit();
			editor.putString(InBiciHelper.TRACK_IDENTIFICATOR, trackId);
			if (editor.commit())
			{
				Log.v("trackcontainer", "wrote");
			}
			///non lo scrive ???????
			Intent intent = new Intent(getActivity(), MapQuestLoggerMap.class);
	         startActivity(intent);
//			abActivity.finish();
			return true;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_track_container, container, false);

		mPager = (ViewPager) viewGroup.findViewById(R.id.pager);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between pages, select the
				// corresponding tab.
				abActivity.getSupportActionBar().setSelectedNavigationItem(position);
			}
		});
		return viewGroup;
	}

	@Override
	public void onStart() {
		super.onStart();
		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mPagerAdapter = new TrackPagerAdapter(getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		// Create a tab listener that is called when the user changes tabs.
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				mPager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}

			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}
		};
		abActivity.getSupportActionBar().removeAllTabs();
		for (int i = 0; i < mPagerTitles.length; i++) {
			abActivity.getSupportActionBar().addTab(
					abActivity.getSupportActionBar().newTab().setText(mPagerTitles[i]).setTag("contacts" + i)
							.setTabListener(tabListener));
		}

		mPager.setCurrentItem(0);
	}

	@Override
	public void onStop() {
		super.onStop();
		abActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	/**
	 * Adapter for the home viewPager
	 */
	private class TrackPagerAdapter extends FragmentStatePagerAdapter {
		public TrackPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return TrackDetailsFragment.newInstance(getArguments().getString(TrackDetailsFragment.ARG_TRACK_ID));
			case 1:
				return TrainingsFragment.newInstance(getArguments().getString(TrackDetailsFragment.ARG_TRACK_ID));
			}
			return null;
		}

		@Override
		public int getCount() {
			return mPagerTitles.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mPagerTitles[position];
		}
	}

}