package test;

import bitbuffer.BitBuffer;

import java.util.ArrayList;

/**
 * Created by Nathan on 1/19/2016.
 */
public class BitBufferTest {
    public static void main(String[] args) {


        // DeltaSerializable interface
        TestStruct master = new TestStruct();
        master.string = "Hello World";
        master.integer = 69;
        master.floatVal = 420.0f;
        master.buttHole = new ArrayList();
        master.buttHole.add(1);
        master.buttHole.add("ass");
        master.buttHole.add(0L);
        master.buttHole.add(1f);
//        master.integers = new int[]{1, 2, 3, 4, 5};

        TestStruct old = new TestStruct();

        BitBuffer buf = BitBuffer.allocateDynamic();
        System.out.println("Buffer size = " + buf.position());
        DeltaCompression.write(TestStruct.class, master, old, buf);
        System.out.println("Buffer size = " + buf.position());

    }
}
