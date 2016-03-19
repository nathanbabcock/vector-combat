package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 8/30/2015.
 */
public class Sprite {
    public int width, height, offsetX, offsetY, rotationX, rotationY;
    public String name, next;
    public boolean interruptible;
    public float time;
    public BufferedImage image;

    public Sprite copy() {
        Sprite sprite = new Sprite(name);
        sprite.width = width;
        sprite.height = height;
        sprite.offsetX = offsetX;
        sprite.offsetY = offsetY;
        sprite.rotationX = rotationX;
        sprite.rotationY = rotationY;
        sprite.next = next;
        sprite.interruptible = interruptible;
        sprite.time = time;
        sprite.image = image;
        return sprite;
    }

    public Sprite setName(String name) {
        this.name = name;
        return this;
    }

    public Sprite(String name) {
        this.name = name;
        interruptible = true;
    }

    public Sprite setImage(BufferedImage spriteSheet, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        image = spriteSheet.getSubimage(x, y, width, height);
        return this;
    }

    /**
     * Convenience, debug only function allowing individual sprite images instead of a spritesheet
     *
     * @return
     */
    public Sprite setImage() {
        try {
            image = ImageIO.read(Game.class.getResourceAsStream("/res/sprites/" + name + ".png"));
            width = image.getWidth();
            height = image.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public Sprite setRotationOrigin(int x, int y) {
        rotationX = x;
        rotationY = y;
        return this;
    }

    /**
     * Defines the offset between the BOTTOM LEFT CORNER of hitbox and sprite. (offsetX, offsetY) should be a vector
     * pointing from the hitbox to the sprite
     *
     * @param hitboxX
     * @param hitboxY
     * @return
     */
    public Sprite setOffset(int hitboxX, int hitboxY) {
        this.offsetX = hitboxX;
        this.offsetY = hitboxY;
        return this;
    }

    public Sprite setNext(String next) {
        this.next = next;
        return this;
    }

    public Sprite setTime(float time) {
        this.time = time;
        return this;
    }

    public Sprite setInterruptible(boolean b) {
        interruptible = b;
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // STATIC
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static List<Sprite> sprites;

    public static Sprite getSprite(String id) {
        if (id == null)
            return null;

        for (Sprite s : sprites)
            if (s.name.equals(id))
                return s;
        System.err.println("Error: could not find requested sprite: " + id);
        return null;
    }

    public static void initSprites() {
        System.out.println("Beginning sprite init");

//        BufferedImage spriteSheet = null;
//        try {
//            spriteSheet = ImageIO.read(Game.class.getResourceAsStream("/res/sprites/spritesheet.png"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        sprites = new ArrayList<>();

        // Newest ninja
        final float NINJA_RUN_TIME = 0.05f;

        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("ninja_crouch_" + team)
                    .setImage()
                    .setOffset(-14, 0));
            sprites.add(new Sprite("ninja_body_" + team)
                    .setImage()
                    .setOffset(1, 18));
        }

        // Run cycle - first half
        sprites.add(new Sprite("ninja_legs_1")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-11, 0)
                .setNext("ninja_legs_2"));
        sprites.add(new Sprite("ninja_legs_2")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-10, 2)
                .setNext("ninja_legs_3"));
        sprites.add(new Sprite("ninja_legs_3")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-16, 0)
                .setNext("ninja_legs_4"));
        sprites.add(new Sprite("ninja_legs_4")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-4, 0)
                .setNext("ninja_legs_5"));
        sprites.add(new Sprite("ninja_legs_5")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(4, 0)
                .setNext("ninja_legs_6"));
        sprites.add(new Sprite("ninja_legs_6")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-5, 0)
                .setNext("ninja_legs_7"));
        sprites.add(new Sprite("ninja_legs_7")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-12, 0)
                .setNext("ninja_legs_8"));

        // Run cycle - second half
        sprites.add(new Sprite("ninja_legs_8")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-11, 0)
                .setNext("ninja_legs_9"));
        sprites.add(new Sprite("ninja_legs_9")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-10, 2)
                .setNext("ninja_legs_10"));
        sprites.add(new Sprite("ninja_legs_10")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-16, 0)
                .setNext("ninja_legs_11"));
        sprites.add(new Sprite("ninja_legs_11")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-4, 0)
                .setNext("ninja_legs_12"));
        sprites.add(new Sprite("ninja_legs_12")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(4, 0)
                .setNext("ninja_legs_13"));
        sprites.add(new Sprite("ninja_legs_13")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-5, 0)
                .setNext("ninja_legs_14"));
        sprites.add(new Sprite("ninja_legs_14")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-12, 0)
                .setNext("ninja_legs_1"));

        // Attack cycle - running
        final float NINJA_ATTACK_TIME = 0.5f / 8;
        sprites.add(new Sprite("ninja_arm")
                .setImage()
                .setOffset(-55, 25));
        sprites.add(new Sprite("ninja_arm_attack_1")
                .setImage()
                .setOffset(-6, 32)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_arm_attack_2")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_arm_attack_2")
                .setImage()
                .setOffset(36, 24)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_arm_attack_3")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_arm_attack_3")
                .setImage()
                .setOffset(33, 11)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_arm_attack_4")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_arm_attack_4")
                .setImage()
                .setOffset(29, 7)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_arm_attack_5")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_arm_attack_5")
                .setImage()
                .setOffset(15, -21)
                .setTime(NINJA_ATTACK_TIME * 3)
                //.setNext("ninja_arm_attack_1")
                .setInterruptible(false));

        // TODO this is redundant
        // Attack cycle - standing
        sprites.add(new Sprite("ninja_stand_arm_attack_1")
                .setImage()
                .setOffset(-14, 39)//(-6, 32)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_stand_arm_attack_2")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_stand_arm_attack_2")
                .setImage()
                .setOffset(23, 31)//(36, 24) // (-13, +7)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_stand_arm_attack_3")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_stand_arm_attack_3")
                .setImage()
                .setOffset(20, 18)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_stand_arm_attack_4")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_stand_arm_attack_4")
                .setImage()
                .setOffset(13, 14)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_stand_arm_attack_5")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_stand_arm_attack_5")
                .setImage()
                .setOffset(2, -14)
                .setTime(NINJA_ATTACK_TIME * 3)
                //.setNext("ninja_stand_arm_attack_1")
                .setInterruptible(false));

        // Ninja miscellaneous
        sprites.add(new Sprite("ninja_arm_grapple")
                .setImage()
                .setOffset(28, 15));
        sprites.add(new Sprite("ninja_arm_grapple_2")
                .setImage()
                .setOffset(29, 30));
        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("ninja_stand_" + team)
                    .setImage()
                    .setOffset(4, 0));
            sprites.add(new Sprite("ninja_parry_" + team)
                    .setImage()
                    .setOffset(0, 0));
            sprites.add(new Sprite("ninja_kick_" + team)
                    .setImage()
                    .setOffset(-35, 0));
            sprites.add(new Sprite("ninja_jump_" + team)
                    .setImage()
                    .setRotationOrigin(29, 0) // ??
                    .setOffset(-6, 0));
        }

        // Generic legs
        final float LEG_TIME = 0.7f / 8;
        sprites.add(new Sprite("legs_stand")
                .setImage()
                .setOffset(0, 0));
        sprites.add(new Sprite("legs_walk_1")
                .setImage()
                .setOffset(0, 0)
                .setNext("legs_walk_2")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_2")
                .setImage()
                .setOffset(-7, 0)
                .setNext("legs_walk_3")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_3")
                .setImage()
                .setOffset(-7, 0)
                .setNext("legs_walk_4")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_4")
                .setImage()
                .setOffset(-4, 0)
                .setNext("legs_walk_5")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_5")
                .setImage()
                .setOffset(0, 0)
                .setNext("legs_walk_6")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_6")
                .setImage()
                .setOffset(-7, 0)
                .setNext("legs_walk_7")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_7")
                .setImage()
                .setOffset(-7, 0)
                .setNext("legs_walk_8")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_8")
                .setImage()
                .setOffset(-4, 0)
                .setNext("legs_walk_1")
                .setTime(LEG_TIME));

        // Commando
        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("commando_body_" + team)
                    .setImage()
                    .setOffset(-6, 31));
            sprites.add(new Sprite("commando_gun_" + team)
                    .setImage()
                    .setOffset(-1, 42));
        }


        // Rocketman
        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("rocketman_body_" + team)
                    .setImage()
                    .setOffset(-1, 31));
            sprites.add(new Sprite("rocketman_launcher_" + team)
                    .setImage()
                    .setOffset(-11, 41));
        }

        // Scout
        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("scout_walljump_" + team)
                    .setImage()
                    .setOffset(-4, -3));
            sprites.add(new Sprite("scout_body_" + team)
                    .setImage()
                    .setOffset(3, 30));
            sprites.add(new Sprite("scout_run_body_" + team)
                    .setImage()
                    .setOffset(1, 31));
        }
        sprites.add(new Sprite("scout_gun")
                .setImage()
                .setOffset(-3, 48));
        sprites.add(new Sprite("scout_gun_onehand")
                .setImage()
                .setOffset(-3, 48));
        sprites.add(new Sprite("scout_head")
                .setImage()
                .setOffset(2, 67));

        // Scout legs
        sprites.add(new Sprite("scout_legs_stand")
                .setImage()
                .setOffset(1, -4));
        sprites.add(new Sprite("scout_legs_run_1")
                .setImage()
                .setOffset(-23, 0)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_2"));
        sprites.add(new Sprite("scout_legs_run_2")
                .setImage()
                .setOffset(-22, 11)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_3"));
        sprites.add(new Sprite("scout_legs_run_3")
                .setImage()
                .setOffset(-23, 15)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_4"));
        sprites.add(new Sprite("scout_legs_run_4")
                .setImage()
                .setOffset(-19, 0)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_5"));
        sprites.add(new Sprite("scout_legs_run_5")
                .setImage()
                .setOffset(-10, -1)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_6"));
        sprites.add(new Sprite("scout_legs_run_6")
                .setImage()
                .setOffset(-6, 0)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_7"));
        sprites.add(new Sprite("scout_legs_run_7")
                .setImage()
                .setOffset(1, -1)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_8"));
        sprites.add(new Sprite("scout_legs_run_8")
                .setImage()
                .setOffset(-3, 0)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_1"));

        // Menu head sprites
        sprites.add(getSprite("scout_head").copy()
                .setName("scout_head_menu")
                .setOffset(-8, 0));
        sprites.add(new Sprite("commando_head_red")
                .setImage()
                .setOffset(-2, -2));
        sprites.add(new Sprite("commando_head_blue")
                .setImage()
                .setOffset(-2, -2));
        sprites.add(new Sprite("ninja_head_red")
                .setImage());
        sprites.add(new Sprite("ninja_head_blue")
                .setImage());
        sprites.add(new Sprite("rocketman_head")
                .setImage());

        // Flag
        sprites.add(new Sprite("flag_red")
                .setImage());
        sprites.add(new Sprite("flag_blue")
                .setImage());

        System.out.println("Finished sprite init");

    }
}
