package com.matthewtruong.model.zones;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocationReference {
    private String source;
    @JsonProperty("ref_id")
    private String refId;
    private Integer start;
    private Integer end;
    private String side;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }
}
