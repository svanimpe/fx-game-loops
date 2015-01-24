package svanimpe.fxgameloop.loop;

import java.util.function.Consumer;

/*
 * Game loop implementation using fixed time steps (with a maximum time step, inherited from the
 * GameLoop superclass) and interpolation. See the blog post for details.
 */
public class FixedStepsWithInterpolation extends GameLoop
{
    private final Consumer<Float> updater;
    private final Runnable renderer;
    private final Consumer<Float> interpolater;
    private final Consumer<Integer> fpsReporter;

    public FixedStepsWithInterpolation(Consumer<Float> updater, Runnable renderer, Consumer<Float> interpolater, Consumer<Integer> fpsReporter)
    {
        this.updater = updater;
        this.renderer = renderer;
        this.interpolater = interpolater;
        this.fpsReporter = fpsReporter;
    }
 
    private static final float timeStep = 0.0166f;
    
    private long previousTime = 0;
    private float accumulatedTime = 0;
    
    private float secondsElapsedSinceLastFpsUpdate = 0f;
    private int framesSinceLastFpsUpdate = 0;
    
    @Override
    public void handle(long currentTime)
    {
        if (previousTime == 0) {
            previousTime = currentTime;
            return;
        }

        float secondsElapsed = (currentTime - previousTime) / 1e9f; /* nanoseconds to seconds */
        float secondsElapsedCapped = Math.min(secondsElapsed, getMaximumStep());
        accumulatedTime += secondsElapsedCapped;
        previousTime = currentTime;

        if (accumulatedTime < timeStep) {
            float remainderOfTimeStepSincePreviousInterpolation = timeStep - (accumulatedTime - secondsElapsed);
            float alphaInRemainderOfTimeStep = secondsElapsed / remainderOfTimeStepSincePreviousInterpolation;
            interpolater.accept(alphaInRemainderOfTimeStep);
            return;
        }

        while (accumulatedTime >= 2 * timeStep) {
            updater.accept(timeStep);
            accumulatedTime -= timeStep;
        }
        renderer.run();
        updater.accept(timeStep);
        accumulatedTime -= timeStep;
        float alpha = accumulatedTime / timeStep;
        interpolater.accept(alpha);

        secondsElapsedSinceLastFpsUpdate += secondsElapsed;
        framesSinceLastFpsUpdate++;
        if (secondsElapsedSinceLastFpsUpdate >= 0.5f) {
            int fps = Math.round(framesSinceLastFpsUpdate / secondsElapsedSinceLastFpsUpdate);
            fpsReporter.accept(fps);
            secondsElapsedSinceLastFpsUpdate = 0;
            framesSinceLastFpsUpdate = 0;
        }
    }

    @Override
    public void stop()
    {
        previousTime = 0;
        accumulatedTime = 0;
        secondsElapsedSinceLastFpsUpdate = 0f;
        framesSinceLastFpsUpdate = 0;
        super.stop();
    }
    
    @Override
    public String toString()
    {
        return "Fixed time steps with interpolation";
    }
}
