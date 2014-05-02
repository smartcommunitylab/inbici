package eu.iescities.pilot.rovereto.inbici.entities.track.info;

import java.util.List;

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;


public class TrackInfo {

	private String name;
	
	//it defines whether the field is an attribute (e.g., abbigliamento consigliato) or a value (e.g., sport clothes)
	private String value;
	
	
	private int leftIconId = -1;
	
	
	private int divider_height=1;
	
	private int divider_color = R.color.jungle_green;
	
	
	private boolean isStatistics=false;
	

	private boolean textInBold = false;
	private int default_text_color = R.color.black_background;
	
	
	


	public TrackInfo(String name, String value) {
		setName(name);
		setValue(value);
	}
	
	
	
	public TrackInfo(String name, String value, boolean isStatistics) {
		setName(name);
		setValue(value);
		setIsStatistics(isStatistics);
	}
	
	

	public TrackInfo (String name, String value, boolean isStatistics, int icon_id){
		setName(name);
		setValue(value);
		setIsStatistics(isStatistics);
		setLeftIconId(icon_id);
	}

	public TrackInfo (String name, String value, boolean isStatistics, int icon_id, int divider_height, int divider_color){
		setName(name);
		setValue(value);
		setIsStatistics(isStatistics);
		setLeftIconId(icon_id);
		setDividerHeight(divider_height);
		setDividerColor(divider_color);
	}

	
	
	public boolean getIsStatistics() {
		return isStatistics;
	}



	public void setIsStatistics(boolean isStatistics) {
		this.isStatistics = isStatistics;
	}

	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getValue() {
		return value;
	}


	public void setValue(String type) {
		this.value = type;
	}



	public int getLeftIconId()
	{
		return leftIconId;
	}
	
	public void setLeftIconId(int leftIconId)
	{
		this.leftIconId = leftIconId;
	}
	
	
	
	
	public int getDividerColor()
	{
		return divider_color;
	}
	
	public void setDividerColor(int divider_colorId)
	{
		this.divider_color = divider_colorId;
	}
	
	
	public int getDividerHeight()
	{
		return divider_height;
	}
	
	public void setDividerHeight(int divider_height)
	{
		this.divider_height = divider_height;
	}
	
	
	public boolean getTextInBold()
	{
		return textInBold;
	}
	
	
	public void setTextInBold(boolean text_in_bold)
	{
		this.textInBold = text_in_bold;
	}

	
	
	

	
	public int getDefault_text_color() {
		return default_text_color;
	}


	public void setDefault_text_color(int default_text_color) {
		this.default_text_color = default_text_color;
	}
	
	


	

	
	
	
}
