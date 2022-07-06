package pt.unl.fct.civitas.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.geometry.Point;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.List;

public class GeometryHelper {
    public static boolean checkIntersections(boolean inProgress, List<LatLng> newShape, List<List<LatLng>> oldShapes) {
        if(newShape.size() == 1)
            for (List<LatLng> terrain : oldShapes) {
                return PolyUtil.containsLocation(newShape.get(0), terrain, false);
            }
        else {
            org.locationtech.jts.geom.Polygon oldPoly;
            org.locationtech.jts.geom.Geometry newGeom;
            if(inProgress)
                newGeom = createJstsLineString(newShape);
            else {
                newShape.add(newShape.get(0));
                newGeom = createJstsPolygon(newShape);
            }
            for (List<LatLng> terrain : oldShapes) {
                oldPoly = createJstsPolygon(terrain);
                if (oldPoly.intersects(newGeom))
                    return true;
            }
        }

        return false;
    }

    public static org.locationtech.jts.geom.Polygon createJstsPolygon(List<LatLng> points) {
        GeometryFactory geomFactory = new GeometryFactory();
        Coordinate[] coords = new Coordinate[points.size()];
        for (int i = 0; i < coords.length; i++) {
            LatLng point = points.get(i);
            coords[i] = new Coordinate(point.latitude, point.longitude);
        }
        return geomFactory.createPolygon(coords);
    }

    public static org.locationtech.jts.geom.LineString createJstsLineString(List<LatLng> points) {
        GeometryFactory geomFactory = new GeometryFactory();
        Coordinate[] coords = new Coordinate[points.size()];
        for (int i = 0; i < coords.length; i++) {
            LatLng point = points.get(i);
            coords[i] = new Coordinate(point.latitude, point.longitude);
        }
        return geomFactory.createLineString(coords);
    }
}
