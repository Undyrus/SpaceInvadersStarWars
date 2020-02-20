package code;

import java.awt.Image;
import javax.imageio.ImageIO;

/**
 *
 * @author Ramiro Diego
 */
public class tieFighter {

    Image image;
    public int posX = 0;
    public int posY = 0;
    
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    

    public tieFighter() {
    }
    
    public void move(){
        if(leftPressed && posX > -13){
            posX -= 6;
        }
        if(rightPressed && posX < (gameWindow.SCREENWIDTH - image.getWidth(null)+1)){
            posX += 6;
        }
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public void setLeftPressed(boolean leftPressed) {
        this.leftPressed = leftPressed;
        this.rightPressed = false;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public void setRightPressed(boolean rightPressed) {
        this.rightPressed = rightPressed;
        this.leftPressed = false;
    }
}
