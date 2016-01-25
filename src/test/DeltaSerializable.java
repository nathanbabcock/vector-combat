package test;

import bitbuffer.BitBuffer;

/**
 * Created by Nathan on 1/21/2016.
 */
public interface DeltaSerializable<T> {
    void deltaWrite(T other, BitBuffer buf);

    void deltaRead(BitBuffer buf);
}
