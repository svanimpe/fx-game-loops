package svanimpe.fxgameloop.game;

import javafx.scene.layout.Pane;
import org.jbox2d.dynamics.World;

public interface Game
{
    /*
     * Load the game into the given world. The game's nodes are to be placed on the given pane. The
     * nrOfObjects parameter can be used to control the complexity of the game.
     */
    void load(World world, Pane pane, int nrOfObjects);
    
    /*
     * Updates the position of all the nodes in the game based on their physics state.
     */
    void updatePositions();
    
    /*
     * Interpolates the position of every node in the game between their current value and their
     * next value as indicated by their physics state (which in this case will be one step ahead).
     */
    void interpolatePositions(float alpha);
}
