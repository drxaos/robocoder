package robo2d.game.box2d;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import mxb.jts.triangulate.EarClipper;
import straightedge.geom.KPolygon;
import straightedge.geom.PolygonConverter;

import java.util.ArrayList;
import java.util.List;

public class PolygonUtil {

    public static List<KPolygon> triangulate(KPolygon kPolygon) {
        PolygonConverter polygonConverter = new PolygonConverter();
        List<KPolygon> result = new ArrayList<KPolygon>();
        Polygon jtsPolygon = polygonConverter.makeJTSPolygonFrom(kPolygon);
        EarClipper earClipper = new EarClipper(jtsPolygon);
        Geometry triangles = earClipper.getResult(true);
        for (int i = 0; i < triangles.getNumGeometries(); i++) {
            Polygon polygonTriangle = (Polygon) triangles.getGeometryN(i);
            KPolygon kPolygonTriangle = polygonConverter.makeKPolygonFromExterior(polygonTriangle);
            result.add(kPolygonTriangle);
        }
        return result;
    }
}
