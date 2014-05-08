package eu.iescities.pilot.rovereto.inbici.entities.track.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;
import eu.iescities.pilot.rovereto.inbici.R;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.network.RemoteException;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class TrackDetailsInfoAdapter extends ArrayAdapter<TrackInfo> {

	private Context mContext;
	private int layoutResourceId;
	private String mTag;
	private String mTrackId;

	private View row = null;
	private TrackInfoChildViewHolder trackChildViewHolder = null;


	int matchStart=-1;
	int matchEnd=-1;
	String txt;
	String url;



	public TrackDetailsInfoAdapter(Context mContext, int layoutResourceId, String mTag, String mTrackId) {
		super(mContext, layoutResourceId);
		this.mContext = mContext;
		this.layoutResourceId = layoutResourceId;
		this.mTag = mTag;
		this.mTrackId = mTrackId;
	}

	@Override
	public void addAll(Collection<? extends TrackInfo> collection) {
		for (TrackInfo trackInfoItem: collection){
			add(trackInfoItem);
		}
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final TrackInfo trackInfo = getItem(position);

		row = convertView;

		if (row == null) {

			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			trackChildViewHolder = new TrackInfoChildViewHolder();

			trackChildViewHolder.textAttrName = (TextView) row
					.findViewById(R.id.track_info_attribute_name);

			trackChildViewHolder.textAttrVal = (TextView) row
					.findViewById(R.id.track_info_attribute_value);


			//			trackChildViewHolder.divider = (View) row
			//					.findViewById(R.id.event_info_item_divider);

			row.setTag(trackChildViewHolder);
		} else {
			trackChildViewHolder = (TrackInfoChildViewHolder) row.getTag();
		}


		Log.d("TRACKINFO", "EventDetailToKnowAdapter --> toKnow NAME: " + trackInfo.getName() );


		if (!trackInfo.getIsStatistics()){
			trackChildViewHolder.textAttrVal.setVisibility(View.INVISIBLE);
			txt = "<b>" + trackInfo.getName() + ": </b>" + ((trackInfo.getValue()!=null) ? trackInfo.getValue() : ""); 
			trackChildViewHolder.textAttrName.setText(txt);
		}
		else{
			trackChildViewHolder.textAttrVal.setVisibility(View.VISIBLE);
			trackChildViewHolder.textAttrVal.setText(trackInfo.getValue());
			txt = "<b>" + trackInfo.getName() + "</b>";
		}


		trackChildViewHolder.textAttrName.setMovementMethod(LinkMovementMethod.getInstance());
		trackChildViewHolder.textAttrName.setText(Html.fromHtml(txt), TextView.BufferType.SPANNABLE);
		Spannable mySpannable = (Spannable)trackChildViewHolder.textAttrName.getText();

		Object[] urlAndIndex = extractUrlFromText(trackChildViewHolder.textAttrName.getText().toString());

		if (urlAndIndex[0]!=null){
			//set clickable url link
			url = (String) urlAndIndex[0];

			ClickableSpan myClickableSpan = new ClickableSpan()
			{
				@Override
				public void onClick(View widget) { 
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(url)); 
					mContext.startActivity(i); 

				}
			};
			mySpannable.setSpan(myClickableSpan, (Integer) urlAndIndex[1], (Integer) urlAndIndex[2], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}




		// set icon on the left side
		if (trackInfo.getLeftIconId() != -1) {
			trackChildViewHolder.textAttrName.setCompoundDrawablesWithIntrinsicBounds(trackInfo.getLeftIconId(), 0, 0, 0);
		}else
			trackChildViewHolder.textAttrName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


		//set divider line height and color
		//		trackChildViewHolder.divider.setBackgroundColor(mContext.getResources().getColor(trackInfo.getDividerColor()));
		//
		//		trackChildViewHolder.divider.setLayoutParams(new LinearLayout.LayoutParams(
		//				LinearLayout.LayoutParams.MATCH_PARENT,
		//				trackInfo.getDividerHeight()));

		return row;
	}


	private  Object[] extractUrlFromText(String txt) {

		Object[] urlAndIndex= new Object[3];


		// Pattern for recognizing a URL, based off RFC 3986
		Pattern urlPattern = Pattern.compile(
				"(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
						+ "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
						+ "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
						Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


		Log.d("TRACKINFO", "EventDetailToKnowAdapter --> TXT: " + txt);

		//url extraction
		Matcher matcher = urlPattern.matcher(txt);
		while (matcher.find()) {
			urlAndIndex[1] = matcher.start(1);
			urlAndIndex[2]= matcher.end();
			//			Log.d("TRACKINFO", "EventDetailToKnowAdapter --> matchStart: " + urlAndIndex[1]);
			//			Log.d("TRACKINFO", "EventDetailToKnowAdapter --> matchEnd: " + urlAndIndex[2]);
			// now you have the offsets of a URL match
			urlAndIndex[0]=txt.substring((Integer)urlAndIndex[1], (Integer) urlAndIndex[2]);
			//			Log.d("TRACKINFO", "EventDetailToKnowAdapter --> SUB TXT: " + urlAndIndex[0]);
		}

		return urlAndIndex;


	}


	private static class TrackInfoChildViewHolder {
		TextView textAttrName;
		TextView textAttrVal;
		View divider;
		int position;
	}




}
