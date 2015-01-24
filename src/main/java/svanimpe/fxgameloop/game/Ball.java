package svanimpe.fxgameloop.game;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import static svanimpe.fxgameloop.Main.SCALE;
import static svanimpe.fxgameloop.Main.WIDTH;
import static svanimpe.fxgameloop.Main.HEIGHT;

public class Ball extends Circle
{
    private static final Random random = new Random();
    
    private final Body body;
    
    public Ball(World world)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        
        float x = random.nextFloat() * WIDTH;
        float y = HEIGHT + random.nextFloat() * HEIGHT * 2; /* A ball starts above the screen so we can watch it drop. */
        bodyDef.position = new Vec2(x, y);
        
        Shape shape = new CircleShape();
        shape.m_radius = 0.1f; /* 10cm */
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.restitution = 0.6f;
        
        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        
        setRadius(SCALE * 0.1);
        setFill(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        
        updatePosition();
    }

    public void updatePosition()
    {
        if (!body.isAwake()) {
            return;
        }

        setTranslateX(body.getPosition().x * SCALE);
        setTranslateY((HEIGHT - body.getPosition().y) * SCALE);
    }

    public void interpolatePosition(float alpha)
    {
        if (!body.isAwake()) {
            return;
        }

        setTranslateX(alpha * body.getPosition().x * SCALE + (1 - alpha) * getTranslateX());
        setTranslateY(alpha * (HEIGHT - body.getPosition().y) * SCALE + (1- alpha) * getTranslateY());
    }
}
