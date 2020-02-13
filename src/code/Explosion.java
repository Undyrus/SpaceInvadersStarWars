/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.awt.Image;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Ramiro Diego
 */
public class Explosion {

    Image image1 = null;
    Image image2 = null;

    public int posX = 0;
    public int posY = 0;

    public int lifeTime = 20;

    Clip explosionSound;

    public Explosion() {
        try {
            explosionSound = AudioSystem.getClip();
            explosionSound.open(AudioSystem.getAudioInputStream(getClass().getResource("/sound/explosion.wav")));
        } catch (Exception ex) {

        }
    }
}
