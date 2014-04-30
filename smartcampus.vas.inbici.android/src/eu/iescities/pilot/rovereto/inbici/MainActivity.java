package eu.iescities.pilot.rovereto.inbici;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.inbici.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.inbici.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.entities.track.TrackListingFragment;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.ControlTracking;
import eu.iescities.pilot.rovereto.inbici.entities.track.logger.GoogleLoggerMap;
import eu.iescities.pilot.rovereto.inbici.map.MapFragment;
import eu.iescities.pilot.rovereto.inbici.ui.navdrawer.AbstractNavDrawerActivity;
import eu.iescities.pilot.rovereto.inbici.ui.navdrawer.NavDrawerActivityConfiguration;
import eu.iescities.pilot.rovereto.inbici.ui.navdrawer.NavDrawerAdapter;
import eu.iescities.pilot.rovereto.inbici.ui.navdrawer.NavDrawerItem;
import eu.iescities.pilot.rovereto.inbici.ui.navdrawer.NavMenuItem;
import eu.iescities.pilot.rovereto.inbici.ui.navdrawer.NavMenuSection;
import eu.trentorise.smartcampus.ac.AACException;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.android.common.GlobalConfig;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class MainActivity extends AbstractNavDrawerActivity {

	public static final String TAG_FRAGMENT_MAP = "fragmap";
	public static final String TAG_FRAGMENT_TRACK_LIST = "fragtrack";


	private FragmentManager mFragmentManager;

	private boolean isLoading;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Log.i("AB TITLE", "MainActivity start on create!!!");
		
		mFragmentManager = getSupportFragmentManager();
		

		if (signedIn()) {
			initDataManagement();
		}



		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// this is a class created to avoid an Android bug
		// see the class for further infos.
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				mDrawerLayout.bringChildToFront(drawerView);
				mDrawerLayout.requestLayout();
			}

		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	protected boolean signedIn() {
		SCAccessProvider provider = SCAccessProvider.getInstance(this);
		try {
			if (provider.isLoggedIn(this)) {
				return true;
			}
			showLoginDialog(provider);
		} catch (AACException e) {
			e.printStackTrace();
			return false;
		}

		return false;
	}

	private void showLoginDialog(final SCAccessProvider accessprovider) {
		// dialogbox for registration
		DialogInterface.OnClickListener updateDialogClickListener;

		updateDialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					try {
						accessprovider.login(MainActivity.this, null);
						break;
					} catch (AACException e) {

						e.printStackTrace();
					}
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage(getString(R.string.auth_required))
		.setPositiveButton(android.R.string.yes, updateDialogClickListener)
		.setNegativeButton(android.R.string.no, updateDialogClickListener).show();
	}

	private void initDataManagement() {
		try {

			initGlobalConstants();

			try {
				// if (!SCAccessProvider.getInstance(this).login(this, null)) {
				InBiciHelper.init(getApplicationContext());
				initData();
				// }

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
				finish();
			}

		} catch (Exception e) {
			Toast.makeText(this, R.string.app_failure_init, Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return;
		}
	}

	private void initGlobalConstants() throws NameNotFoundException, NotFoundException {
		GlobalConfig.setAppUrl(this, getResources().getString(R.string.smartcampus_app_url));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SCAccessProvider.SC_AUTH_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String token = data.getExtras().getString(AccountManager.KEY_AUTHTOKEN);
				if (token == null) {
					Toast.makeText(this, R.string.app_failure_security, Toast.LENGTH_LONG).show();
					finish();
				} else {
					initDataManagement();
				}
			} else if (resultCode == RESULT_CANCELED && requestCode == SCAccessProvider.SC_AUTH_ACTIVITY_REQUEST_CODE) {
				InBiciHelper.endAppFailure(this, R.string.app_failure_security);
			}
		}
	}

	private boolean initData() {
		try {
			// to start with the map.
			mFragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment(), TAG_FRAGMENT_MAP)
			.commit();
			new SCAsyncTask<Void, Void, BaseDTObject>(this, new LoadDataProcessor(this)).execute();
		} catch (Exception e1) {
			Toast.makeText(this, R.string.app_failure_init, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private class LoadDataProcessor extends AbstractAsyncTaskProcessor<Void, BaseDTObject> {

		private int syncRequired = 0;
		private FragmentActivity currentRootActivity = null;

		public LoadDataProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public BaseDTObject performAction(Void... params) throws SecurityException, Exception {

			Exception res = null;

			try {
				syncRequired = InBiciHelper.SYNC_REQUIRED;// DTHelper.syncRequired();
			} catch (Exception e) {
				res = e;
			}

			if (res != null) {
				throw res;
			}
			return null;
		}

		@Override
		public void handleResult(BaseDTObject result) {
			if (syncRequired != InBiciHelper.SYNC_NOT_REQUIRED) {
				Log.d("MAP", "Main Activity--> syncRequired != DTHelper.SYNC_NOT_REQUIRED");

				if (syncRequired == InBiciHelper.SYNC_REQUIRED_FIRST_TIME) {
					Log.d("MAP", "Main Activity--> SYNC_REQUIRED_FIRST_TIME");
					Toast.makeText(MainActivity.this, R.string.initial_data_load, Toast.LENGTH_LONG).show();
				}
				setSupportProgressBarIndeterminateVisibility(true);
				isLoading = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Log.d("MAP", "Main Activity--> currentRootActivity");
							currentRootActivity = InBiciHelper.start(MainActivity.this);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (currentRootActivity != null) {
								Log.d("MAP", "Main Activity--> currentRootActivity != null");
								currentRootActivity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										currentRootActivity.setProgressBarIndeterminateVisibility(false);
										if (MainActivity.this != null) {
											MainActivity.this.setSupportProgressBarIndeterminateVisibility(false);
										}
										isLoading = false;
									}
								});
							}
						}
					}
				}).start();
			} else {
				setSupportProgressBarIndeterminateVisibility(false);
				// DTHelper.activateAutoSync();
			}
		}
	}

	//	/**
	//	 * @param items
	//	 *            where to put elements
	//	 * @param ids
	//	 *            array of arrays made in xml
	//	 */
	//	private ArrayList<NavDrawerItem> getMenuItems(int... ids) {
	//
	//		ArrayList<NavDrawerItem> menu_items = new ArrayList<NavDrawerItem>();
	//
	//		menu_items.add(NavMenuSection.create(0, "Eventi"));
	//	
	//		String[] labels = getResources().getStringArray(ids[0]);
	//		String[] abTitles = getResources().getStringArray(ids[2]);
	//
	//		TypedArray drawIds = getResources().obtainTypedArray((ids[1]));
	//		
	//		for (int j = 0; j < labels.length; j++) {
	//			int imgd = drawIds.getResourceId(j, -1);
	//			menu_items.add(NavMenuItem.create(j + 1, labels[j], abTitles[j], ((imgd != -1) ? imgd : null), true, false,
	//					this));
	//		}
	//		drawIds.recycle();
	//		return menu_items;
	//	}


	/**
	 * @param items
	 *            where to put elements
	 * @param ids
	 *            array of arrays made in xml
	 */
	private ArrayList<NavDrawerItem> getMenuItems(int... ids) {

		ArrayList<NavDrawerItem> menu_items = new ArrayList<NavDrawerItem>();

		menu_items.add(NavMenuSection.create(0, "Percorsi"));

		String[] labels = getResources().getStringArray(ids[0]);
		String[] abTitles = getResources().getStringArray(ids[2]);

		TypedArray drawIds = getResources().obtainTypedArray((ids[1]));

		for (int j = 0; j < labels.length; j++) {
			int imgd = drawIds.getResourceId(j, -1);
			menu_items.add(NavMenuItem.create(j+1, labels[j], abTitles[j], ((imgd != -1) ? imgd : null), true, false,
					this));
		}


		menu_items.add(NavMenuSection.create(4, "Allenamento"));

		labels = getResources().getStringArray(ids[3]);
		abTitles = getResources().getStringArray(ids[5]);
		drawIds = getResources().obtainTypedArray((ids[4]));



		for (int j = 0; j < labels.length; j++) {
			int imgd = drawIds.getResourceId(j, -1);
			menu_items.add(NavMenuItem.create(j + 5, labels[j], abTitles[j], ((imgd != -1) ? imgd : null), true, false,
					this));
		}

		drawIds.recycle();


		return menu_items;
	}



	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {

		NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
		navDrawerActivityConfiguration.setMainLayout(R.layout.activity_main);
		navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
		navDrawerActivityConfiguration.setLeftDrawerId(R.id.drawer_wrapper);
		navDrawerActivityConfiguration.setLeftDrawerListId(R.id.left_drawer_list);


		navDrawerActivityConfiguration.setDrawerShadow(R.drawable.drawer_shadow);
		navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
		navDrawerActivityConfiguration.setDrawerCloseDesc(R.string.drawer_close);


		ArrayList<NavDrawerItem> menu_items = getMenuItems(R.array.drawer_items_paths_labels, R.array.drawer_items_paths_icons,
				R.array.drawer_items_actionbar_paths_titles, R.array.drawer_items_training_labels, R.array.drawer_items_training_icons, 
				R.array.drawer_items_actionbar_training_titles);

		navDrawerActivityConfiguration.setMenuItems(menu_items);

		navDrawerActivityConfiguration.setBaseAdapter(new NavDrawerAdapter(this, R.layout.navdrawer_item, menu_items));

		navDrawerActivityConfiguration.setDrawerIcon(R.drawable.ic_drawer);

		return navDrawerActivityConfiguration;
	}



	@Override
	protected void onNavItemSelected(int id) {

		Log.i("NAVDRAWER","start onNavItemSelected");

		Object[] objects = getFragmentAndTag(id);
		// can't replace the current fragment with nothing or with one of the
		// same type
		if (objects != null) {
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.setCustomAnimations(R.anim.enter, R.anim.exit);
			ft.replace(R.id.content_frame, (Fragment) objects[0], objects[1].toString());
			// ft.addToBackStack(objects[1].toString());
			mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			ft.commit();
		}
	}


	public void navDrawerOutItemClick(View v) {
		if (v.getId() == R.id.nav_drawer_map_tv) {
			Log.i("NAVDRAWER","clicked on the map!!");
			onNavItemSelected(-1);
			mDrawerLayout.closeDrawers();

		} 
		//		if (v.getId() == R.id.nav_drawer_info) {
		//			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
		//					Uri.parse(getString(R.string.trentinofamiglia_url_credits)));
		//			startActivity(browserIntent);	} 

	}


	private Object[] getFragmentAndTag(int pos_in_list) {

		Object[] out = new Object[2];
		String cat = null;
		Bundle args = new Bundle();
		Fragment f = null;
		String tag = null;

		if (pos_in_list == -1) { // map
			out = new Object[2];
			f = new MapFragment();
			tag = TAG_FRAGMENT_MAP;
		}else{
			switch (pos_in_list) {
			case 0: // click on "Percorsi" section
				Log.i("NAVDRAWER","clicked header Percorsi");
				return null;
			case 1: // click on "I miei percorsi" item
				f = new TrackListingFragment();
				args.putString(TrackListingFragment.ARG_CATEGORY, CategoryHelper.TYPE_MY);
				tag = TAG_FRAGMENT_TRACK_LIST;
				Log.i("NAVDRAWER","clicked I miei Percorsi");
				break;

			case 2: // click on "Ciclabili provinciali" item
				Log.i("NAVDRAWER","clicked Ciclabili provinciali");
				f = new TrackListingFragment();
				args.putString(TrackListingFragment.ARG_CATEGORY, CategoryHelper.TYPE_OFFICIAL);
				tag = TAG_FRAGMENT_TRACK_LIST;
				break;
			case 3: // click on "Di altri utenti" item
				f = new TrackListingFragment();
				args.putString(TrackListingFragment.ARG_CATEGORY, CategoryHelper.TYPE_USER);
				tag = TAG_FRAGMENT_TRACK_LIST;
				Log.i("NAVDRAWER","clicked Di altri utenti");
				break;
			case 4: // click on "Allenamento" section
				Log.i("NAVDRAWER","clicked header Allenamento");
				return null;
			case 5: // click on "Svago" item
				Log.i("NAVDRAWER","clicked on Inizia ora");
	            Intent intent = new Intent(this, GoogleLoggerMap.class);
	            startActivity(intent);
				return null;
			default:
				return null;
			}
		}
		if (f != null){
			out[0] = f;
			f.setArguments(args);
			out[1] = tag;
		}
		return out;
		//return null;
	}

	// to handle action bar menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("MENU", "start on Create Options Menu MAIN frag");
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.global_menu, menu);

		// if (listmenu) {
		// Log.i("MENU", "ITEM 0" + menu.getItem(0).toString());
		// menu.getItem(0).setVisible(false);
		// }

		// else {
		// Log.i("MENU", "ITEM 1" + menu.getItem(1).toString());
		// menu.getItem(1).setVisible(false);
		// }
		// super.onCreateOptionsMenu(menu, inflater);

		return true;
	}

	@Override
	public void onBackPressed() {

		Log.i("BACKPRESSED", "MainActivity --> OnBackPressed ");

		// See bug:
		// http://stackoverflow.com/questions/13418436/android-4-2-back-stack-behaviour-with-nested-fragments/14030872#14030872
		// If the fragment exists and has some back-stack entry
		FragmentManager fm = getSupportFragmentManager();
		Fragment currentFragment = fm.findFragmentById(R.id.content_frame);
		if (currentFragment != null && currentFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
			// Get the fragment fragment manager - and pop the backstack
			currentFragment.getChildFragmentManager().popBackStack();
		}
		// Else, nothing in the direct fragment back stack
		else {

			Log.i("BACKPRESSED", "MainActivity --> current fragment: " + currentFragment.getTag() + "!");

			if (!this.TAG_FRAGMENT_MAP.equals(currentFragment.getTag()))
				this.setTitleWithDrawerTitle();

			super.onBackPressed();

		}
	}

	/*
	 * public void goHomeFragment( AbstractNavDrawerActivity activity) {
	 * activity.getSupportFragmentManager().beginTransaction()
	 * .replace(R.id.content_frame, new MainFragment(),
	 * HOME_FRAGMENT_TAG).commit(); activity.setTitleWithDrawerTitle(); }
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return false;
	}

}
