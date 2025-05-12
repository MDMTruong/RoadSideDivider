package com.matthewtruong.model.zones;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Zone {
    @JsonProperty("curb_zone_id")
    private String curbZoneId;
    private Geometry geometry;
    @JsonProperty("published_date")
    private Integer publishedDate;
    @JsonProperty("last_updated_date")
    private Integer lastUpdatedDate;
    @JsonProperty("start_date")
    private Integer startDate;
    @JsonProperty("location_references")
    private List<LocationReference> locationReferences;
    @JsonProperty("street_name")
    private String streetName;
    @JsonProperty("cross_street_start_name")
    private String crossStreetStartName;
    @JsonProperty("cross_street_end_name")
    private String crossStreetEndName;
    @JsonProperty("curb_policy_ids")
    private List<String> curbPolicyIds;
    @JsonProperty("parking_angle")
    private Object parkingAngle;
    @JsonProperty("num_spaces")
    private Integer numSpaces;

    public String getCurbZoneId() {
        return curbZoneId;
    }


    public void setCurbZoneId(String curbZoneId) {
        this.curbZoneId = curbZoneId;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Integer getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Integer publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Integer getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Integer lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }

    public List<LocationReference> getLocationReferences() {
        return locationReferences;
    }

    public void setLocationReferences(List<LocationReference> locationReferences) {
        this.locationReferences = locationReferences;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCrossStreetStartName() {
        return crossStreetStartName;
    }

    public void setCrossStreetStartName(String crossStreetStartName) {
        this.crossStreetStartName = crossStreetStartName;
    }

    public String getCrossStreetEndName() {
        return crossStreetEndName;
    }

    public void setCrossStreetEndName(String crossStreetEndName) {
        this.crossStreetEndName = crossStreetEndName;
    }

    public List<String> getCurbPolicyIds() {
        return curbPolicyIds;
    }

    public void setCurbPolicyIds(List<String> curbPolicyIds) {
        this.curbPolicyIds = curbPolicyIds;
    }

    public Object getParkingAngle() {
        return parkingAngle;
    }

    public void setParkingAngle(Object parkingAngle) {
        this.parkingAngle = parkingAngle;
    }

    public Integer getNumSpaces() {
        return numSpaces;
    }

    public void setNumSpaces(Integer numSpaces) {
        this.numSpaces = numSpaces;
    }
}
