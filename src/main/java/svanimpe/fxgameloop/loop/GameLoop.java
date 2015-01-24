package svanimpe.fxgameloop.loop;

import javafx.animation.AnimationTimer;

/*
 * Abstract superclass for all GameLoop subclasses. Holds the common maximumStep property.
 */
public abstract class GameLoop extends AnimationTimer
{
    private float maximumStep = Float.MAX_VALUE;

    public float getMaximumStep()
    {
        return maximumStep;
    }

    public void setMaximumStep(float maximumStep)
    {
        this.maximumStep = maximumStep;
    }
}
