package model.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.minlog.Log;
import model.Game;
import model.Player;
import model.characters.*;
import model.characters.Character;
import model.entities.Bullet;
import model.entities.Entity;
import model.entities.Grapple;
import model.entities.Rocket;
import model.geometry.*;
import model.maps.Map;
import model.maps.Map1;
import model.maps.Map2;
import network.ChatMessage;
import network.InputState;
import network.SpawnParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nathan on 1/10/2016.
 */
public class Network {
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;

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
        kryo.register(Character.class);
        kryo.register(CharClass.class);
        kryo.register(Ninja.class);
        kryo.register(Rocketman.class);
        kryo.register(Scout.class);
        kryo.register(Soldier.class);
        kryo.register(Team.class);

        // Entities
        kryo.register(Entity.class);
        kryo.register(Bullet.class);
        kryo.register(Grapple.class);
        kryo.register(Rocket.class);

        // Geometry
        kryo.register(Shape2D.class);
        kryo.register(AABB.class);
        kryo.register(Circle2D.class);
        kryo.register(Point2D.class);
        kryo.register(Vector2D.class);

        // Network
        kryo.register(SpawnParams.class);
        kryo.register(InputState.class);
        kryo.register(ChatMessage.class);
        kryo.register(Date.class);

        // Maps
        kryo.register(Map.class);
        kryo.register(Map1.class);
        kryo.register(Map2.class);
    }
}
