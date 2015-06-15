package com.github.drxaos.robocoder.game.levels;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import straightedge.geom.KPoint;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapParser {
    public static class MapDesc {
        public String name;
        public Map<String, MapObject> objects = new HashMap<String, MapObject>();
    }

    public static class MapObject {
        public ArrayList<Point2D> points = new ArrayList<Point2D>();

        public ArrayList<Point2D> getPoints() {
            return points;
        }
    }

    public static class MapVector extends MapObject {
        MapVector(ArrayList<Point2D> points) {
            this.points = points;
        }

        public float angle() {
            return (float) KPoint.findAngle(points.get(0).getX(), points.get(0).getY(), points.get(1).getX(), points.get(1).getY());
        }

        public KPoint point() {
            return new KPoint(points.get(0).getX(), points.get(0).getY());
        }
    }

    public static class MapPolygon extends MapObject {
        MapPolygon(ArrayList<Point2D> points) {
            this.points = points;
        }
    }

    public static class MapCircle extends MapObject {
        float radius;

        MapCircle(Point2D center, float radius) {
            this.points = new ArrayList<Point2D>();
            points.add(center);
            this.radius = radius;
        }

        public float getRadius() {
            return radius;
        }
    }

    public static MapDesc parseXml(String filePath, float scale) throws MapParserException {
        MapDesc mapDesc = new MapDesc();
        File fXmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new MapParserException("parser error", e);
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(fXmlFile);
        } catch (SAXException e) {
            throw new MapParserException("parser error", e);
        } catch (IOException e) {
            throw new MapParserException("parser error", e);
        }
        doc.getDocumentElement().normalize();
        mapDesc.name = doc.getDocumentElement().getAttribute("name");
        NodeList nList = doc.getElementsByTagName("area");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String shape = eElement.getAttribute("shape");
                String coords = eElement.getAttribute("coords");
                String name = eElement.getAttribute("name");
                String type = eElement.getAttribute("type");
                String extra = eElement.getAttribute("extra");

                if (mapDesc.objects.containsKey(name)) {
                    throw new MapParserException("duplicate " + type + ": " + name);
                }

                if (shape.equals("poly") || shape.equals("rect") || shape.equals("vector")) {
                    ArrayList<Point2D> points = new ArrayList<Point2D>();
                    String[] split = coords.split(",");
                    for (int i = 0; i < split.length; i += 2) {
                        float x = Float.parseFloat(split[i].trim());
                        float y = Float.parseFloat(split[i + 1].trim());
                        points.add(new Point2D.Float(x * scale, -y * scale));
                    }
                    if (shape.equals("vector")) {
                        mapDesc.objects.put(name, new MapVector(points));
                    } else {
                        mapDesc.objects.put(name, new MapPolygon(points));
                    }
                } else if (type.equals("circle")) {
                    String[] split = coords.split(",");
                    float x = Float.parseFloat(split[0].trim());
                    float y = Float.parseFloat(split[1].trim());
                    float r = Float.parseFloat(split[2].trim());
                    mapDesc.objects.put(name, new MapCircle(new Point2D.Float(x, y), r));
                } else {
                    throw new MapParserException("Unknown type: " + type + " (name: " + name + ")");
                }
            }
        }
        return mapDesc;
    }

}
