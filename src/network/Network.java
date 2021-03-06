package network;

import characters.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.minlog.Log;
import core.Game;
import core.Player;
import entities.Bullet;
import entities.Flag;
import entities.Grapple;
import entities.Rocket;
import geometry.Point2f;
import geometry.Polygon;
import geometry.Vector2f;
import maps.Map1;
import maps.Map2;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nathan on 1/10/2016.
 */
public class Network {
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;

    public static final float TIMESCALE = 1f;

    public static void register(EndPoint endpoint) {
/*        try {
            PrintStream out = new PrintStream(new FileOutputStream("log.txt"));
            System.setOut(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        Log.set(Log.LEVEL_WARN);

        Kryo kryo = endpoint.getKryo();

        // General
        kryo.register(Game.class);
        kryo.register(ArrayList.class);
        kryo.register(CopyOnWriteArrayList.class);

        // Players
        kryo.register(Player.class);
        kryo.register(CharClass.class);
        kryo.register(Ninja.class);
        kryo.register(Rocketman.class);
        kryo.register(Scout.class);
        kryo.register(Commando.class);
        kryo.register(Team.class);

        // Entities
        kryo.register(Bullet.class);
        kryo.register(Grapple.class);
        kryo.register(Rocket.class);
        kryo.register(Flag.class);

        // Geometry
        kryo.register(Point2f.class);
        kryo.register(Vector2f.class);
        kryo.register(Polygon.class);

        // Network
        kryo.register(SpawnParams.class);
        kryo.register(InputState.class);
        kryo.register(ChatMessage.class);
        kryo.register(Date.class);

        // Maps
        kryo.register(Map1.class);
        kryo.register(Map2.class);
    }
}
