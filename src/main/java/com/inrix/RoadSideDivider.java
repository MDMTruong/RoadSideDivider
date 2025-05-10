package com.inrix;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inrix.model.SortedZones;
import com.inrix.model.areas.Area;
import com.inrix.model.areas.Areas;
import com.inrix.model.zones.Zone;
import com.inrix.model.zones.Zones;
import org.locationtech.jts.algorithm.MinimumAreaRectangle;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;
import org.locationtech.jts.triangulate.DelaunayTriangulationBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadSideDivider {
    public static void main(String[] args) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Zones zones = getZones(objectMapper, args[0]);
        Areas areas = getAreas(objectMapper, args[1]);

        ArrayList<SortedZones> sortedZones = getSortedZones(areas, zones);

        writeOutput(sortedZones);
    }

    protected static ArrayList<SortedZones> getSortedZones(Areas areas, Zones zones) {
        HashMap<String, Zone> curbZones = new HashMap<>();

        for (Area area : areas.getAreas()) {
            area.getCurbZoneIds().forEach(s -> curbZones.put(s, null));
        }
        //In the sample data, not all zones have an area
        for (Zone zone : zones.getZones()) {
            if (curbZones.containsKey(zone.getCurbZoneId())) {
                curbZones.put(zone.getCurbZoneId(), zone);
            }
        }

        GeometryFactory geometryFactory = new GeometryFactory();

        //Group zones by area
        HashMap<Area, List<Zone>> areasWithZones = new HashMap<>();
        for (Area area : areas.getAreas()) {
            areasWithZones.put(area,
                    area.getCurbZoneIds().stream().map(curbZones::get).toList());
        }

        ArrayList<SortedZones> sortedZones = new ArrayList<>();
        for (Map.Entry<Area, List<Zone>> entry : areasWithZones.entrySet()) {
            Area area = entry.getKey();
            Polygon polygon = createPolygonFromArea(geometryFactory, area);
            LineString middleLine = getMiddleLine(polygon, geometryFactory);
            ArrayList<Zone> leftZones = new ArrayList<>();
            ArrayList<Zone> rightZones = new ArrayList<>();
            for (Zone zone : entry.getValue()) {
                if (zone != null) {
                    LineString zoneLineString = getLineStringFromZone(zone, geometryFactory);
                    int orientation = getOrientation(middleLine, zoneLineString);
                    if (orientation == Orientation.LEFT) {
                        leftZones.add(zone);
                    } else {
                        rightZones.add(zone);
                    }
                }
            }
            sortedZones.add(new SortedZones(area, leftZones, rightZones));
        }
        return sortedZones;
    }

    protected static Areas getAreas(ObjectMapper objectMapper, String pathname) throws IOException {
        return objectMapper.readValue(new File(pathname), Areas.class);
    }

    protected static Zones getZones(ObjectMapper objectMapper, String pathname) throws IOException {
        return objectMapper.readValue(new File(pathname), Zones.class);
    }

    private static void writeOutput(ArrayList<SortedZones> sortedZonesList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            FileWriter fileWriter = new FileWriter("./output.json");
            objectMapper.writeValue(fileWriter, sortedZonesList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Break the middle line into sections, find the section closest to the zone line and use that
     * section to determine if the zone is on the left or the right
     *
     * @param middleLine - LineString representing the middle line of the road
     * @param zoneLineString - LineString representing a zone at the side of the road
     * @return Whether the zone is the left or the right
     */
    private static int getOrientation(LineString middleLine, LineString zoneLineString) {

        Point centroid = zoneLineString.getCentroid();

        Coordinate[] nearestPoints = DistanceOp.nearestPoints(middleLine, centroid);

        Coordinate[] coordinates = middleLine.getCoordinates();

        int closestIndex = -1;
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < coordinates.length - 1; i++) {
            LineSegment segment = new LineSegment(coordinates[i], coordinates[i+1]);
            double distance = segment.distance(nearestPoints[0]);
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return Orientation.index(coordinates[closestIndex], coordinates[closestIndex + 1], centroid.getCoordinate());
    }

    /**
     * This initially tries to use a straight middle line through a roughly rectangular shape.
     * If the resulting line doesn't fit within the shape, use dalaunay triangulation to calculate
     * the line
     *
     * @param polygon
     * @param geometryFactory
     * @return line string going through the middle of the polygon
     */
    private static LineString getMiddleLine(Polygon polygon, GeometryFactory geometryFactory) {
        Geometry minimumRectangle = MinimumAreaRectangle.getMinimumRectangle(polygon.getBoundary());
        Coordinate[] coordinates = minimumRectangle.getCoordinates();
        LineSegment h = new LineSegment(coordinates[0], coordinates[1]);
        LineSegment w = new LineSegment(coordinates[1], coordinates[2]);

        Coordinate midPoint1, midPoint2;
        if (h.getLength() < w.getLength()) {
            midPoint1 = h.midPoint();
            midPoint2 = new LineSegment(coordinates[2], coordinates[3]).midPoint();
        } else {
            midPoint1 = w.midPoint();
            midPoint2 = new LineSegment(coordinates[3], coordinates[0]).midPoint();
        }

        LineString middleLine = geometryFactory.createLineString(new Coordinate[]{midPoint1, midPoint2});

        middleLine.intersection(polygon.getBoundary());

        //Approximately a meter
        double distanceTolerance = 0.00001;
        if (middleLine.within(polygon.buffer(distanceTolerance))){
            return middleLine;
        }

        DelaunayTriangulationBuilder delaunayTriangulationBuilder = new DelaunayTriangulationBuilder();
        delaunayTriangulationBuilder.setSites(polygon);
        GeometryCollection triangles = (GeometryCollection) delaunayTriangulationBuilder.getTriangles(geometryFactory);

        //Filter out triangles that aren't within the polygon
        List<Coordinate> interiorTriangles = new ArrayList<>();
        for (int i = 0; i < triangles.getNumGeometries(); i++) {
            if (polygon.contains(triangles.getGeometryN(i))) {
                interiorTriangles.add(triangles.getGeometryN(i).getCentroid().getCoordinate());
            }
        }

        //Order the triangles starting with the start point of the polygon
        //and creating a chain of nearest triangles from there, based on their centroid
        Coordinate polygonStartPoint = polygon.getExteriorRing().getStartPoint().getCoordinate();

        Coordinate lastPoint = Collections.min(interiorTriangles, Comparator.comparingDouble(t -> t.distance(polygonStartPoint)));

        List<Coordinate> orderedPoints = new ArrayList<>();
        orderedPoints.add(lastPoint);
        interiorTriangles.remove(lastPoint);

        while (!interiorTriangles.isEmpty()) {
            Coordinate previousPoint = lastPoint;
            Coordinate currentPoint = Collections.min(interiorTriangles, Comparator.comparingDouble(t -> t.distance(previousPoint)));
            orderedPoints.add(currentPoint);
            interiorTriangles.remove(currentPoint);
            lastPoint = currentPoint;
        }

        LineString lineString = geometryFactory.createLineString(orderedPoints.toArray(new Coordinate[0]));

        DouglasPeuckerSimplifier simplifier = new DouglasPeuckerSimplifier(lineString);
        simplifier.setDistanceTolerance(distanceTolerance);
        return (LineString) simplifier.getResultGeometry();

    }

    private static Polygon createPolygonFromArea(GeometryFactory geometryFactory, Area area) {
        Coordinate[] coordinates = toCoordinates(area.getGeometry().getCoordinates().getFirst());
        return geometryFactory.createPolygon(coordinates);
    }

    private static LineString getLineStringFromZone(Zone zone, GeometryFactory geometryFactory) {
        Coordinate[] coordinates = toCoordinates(zone.getGeometry().getCoordinates());
        return geometryFactory.createLineString(coordinates);
    }

    public static Coordinate[] toCoordinates(List<List<Double>> listOfCoordinates) {
        if (listOfCoordinates == null) {
            return new Coordinate[0];
        }
        List<Coordinate> coordinates = new ArrayList<>();
        for (List<Double> coordPair : listOfCoordinates) {
            if (coordPair == null || coordPair.size() < 2) {
                throw new IllegalArgumentException("Each coordinate pair must have at least an X and Y value. Found: " + coordPair);
            }
            double x = coordPair.get(0);
            double y = coordPair.get(1);

            coordinates.add(new Coordinate(x, y));

        }
        return coordinates.toArray(new Coordinate[0]);
    }
}
