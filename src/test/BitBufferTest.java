package test;

import bitbuffer.BitBuffer;

/**
 * Created by Nathan on 1/19/2016.
 */
public class BitBufferTest {
    public static void main(String[] args) {
        BitBuffer buf = BitBuffer.allocateDynamic();
        buf.put(1);
        buf.put(2.0f);
        buf.put(3L);
        buf.put("Hello world");
        buf.put(true);
        buf.put(4);

        System.out.println("Buffer size = " + buf.position());
        buf.flip();

        System.out.println(buf.getInt());
        System.out.println(buf.getFloat());
        System.out.println(buf.getLong());
        System.out.println(buf.getString());
        System.out.println(buf.getBoolean());
        System.out.println(buf.getInt());
    }
}
