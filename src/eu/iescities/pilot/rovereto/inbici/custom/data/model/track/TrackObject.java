package eu.iescities.pilot.rovereto.inbici.custom.data.model.track;

import java.io.Serializable;
import java.util.List;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import eu.iescities.pilot.rovereto.inbici.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.inbici.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.inbici.utils.TrackUtils;



public class TrackObject extends BaseDTObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3399886172947825575L;

	private transient List<LatLng> decodedLine = null;
	
	private String track;
	
	
	/* Official Data */
	
	//data taken from the province website http://www.ciclabili.provincia.tn.it/tracciati_ciclopedonali/
	
	private int track_lenght; //this is taken from the gpx file  
	private String track_lenght_descriptive; //this might be taken from the web (i.e., 	field "lunghezza" in http://www.ciclabili.provincia.tn.it/tracciati_ciclopedonali/-valle_adige/pagina25.html)
	private String average_travel_time;  // i.e.: "6h-7h"
	private String wind; //i.e.: "prevalentemente direzione sud al mattino, direzione nord al pomeriggio"
	private String altitude_gap; //i.e.: "non significativo (100 m in salita tra Pilcante S. Lucia direzione nord)"
	private String type_of_surface; // i.e.: "asfalto"
	private String crossing_with_other_paths; //i.e.: "a S. Michele all'Adige verso rete Piana rotaliana; a Mori con..."
	private String advised_season; //i.e.: "tutte"
	private String traffic; //i.e.: "limitato ai soli  attraversamenti di ponti o attraversamenti centri abitati"
	
	
	/* Total Statistical Data */
	//these data are computed by taking into account all the trainings on this track
	
	private long elapsed_time;
	private double traveled_distance;
	private double avg_speed;
	private double max_speed;
	private double total_elevation;
	
	
	/* Other Data */
	
	private int number_of_registered_uses;
	private String creator;
	private long last_training_date;
	
	
	public long getElapsed_time() {
		return elapsed_time;
	}

	public void setElapsed_time(long elapsed_time) {
		this.elapsed_time = elapsed_time;
	}

	public List<LatLng> decodedLine() {
		if (decodedLine == null) {
			decodedLine = TrackUtils.decodePolyline(track);
		}
		return decodedLine;
	}
	
	public LatLng startingPoint() {
		if (decodedLine() != null && !decodedLine.isEmpty()) {
			return decodedLine.get(0);
		}
		return null;
	}
	
	public double[] getLocation() {
		if (decodedLine() != null && !decodedLine.isEmpty()) {
			LatLng ll= decodedLine.get(0);
			return new double[]{ll.latitude,ll.longitude};
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}
	
	public String customDescription(Context ctx) {
		String d = getDescription();
		if (CategoryHelper.CAT_TRACK_PASSEGGIATE.equals(getType())) {
			String link = (String)getCustomData().get("link");
			if (link != null && link.length() > 0) {
				return d +"<br/>"+link;
			}
			
		}
		if (CategoryHelper.CAT_TRACK_PISTE_CICLOPEDONALI.equals(getType())) {
			// TODO
		}
		return d;
	}
	
	public int getTrack_lenght() {
		return track_lenght;
	}

	public void setTrack_lenght(int track_lenght) {
		this.track_lenght = track_lenght;
	}

	public String getTrack_lenght_descriptive() {
		return track_lenght_descriptive;
	}

	public void setTrack_lenght_descriptive(String track_lenght_descriptive) {
		this.track_lenght_descriptive = track_lenght_descriptive;
	}

	public String getAverage_travel_time() {
		return average_travel_time;
	}

	public void setAverage_travel_time(String average_travel_time) {
		this.average_travel_time = average_travel_time;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getAltitude_gap() {
		return altitude_gap;
	}

	public void setAltitude_gap(String altitude_gap) {
		this.altitude_gap = altitude_gap;
	}

	public String getType_of_surface() {
		return type_of_surface;
	}

	public void setType_of_surface(String type_of_surface) {
		this.type_of_surface = type_of_surface;
	}

	public String getCrossing_with_other_paths() {
		return crossing_with_other_paths;
	}

	public void setCrossing_with_other_paths(String crossing_with_other_paths) {
		this.crossing_with_other_paths = crossing_with_other_paths;
	}

	public String getAdvised_season() {
		return advised_season;
	}

	public void setAdvised_season(String advised_season) {
		this.advised_season = advised_season;
	}

	public String getTraffic() {
		return traffic;
	}

	public void setTraffic(String traffic) {
		this.traffic = traffic;
	}

	public double getTraveled_distance() {
		return traveled_distance;
	}

	public void setTraveled_distance(double traveled_distance) {
		this.traveled_distance = traveled_distance;
	}

	public double getAvg_speed() {
		return avg_speed;
	}

	public void setAvg_speed(double avg_speed) {
		this.avg_speed = avg_speed;
	}

	public double getMax_speed() {
		return max_speed;
	}

	public void setMax_speed(double max_speed) {
		this.max_speed = max_speed;
	}

	public double getTotal_elevation() {
		return total_elevation;
	}

	public void setTotal_elevation(double total_elevation) {
		this.total_elevation = total_elevation;
	}

	public int getNumber_of_registered_uses() {
		return number_of_registered_uses;
	}

	public void setNumber_of_registered_uses(int number_of_registered_uses) {
		this.number_of_registered_uses = number_of_registered_uses;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Long getLast_training_date() {
		return last_training_date;
	}

	public void setLast_training_date(long last_training_date) {
		this.last_training_date = last_training_date;
	}

	
	
}
