package eu.iescities.pilot.rovereto.inbici.custom.data.model.event;

import java.util.List;

import eu.iescities.pilot.rovereto.inbici.custom.data.Constants;
import eu.iescities.pilot.rovereto.inbici.R;

public class ToKnow {

	private String name;
	
	//it defines whether the field is an attribute (e.g., abbigliamento consigliato) or a value (e.g., sport clothes)
	private String type;
	
	//it defines whether the field of type "attribute" allows for multiple values. If the field is of "value" type then this attribute is null.
	private Boolean multiValue = null;
	private Boolean addedbyUser = null;
	
	
	
	
	private int leftIconId = -1;
	
	private int[] rightIconIds = null;
	
	private int divider_height=1;
	
	private int divider_color = R.color.jungle_green;
	
	
	
	private boolean textInBold = false;
	private int default_text_color = R.color.black_background;
	
	
	


	public ToKnow() {	
	}
	
	
	public ToKnow(String name, String type) {
		setName(name);
		setType(type);
	}
	
	
	public ToKnow(String name, String type, Boolean multivalue) {
		setName(name);
		setType(type);
		setMultiValue(multivalue);
	}
	
	
	public Boolean getMultiValue() {
		return multiValue;
	}


	public void setMultiValue(Boolean multiValue) {
		this.multiValue = multiValue;
	}
	
	
	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}



	public int getLeftIconId()
	{
		return leftIconId;
	}
	
	public void setLeftIconId(int leftIconId)
	{
		this.leftIconId = leftIconId;
	}
	
	public int[] getRightIconIds()
	{
		return rightIconIds;
	}
	
	public void setRightIconIds(int[] rightIconIds)
	{
		this.rightIconIds = rightIconIds;
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

	
	
	public Boolean getAddedbyUser() {
		return addedbyUser;
	}


	public void setAddedbyUser(Boolean addedbyUser) {
		this.addedbyUser = addedbyUser;
	}

	
	public int getDefault_text_color() {
		return default_text_color;
	}


	public void setDefault_text_color(int default_text_color) {
		this.default_text_color = default_text_color;
	}
	
	public static ToKnow newCustomDataAttributeField(String name, Boolean addedByUser, int divider_height){

		ToKnow toKnow = new ToKnow(name, Constants.CUSTOM_TOKNOW_TYPE_ATTRIBUTE);

		if ((name.matches(Constants.CUSTOM_TOKNOW_LANGUAGE_MAIN)) || (name.matches(Constants.CUSTOM_TOKNOW_CLOTHING)) 
				|| (name.matches(Constants.CUSTOM_TOKNOW_TO_BRING)))
			toKnow.setMultiValue(true);
		else
			toKnow.setMultiValue(false);
		
		if (addedByUser){
			toKnow.setAddedbyUser(true);
			toKnow.setMultiValue(true);
		}else
			toKnow.setAddedbyUser(false);

		
		toKnow.setDividerHeight(divider_height);
		toKnow.setTextInBold(true);
		int[] rightIconIds1 = new int[] {R.drawable.ic_action_edit};
		toKnow.setRightIconIds(rightIconIds1);

		return toKnow;

	}


	public static ToKnow newCustomDataValueField(String name, int divider_height){

		ToKnow toKnow = new ToKnow(name, Constants.CUSTOM_TOKNOW_TYPE_VALUE);

		toKnow.setDividerHeight(divider_height);
		toKnow.setTextInBold(false);

		return toKnow;

	}
	
	
	
}
