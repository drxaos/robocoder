package com.github.drxaos.robocoder.ui;

import org.jbox2d.callbacks.*;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Collision.PointState;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;
import org.jbox2d.serialization.*;
import org.jbox2d.serialization.JbDeserializer.ObjectListener;
import org.jbox2d.serialization.JbSerializer.ObjectSigner;
import org.jbox2d.serialization.pb.PbDeserializer;
import org.jbox2d.serialization.pb.PbSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel Murphy
 */
public abstract class TestbedTest
        implements
        ContactListener,
        ObjectListener,
        ObjectSigner,
        UnsupportedListener {
    public static final int MAX_CONTACT_POINTS = 4048;

    protected static final long GROUND_BODY_TAG = 1897450239847L;
    protected static final long MOUSE_JOINT_TAG = 4567893364789L;

    private static final Logger log = LoggerFactory.getLogger(TestbedTest.class);

    // keep these static so we don't have to recreate them every time
    public final static ContactPoint[] points = new ContactPoint[MAX_CONTACT_POINTS];

    static {
        for (int i = 0; i < MAX_CONTACT_POINTS; i++) {
            points[i] = new ContactPoint();
        }
    }

    /**
     * Only visible for compatibility. Should use {@link #getWorld()} instead.
     */
    protected World m_world;
    protected Body groundBody;
    private MouseJoint mouseJoint;

    private final Vec2 mouseWorld = new Vec2();
    private int pointCount;
    private int stepCount;

    private TestbedModel model;
    protected DestructionListener destructionListener;

    private final LinkedList<QueueItem> inputQueue;

    private String title = null;
    protected int m_textLine;
    private final LinkedList<String> textList = new LinkedList<String>();

    private float cachedCameraScale;
    private final Vec2 cachedCameraPos = new Vec2();
    private boolean hasCachedCamera = false;

    private JbSerializer serializer;
    private JbDeserializer deserializer;

    private boolean dialogOnSaveLoadErrors = true;

    private boolean savePending, loadPending, resetPending = false;

    public TestbedTest() {
        inputQueue = new LinkedList<QueueItem>();
        serializer = new PbSerializer(this, new SignerAdapter(this) {
            @Override
            public Long getTag(Body argBody) {
                if (isSaveLoadEnabled()) {
                    if (argBody == groundBody) {
                        return GROUND_BODY_TAG;
                    }
                }
                return super.getTag(argBody);
            }

            @Override
            public Long getTag(Joint argJoint) {
                if (isSaveLoadEnabled()) {
                    if (argJoint == mouseJoint) {
                        return MOUSE_JOINT_TAG;
                    }
                }
                return super.getTag(argJoint);
            }
        });
        deserializer = new PbDeserializer(this, new ListenerAdapter(this) {
            @Override
            public void processBody(Body argBody, Long argTag) {
                if (isSaveLoadEnabled()) {
                    if (argTag == GROUND_BODY_TAG) {
                        groundBody = argBody;
                        return;
                    }
                }
                super.processBody(argBody, argTag);
            }

            @Override
            public void processJoint(Joint argJoint, Long argTag) {
                if (isSaveLoadEnabled()) {
                    if (argTag == MOUSE_JOINT_TAG) {
                        mouseJoint = (MouseJoint) argJoint;
                        return;
                    }
                }
                super.processJoint(argJoint, argTag);
            }
        });
    }

    public void init(TestbedModel argModel) {
        model = argModel;
        destructionListener = new DestructionListener() {

            public void sayGoodbye(Fixture fixture) {
            }

            public void sayGoodbye(Joint joint) {
                if (mouseJoint == joint) {
                    mouseJoint = null;
                } else {
                    jointDestroyed(joint);
                }
            }
        };

        Vec2 gravity = new Vec2(0, -10f);
        m_world = new World(gravity);
        mouseJoint = null;

        BodyDef bodyDef = new BodyDef();
        groundBody = m_world.createBody(bodyDef);

        init(m_world, false);
    }

    public void init(World argWorld, boolean argDeserialized) {
        pointCount = 0;
        stepCount = 0;

        argWorld.setDestructionListener(destructionListener);
        argWorld.setContactListener(this);
        argWorld.setDebugDraw(model.getDebugDraw());

        if (hasCachedCamera) {
            setCamera(cachedCameraPos, cachedCameraScale);
        } else {
            setCamera(getDefaultCameraPos(), getDefaultCameraScale());
        }
        setTitle(getLevelName());

        initLevel(argDeserialized);
    }

    /**
     * Gets the current world
     *
     * @return
     */
    public World getWorld() {
        return m_world;
    }

    /**
     * Gets the testbed model
     *
     * @return
     */
    public TestbedModel getModel() {
        return model;
    }

    /**
     * Gets the contact points for the current test
     *
     * @return
     */
    public static ContactPoint[] getContactPoints() {
        return points;
    }

    /**
     * Gets the ground body of the world, used for some joints
     *
     * @return
     */
    public Body getGroundBody() {
        return groundBody;
    }

    /**
     * Gets the debug draw for the testbed
     *
     * @return
     */
    public DebugDraw getDebugDraw() {
        return model.getDebugDraw();
    }

    /**
     * Gets the world position of the mouse
     *
     * @return
     */
    public Vec2 getWorldMouse() {
        return mouseWorld;
    }

    public int getStepCount() {
        return stepCount;
    }

    /**
     * The number of contact points we're storing
     *
     * @return
     */
    public int getPointCount() {
        return pointCount;
    }

    /**
     * Gets the 'bomb' body if it's present
     *
     * @return
     */
    public float getCachedCameraScale() {
        return cachedCameraScale;
    }

    public void setCachedCameraScale(float cachedCameraScale) {
        this.cachedCameraScale = cachedCameraScale;
    }

    public Vec2 getCachedCameraPos() {
        return cachedCameraPos;
    }

    public void setCachedCameraPos(Vec2 argPos) {
        cachedCameraPos.set(argPos);
    }

    public boolean isHasCachedCamera() {
        return hasCachedCamera;
    }

    public void setHasCachedCamera(boolean hasCachedCamera) {
        this.hasCachedCamera = hasCachedCamera;
    }

    public boolean isDialogOnSaveLoadErrors() {
        return dialogOnSaveLoadErrors;
    }

    public void setDialogOnSaveLoadErrors(boolean dialogOnSaveLoadErrors) {
        this.dialogOnSaveLoadErrors = dialogOnSaveLoadErrors;
    }

    /**
     * Override for a different default camera pos
     *
     * @return
     */
    public Vec2 getDefaultCameraPos() {
        return new Vec2(0, 10);
    }

    /**
     * Override for a different default camera scale
     *
     * @return
     */
    public float getDefaultCameraScale() {
        return 10;
    }

    /**
     * Gets the filename of the current test. Default implementation uses the test name with no
     * spaces".
     *
     * @return
     */
    public String getFilename() {
        return getLevelName().toLowerCase().replaceAll(" ", "_") + ".box2d";
    }

    /**
     * Resets the test
     */
    public void reset() {
        resetPending = true;
    }

    /**
     * Saves the test
     */
    public void save() {
        savePending = true;
    }

    /**
     * Loads the test from file
     */
    public void load() {
        loadPending = true;
    }

    protected void _reset() {
        init(model);
    }

    protected void _save() {

        SerializationResult result;
        try {
            result = serializer.serialize(m_world);
        } catch (UnsupportedObjectException e1) {
            log.error("Error serializing world", e1);
            if (dialogOnSaveLoadErrors) {
                JOptionPane.showConfirmDialog(null, "Error serializing the object: " + e1.toString(),
                        "Serialization Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(getFilename());
            result.writeTo(fos);
        } catch (FileNotFoundException e) {
            log.error("File not found exception while saving", e);
            if (dialogOnSaveLoadErrors) {
                JOptionPane.showConfirmDialog(null, "File not found exception while saving: "
                        + getFilename(), "Serialization Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        } catch (IOException e) {
            log.error("Exception while writing world", e);
            if (dialogOnSaveLoadErrors) {
                JOptionPane.showConfirmDialog(null, "Error while writing world: " + e.toString(),
                        "Serialization Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        return;
    }

    protected void _load() {

        World w;
        try {
            FileInputStream fis = new FileInputStream(getFilename());
            w = deserializer.deserializeWorld(fis);
        } catch (FileNotFoundException e) {
            log.error("File not found error while loading", e);
            if (dialogOnSaveLoadErrors) {
                JOptionPane.showMessageDialog(null, "File not found exception while loading: "
                        + getFilename(), "Serialization Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        } catch (UnsupportedObjectException e) {
            log.error("Error deserializing world", e);
            if (dialogOnSaveLoadErrors) {
                JOptionPane.showMessageDialog(null, "Error serializing the object: " + e.toString(),
                        "Serialization Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        } catch (IOException e) {
            log.error("Exception while writing world", e);
            if (dialogOnSaveLoadErrors) {
                JOptionPane.showMessageDialog(null, "Error while reading world: " + e.toString(),
                        "Serialization Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        m_world = w;

        init(m_world, true);
        return;
    }

    public void setCamera(Vec2 argPos) {
        model.getDebugDraw().getViewportTranform().setCenter(argPos);
    }

    /**
     * Sets the current testbed camera
     *
     * @param argPos
     * @param scale
     */
    public void setCamera(Vec2 argPos, float scale) {
        model.getDebugDraw().setCamera(argPos.x, argPos.y, scale);
        hasCachedCamera = true;
        cachedCameraScale = scale;
        cachedCameraPos.set(argPos);
    }

    public abstract void initLevel(boolean deserialized);

    /**
     * The name of the test
     *
     * @return
     */
    public abstract String getLevelName();

    /**
     * called when the tests exits
     */
    public void exit() {
    }

    public void update() {
        if (resetPending) {
            _reset();
            resetPending = false;
        }
        if (savePending) {
            _save();
            savePending = false;
        }
        if (loadPending) {
            _load();
            loadPending = false;
        }

        m_textLine = 20;

        if (title != null) {
            model.getDebugDraw().drawString(model.getPanelWidth() / 2, 15, title, Color3f.WHITE);
            m_textLine += 15;
        }

        // process our input
        if (!inputQueue.isEmpty()) {
            synchronized (inputQueue) {
                while (!inputQueue.isEmpty()) {
                    QueueItem i = inputQueue.pop();
                    switch (i.type) {
                        case KeyPressed:
                            keyPressed(i.c, i.code);
                            break;
                        case KeyReleased:
                            keyReleased(i.c, i.code);
                            break;
                        case MouseDown:
                            mouseDown(i.p);
                            break;
                        case MouseMove:
                            mouseMove(i.p);
                            break;
                        case MouseUp:
                            mouseUp(i.p);
                            break;
                        case ShiftMouseDown:
                            shiftMouseDown(i.p);
                            break;
                    }
                }
            }
        }

        step();
    }

    private final Color3f color1 = new Color3f(.3f, .95f, .3f);
    private final Color3f color2 = new Color3f(.3f, .3f, .95f);
    private final Color3f color3 = new Color3f(.9f, .9f, .9f);
    private final Color3f color4 = new Color3f(.6f, .61f, 1);
    private final Color3f color5 = new Color3f(.9f, .9f, .3f);
    private final Color3f mouseColor = new Color3f(0f, 1f, 0f);
    private final Vec2 p1 = new Vec2();
    private final Vec2 p2 = new Vec2();
    private final Vec2 tangent = new Vec2();
    private final List<String> statsList = new ArrayList<String>();

    public synchronized void step() {
        float hz = 60;
        float timeStep = hz > 0f ? 1f / hz : 0;

        final DebugDraw debugDraw = model.getDebugDraw();


        int flags = 0;
        flags += DebugDraw.e_shapeBit;
        debugDraw.setFlags(flags);

        m_world.setAllowSleep(true);
        m_world.setWarmStarting(true);
        m_world.setSubStepping(true);
        m_world.setContinuousPhysics(true);

        pointCount = 0;

        m_world.step(timeStep, 10, 10);

        m_world.drawDebugData();

        if (timeStep > 0f) {
            ++stepCount;
        }

        if (mouseJoint != null) {
            mouseJoint.getAnchorB(p1);
            Vec2 p2 = mouseJoint.getTarget();
            debugDraw.drawSegment(p1, p2, mouseColor);
        }
    }

    public void queueShiftMouseDown(Vec2 p) {
        synchronized (inputQueue) {
            inputQueue.addLast(new QueueItem(QueueItemType.ShiftMouseDown, p));
        }
    }

    public void queueMouseUp(Vec2 p) {
        synchronized (inputQueue) {
            inputQueue.addLast(new QueueItem(QueueItemType.MouseUp, p));
        }
    }

    public void queueMouseDown(Vec2 p) {
        synchronized (inputQueue) {
            inputQueue.addLast(new QueueItem(QueueItemType.MouseDown, p));
        }
    }

    public void queueMouseMove(Vec2 p) {
        synchronized (inputQueue) {
            inputQueue.addLast(new QueueItem(QueueItemType.MouseMove, p));
        }
    }

    public void queueKeyPressed(char c, int code) {
        synchronized (inputQueue) {
            inputQueue.addLast(new QueueItem(QueueItemType.KeyPressed, c, code));
        }
    }

    public void queueKeyReleased(char c, int code) {
        synchronized (inputQueue) {
            inputQueue.addLast(new QueueItem(QueueItemType.KeyReleased, c, code));
        }
    }

    /**
     * Called when shift-mouse down occurs
     *
     * @param p
     */
    public void shiftMouseDown(Vec2 p) {
        mouseWorld.set(p);

        if (mouseJoint != null) {
            return;
        }
    }

    /**
     * Called for mouse-up
     *
     * @param p
     */
    public void mouseUp(Vec2 p) {
        if (mouseJoint != null) {
            m_world.destroyJoint(mouseJoint);
            mouseJoint = null;
        }
    }

    private final AABB queryAABB = new AABB();
    private final TestQueryCallback callback = new TestQueryCallback();

    /**
     * Called for mouse-down
     *
     * @param p
     */
    public void mouseDown(Vec2 p) {
        mouseWorld.set(p);

        if (mouseJoint != null) {
            return;
        }

        queryAABB.lowerBound.set(p.x - .001f, p.y - .001f);
        queryAABB.upperBound.set(p.x + .001f, p.y + .001f);
        callback.point.set(p);
        callback.fixture = null;
        m_world.queryAABB(callback, queryAABB);

        if (callback.fixture != null) {
            Body body = callback.fixture.getBody();
            MouseJointDef def = new MouseJointDef();
            def.bodyA = groundBody;
            def.bodyB = body;
            def.target.set(p);
            def.maxForce = 1000f * body.getMass();
            mouseJoint = (MouseJoint) m_world.createJoint(def);
            body.setAwake(true);
        }
    }

    /**
     * Called when mouse is moved
     *
     * @param p
     */
    public void mouseMove(Vec2 p) {
        mouseWorld.set(p);

        if (mouseJoint != null) {
            mouseJoint.setTarget(p);
        }
    }

    /**
     * Sets the title of the test
     *
     * @param argTitle
     */
    public void setTitle(String argTitle) {
        title = argTitle;
    }

    private final Vec2 p = new Vec2();
    private final Vec2 v = new Vec2();

    private final AABB aabb = new AABB();

    private final Vec2 vel = new Vec2();

    /**
     * Override to enable saving and loading. Remember to also override the {@link ObjectListener} and
     * {@link ObjectSigner} methods if you need to
     *
     * @return
     */
    public boolean isSaveLoadEnabled() {
        return false;
    }

    @Override
    public Long getTag(Body body) {
        return null;
    }

    @Override
    public Long getTag(Fixture fixture) {
        return null;
    }

    @Override
    public Long getTag(Joint joint) {
        return null;
    }

    @Override
    public Long getTag(Shape shape) {
        return null;
    }

    @Override
    public Long getTag(World world) {
        return null;
    }

    @Override
    public void processBody(Body body, Long tag) {
    }

    @Override
    public void processFixture(Fixture fixture, Long tag) {
    }

    @Override
    public void processJoint(Joint joint, Long tag) {
    }

    @Override
    public void processShape(Shape shape, Long tag) {
    }

    @Override
    public void processWorld(World world, Long tag) {
    }

    @Override
    public boolean isUnsupported(UnsupportedObjectException exception) {
        return true;
    }

    public void jointDestroyed(Joint joint) {
    }

    public void beginContact(Contact contact) {
    }

    public void endContact(Contact contact) {
    }

    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    private final PointState[] state1 = new PointState[Settings.maxManifoldPoints];
    private final PointState[] state2 = new PointState[Settings.maxManifoldPoints];
    private final WorldManifold worldManifold = new WorldManifold();

    public void preSolve(Contact contact, Manifold oldManifold) {
        Manifold manifold = contact.getManifold();

        if (manifold.pointCount == 0) {
            return;
        }

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Collision.getPointStates(state1, state2, oldManifold, manifold);

        contact.getWorldManifold(worldManifold);

        for (int i = 0; i < manifold.pointCount && pointCount < MAX_CONTACT_POINTS; i++) {
            ContactPoint cp = points[pointCount];
            cp.fixtureA = fixtureA;
            cp.fixtureB = fixtureB;
            cp.position.set(worldManifold.points[i]);
            cp.normal.set(worldManifold.normal);
            cp.state = state2[i];
            cp.normalImpulse = manifold.points[i].normalImpulse;
            cp.tangentImpulse = manifold.points[i].tangentImpulse;
            ++pointCount;
        }
    }

    public void keyPressed(char keyCar, int keyCode) {
    }

    public void keyReleased(char keyChar, int keyCode) {
    }
}


class TestQueryCallback implements QueryCallback {

    public final Vec2 point;
    public Fixture fixture;

    public TestQueryCallback() {
        point = new Vec2();
        fixture = null;
    }

    public boolean reportFixture(Fixture argFixture) {
        Body body = argFixture.getBody();
        if (body.getType() == BodyType.DYNAMIC) {
            boolean inside = argFixture.testPoint(point);
            if (inside) {
                fixture = argFixture;

                return false;
            }
        }

        return true;
    }
}


enum QueueItemType {
    MouseDown, MouseMove, MouseUp, ShiftMouseDown, KeyPressed, KeyReleased
}


class QueueItem {
    public QueueItemType type;
    public Vec2 p;
    public char c;
    public int code;

    public QueueItem(QueueItemType t, Vec2 pt) {
        type = t;
        p = pt;
    }

    public QueueItem(QueueItemType t, char cr, int cd) {
        type = t;
        c = cr;
        code = cd;
    }
}


class SignerAdapter implements ObjectSigner {
    private final ObjectSigner delegate;

    public SignerAdapter(ObjectSigner argDelegate) {
        delegate = argDelegate;
    }

    public Long getTag(World argWorld) {
        return delegate.getTag(argWorld);
    }

    public Long getTag(Body argBody) {
        return delegate.getTag(argBody);
    }

    public Long getTag(Shape argShape) {
        return delegate.getTag(argShape);
    }

    public Long getTag(Fixture argFixture) {
        return delegate.getTag(argFixture);
    }

    public Long getTag(Joint argJoint) {
        return delegate.getTag(argJoint);
    }
}


class ListenerAdapter implements ObjectListener {
    private final ObjectListener listener;

    public ListenerAdapter(ObjectListener argListener) {
        listener = argListener;
    }

    public void processWorld(World argWorld, Long argTag) {
        listener.processWorld(argWorld, argTag);
    }

    public void processBody(Body argBody, Long argTag) {
        listener.processBody(argBody, argTag);
    }

    public void processFixture(Fixture argFixture, Long argTag) {
        listener.processFixture(argFixture, argTag);
    }

    public void processShape(Shape argShape, Long argTag) {
        listener.processShape(argShape, argTag);
    }

    public void processJoint(Joint argJoint, Long argTag) {
        listener.processJoint(argJoint, argTag);
    }
}
