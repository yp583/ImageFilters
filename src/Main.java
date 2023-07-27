import java.awt.*;

public class Main {

    public static void main(String[] args) {
        String filepath = "sunset.jpg";
        PixImage tsa = new PixImage(filepath);
        int[] col1 = new int[]{122,121, 0};
        int[] col2 = new int[]{0, 0, 255};
        tsa = ImageFilters.spiralFilter(tsa, 6);
        tsa.saveImage("spiral.png");
    }
}
