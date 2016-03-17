package model.geometry;

/**
 * Created by Nathan on 3/17/2016.
 */
public class Projection {
    float min, max;

    public Projection(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public boolean overlaps(Projection other) {
        return (min < other.max && min >= other.min) || (max <= other.max && max > other.min) || (other.min < max && other.min >= min) || (other.max <= max && other.max > min);
    }

    public boolean contains(float p) {
        return p > min && p < max;
    }

    public float getOverlap(float p) {
        float a = min - p;
        float b = max - p;
        if (Math.abs(a) < Math.abs(b))
            return a;
        return b;
    }

    public float getOverlap(Projection other) {
        //return Math.min(other.max, max) - Math.max(other.min, min);
//            return (this.max < other.max) ? max - other.min : other.max - min;
        // if min inside other, push it
        float a = other.max - min;
        float b = other.min - max;
        if (Math.abs(a) < Math.abs(b))
            return a;
        return b;
    }

    @Override
    public String toString() {
        return "(" + min + ", " + max + ")";
    }
}