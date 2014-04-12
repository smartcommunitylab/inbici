package eu.iescities.pilot.rovereto.inbici.ui.navdrawer;

import eu.iescities.pilot.rovereto.inbici.resources.ResourceUtils;
import android.content.Context;
import android.util.Log;


public class NavMenuItem implements NavDrawerItem {

	public static final int ITEM_TYPE = 1 ;
	
	private int id ;
	
	private String label ;
	
	private String actionbarTitle;

	
	private int icon ;
	
	private boolean updateActionBarTitle ;

    private boolean checkable ;

	private NavMenuItem() {
	}

	public static NavMenuItem create( int id, String label, int iconID, boolean updateActionBarTitle, boolean checkable, Context context ) {
		NavMenuItem item = new NavMenuItem();
		item.setId(id);
		item.setLabel(label);
		item.setIcon(iconID);
		item.setUpdateActionBarTitle(updateActionBarTitle);
        item.setCheckable(checkable);
		return item;
	}
	
	
	public static NavMenuItem create( int id, String label, String abTitle, int iconID, boolean updateActionBarTitle, boolean checkable, Context context ) {
		Log.i("NAVDRAWER","init navmenuitem create");

		NavMenuItem item = new NavMenuItem();
		item.setId(id);
		item.setLabel(label);
		item.setActionBarTitle(abTitle);
		item.setIcon(iconID);
		item.setUpdateActionBarTitle(updateActionBarTitle);
        item.setCheckable(checkable);
		return item;
	}
	
	@Override
	public int getType() {
		return ITEM_TYPE;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getActionBarTitle() {
		Log.i("AB TITLE", "NavMenuItem.getActionBarTitle: " + actionbarTitle);
		return actionbarTitle;
	}

	public void setActionBarTitle(String actionbarTitle) {
		this.actionbarTitle = actionbarTitle;
	}
	
	

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	@Override
	public boolean updateActionBarTitle() {
		return this.updateActionBarTitle;
	}

	public void setUpdateActionBarTitle(boolean updateActionBarTitle) {
		this.updateActionBarTitle = updateActionBarTitle;
	}

    public boolean isCheckable() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }
}
