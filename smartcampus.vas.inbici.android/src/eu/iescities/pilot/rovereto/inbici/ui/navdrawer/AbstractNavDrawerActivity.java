package eu.iescities.pilot.rovereto.inbici.ui.navdrawer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.InBiciHelper;

public abstract class AbstractNavDrawerActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private ListView mDrawerList;
	
	private LinearLayout mDrawerLinearLayout;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private NavDrawerActivityConfiguration navConf ;

	private int lastItemChecked = 0 ;


	protected abstract NavDrawerActivityConfiguration getNavDrawerConfiguration();

	protected abstract void onNavItemSelected( int id );

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		navConf = getNavDrawerConfiguration();

		setContentView(navConf.getMainLayout()); 

		if ( savedInstanceState == null ) {
			mTitle = mDrawerTitle = getTitle();
		}
		else {
			mTitle = savedInstanceState.getCharSequence("title");
			mDrawerTitle = savedInstanceState.getCharSequence("drawerTitle");
			lastItemChecked = savedInstanceState.getInt("lastItemChecked");
			setTitle(mTitle);
		}

		mDrawerLayout = (DrawerLayout) findViewById(navConf.getDrawerLayoutId());
		
		mDrawerLinearLayout = (LinearLayout) findViewById(navConf.getLeftDrawerId());
		
		mDrawerList = (ListView) findViewById(navConf.getLeftDrawerListId());
		mDrawerList.setAdapter(navConf.getBaseAdapter());
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		this.initDrawerShadow();

		
		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				navConf.getDrawerIcon(),
				navConf.getDrawerOpenDesc(),
				navConf.getDrawerCloseDesc()
				) {
			
			
		    @Override
			public void onDrawerClosed(View view) {
				Log.i("NAVDRAWER","set close  drawer TITLE: " + mTitle);
				getSupportActionBar().setTitle(mTitle);
				//getActionBar().setTitle(mTitle);
				ActivityCompat.invalidateOptionsMenu(AbstractNavDrawerActivity.this);
			}

		    @Override
		    public void onDrawerOpened(View drawerView) {
				
				Log.i("NAVDRAWER","set open drawer TITLE: " + mDrawerTitle);
				
				getSupportActionBar().setTitle(mDrawerTitle);
				//getActionBar().setTitle(mDrawerTitle);

				ActivityCompat.invalidateOptionsMenu(AbstractNavDrawerActivity.this);
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		
	}
	
	

	protected void initDrawerShadow() {
		mDrawerLayout.setDrawerShadow(navConf.getDrawerShadow(), GravityCompat.START);
	}

	
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		
		Log.i("NAVDRAWER","onPostCreate");

		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("NAVDRAWER","onConfigurationChanged");

		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	
		
		

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		Log.i("NAVDRAWER","onPrepareOptionsMenu");

		Log.i("MENU", "start on Prepare Options Menu MAIN frag: " + menu.toString());
		

		if ( navConf.getActionMenuItemsToHideWhenDrawerOpen() != null ) {
			//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLinearLayout);
			for( int iItem : navConf.getActionMenuItemsToHideWhenDrawerOpen()) {
				menu.findItem(iItem).setVisible(!drawerOpen);
			}
		}
		
		//MenuItem ovItem = menu.add("overflow");
		//ovItem.setIcon(getResources().getDrawable(R.drawable.ic_action_overflow));
		//ovItem.setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Log.i("NAVDRAWER","onOptionsItemSelected");

		Log.i("MENU", "start on Option Item Selected MAIN frag");

		 // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			Log.i("MENU", "ho cliccato su overflow 2");
			return true;
		}
		else {
			Log.i("MENU", "clicked here");
			
			return false;
		}
	} 

	
/*	//Handling the action bar menu
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.global_menu, menu);
		    return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
			Log.i("MENU", "ho cliccato su overflow 1");
			
			switch (item.getItemId()) {
		        case R.id.mainmenu_toggler:
		        	 Toast.makeText(getBaseContext(), "you pressed the overflow", Toast.LENGTH_LONG)
	                 .show();
		            return true;
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		} */
	
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("NAVDRAWER","onKeyDown");

		if ( keyCode == KeyEvent.KEYCODE_MENU ) {
			//if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
				if ( this.mDrawerLayout.isDrawerOpen(mDrawerLinearLayout)) {
				//this.mDrawerLayout.closeDrawer(this.mDrawerList);
				//this.mDrawerLayout.closeDrawer(findViewById(R.id.drawer_wrapper));
				this.mDrawerLayout.closeDrawer(mDrawerLinearLayout);
			}
			else {
				//this.mDrawerLayout.openDrawer(this.mDrawerList);
				this.mDrawerLayout.openDrawer(mDrawerLinearLayout);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	protected ActionBarDrawerToggle getDrawerToggle() {
		return mDrawerToggle;
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	} 

	public void selectItem(int position) {

		Log.i("NAVDRAWER","selectItem");

		
		Log.i("AB TITLE", "AbstractNavDrawerActivity.selectItem");
		
		//NavDrawerItem selectedItem = navConf.getNavItems()[position];
		NavDrawerItem selectedItem = navConf.getMenuItems().get(position);


		this.onNavItemSelected(selectedItem.getId());

		if ( selectedItem.isCheckable()) {
			mDrawerList.setItemChecked(position, true);
			lastItemChecked = position ;
		}
		else {
			mDrawerList.setItemChecked(position, false);
			if ( lastItemChecked != 0 ) {
				mDrawerList.setItemChecked(lastItemChecked, true);
			}
		}

		if ( selectedItem.updateActionBarTitle()) {
			NavMenuItem nmi = (NavMenuItem) selectedItem;
			Log.i("AB TITLE", "update action bar title: " + nmi.getActionBarTitle());
			setTitle(nmi.getActionBarTitle());
		}

		//if ( this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
			if ( this.mDrawerLayout.isDrawerOpen(mDrawerLinearLayout)) {

			//mDrawerLayout.closeDrawer(mDrawerList);
			this.mDrawerLayout.closeDrawer(mDrawerLinearLayout);

		}
	} 

	@Override
	public void setTitle(CharSequence title) {
		Log.i("AB TITLE", "SET TITLE: " + title);
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	public void setTitleWithDrawerTitle() {
		Log.i("AB TITLE", "SET DRAWER TITLE: " + mDrawerTitle);
		setTitle(mDrawerTitle);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i("NAVDRAWER","onSaveInstanceState");

		super.onSaveInstanceState(outState);
		outState.putCharSequence("title", this.mTitle);
		outState.putCharSequence("drawerTitle", this.mDrawerTitle);
		outState.putInt("lastItemChecked", this.lastItemChecked);
	}


	@Override
	protected void onResume() {
		if (InBiciHelper.getLocationHelper() != null)
			InBiciHelper.getLocationHelper().start();
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (InBiciHelper.getLocationHelper() != null)
			InBiciHelper.getLocationHelper().stop();
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		InBiciHelper.destroy();
		super.onDestroy();
	}





}
