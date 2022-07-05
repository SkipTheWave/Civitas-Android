package pt.unl.fct.civitas.util;

import com.google.maps.android.geometry.Point;

/**
 * Author is the kind AmitCharkha on StackOverflow
 */
public class GeometryHelper {

    /**
     * See if two line segments intersect. This uses the
     * vector cross product approach described below:
     * http://stackoverflow.com/a/565282/786339
     *
     * @param {Object} p point object with x and y coordinates
     *  representing the start of the 1st line.
     * @param {Object} p2 point object with x and y coordinates
     *  representing the end of the 1st line.
     * @param {Object} q point object with x and y coordinates
     *  representing the start of the 2nd line.
     * @param {Object} q2 point object with x and y coordinates
     *  representing the end of the 2nd line.
     */
    public static boolean doLineSegmentsIntersect(Point p, Point p2, Point q, Point q2) {
        Point r = subtractPoints(p2, p);
        Point s = subtractPoints(q2, q);

        double uNumerator = crossProduct(subtractPoints(q, p), r);
        double denominator = crossProduct(r, s);

        if (denominator == 0) {
            // lines are paralell
            return false;
        }

        double u = uNumerator / denominator;
        double t = crossProduct(subtractPoints(q, p), s) / denominator;

        return ( (t >= 0) && (t <= 1) && (u > 0) && (u <= 1) );

    }

    /**
     * Calculate the cross product of the two points.
     *
     * @param {Object} point1 point object with x and y coordinates
     * @param {Object} point2 point object with x and y coordinates
     *
     * @return the cross product result as a float
     */
    public static double crossProduct(Point point1, Point point2) {
        return point1.x * point2.y - point1.y * point2.x;
    }

    /**
     * Subtract the second point from the first.
     *
     * @param {Object} point1 point object with x and y coordinates
     * @param {Object} point2 point object with x and y coordinates
     *
     * @return the subtraction result as a point object
     */
    public static Point subtractPoints(Point point1,Point point2) {
        return new Point(point1.x - point2.x, point1.y - point2.y);
    }
}
