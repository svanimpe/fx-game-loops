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

package svanimpe.fxgameloop.animatedballpit;

import javafx.css.PseudoClass;
import javafx.scene.layout.Region;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

import java.util.Random;

import static svanimpe.fxgameloop.Main.*;

public class AnimatedBall extends Region implements PseudoClassNode {
    private static final Random random = new Random();

    private final Body body;

    private CssAnimator animator;

    public AnimatedBall(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;

        float x = random.nextFloat() * WIDTH;
        float y = HEIGHT + random.nextFloat() * HEIGHT * 2; /* A ball starts above the screen so we can watch it drop. */
        bodyDef.position = new Vec2(x, y);

        Shape shape = new CircleShape();
        shape.m_radius = 0.1f; /* 10cm */

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2f;
        fixtureDef.restitution = 0.6f;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setUserData(this);


        this.getStyleClass().add("ball");
        setMinHeight(SCALE * 0.2);
        setMinWidth(SCALE * 0.2);
        setOpacity(1);

        animator = new CssAnimator(this, 10);

        updatePosition();
    }

    public void updatePosition() {
        if (!body.isAwake()) {
            return;
        }
        if (animator.isAnimating()) {
            animator.nextFrame();
        }


        setTranslateX(body.getPosition().x * SCALE);
        setTranslateY((HEIGHT - body.getPosition().y) * SCALE);
    }

    public void interpolatePosition(float alpha) {
        if (!body.isAwake()) {
            return;
        }

        setTranslateX(alpha * body.getPosition().x * SCALE + (1 - alpha) * getTranslateX());
        setTranslateY(alpha * (HEIGHT - body.getPosition().y) * SCALE + (1 - alpha) * getTranslateY());
    }

    @Override
    public void changePseudoClassState(PseudoClass pseudoClass, boolean enabled) {
        this.pseudoClassStateChanged(pseudoClass, enabled);
    }

    public void startAnimation() {
        animator.startAnimation();
    }
}
