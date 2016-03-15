package view;

/**
 * Created by Nathan on 3/14/2016.
 */
public class Clickable {
    int x, y, width, height;
    public Listener listener;

    public boolean isClicked, isMouseover;

    public Clickable(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        listener = new Listener();
    }

    public boolean contains(int x, int y) {
        if (x >= this.x & x <= this.x + width)
            if (y >= this.y & y <= this.y + height)
                return true;
        return false;
    }

//    public boolean click(int x, int y) {
//        if (contains(x, y))
//            listener.onClick(x, y);
//    }

    public static class Listener {
        public void onClick() {
            System.out.println("click");
        }

        public void onMouseover() {
        }
    }

}
