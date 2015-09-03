package model;

import java.awt.*;

/**
 * Created by Nathan on 8/30/2015.
 */
public class Fire extends Particle {
    public Fire(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Color changing over time (yellow -> red -> black)
        int green = color.getGreen();
        int red = color.getRed();
        if (green > 0)
            green -= (255 / 0.5) * deltaTime;
        else if (red > 0)
            red -= (255 / 0.5) * deltaTime;
        if (green <= 0)
            green = 0;
        if (red <= 0)
            red = 0;
        color = new Color(red, green, 0);
    }
}
