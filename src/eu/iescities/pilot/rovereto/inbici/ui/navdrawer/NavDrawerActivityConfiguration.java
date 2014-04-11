package eu.iescities.pilot.rovereto.inbici.ui.navdrawer;

import java.util.ArrayList;

import android.widget.BaseAdapter;

public class NavDrawerActivityConfiguration {

	private int mainLayout;
	private int drawerShadow;
	private int drawerLayoutId;
	
	private int leftDrawerId;
	private int leftDrawerListId;
	
	private int[] actionMenuItemsToHideWhenDrawerOpen;
	private NavDrawerItem[] navItems;
	private ArrayList<NavDrawerItem> menuItems;

	private int drawerOpenDesc;
	private int drawerCloseDesc;
    private int drawerIcon ;
    private BaseAdapter baseAdapter;

	public int getMainLayout() {
		return mainLayout;
	}

	public void setMainLayout(int mainLayout) {
		this.mainLayout = mainLayout;
	}

	public int getDrawerShadow() {
		return drawerShadow;
	}

	public void setDrawerShadow(int drawerShadow) {
		this.drawerShadow = drawerShadow;
	}

	public int getDrawerLayoutId() {
		return drawerLayoutId;
	}

	public void setDrawerLayoutId(int drawerLayoutId) {
		this.drawerLayoutId = drawerLayoutId;
	}

	public int getLeftDrawerId() {
		return leftDrawerId;
	}

	public void setLeftDrawerId(int leftDrawerId) {
		this.leftDrawerId = leftDrawerId;
	}
	
	
	public int getLeftDrawerListId() {
		return leftDrawerListId;
	}

	public void setLeftDrawerListId(int leftDrawerListId) {
		this.leftDrawerListId = leftDrawerListId;
	}


	public int[] getActionMenuItemsToHideWhenDrawerOpen() {
		return actionMenuItemsToHideWhenDrawerOpen;
	}

	public void setActionMenuItemsToHideWhenDrawerOpen(
			int[] actionMenuItemsToHideWhenDrawerOpen) {
		this.actionMenuItemsToHideWhenDrawerOpen = actionMenuItemsToHideWhenDrawerOpen;
	}

	
	public NavDrawerItem[] getNavItems() {
		return navItems;
	}

	public void setNavItems(NavDrawerItem[] navItems) {
		this.navItems = navItems;
	}

	
	public ArrayList<NavDrawerItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(ArrayList<NavDrawerItem> menuItems) {
		this.menuItems = menuItems;
	}
	
	
	
	public int getDrawerOpenDesc() {
		return drawerOpenDesc;
	}

	public void setDrawerOpenDesc(int drawerOpenDesc) {
		this.drawerOpenDesc = drawerOpenDesc;
	}

	public int getDrawerCloseDesc() {
		return drawerCloseDesc;
	}

	public void setDrawerCloseDesc(int drawerCloseDesc) {
		this.drawerCloseDesc = drawerCloseDesc;
	}

	public BaseAdapter getBaseAdapter() {
		return baseAdapter;
	}

	public void setBaseAdapter(BaseAdapter baseAdapter) {
		this.baseAdapter = baseAdapter;
	}

    public int getDrawerIcon() {
        return drawerIcon;
    }

    public void setDrawerIcon(int drawerIcon) {
        this.drawerIcon = drawerIcon;
    }
}
