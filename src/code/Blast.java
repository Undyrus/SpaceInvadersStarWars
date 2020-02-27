package code;

import java.awt.Image;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

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
    Clip blastSound;
    public void blastPosition(tieFighter _tieFighter){
        posX = _tieFighter.posX + _tieFighter.image.getWidth(null)/2 - image.getWidth(null)/2;
        posY = _tieFighter.posY - _tieFighter.image.getHeight(null)/2;
         try {
            blastSound = AudioSystem.getClip();
            blastSound.open(AudioSystem.getAudioInputStream(getClass().getResource("/sound/blast.wav")));
        } catch (Exception ex) {

        }
    }
}
