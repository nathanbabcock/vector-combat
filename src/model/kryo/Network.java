package model.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import model.Game;
import model.Player;
import model.characters.Character;
import model.characters.*;
import model.entities.Bullet;
import model.entities.Entity;
import model.entities.Grapple;
import model.entities.Rocket;
import model.geometry.*;
import model.maps.Map;
import model.maps.Map1;
import model.maps.Map2;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nathan on 1/10/2016.
 */
public class Network {
    public static final int PORT1 = 54555;
    public static final int PORT2 = 54777;

    public static void register(EndPoint endpoint) {
        Kryo kryo = endpoint.getKryo();

        // General
        kryo.register(Game.class);
        kryo.register(CopyOnWriteArrayList.class);
//        kryo.register(HashMap.class);

        // Players
        kryo.register(Player.class);
        kryo.register(Character.class);
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
        kryo.register(Line2D.class);
        kryo.register(Point2D.class);
        kryo.register(Vector2D.class);

        // Maps
        kryo.register(Map.class);
        kryo.register(Map1.class);
        kryo.register(Map2.class);

    }
}
