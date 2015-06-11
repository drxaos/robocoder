package mxb.jts.triangulate;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EarClipper {
    private static final double EPS = 1.0E-4;

    private final GeometryFactory gf;
    private final Polygon inputPolygon;
    private Geometry ears;

    private List<Coordinate> shellCoords;
    private boolean[] shellCoordAvailable;

    List<Triangle> earList;

    /**
     * Constructor
     *
     * @param inputPolygon the input polygon
     */
    public EarClipper(Polygon inputPolygon){
        gf = new GeometryFactory();
        this.inputPolygon = inputPolygon;
    }

    /**
     * Get the result triangular polygons.
     *
     * @return triangles as a GeometryCollection
     */
    public Geometry getResult() {
        return getResult(true);
    }
    public Geometry getResult(boolean improve) {


        if (ears == null) {
            ears = triangulate(improve);
        }

        return ears;
    }

    /**
     * Perform the triangulation
     *
     * @param improve if true, improvement of the triangulation is attempted
     *        as a post-processing step
     *
     * @return GeometryCollection of triangular polygons
     */
    private Geometry triangulate(boolean improve) {
        earList = new ArrayList<Triangle>();
        createShell();

        int N = shellCoords.size() - 1;
        if(N<2) {
            return new GeometryFactory().createPolygon(null, null);
        }
        shellCoordAvailable = new boolean[N];
        Arrays.fill(shellCoordAvailable, true);

        boolean finished = false;
        boolean found = false;
        int k0 = 0;
        int k1 = 1;
        int k2 = 2;
        int firstK = 0;
        do {
            found = false;
            while (CGAlgorithms.computeOrientation(
                    shellCoords.get(k0), shellCoords.get(k1), shellCoords.get(k2)) == 0) {
                k0 = k1;
                if (k0 == firstK) {
                    finished = true;
                    break;
                }
                k1 = k2;
                k2 = nextShellCoord(k2 + 1);
            }

            if (!finished && isValidEdge(k0, k2)) {
                LineString ls = gf.createLineString(new Coordinate[] {shellCoords.get(k0), shellCoords.get(k2)});

                if (inputPolygon.covers(ls)) {
                    Polygon earPoly = gf.createPolygon(gf.createLinearRing(
                                    new Coordinate[]{
                                            shellCoords.get(k0),
                                            shellCoords.get(k1),
                                            shellCoords.get(k2),
                                            shellCoords.get(k0)}),
                            null);

                    if (inputPolygon.covers(earPoly)) {
                        found = true;
                        // System.out.println(earPoly);
                        Triangle ear = new Triangle(k0, k1, k2);
                        earList.add(ear);
                        shellCoordAvailable[k1] = false;
                        N--;
                        k0 = nextShellCoord(0);
                        k1 = nextShellCoord(k0 + 1);
                        k2 = nextShellCoord(k1 + 1);
                        firstK = k0;
                        if (N < 3) {
                            finished = true;
                        }
                    }
                }
            }

            if (!finished && !found) {
                k0 = k1;

                if (k0 == firstK) {
                    finished = true;
                } else {
                    k1 = k2;
                    k2 = nextShellCoord(k2 + 1);
                }
            }

        } while (!finished);

        if (improve) {
            doImprove();
        }

        Geometry[] geoms = new Geometry[earList.size()];
        for (int i = 0; i < earList.size(); i++) {
            geoms[i] = createPolygon(earList.get(i));
        }
        return gf.createGeometryCollection(geoms);
    }

    /**
     * Transforms the input polygon into a single, possible self-intersecting
     * shell by connecting holes to the exterior ring, The holes are added
     * from the lowest upwards. As the resulting shell develops, a hole might
     * be added to what was originally another hole.
     */
    private void createShell() {
        // defensively copy the input polygon
        Polygon poly = (Polygon) inputPolygon.clone();
        poly.normalize();

        shellCoords = new ArrayList<Coordinate>();
        List<Geometry> orderedHoles = getOrderedHoles(poly);

        Coordinate[] coords = poly.getExteriorRing().getCoordinates();
        shellCoords.addAll(Arrays.asList(coords));

        for (int i = 0; i < orderedHoles.size(); i++) {
            joinHoleToShell(orderedHoles.get(i));
        }
    }

    /**
     * Check if a candidate edge between two vertices passes through
     * any other available vertices.
     *
     * @param index0 first vertex
     * @param index1 second vertex
     *
     * @return true if the edge does not pass through any other available
     *         vertices; false otherwise
     */
    private boolean isValidEdge(int index0, int index1) {
        final Coordinate[] line = {shellCoords.get(index0), shellCoords.get(index1)};

        int index = nextShellCoord(index0 + 1);
        while (index != index0) {
            if (index != index1) {
                Coordinate c = shellCoords.get(index);
                if (!(c.equals2D(line[0]) || c.equals2D(line[1]))) {
                    if (CGAlgorithms.isOnLine(c, line)) {
                        return false;
                    }
                }
            }
            index = nextShellCoord(index + 1);
        }
        return true;
    }

    /**
     * Get the index of the next available shell coordinate starting
     * from the given candidate position.
     *
     * @param pos candidate position
     *
     * @return index of the next available shell coordinate
     */
    private int nextShellCoord(int pos) {
        int pnew = pos % shellCoordAvailable.length;
        while (!shellCoordAvailable[pnew]) {
            pnew = (pnew + 1) % shellCoordAvailable.length;
        }

        return pnew;
    }

    /**
     * Attempts to improve the triangulation by examining pairs of
     * triangles with a common edge, forming a quadrilateral, and
     * testing if swapping the diagonal of this quadrilateral would
     * produce two new triangles with larger minimum interior angles.
     */
    private void doImprove() {
        EdgeFlipper ef = new EdgeFlipper(shellCoords);
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < earList.size() - 1 && !changed; i++) {
                Triangle ear0 = earList.get(i);
                for (int j = i + 1; j < earList.size() && !changed; j++) {
                    Triangle ear1 = earList.get(j);
                    int[] sharedVertices = ear0.getSharedVertices(ear1);
                    if (sharedVertices != null && sharedVertices.length == 2) {
                        if (ef.flip(ear0, ear1, sharedVertices)) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
    }

    /**
     * Create a Polygon from a Triangle object
     *
     * @param t the triangle
     * @return a new Polygon object
     */
    private Polygon createPolygon(final Triangle t) {
        final int[] vertices = t.getVertices();
        return gf.createPolygon(gf.createLinearRing(new Coordinate[]{shellCoords.get(vertices[0]), shellCoords.get(vertices[1]), shellCoords.get(vertices[2]), shellCoords.get(vertices[0])}), null);
    }

    /**
     * Returns a list of holes in the input polygon (if any) ordered
     * by y coordinate with ties broken using x coordinate.
     *
     * @param poly input polygon
     * @return a list of Geometry objects representing the ordered holes
     *         (may be empty)
     */
    private List<Geometry> getOrderedHoles(final Polygon poly) {
        List<Geometry> holes = new ArrayList<Geometry>();
        List<IndexedEnvelope> bounds = new ArrayList<IndexedEnvelope>();

        if (poly.getNumInteriorRing() > 0) {
            for (int i = 0; i < poly.getNumInteriorRing(); i++) {
                bounds.add( new IndexedEnvelope(i, poly.getInteriorRingN(i).getEnvelopeInternal()) );
            }

            Collections.sort(bounds, new IndexedEnvelopeComparator());

            for (int i = 0; i < bounds.size(); i++) {
                holes.add(poly.getInteriorRingN(bounds.get(i).index));
            }
        }

        return holes;
    }

    /**
     * Join a given hole to the current shell. The hole coordinates are
     * inserted into the list of shell coordinates.
     *
     * @param hole the hole to join
     */
    private void joinHoleToShell(Geometry hole) {
        double minD2 = Double.MAX_VALUE;
        int shellVertexIndex = -1;

        final int Ns = shellCoords.size() - 1;

        final int holeVertexIndex = getLowestVertex(hole);
        final Coordinate[] holeCoords = hole.getCoordinates();

        final Coordinate ch = holeCoords[holeVertexIndex];
        List<IndexedDouble> distanceList = new ArrayList<IndexedDouble>();

        /*
         * Note: it's important to scan the shell vertices in reverse so
         * that if a hole ends up being joined to what was originally
         * another hole, the previous hole's coordinates appear in the shell
         * before the new hole's coordinates (otherwise the triangulation
         * algorithm tends to get stuck).
         */
        for (int i = Ns - 1; i >= 0; i--) {
            Coordinate cs = shellCoords.get(i);
            double d2 = (ch.x - cs.x) * (ch.x - cs.x) + (ch.y - cs.y) * (ch.y - cs.y);
            if (d2 < minD2) {
                minD2 = d2;
                shellVertexIndex = i;
            }
            distanceList.add(new IndexedDouble(i, d2));
        }
        
        /*
         * Try a quick join: if the closest shell vertex is reachable without
         * crossing any holes.
         */
        LineString join = gf.createLineString(new Coordinate[]{ch, shellCoords.get(shellVertexIndex)});
        if (inputPolygon.covers(join)) {
            doJoinHole(shellVertexIndex, holeCoords, holeVertexIndex);
            return;
        }

        /*
         * Quick join didn't work. Sort the shell coords on distance to the
         * hole vertex nnd choose the closest reachable one.
         */
        Collections.sort(distanceList, new IndexedDoubleComparator());
        for (int i = 1; i < distanceList.size(); i++) {
            join = gf.createLineString(new Coordinate[] {ch, shellCoords.get(distanceList.get(i).index)});
            if (inputPolygon.covers(join)) {
                shellVertexIndex = distanceList.get(i).index;
                doJoinHole(shellVertexIndex, holeCoords, holeVertexIndex);
                return;
            }
        }

        throw new IllegalStateException("Failed to join hole to shell");
    }

    /**
     * Helper method for joinHoleToShell. Insert the hole coordinates into
     * the shell coordinate list.
     *
     * @param shellCoords list of current shell coordinates
     * @param shellVertexIndex insertion point in the shell coordinate list
     * @param holeCoords array of hole coordinates
     * @param holeVertexIndex attachment point of hole
     */
    private void doJoinHole(int shellVertexIndex, Coordinate[] holeCoords, int holeVertexIndex) {
        List<Coordinate> newCoords = new ArrayList<Coordinate>();
        List<Integer> newRingIndices = new ArrayList<Integer>();

        newCoords.add(new Coordinate(shellCoords.get(shellVertexIndex)));

        final int N = holeCoords.length - 1;
        int i = holeVertexIndex;
        do {
            newCoords.add(new Coordinate(holeCoords[i]));
            i = (i + 1) % N;
        } while (i != holeVertexIndex);

        newCoords.add(new Coordinate(holeCoords[holeVertexIndex]));

        shellCoords.addAll(shellVertexIndex, newCoords);
    }

    /**
     * Return the index of the lowest vertex
     *
     * @param geom input geometry
     * @return index of the first vertex found at lowest point
     *         of the geometry
     */
    private int getLowestVertex(Geometry geom) {
        Coordinate[] coords = geom.getCoordinates();
        double minY = geom.getEnvelopeInternal().getMinY();
        for (int i = 0; i < coords.length; i++) {
            if (Math.abs(coords[i].y - minY) < EPS) {
                return i;
            }
        }

        throw new IllegalStateException("Failed to find lowest vertex");
    }

    private static class IndexedEnvelope {
        int index;
        Envelope envelope;

        public IndexedEnvelope(int i, Envelope env) { index = i; envelope = env; }
    }

    private static class IndexedEnvelopeComparator implements Comparator<IndexedEnvelope> {
        public int compare(IndexedEnvelope o1, IndexedEnvelope o2) {
            double delta = o1.envelope.getMinY() - o2.envelope.getMinY();
            if (Math.abs(delta) < EPS) {
                delta = o1.envelope.getMinX() - o2.envelope.getMinX();
                if (Math.abs(delta) < EPS) {
                    return 0;
                }
            }
            return (delta > 0 ? 1 : -1);
        }
    }

    private static class IndexedDouble {
        int index;
        double value;

        public IndexedDouble(int i, double v) { index = i; value = v; }
    }

    private static class IndexedDoubleComparator implements Comparator<IndexedDouble> {
        public int compare(IndexedDouble o1, IndexedDouble o2) {
            double delta = o1.value - o2.value;
            if (Math.abs(delta) < EPS) {
                return 0;
            }
            return (delta > 0 ? 1 : -1);
        }
    }

}