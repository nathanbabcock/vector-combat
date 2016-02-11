import model.geometry.Vector2f;

/**
 * Created by Nathan on 8/21/2015.
 */
public class test {
    public static void main(String[] args) {
        Vector2f v = new Vector2f(1, 0);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2f(1, 1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2f(0, 1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2f(-1, 1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2f(-1, 0);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2f(-1, -1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2f(0, -1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2f(1, -1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
    }
}
