package svanimpe.fxgameloop.loop;

import java.util.function.Consumer;

/*
 * Game loop implementation using variable time steps (with a maximum time step, inherited from the
 * GameLoop superclass). See the blog post for details.
 */
public class VariableSteps extends GameLoop
{
    private final Consumer<Float> updater;
    private final Runnable renderer;
    private final Consumer<Integer> fpsReporter;

    public VariableSteps(Consumer<Float> updater, Runnable renderer, Consumer<Integer> fpsReporter)
    {
        this.updater = updater;
        this.renderer = renderer;
        this.fpsReporter = fpsReporter;
    }
    
    private long previousTime = 0;
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
        previousTime = currentTime;

        updater.accept(secondsElapsedCapped);
        renderer.run();

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
        secondsElapsedSinceLastFpsUpdate = 0f;
        framesSinceLastFpsUpdate = 0;
        super.stop();
    }
    
    @Override
    public String toString()
    {
        return "Variable time steps";
    }
}
