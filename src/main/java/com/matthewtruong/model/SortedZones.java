package com.matthewtruong.model;


import com.matthewtruong.model.areas.Area;
import com.matthewtruong.model.zones.Zone;

import java.util.List;

public class SortedZones {
    private final Area area;
    private final List<Zone> leftZones;
    private final List<Zone> rightZones;

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
