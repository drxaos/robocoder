/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package th;

import straightedge.geom.KMultiPolygon;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;
import straightedge.test.demo.Main;
import straightedge.test.demo.World;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author Keith
 */
public class WorldCustom extends World {
    public WorldCustom(Main main) {
        super(main);
    }

    public void fillMultiPolygonsList() {
        Container cont = main.getParentFrameOrApplet();
        double contW = cont.getWidth() - (cont.getInsets().right + cont.getInsets().left);
        double contH = cont.getHeight() - (cont.getInsets().top + cont.getInsets().bottom);


        ArrayList<KPolygon> allPolys = new ArrayList<KPolygon>();
        {
            ArrayList<KPoint> pointList = new ArrayList<KPoint>();
            pointList.add(new KPoint(0, 0));
            pointList.add(new KPoint(150, 50));
            pointList.add(new KPoint(300, 0));
            pointList.add(new KPoint(300, 300));
            pointList.add(new KPoint(0, 300));
            KPolygon poly = new KPolygon(pointList);
            assert poly.isCounterClockWise();
            poly.translate(60 + 45, 50 + 45);
            poly.rotate(2);
            allPolys.add(poly);
        }

        for (int i = 0; i < allPolys.size(); i++) {
            KMultiPolygon multiPolygon = new KMultiPolygon(allPolys.get(i).getPolygon().copy());
            allMultiPolygons.add(multiPolygon);
        }
    }
}
