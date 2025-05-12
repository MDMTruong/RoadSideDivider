package com.matthewtruong.model.areas;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Area {
    @JsonProperty("curb_area_id")
    private Integer curbAreaId;
    private Polygon geometry;
    @JsonProperty("published_date")
    private Integer publishedDate;
    @JsonProperty("last_updated_date")
    private Integer lastUpdatedDate;
    @JsonProperty("curb_zone_ids")
    private List<String> curbZoneIds;

    public Integer getCurbAreaId() {
        return curbAreaId;
    }

    public void setCurbAreaId(Integer curbAreaId) {
        this.curbAreaId = curbAreaId;
    }

    public Polygon getGeometry() {
        return geometry;
    }

    public void setGeometry(Polygon geometry) {
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

    public List<String> getCurbZoneIds() {
        return curbZoneIds;
    }

    public void setCurbZoneIds(List<String> curbZoneIds) {
        this.curbZoneIds = curbZoneIds;
    }
}
