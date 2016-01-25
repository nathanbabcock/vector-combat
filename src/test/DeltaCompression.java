package test;

import bitbuffer.BitBuffer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 1/21/2016.
 * <p>
 * TODO no support for primitive arrays
 */
public class DeltaCompression {

    /**
     * Statically register classes that will be involved in DeltaCompression, so that type information can be included
     * if necessary by referencing just their array index. Null needs to be included as a "class" of its own.
     * <p>
     * Because this ID is stored in a single byte, only up to 127 unique classes are supported.
     */
    static final Class[] classIDs = new Class[]{null, TestStruct.class, Array.class, ArrayList.class};

    /**
     * Given an object o, returns the ID of the object's class in byte form, ready to be written into a buffer.
     *
     * @param o The object to find an ID for
     * @return The ID of this object's class, as a byte
     */
    static byte getClassID(Object o) {
        if (o == null)
            return (byte) 0;
        for (int id = 1; id < classIDs.length; id++)
            if (classIDs[id] == o.getClass())
                return (byte) id;
        System.err.println("Error: class ID lookup failed for class " + o.getClass());
        return (byte) -1;
    }

    /**
     * Writes the delta of given objects to the given buffer. Each field is preceded by a single bit flag indicating if
     * o1 differs from o2 at this field. True = different, and will then be followed by the full serialized version from
     * o1. False = the same. For non-primitives, recursively steps through all public non-transient fields looking for
     * primitives to compare.
     *
     * @param type The type of objects being compared (necessary because it cannot be inferred from o1 and o2 in the
     *             case of null values
     * @param o1   The desired object state (e.g. server gamestate)
     * @param o2   The actual object state (e.g. client gamestate)
     * @param buf  Buffer to write to
     */
    static void write(Class type, Object o1, Object o2, BitBuffer buf) {
        // Base case: primitives & strings
        // TODO: DRY
        boolean same = (o1 == o2) || (o1 != null && o1.equals(o2));
        if (type == int.class || type == Integer.TYPE) {
            if (same)
                buf.put(false);
            else {
                buf.put(true);
                buf.putInt((int) o1);
            }
        } else if (type == float.class || type == java.lang.Float.class) {
            if (same)
                buf.put(false);
            else {
                buf.put(true);
                buf.putFloat((float) o1);
            }
        } else if (type == double.class || type == java.lang.Double.class) {
            if (same)
                buf.put(false);
            else {
                buf.put(true);
                buf.putDouble((double) o1);
            }
        } else if (type == long.class || type == java.lang.Long.class) {
            if (same)
                buf.put(false);
            else {
                buf.put(true);
                buf.putLong((long) o1);
            }
        } else if (type == byte.class || type == java.lang.Byte.class) {
            if (same)
                buf.put(false);
            else {
                buf.put(true);
                buf.putByte((byte) o1);
            }
        }
        if (type == String.class) {
            if (same)
                buf.put(false);
            else {
                buf.put(true);
                buf.putString((String) o1);
            }
        } else if (type == boolean.class || type == Boolean.class) {
            // No diff marker for booleans (1 bit regardless!)
            buf.putBoolean((boolean) o1);
        }

        // Recursive case: Arrays and Lists
        else if (o1 instanceof List) {
            List o1list = (List) o1;
            List o2list = (List) o2;
            buf.putInt(o1list.size());
            for (int i = 0; i < o1list.size(); i++) {
                Object o1item = o1list.get(i);
                Object o2item;
                if (o2list == null || i >= o2list.size())
                    o2item = null;
                else
                    o2item = o2list.get(i);
                write(o1item == null ? null : o1item.getClass(), o1item, o2item, buf);
            }
        }

        // Recursive case: Other complex objects
        else {
            // Write object class (if necessary)
            Class o1c = o1 == null ? null : o1.getClass();
            Class o2c = o2 == null ? null : o2.getClass();
            if (o1c == o2c)
                buf.putBoolean(false); // Same class
            else {
                buf.putBoolean(true); // Different class
                buf.putByte(DeltaCompression.getClassID(o1));
                System.out.println("Including type information for object " + o1);
            }

            // Recursively loop over fields
            for (Field f : o1.getClass().getDeclaredFields()) {
                // Excluded fields
                int mod = f.getModifiers();
                if (Modifier.isTransient(mod) || !Modifier.isPublic(mod) || Modifier.isStatic(mod)) continue;

                // Debug
                System.out.println("Processing field " + f.getName() + " on object " + o1);

                // Recursion
                try {
                    write(f.getType(), f.get(o1), f.get(o2), buf);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
