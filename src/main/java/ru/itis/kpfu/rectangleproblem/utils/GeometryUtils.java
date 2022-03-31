package ru.itis.kpfu.rectangleproblem.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import ru.itis.kpfu.rectangleproblem.model.Rectangle;
import ru.itis.kpfu.rectangleproblem.model.enumerated.Orientation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * @author Zagir Dingizbaev
 */

@Slf4j
@UtilityClass
public class GeometryUtils {

    private static final GeometryFactory factory = new GeometryFactory();

    public static Double getLongestSide(Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();

        var dist1 = coordinates[0].distance(coordinates[1]);
        var dist2 = coordinates[1].distance(coordinates[2]);

        return Math.max(dist1, dist2);
    }

    public static Double getShortestSide(Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();

        var dist1 = coordinates[0].distance(coordinates[1]);
        var dist2 = coordinates[1].distance(coordinates[2]);

        return Math.min(dist1, dist2);
    }

    public static Polygon createRectangularPolygon(Point bottomLeft, Point upperRight, Orientation orientation) {
        Coordinate[] coordinates = new Coordinate[5];

        if (orientation == Orientation.HORIZONTAL) {
            coordinates[0] = bottomLeft.getCoordinate();
            coordinates[1] = new Coordinate(bottomLeft.getX(), upperRight.getY());
            coordinates[2] = upperRight.getCoordinate();
            coordinates[3] = new Coordinate(upperRight.getX(), bottomLeft.getY());
            coordinates[4] = bottomLeft.getCoordinate();
        } else {
            coordinates[4] = bottomLeft.getCoordinate();
            coordinates[3] = new Coordinate(bottomLeft.getX(), upperRight.getY());
            coordinates[2] = upperRight.getCoordinate();
            coordinates[1] = new Coordinate(upperRight.getX(), bottomLeft.getY());
            coordinates[0] = bottomLeft.getCoordinate();
        }

        return factory.createPolygon(coordinates);
    }

    public static Orientation computeOrientation(Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();

        var dist1 = coordinates[0].distance(coordinates[1]);
        var dist2 = coordinates[1].distance(coordinates[2]);

        return dist1 > dist2 ? Orientation.VERTICAL : Orientation.HORIZONTAL;
    }

    public static Double getX(Rectangle rectangle) {
        var orientation = computeOrientation(rectangle.getFigure());
        if (orientation == Orientation.VERTICAL) {
            return rectangle.getFigure().getCoordinates()[2].getX();
        } else {
            return rectangle.getFigure().getCoordinates()[2].getY();
        }
    }

    public static boolean covers(Polygon polygon, Point point) {
        var pointX = round(point.getX());
        var pointY = round(point.getY());
        var minX = round(Arrays.stream(polygon.getCoordinates())
                .min(Comparator.comparing(x -> round(x.getX())))
                .orElseThrow(NoSuchElementException::new)
                .getX());
        var minY = round(Arrays.stream(polygon.getCoordinates())
                .min(Comparator.comparing(x -> round(x.getY())))
                .orElseThrow(NoSuchElementException::new)
                .getY());

        var maxX = round(Arrays.stream(polygon.getCoordinates())
                .max(Comparator.comparing(x -> round(x.getX())))
                .orElseThrow(NoSuchElementException::new)
                .getX());
        var maxY = round(Arrays.stream(polygon.getCoordinates())
                .max(Comparator.comparing(x -> round(x.getY())))
                .orElseThrow(NoSuchElementException::new)
                .getY());

        return pointX >= minX &&
                pointX <= maxX &&
                pointY >= minY &&
                pointY <= maxY;
    }

    private static double round(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(16, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
