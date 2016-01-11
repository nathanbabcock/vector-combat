package model.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * Created by Nathan on 1/10/2016.
 */
public class Network {
    public static void register(EndPoint endpoint) {
        Kryo kryo = endpoint.getKryo();
        kryo.register(FromClient.class);
        kryo.register(FromServer.class);
    }
}
