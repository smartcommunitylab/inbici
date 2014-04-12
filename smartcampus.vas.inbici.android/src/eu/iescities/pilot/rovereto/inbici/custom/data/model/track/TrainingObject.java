package eu.iescities.pilot.rovereto.inbici.custom.data.model.track;

import java.io.Serializable;

import eu.trentorise.smartcampus.storage.BasicObject;

public class TrainingObject extends BasicObject implements Serializable{
	private static final long serialVersionUID = -2013631618034067100L;

	private String trackId;
	private Long startTime;
	private Long endTime;
	private Double maxSpeed;
	private Double avgSpeed;
	private Double distance;
	private Double runningTime;
	private Double elevation;
	
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public Double getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(Double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public Double getAvgSpeed() {
		return avgSpeed;
	}
	public void setAvgSpeed(Double avgSpeed) {
		this.avgSpeed = avgSpeed;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public Double getRunningTime() {
		return runningTime;
	}
	public void setRunningTime(Double runningTime) {
		this.runningTime = runningTime;
	}
	public Double getElevation() {
		return elevation;
	}
	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
}
