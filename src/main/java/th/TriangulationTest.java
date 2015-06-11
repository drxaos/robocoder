package th;

import robo2d.game.box2d.PolygonUtil;
import straightedge.geom.KPoint;
import straightedge.geom.KPolygon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TriangulationTest extends JPanel {

    List<KPolygon> polygons;

    public TriangulationTest(List<KPolygon> polygons) {
        this.polygons = polygons;
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        for (KPolygon polygon : polygons) {
            g.draw(polygon);
        }
    }

    public static void main(String[] args) {
        final List<KPolygon> list = new ArrayList<KPolygon>();
        list.add(new KPolygon(
                new KPoint(50, 50),
                new KPoint(150, 50),
                new KPoint(100, 100),
                new KPoint(170, 170),
                new KPoint(150, 200),
                new KPoint(50, 150),
                new KPoint(50, 50)
        ));

        final JFrame frame = new JFrame();
        frame.setTitle("DrawPoly");
        frame.setSize(400, 400);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ArrayList<KPolygon> result = new ArrayList<KPolygon>();
                for (KPolygon kPolygon : list) {
                    List<KPolygon> triangulated = PolygonUtil.triangulate(kPolygon);
                    result.addAll(triangulated);
                }
                list.clear();
                list.addAll(result);
                frame.repaint();
            }
        });
        Container contentPane = frame.getContentPane();
        contentPane.add(new TriangulationTest(list));

        frame.show();
    }

}
