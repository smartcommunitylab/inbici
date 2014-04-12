package eu.iescities.pilot.rovereto.inbici.ui.navdrawer;

public class NavMenuSection implements NavDrawerItem {

	public static final int SECTION_TYPE = 0;

	private int id;
	
	private String label;

	private NavMenuSection() {
		
	}
	
	public static NavMenuSection create( int id, String label ) {
		NavMenuSection section = new NavMenuSection();
		section.setLabel(label);
		return section;
	}
	
	@Override
	public int getType() {
		return SECTION_TYPE;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean updateActionBarTitle() {
		return false;
	}

    @Override
    public boolean isCheckable() {
        return false;
    }
}
