package eu.iescities.pilot.rovereto.inbici.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;

import eu.iescities.pilot.rovereto.inbici.R;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.track.TrackObject;

//import eu.iescities.pilot.rovereto.inbici.entities.track.info.TrackInfo;

public class Utils {
	public static final DateFormat DATE_FORMAT_2_with_dayweek = new SimpleDateFormat("EEEEEE dd MMM. yyyy");
	public static final DateFormat DATE_FORMAT_TRAINIGN = new SimpleDateFormat("dd-MM-yy");

	public static final int PAST_MINUTES_SPAN = -5; // has to be negative

	public static final String ARG_TRACK_ID = "track_id";

	public static List<LatLng> decodePolyline(String encoded) {
		List<LatLng> polyline = new ArrayList<LatLng>();

		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			if (index >= len) {
				break;
			}
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			polyline.add(p);
		}
		return polyline;
	}

	public static Long toDateTimeLong(SimpleDateFormat sdf, String date_string) {
		Date date;
		Long mills = null;
		try {
			date = (Date) sdf.parse(date_string);
			mills = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mills;
	}

	// public static List<ToKnow> toKnowMapToList(Map<String, String> map) {
	// List<ToKnow> list = new ArrayList<ToKnow>();
	//
	// for (Entry<String, String> entry : map.entrySet()) {
	// ToKnow toKnow = new ToKnow(entry.getKey(), entry.getValue());
	// list.add(toKnow);
	// }
	//
	// return list;
	// }

	public static void hideKeyboard(Activity activity) {
		// hide keyboard if it is still open
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(activity.findViewById(R.id.content_frame).getWindowToken(), 0);
	}

	/**
	 * @param mTrack
	 * @return
	 */
	public static Address getTrackAsGoogleAddress(TrackObject mTrack) {
		Address a = new Address(Locale.getDefault());
		a.setLatitude(mTrack.startingPoint().latitude);
		a.setLongitude(mTrack.startingPoint().longitude);
		a.setAddressLine(0, mTrack.getTitle());
		return a;
	}

	public static CharSequence setDateString(Long startTime) {

		return DATE_FORMAT_TRAINIGN.format(startTime);
	}

	public static CharSequence getTimeTrainingFormatted(long ms) {
		int SECOND = 1000;
		int MINUTE = 60 * SECOND;
		int HOUR = 60 * MINUTE;
		int DAY = 24 * HOUR;
		StringBuffer text = new StringBuffer("");
		if (ms > DAY) {
			text.append(ms / DAY).append(" d ");
			ms %= DAY;
		}
		if (ms > HOUR) {
			text.append(ms / HOUR).append(" h ");
			ms %= HOUR;
		}
		if (ms > MINUTE) {
			text.append(ms / MINUTE).append(" \' ");
			ms %= MINUTE;
		}
		if (ms > SECOND) {
			text.append(ms / SECOND).append(" \" ");
			ms %= SECOND;
		}
		text.append(ms + " ms");
		return text;
	}

}
