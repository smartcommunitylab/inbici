package eu.iescities.pilot.rovereto.inbici.entities.event.info;

import java.util.ArrayList;

public class EventInfoParent
{
	private String name;
	private String text;

	private String checkedtype;
	
	private boolean checked;
	private ArrayList<EventInfoChild> children;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getText1()
	{
		return text;
	}
	
	public void setText1(String text1)
	{
		this.text = text1;
	}
	
	
	public String getCheckedType()
	{
		return checkedtype;
	}
	
	public void setCheckedType(String checkedtype)
	{
		this.checkedtype = checkedtype;
	}
	
	
	public boolean isChecked()
	{
		return checked;
	}
	public void setChecked(boolean checked)
	{
		this.checked = checked;
	}
	
	public ArrayList<EventInfoChild> getChildren()
	{
		return children;
	}
	
	public void setChildren(ArrayList<EventInfoChild> children)
	{
		this.children = children;
	}
}

