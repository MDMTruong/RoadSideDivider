package com.matthewtruong;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthewtruong.model.SortedZones;
import com.matthewtruong.model.areas.Areas;
import com.matthewtruong.model.zones.Zone;
import com.matthewtruong.model.zones.Zones;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.matthewtruong.RoadSideDivider.getSortedZones;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoadSideDividerTest {

    @Test
    void readZones() throws IOException {
        Zones zones = RoadSideDivider.getZones(new ObjectMapper(), "src/test/resources/zones.json");
        assertNotNull(zones);
    }

    @Test
    void readAreas() throws IOException {
        Areas areas = RoadSideDivider.getAreas(new ObjectMapper(), "src/test/resources/area.json");
        assertNotNull(areas);
    }

    @Test
    void splitZonesIntoLeftAndRight() throws IOException {
        Zones zones = RoadSideDivider.getZones(new ObjectMapper(), "src/test/resources/zones.json");
        Areas areas = RoadSideDivider.getAreas(new ObjectMapper(), "src/test/resources/area.json");

        SortedZones sortedZones = getSortedZones(areas, zones).getFirst();

        assertEquals(1, sortedZones.getRightZones().size());
        assertEquals("49147af1-76e2-4b61-ab98-a4c93041d2ac", sortedZones.getRightZones().getFirst().getCurbZoneId());

        assertEquals(1, sortedZones.getLeftZones().size());
        assertEquals("fe66b357-9e0e-4056-aede-663d4c82f24a", sortedZones.getLeftZones().getFirst().getCurbZoneId());
    }

    @Test
    void splitZonesIntoLeftAndRightForCurvedStreet() throws IOException {
        Zones zones = RoadSideDivider.getZones(new ObjectMapper(), "src/test/resources/curved-area-zones.json");
        Areas areas = RoadSideDivider.getAreas(new ObjectMapper(), "src/test/resources/curved-area.json");

        SortedZones sortedZones = getSortedZones(areas, zones).getFirst();

        assertEquals(3, sortedZones.getRightZones().size());
        assertThat(sortedZones.getRightZones())
                .extracting(Zone::getCurbZoneId)
                        .contains("e6aef843-3a99-40f3-a5d5-e00a86840145",
                                "e217c817-a535-43e7-9f24-87967b9c7966",
                                "26690c95-a089-4713-a291-ff30fdc43c23");

        assertEquals(2, sortedZones.getLeftZones().size());
        assertThat(sortedZones.getLeftZones())
                .extracting(Zone::getCurbZoneId)
                .contains("fe66b357-9e0e-4056-aede-663d4c82f24a",
                        "d8c9efd3-d2b2-404f-bed3-0bb5770feb00");
    }
}