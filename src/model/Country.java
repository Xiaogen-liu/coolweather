package model;

public class Country {
private int id;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getCountryName() {
	return countryName;
}
public void setCountryName(String countryName) {
	this.countryName = countryName;
}
public String getCountyCode() {
	return countyCode;
}
public void setCountyCode(String countyCode) {
	this.countyCode = countyCode;
}
public int getCityId() {
	return cityId;
}
public void setCityId(int cityId) {
	this.cityId = cityId;
}
private String countryName;
private String countyCode;
private int cityId;
}
