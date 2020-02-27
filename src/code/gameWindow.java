/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 *
 * @author Ramiro Diego
 */
public class gameWindow extends javax.swing.JFrame {

    static int SCREENWIDTH = 1300;
    static int SCREENHEIGHT = 860;

    int xWingRows = 4;
    int xWingColumns = 9;

    int xWingGapX = 15;//separacion en la misma fila
    int xWingGapY = 10;//separacion entre filas de marcianos

    int counter = 0;

    int destroyedShips = 0;
    boolean keyReleased = true;

    Graphics2D g2;

    BufferedImage buffer = null;
    BufferedImage template = null;
    Image[] img = new Image[30];
    //bucle animacion del juego
    //en este caso, es un hilo de ejecucuion nuevo que se 
    //encarga de refrescar el contenido de la pantalla
    Timer timer = new Timer(10, new ActionListener() { //1º tiempo en milisegundos
        @Override
        public void actionPerformed(ActionEvent e) {
            //TODO : codigo de la animacion
            gameLoop();
        }
    });

    xWing myXwing = new xWing(SCREENWIDTH);
    xWing[][] xWingList = new xWing[xWingRows][xWingColumns];
    boolean xWingDirection = false; //false se move hacia la dcha, true hacia la izq

    tieFighter myTieFighter = new tieFighter();
    Blast myBlast = new Blast();
    ArrayList<Blast> blastList = new ArrayList();
    ArrayList<Explosion> explosionList = new ArrayList();

    public gameWindow() {
        initComponents();

        try {
            template = ImageIO.read(getClass().getResource("/img/xWing2.png"));
        } catch (IOException ex) {
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                img[i * 4 + j] = template.getSubimage(j * 64, i * 64, 64, 64).getScaledInstance(96, 96, Image.SCALE_SMOOTH);
            }
        }

        img[20] = template.getSubimage(0, 320, 66, 32);
        img[21] = template.getSubimage(66, 320, 64, 32);
        img[22] = template.getSubimage(130, 320, 64, 32);
        img[23] = template.getSubimage(194, 320, 64, 32);

        setSize(SCREENWIDTH, SCREENHEIGHT);
        buffer = (BufferedImage) jPanel1.createImage(SCREENWIDTH, SCREENHEIGHT);
        buffer.createGraphics();

        timer.start();// arranco el temporizador para que empiece el juego

        myTieFighter.image = img[21];
        myTieFighter.posX = SCREENWIDTH / 2 - myTieFighter.image.getWidth(this) / 2;
        myTieFighter.posY = SCREENHEIGHT - 90;
        for (int i = 0; i < xWingRows; i++) {
            for (int j = 0; j < xWingColumns; j++) {
                xWingList[i][j] = new xWing(SCREENWIDTH);
                xWingList[i][j].image1 = img[2 * i];
                xWingList[i][j].image2 = img[2 * i + 1];
                xWingList[i][j].posX = j * (xWingGapX + xWingList[i][j].image1.getWidth(null));
                xWingList[i][j].posY = i * (xWingGapY + xWingList[i][j].image1.getHeight(null));
            }
        }
    }

    public class blastSound extends Thread {//Creamos un hilo para que  												

        public void run() {                     //reproduzca el sonido a la  vez
            playSound s = new playSound(); //que sigue el juego
//            s.ReproducirSonido(s.getClass().getResource("/sonidos/disparosXBOX.wav").getFile());
            s.playSound(s.getClass().getResource("/sound/blast.wav").getFile());
        }
    }

    private void paintBlast(Graphics2D g2) {
        Blast auxBlast;
        for (int i = 0; i < blastList.size(); i++) {
            auxBlast = blastList.get(i);
            auxBlast.move();
            if (auxBlast.posY < 0) {
                blastList.remove(i);
            } else {

                g2.drawImage(auxBlast.image, auxBlast.posX, auxBlast.posY, null);
            }
        }
    }

    private void paintExplosion(Graphics2D g2) {
        Explosion auxExplosion;
        for (int i = 0; i < explosionList.size(); i++) {
            auxExplosion = explosionList.get(i);
            auxExplosion.lifeTime--;
            if (auxExplosion.lifeTime > 25) {
                g2.drawImage(auxExplosion.image1, auxExplosion.posX, auxExplosion.posY, null);
            } else {
                g2.drawImage(auxExplosion.image2, auxExplosion.posX, auxExplosion.posY, null);
            }
            if (auxExplosion.lifeTime == 0) {
                explosionList.remove(i);
            }
        }
    }

    private void gameLoop() {
        //este metodo gobierna el redibujado de los objetos en el jPanel1
        //primero borro todo lo que hay en el buffer
        g2 = (Graphics2D) buffer.getGraphics();
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, SCREENWIDTH, SCREENHEIGHT);
        counter++;
        printXwing(g2);
        /////////////////////////////////////////////////////////////////
        g2.drawImage(myTieFighter.image, myTieFighter.posX, myTieFighter.posY, null);//dibujo nave

        myTieFighter.move();
        paintBlast(g2);
        paintExplosion(g2);
        checkCollision();

        /////////////////////////////////////////////////////////////////
        //dibujo de golpe todo el buffer sobre el jPanel1
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);

    }

    private void checkCollision() {
        Rectangle2D.Double alienRectangle = new Rectangle2D.Double();
        Rectangle2D.Double blasterRectangle = new Rectangle2D.Double();
        Rectangle2D.Double shipRectangle = new Rectangle2D.Double();
        for (int k = 0; k < blastList.size(); k++) {
            blasterRectangle.setFrame(blastList.get(k).posX, blastList.get(k).posY, blastList.get(k).image.getWidth(null), blastList.get(k).image.getHeight(null));
            for (int i = 0; i < xWingRows; i++) {
                for (int j = 0; j < xWingColumns; j++) {
                    alienRectangle.setFrame(xWingList[i][j].posX, xWingList[i][j].posY,
                            xWingList[i][j].image1.getWidth(null),
                            xWingList[i][j].image1.getWidth(null));
                    if (blasterRectangle.intersects(alienRectangle)) {
                        destroyedShips++;
                        Explosion e = new Explosion();
                        e.posX = xWingList[i][j].posX;
                        e.posY = xWingList[i][j].posY;
                        e.image1 = img[23];
                        e.image2 = img[22];
                        explosionList.add(e);
                        e.explosionSound.start();

                        xWingList[i][j].posY = 2000;
                        blastList.remove(k);
                        if (destroyedShips == xWingRows * xWingColumns) {
                            timer.stop();

                            try {
                                g2.drawImage(ImageIO.read(getClass().getResource("/img/gameOver.jpg")), 0, 0, SCREENWIDTH, SCREENHEIGHT, null);
                            } catch (IOException ex) {

                            }
                        }
                    }
                }
            }

        }

        for (int i = 0; i < xWingRows; i++) {
            for (int j = 0; j < xWingColumns; j++) {
                alienRectangle.setFrame(xWingList[i][j].posX, xWingList[i][j].posY,
                        xWingList[i][j].image1.getWidth(null),
                        xWingList[i][j].image1.getWidth(null));
                shipRectangle.setFrame(myTieFighter.posX,
                        myTieFighter.posY,
                        myTieFighter.image.getWidth(null),
                        myTieFighter.image.getHeight(null));
                if (shipRectangle.intersects(alienRectangle)) {

                    timer.stop();
                    try {
                        g2.drawImage(ImageIO.read(getClass().getResource("/img/gameOver.jpg")), 0, 0, SCREENWIDTH, SCREENHEIGHT, null);
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }

    public void printXwing(Graphics2D _g2) {
        for (int i = 0; i < xWingRows; i++) {
            for (int j = 0; j < xWingColumns; j++) {
                xWingList[i][j].move(xWingDirection);
                if (xWingList[i][j].posX > SCREENWIDTH - 5 - xWingList[i][j].image1.getWidth(null)
                        || xWingList[i][j].posX <= 0) {
                    xWingDirection = !xWingDirection;
                    for (int k = 0; k < xWingRows; k++) {
                        for (int m = 0; m < xWingColumns; m++) {
                            xWingList[k][m].posY += xWingList[k][m].image1.getHeight(null);
                        }
                    }
                }
                if (counter < 50) {
                    _g2.drawImage(xWingList[i][j].image1, xWingList[i][j].posX, xWingList[i][j].posY, null);
                } else if (counter < 100) {
                    _g2.drawImage(xWingList[i][j].image2, xWingList[i][j].posX, xWingList[i][j].posY, null);
                } else {
                    counter = 0;
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusCycleRoot(false);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 784, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 404, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                myTieFighter.setLeftPressed(true);
                break;
            case KeyEvent.VK_RIGHT:
                myTieFighter.setRightPressed(true);
                break;
            case KeyEvent.VK_SPACE:
                if (keyReleased == true) {
                    Blast b = new Blast();
                    b.blastPosition(myTieFighter);
                    //agregamos el disparo a la lista de disparos
                    blastList.add(b);
                    blastSound s = new blastSound();
                    s.start();
                    keyReleased = false;
                }
                break;

        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                myTieFighter.setLeftPressed(false);
                break;
            case KeyEvent.VK_RIGHT:
                myTieFighter.setRightPressed(false);
                break;
            case KeyEvent.VK_SPACE:
                keyReleased = true;
                break;
        }
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(gameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(gameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(gameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(gameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new gameWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
