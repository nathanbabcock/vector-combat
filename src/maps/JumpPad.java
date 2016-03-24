package maps;

import geometry.Point2f;
import geometry.Polygon;
import geometry.Vector2f;
import view.Canvas;

import java.awt.*;

/**
 * Created by Nathan on 3/17/2016.
 */
public class JumpPad extends Polygon {
    public Vector2f velocity;

    public JumpPad() {
        super();
    }

    public JumpPad(Point2f[] points) {
        super(points);
    }

    public JumpPad setVelocity(Vector2f velocity) {
        this.velocity = velocity;
        return this;
    }


    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillPolygon(getAwtPoly());
    }
}
