package eu.iescities.pilot.rovereto.inbici.entities.event.info;

import android.view.View;
import eu.iescities.pilot.rovereto.inbici.R;


public class EventInfoChild{
	
	
	
	private String name;
	
	private String text;
	
	private int type; 
	
	private int leftIconId = -1;
	
	private int[] rightIconIds = null;
	
	private int divider_height=1;
	private int divider_color = R.color.jungle_green;
	private boolean textInBold = false;
	

	
	
	
	
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type= type;
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

	

	
	
}
