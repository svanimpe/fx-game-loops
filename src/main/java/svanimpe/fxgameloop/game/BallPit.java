package svanimpe.fxgameloop.game;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import static svanimpe.fxgameloop.Main.HEIGHT;
import static svanimpe.fxgameloop.Main.WIDTH;

/*
 * Game/animation that drops a given number of bouncy balls into a pit.
 */
public class BallPit implements Game
{
    private final List<Ball> balls = new ArrayList<>();
    
    @Override
    public void load(World world, Pane pane, int nrOfObjects)
    {
        BodyDef floorDef = new BodyDef();
        floorDef.position.set(WIDTH / 2, -1f);
        Body floor = world.createBody(floorDef);
        PolygonShape floorShape = new PolygonShape();
        floorShape.setAsBox(WIDTH / 2, 1f);
        floor.createFixture(floorShape, 0f);

        BodyDef leftWallDef = new BodyDef();
        leftWallDef.position.set(-1f, HEIGHT);
        BodyDef rightWallDef = new BodyDef();
        rightWallDef.position.set(WIDTH + 1, HEIGHT);
        Body leftWall = world.createBody(leftWallDef);
        Body rightWall = world.createBody(rightWallDef);
        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(1f, HEIGHT * 2);
        leftWall.createFixture(wallShape, 0f);
        rightWall.createFixture(wallShape, 0f);
        
        balls.clear();
        for (int i = 0; i < nrOfObjects; i++) {
            Ball ball = new Ball(world);
            balls.add(ball);
            pane.getChildren().add(ball);
        }
    }
    
    @Override
    public void updatePositions()
    {
        balls.forEach(Ball::updatePosition);
    }
    
    @Override
    public void interpolatePositions(float alpha)
    {
        balls.forEach(ball -> ball.interpolatePosition(alpha));
    }
    
    @Override
    public String toString()
    {
        return "Ball pit";
    }
}
