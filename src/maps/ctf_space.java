package maps;

import characters.Team;
import core.Game;
import entities.Flag;
import geometry.Point2f;
import geometry.Polygon;
import geometry.Vector2f;
import view.Canvas;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Nathan on 3/17/2016.
 */
public class ctf_space extends Map {
    BufferedImage background, clouds, level;// bg_a, bg_b;

    public ctf_space() {
        width = 5584;
        height = 1800;
        statics = new ArrayList<>();

        // RED SIDE
        statics.add(new Polygon().makeAABB(599, 819, 900, 55)); // Mid
        statics.add(new JumpPad().setVelocity(new Vector2f(0, 1).setMagnitude(500f)).makeAABB(629, 853, 52, 22)); // Jump pad
        statics.add(new Polygon().makeAABB(1208, 874, 60, 60)); // Crate

        statics.add(new Polygon().makeAABB(698, 1072, 400, 55)); // Flag platform
        //redflag = new Point2f(849, 1127);

        statics.add(new Polygon().makeAABB(1600, 819, 128, 55)); // Upper jump platform
        statics.add(new JumpPad(new Point2f[]{new Point2f(1660, 874), new Point2f(1728, 874), new Point2f(1728, 934)}).setVelocity(new Vector2f(1, 1.5f).setMagnitude(600f))); // Jump pad

        statics.add(new Polygon().makeAABB(1504, 583, 560, 62)); // Lower jump platform
        statics.add(new JumpPad().setVelocity(new Vector2f(0, 1).setMagnitude(500f)).makeAABB(1504, 625, 65, 21)); // Up jump pad
        statics.add(new JumpPad(new Point2f[]{new Point2f(1997, 645), new Point2f(2065, 645), new Point2f(2065, 705)}).setVelocity(new Vector2f(1, 1.1f).setMagnitude(550f))); // Across jump pad

        statics.add(new Polygon().makeAABB(2065, 1048, 328, 62)); // Upper

        // MID
        statics.add(new Polygon().makeAABB(2396, 583, 790, 62)); // Mid
        statics.add(new JumpPad(new Point2f[]{new Point2f(2396, 645), new Point2f(2396, 705), new Point2f(2464, 645)}).setVelocity(new Vector2f(-1, 1.1f).setMagnitude(550f))); // Left jump pad
        statics.add(new JumpPad(new Point2f[]{new Point2f(3118, 645), new Point2f(3186, 705), new Point2f(3186, 645)}).setVelocity(new Vector2f(1, 1.1f).setMagnitude(550f))); // Right jump pad

        // BLUE SIDE
        statics.add(new Polygon().makeAABB(4079, 819, 900, 55)); // Mid
        statics.add(new JumpPad().setVelocity(new Vector2f(0, 1).setMagnitude(500f)).makeAABB(4900, 853, 52, 22)); // Jump pad
        statics.add(new Polygon().makeAABB(4315, 874, 60, 60)); // Crate

        statics.add(new Polygon().makeAABB(4486, 1072, 400, 55)); // Flag platform
        //blueflag = new Point2f(4650, 1127);

        statics.add(new Polygon().makeAABB(3854, 819, 128, 55)); // Upper jump platform
        statics.add(new JumpPad(new Point2f[]{new Point2f(3854, 874), new Point2f(3922, 874), new Point2f(3854, 934)}).setVelocity(new Vector2f(-1, 1.5f).setMagnitude(600f))); // Jump pad

        statics.add(new Polygon().makeAABB(3517, 583, 560, 62)); // Lower jump platform
        statics.add(new JumpPad().setVelocity(new Vector2f(0, 1).setMagnitude(500f)).makeAABB(4013, 625, 65, 21)); // Up jump pad
        statics.add(new JumpPad(new Point2f[]{new Point2f(3517, 645), new Point2f(3584, 645), new Point2f(3517, 705)}).setVelocity(new Vector2f(-1, 1.1f).setMagnitude(550f))); // Across jump pad

        statics.add(new Polygon().makeAABB(3186, 1048, 328, 62)); // Upper

        // Spawns
        spawnpoints_blue = new ArrayList<>();
        spawnpoints_blue.add(new Point2f(4674, 977));

        spawnpoints_red = new ArrayList<>();
        spawnpoints_red.add(new Point2f(889, 977));

        // Backgrounds
        background = null;
        clouds = null;
        try {
            background = ImageIO.read(Game.class.getResourceAsStream("/res/maps/ctf_space/background.png"));
            background = Map.getCompatibleImage(background, Transparency.OPAQUE);
            clouds = ImageIO.read(Game.class.getResourceAsStream("/res/maps/ctf_space/clouds.png"));
            clouds = Map.getCompatibleImage(clouds, Transparency.TRANSLUCENT);
            level = ImageIO.read(Game.class.getResourceAsStream("/res/maps/ctf_space/ctf_space.png"));
            level = Map.getCompatibleImage(level, Transparency.TRANSLUCENT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Flags
        redflag = new Flag(null, Team.RED).setSpawn(new Point2f(874, 1127));
        blueflag = new Flag(null, Team.BLUE).setSpawn(new Point2f(4676, 1127));
    }


    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
        Graphics2D bgCanvas = (Graphics2D) g2.create();
        bgCanvas.setClip(0, 0, canvas.getWidth(), canvas.getHeight());
        bgCanvas.translate((canvas.getWidth() - background.getWidth()) / 2, (canvas.getHeight() - background.getHeight()) / 2);
        //g2.drawImage(bg_a, 0, 0, null);
        bgCanvas.drawImage(background, 0, 0, null);
        bgCanvas.dispose();
//        g2.drawImage(clouds, canvas.cameraOffsetX / 2, 0, null);


        Graphics2D levelCanvas = (Graphics2D) g2.create();
        levelCanvas.translate(canvas.cameraOffsetX, canvas.getHeight() - level.getHeight() - canvas.cameraOffsetY);
        levelCanvas.translate(600, -559);
        levelCanvas.drawImage(level, 0, 0, null);
        levelCanvas.dispose();

        super.draw(canvas, g2);
    }
}
