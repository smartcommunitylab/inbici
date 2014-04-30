package eu.iescities.pilot.rovereto.inbici.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class TrackUtils {
	
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
	
	
	private static StringBuffer encodeSignedNumber(int num) {
        int sgn_num = num << 1;
        if (num < 0) {
            sgn_num = ~(sgn_num);
        }
        return(encodeNumber(sgn_num));
    }

    private static StringBuffer encodeNumber(int num) {
        StringBuffer encodeString = new StringBuffer();
        while (num >= 0x20) {
                int nextValue = (0x20 | (num & 0x1f)) + 63;
                encodeString.append((char)(nextValue));
            num >>= 5;
        }
        num += 63;
        encodeString.append((char)(num));
        return encodeString;
    }
    
    /**
     * Encode a polyline with Google polyline encoding method
     * @param polyline the polyline
     * @param precision 1 for a 6 digits encoding, 10 for a 5 digits encoding. 
     * @return the encoded polyline, as a String
     */
    public static String encodePolyline(List<LatLng> polyline) {
                StringBuffer encodedPoints = new StringBuffer();
                int prev_lat = 0, prev_lng = 0;
                for (LatLng trackpoint:polyline) {
                        int lat = (int)(trackpoint.latitude*1E5);
                        int lng = (int)(trackpoint.longitude*1E5);
                        encodedPoints.append(encodeSignedNumber(lat - prev_lat));
                        encodedPoints.append(encodeSignedNumber(lng - prev_lng));                       
                        prev_lat = lat;
                        prev_lng = lng;
                }
                return encodedPoints.toString();
        }

}
