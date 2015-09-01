package model;

import view.Canvas;

import java.awt.*;

/**
 * Created by Nathan on 8/31/2015.
 */
public interface Entity {
    void update(float deltaTime);

    void draw(Canvas canvas, Graphics2D g2);
}
