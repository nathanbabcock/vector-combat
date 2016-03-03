import java.util.Scanner;

/**
 * Created by Nathan on 8/21/2015.
 */
public class test {
    public static void main(String[] args) {
//        Vector2f v1 = new Vector2f(1, 0);
//        Vector2f v2 = v1.normal();
//        System.out.println(v1);
//        System.out.println(Math.toDegrees(v1.getDirection()));
//        System.out.println(v2);
//        System.out.println(Math.toDegrees(v2.getDirection()));


        Scanner fileScan = new Scanner("Here is a sentence: it has some strange punctuation, and some random-@#% &^@! in it.").useDelimiter("[ \n]");
        String word;
        while (fileScan.hasNext()) {
            word = fileScan.next();
            System.out.println(word);
        }

    }
}
