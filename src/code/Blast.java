package code;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Ramiro Diego
 */
public class Blast {

    Image image;
    public int posX = 0;
    public int posY = -2000;

    public Blast() {
        try {
            image = ImageIO.read(getClass().getResource("/img/disparo.png"));
        } catch (Exception e) {
        }
    }

    public void move() {
        posY -= 9;

    }
    
    public void blastPosition(tieFighter _tieFighter){
        posX = _tieFighter.posX + _tieFighter.image.getWidth(null)/2 - image.getWidth(null)/2;
        posY = _tieFighter.posY - _tieFighter.image.getHeight(null)/2;
    }
}
