/*
 * Copyright (c) 2014, Steven Van Impe
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *     following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
