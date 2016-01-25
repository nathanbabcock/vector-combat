package test;

import bitbuffer.BitBuffer;

import java.util.List;

/**
 * Created by Nathan on 1/19/2016.
 */
public class TestStruct implements DeltaSerializable<TestStruct> {
    public String string;
    public int integer;
    public float floatVal;
    public List buttHole;
//    public int[] integers;

    private int fucksGiven = -1;
    public transient String butts = "LOL";

    @Override
    public void deltaWrite(TestStruct other, BitBuffer buf) {
        // string
        if ((string != null && !string.equals(other.string)) || (string == other.string)) {
            buf.put(true);
            buf.put(string);
        } else
            buf.put(false);

        // integer
        if (other.integer != integer) {
            buf.put(true);
            buf.put(integer);
        } else
            buf.put(false);

        // floatVal
        if (other.floatVal != floatVal) {
            buf.put(true);
            buf.put(floatVal);
        } else
            buf.put(false);
    }

    @Override
    public void deltaRead(BitBuffer buf) {
        if (buf.getBoolean())
            string = buf.getString();
        if (buf.getBoolean())
            integer = buf.getInt();
        if (buf.getBoolean())
            floatVal = buf.getFloat();
    }
}
