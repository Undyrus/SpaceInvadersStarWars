package code;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Ramiro Diego
 */
public class xWing {

    public Image image1;
    public Image image2;

    public int posX;
    public int posY;

    private int screenWidth;
    public int lifes = 50;

    public xWing(int _screenWidth) {
        screenWidth = _screenWidth;

    }

    public void move(boolean direction) {
        if (direction) {
            posX--;

        } else {
            posX++;
        }
    }
}
