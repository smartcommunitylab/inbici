package eu.iescities.pilot.rovereto.inbici.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.inbici.R;

public class DrawerItemOld {
	public String text;
	public Drawable icon;

	public DrawerItemOld(String text, Drawable icon) {
		super();
		this.text = text;
		this.icon = icon;
	}
}
