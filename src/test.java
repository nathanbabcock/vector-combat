import model.geometry.Vector2D;

/**
 * Created by Nathan on 8/21/2015.
 */
public class test {
    public static void main(String[] args) {
        Vector2D v = new Vector2D(1, 0);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2D(1, 1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2D(0, 1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2D(-1, 1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2D(-1, 0);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2D(-1, -1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2D(0, -1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
        v = new Vector2D(1, -1);
        System.out.println(v.toString() + Math.toDegrees(v.getDirection()));
    }
}
