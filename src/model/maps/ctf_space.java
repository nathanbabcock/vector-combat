package model.maps;

import model.geometry.Point2f;
import model.geometry.Polygon;
import model.geometry.Vector2f;

import java.util.ArrayList;

/**
 * Created by Nathan on 3/17/2016.
 */
public class ctf_space extends Map {
    public ctf_space() {
        width = 5584;
        height = 1800;
        statics = new ArrayList<>();


        // RED SIDE
        statics.add(new Polygon().makeAABB(599, 819, 900, 55)); // Mid
        statics.add(new JumpPad().setVelocity(new Vector2f(0, 1).setMagnitude(500f)).makeAABB(629, 853, 52, 22)); // Jump pad
        statics.add(new Polygon().makeAABB(1208, 875, 60, 60)); // Crate

        statics.add(new Polygon().makeAABB(698, 1072, 400, 55)); // Flag platform
        redflag = new Point2f(849, 1127);

        statics.add(new Polygon().makeAABB(1600, 819, 128, 55)); // Upper jump platform
        statics.add(new JumpPad(new Point2f[]{new Point2f(1660, 875), new Point2f(1728, 875), new Point2f(1728, 934)}).setVelocity(new Vector2f(1, 1.5f).setMagnitude(600f))); // Jump pad

        statics.add(new Polygon().makeAABB(1504, 583, 560, 62)); // Lower jump platform
        statics.add(new JumpPad().setVelocity(new Vector2f(0, 1).setMagnitude(500f)).makeAABB(1504, 625, 65, 21)); // Up jump pad
        statics.add(new JumpPad(new Point2f[]{new Point2f(1997, 646), new Point2f(2065, 646), new Point2f(2065, 705)}).setVelocity(new Vector2f(1, 1.1f).setMagnitude(550f))); // Across jump pad

        statics.add(new Polygon().makeAABB(2065, 1048, 328, 62)); // Upper

        statics.add(new JumpPad(new Point2f[]{new Point2f(2396, 646), new Point2f(2396, 705), new Point2f(2464, 646)}).setVelocity(new Vector2f(-1, 1.1f).setMagnitude(550f))); // Left jump pad

        // MID
        statics.add(new Polygon().makeAABB(2396, 583, 790, 62)); // Mid

        // BLUE SIDE
        statics.add(new Polygon().makeAABB(4079, 819, 900, 55)); // Mid
        statics.add(new JumpPad().setVelocity(new Vector2f(0, 1).setMagnitude(500f)).makeAABB(4900, 853, 52, 22)); // Jump pad
        statics.add(new Polygon().makeAABB(4315, 875, 60, 60)); // Crate

        statics.add(new Polygon().makeAABB(4486, 1072, 400, 55)); // Flag platform
        blueflag = new Point2f(4650, 1127);

        statics.add(new Polygon().makeAABB(3854, 819, 128, 55)); // Upper jump platform
        statics.add(new JumpPad(new Point2f[]{new Point2f(3854, 875), new Point2f(3922, 875), new Point2f(3854, 934)}).setVelocity(new Vector2f(-1, 1.5f).setMagnitude(600f))); // Jump pad

        statics.add(new Polygon().makeAABB(3517, 583, 560, 62)); // Lower jump platform
        statics.add(new JumpPad().setVelocity(new Vector2f(0, 1).setMagnitude(500f)).makeAABB(4013, 625, 65, 21)); // Up jump pad
        statics.add(new JumpPad(new Point2f[]{new Point2f(3517, 646), new Point2f(3584, 646), new Point2f(3517, 705)}).setVelocity(new Vector2f(-1, 1.1f).setMagnitude(550f))); // Across jump pad

        statics.add(new Polygon().makeAABB(3186, 1048, 328, 62)); // Upper

        statics.add(new JumpPad(new Point2f[]{new Point2f(3118, 646), new Point2f(3186, 705), new Point2f(3186, 646)}).setVelocity(new Vector2f(1, 1.1f).setMagnitude(550f))); // Right jump pad

        // Spawns
        spawnpoints_blue = new ArrayList<>();
        spawnpoints_blue.add(new Point2f(889, 977));

        spawnpoints_red = new ArrayList<>();
        spawnpoints_red.add(new Point2f(889, 977));
    }
}
