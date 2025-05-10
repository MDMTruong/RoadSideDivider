package com.inrix.model;


import com.inrix.model.areas.Area;
import com.inrix.model.zones.Zone;

import java.util.List;

public class SortedZones {
    private Area area;
    private List<Zone> leftZones;
    private List<Zone> rightZones;

    public SortedZones(Area area, List<Zone> leftZones, List<Zone> rightZones) {
        this.area = area;
        this.leftZones = leftZones;
        this.rightZones = rightZones;
    }

    public Area getArea() {
        return area;
    }

    public List<Zone> getLeftZones() {
        return leftZones;
    }

    public List<Zone> getRightZones() {
        return rightZones;
    }

}
