package svanimpe.fxgameloop.animatedballpit;

import javafx.css.PseudoClass;
import javafx.scene.Parent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CssAnimatorTest {

    @Test
    public void testStartAnimation() throws Exception {
        CssAnimator animatedNode = new CssAnimator((pseudoClass, enabled) -> {

        },3);

        assertThat(animatedNode.getCurrentFrameNumber(), is(0));

        animatedNode.nextFrame();
        assertThat(animatedNode.getCurrentFrameNumber(), is(0));

        animatedNode.startAnimation();

        animatedNode.nextFrame();

        assertThat(animatedNode.getCurrentFrameNumber(), is(1));

        animatedNode.nextFrame();
        animatedNode.nextFrame();

        assertThat(animatedNode.getCurrentFrameNumber(), is(0));
    }

    @Test
    public void testStopAnimation() throws Exception {
        CssAnimator animatedNode = new CssAnimator((pseudoClass, enabled) -> {

        }, 3);

        animatedNode.startAnimation();
        animatedNode.nextFrame();
        animatedNode.stopAnimation();
        animatedNode.nextFrame();

        assertThat(animatedNode.getCurrentFrameNumber(), is(0));
    }

    @Test
    public void nodeTest() throws Exception {
        TestNode node = new TestNode();
        CssAnimator animator = new CssAnimator(node, 3);

        animator.nextFrame();

        assertEnabledPseudoClassIs(node, "frame0");

        animator.startAnimation();
        animator.nextFrame();
        assertEnabledPseudoClassIs(node, "frame1");

        animator.nextFrame();
        assertEnabledPseudoClassIs(node, "frame2");

        animator.nextFrame();
        assertEnabledPseudoClassIs(node, "frame0");
    }

    private void assertEnabledPseudoClassIs(TestNode node, String pseudoClass) {
        assertThat(node.getPseudoClassStates().size(), is(1));
        assertThat(node.getEnabledPseudoClasses().get(0), is(pseudoClass));
    }

    private class TestNode extends Parent implements PseudoClassNode{

        @Override
        public void changePseudoClassState(PseudoClass pseudoClass, boolean enabled) {
            this.pseudoClassStateChanged(pseudoClass, enabled);
        }

        public List<String> getEnabledPseudoClasses(){
            List<String> list = new ArrayList<>();
            for (PseudoClass pseudoClass : getPseudoClassStates()) {
                list.add(pseudoClass.getPseudoClassName());
            }

            return list;
        }

    }
}
