package svanimpe.fxgameloop.animatedballpit;

import javafx.css.PseudoClass;

import java.util.ArrayList;
import java.util.List;

public class CssAnimator {

    private List<PseudoClass> frames;
    private PseudoClassNode node;

    protected int getCurrentFrameNumber(){
        return currentFrame;
    }

    private int currentFrame = 0;
    private boolean animating = false;


    public CssAnimator(PseudoClassNode node, int numberOfFrames) {
        this.node = node;
        frames = new ArrayList<>(numberOfFrames);
        for (int i = 0; i < numberOfFrames; i++) {
            frames.add(PseudoClass.getPseudoClass("frame" + i));
        }

        node.changePseudoClassState(getCurrentFrame(), true);

    }

    public void startAnimation() {
        animating = true;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void stopAnimation() {
        animating = false;
    }

    public void nextFrame() {
        if (animating) {
            resetCurrentFrame();

            if (currentFrame + 1 < frames.size())
                currentFrame++;
            else
            {
                currentFrame = 0;
                animating = false;
            }

            node.changePseudoClassState(getCurrentFrame(), true);
        } else if (currentFrame != 0) {
            resetCurrentFrame();
            currentFrame = 0;
            node.changePseudoClassState(getCurrentFrame(), true);
        }


    }

    private void resetCurrentFrame() {
        node.changePseudoClassState(getCurrentFrame(), false);
    }

    private PseudoClass getCurrentFrame() {
        return frames.get(currentFrame);
    }
}
