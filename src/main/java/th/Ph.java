package th;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.testbed.framework.*;
import org.jbox2d.testbed.framework.j2d.TestPanelJ2D;

import javax.swing.*;

public class Ph extends TestbedTest {

    Body robot;

    @Override
    public void initTest(boolean deserialized) {
        setTitle("Couple of Things Test");

        getWorld().setGravity(new Vec2());
        {
            CircleShape shape = new CircleShape();
            shape.setRadius(4f);
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.STATIC;
            bodyDef.position.set(0, 0);
            bodyDef.angle = (float) (Math.PI * 2 * Math.random());
            bodyDef.allowSleep = false;
            bodyDef.linearDamping = 10f;
            bodyDef.angularDamping = 8f;
            Body body = getWorld().createBody(bodyDef);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.friction = 0.8f;
            body.createFixture(fixtureDef);
        }
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.DYNAMIC;
            bodyDef.position.set(10, 0);
            bodyDef.angle = (float) (Math.PI * 2 * Math.random());
            bodyDef.allowSleep = false;
            bodyDef.linearDamping = 10f;
            bodyDef.angularDamping = 30f;
            Body body = getWorld().createBody(bodyDef);
            {
                PolygonShape shape = new PolygonShape();
                shape.setAsBox(1, 1);
                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.friction = 0.8f;
                fixtureDef.density = 1f;
                body.createFixture(fixtureDef);
            }

            robot = body;
        }

        {
            CircleShape shape = new CircleShape();
            shape.setRadius(0.3f);
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyType.DYNAMIC;
            bodyDef.position.set(0, 10);
            bodyDef.angle = (float) (Math.PI * 2 * Math.random());
            bodyDef.allowSleep = false;
            bodyDef.linearDamping = 10f*5;
            bodyDef.angularDamping = 8f*5;
            Body body = getWorld().createBody(bodyDef);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.friction = 0.8f;
            fixtureDef.density = 1000f;
            body.createFixture(fixtureDef);
        }

    }

    @Override
    public synchronized void step(TestbedSettings settings) {
        float a = robot.getAngle();
        Vec2 force = new Vec2((float) Math.cos(a), (float) Math.sin(a));
        force.normalize();
        force = force.mul(500 / 2);
        robot.applyForce(force, robot.getWorldPoint(new Vec2(0, 1)));
        robot.applyForce(force, robot.getWorldPoint(new Vec2(0, -1)));

        super.step(settings);
    }

    @Override
    public String getTestName() {
        return "Couple of Things";
    }

    public static void main(String[] args) {
        TestbedModel model = new TestbedModel();         // create our model

//        TestList.populateModel(model);                   // populate the provided testbed tests
        model.addCategory("My Super Tests");             // add a category
        model.addTest(new Ph());                // add our test

        model.getSettings().addSetting(new TestbedSetting("My Range Setting", TestbedSetting.SettingType.ENGINE, 10, 0, 20));
        TestbedPanel panel = new TestPanelJ2D(model);    // create our testbed panel
        JFrame testbed = new TestbedFrame(model, panel, TestbedController.UpdateBehavior.UPDATE_CALLED); // put both into our testbed frame

        testbed.setVisible(true);
        testbed.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
