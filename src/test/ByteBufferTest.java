package test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * Created by Nathan on 1/18/2016.
 */
public class ByteBufferTest {
    public static void main(String[] args) {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.putInt(Integer.MIN_VALUE);
        String string = "Hello world";
        for (int i = 0; i < string.length(); i++)
            buf.putChar(string.charAt(i));
        buf.putChar('\0');
        buf.putFloat(69.420f);
        buf.putInt(2);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        WritableByteChannel channel = Channels.newChannel(bos);
        try {
//            channel.write(buf);
            bos.write(buf.array(), 0, buf.array().length - buf.remaining());
            bos.flush();
//            bos.close();
//            bos.write(buf.array());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Encoded data is " + (buf.array().length - buf.remaining()) + " bytes");
        System.out.println(bos.toByteArray().length + " bytes written.");

        ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
        byte[] array = new byte[bais.available()];
        try {
            bais.read(array);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer output = ByteBuffer.wrap(array);
        System.out.println("Output len = " + array.length);

//        buf.flip();
        buf = output;

        System.out.println(buf.getInt());
        String newString = "";
        char curChar;
        while ((curChar = buf.getChar()) != '\0')
            newString += curChar;
        System.out.println(newString);
        System.out.println(buf.getFloat());
        System.out.println(buf.getInt());

    }
}
