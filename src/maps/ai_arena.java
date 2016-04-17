package maps;

import geometry.Point2f;
import geometry.Polygon;

import java.util.ArrayList;

/**
 * Created by Nathan on 4/17/2016.
 */
public class ai_arena extends Map {

    public ai_arena() {
        width = 2000;
        height = 1000;
        statics = new ArrayList<>();

        statics.add(new Polygon().makeAABB(500, 500, 1000, 50));

        // Spawns
        spawnpoints_blue = new ArrayList<>();
        spawnpoints_blue.add(new Point2f(550, 550));

        spawnpoints_red = new ArrayList<>();
        spawnpoints_red.add(new Point2f(1450, 550));
    }
}
