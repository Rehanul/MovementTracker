package com.lpu.MovementTracker;

public class Status {
	private double Latitude;
	private double Longitude;
	private String Time;
	private String Status;

	Status() {
	}

	Status(float lat, float lng, String time, String status) {
		this.setLatitude(lat);
		this.setLongitude(lng);
		this.setTime(time);
		this.setStatus(status);
	}

	public double getLatitude() {
		return Latitude;
	}

	public void setLatitude(double latitude) {
		Latitude = latitude;
	}

	public double getLongitude() {
		return Longitude;
	}

	public void setLongitude(double longitude) {
		Longitude = longitude;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}
}
