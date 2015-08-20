import java.awt.*;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Player {
    int x, y, width, height;

    public Rectangle getHitBox() {
        return new Rectangle(x, y, width, height);
    }
}
