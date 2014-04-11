package eu.iescities.pilot.rovereto.inbici.entities.event.info.edit;

public class Position {

	protected String addressLine;
	protected String countryName;
	protected String locality;
	protected double lat;
	protected double lon;
	public String getAddressLine() {
		return addressLine;
	}
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public String getLocality() {
		return locality;
	}
	public void setLocality(String locality) {
		this.locality = locality;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public Position(String addressLine, String countryName, String locality, double lat, double lon) {
		this.addressLine=addressLine;
		this.countryName = countryName;
		this.locality = locality;
		this.lat = lat;
		this.lon = lon;
	}

}
