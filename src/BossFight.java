import javax.swing.*;

/**
 * Created by Daniel Hyman
 * Boss Fight class
 * Creates the Boss character after a certain amount of asteroids are destroyed
 * Once the Boss spawns, the asteroids stop falling
 * The boss has 20 health where one tick of laser impact reduces health by one unit
 * The Boss can shoot occasional lasers back at the player,
 * The boss can move to a random column coordinate every few seconds
 * Win the game by defeating the Boss
 * Extends the SpaceShooter_Main class
 * Uses static variables from Asteroid class
 */
public class BossFight extends SpaceShooter_Main {

    final static int ROW = 2;   //The row coordinate of the boss character
    static int col = rand.nextInt(SIZE - 2) + 1, ticks = 0, health = 20;

    static boolean bossMode = false;    //Initiates the boss fight

    static boolean lost = false, laserOn = false;

    /*Creates the boss character
    * Displays the boss health
    * Resets the clock and ticks
    */
    public static void makeBoss() throws InterruptedException {
        b.displayMessage("Boss Fight! Health: " + health + "                            Points: " + Points.points);
        Asteroids.clock.start();

        b.putPeg("boss", ROW, col);
        ticks++;

        //Has the spaceship shoot lasers
        if (ticks%15 == 0) {
            b.drawLine(ROW + 1, col, SIZE, col);
            laserOn = true;

        }
        if (ticks%8 == 0) {
            b.removeLine(ROW + 1, col, SIZE, col);
            laserOn = false;
        }

        //Has the boss move to a new location every few seconds
        if (ticks%20==0){
            b.removeLine(ROW + 1, col, SIZE, col);
            laserOn = false;
            b.removePeg(ROW, col);
            col = rand.nextInt(SIZE - 2) + 1;
        }

        //Checks if spaceship collided with boss laser, which would end the game
        if (col == SHIPX && laserOn){
            lost = true;
        }

        //Winning the game and defeating the boss
        if (health<=0){
            playing = false;
            Asteroids.clock.stop();
            b.removePeg(ROW, col);
            JOptionPane.showMessageDialog(null, "YOU WON! You destroyed >> " + cntDestroyed + " asteroids and beat the boss", "You win", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public static void resetBossFight() {
        col = rand.nextInt(SIZE - 2) + 1;
        ticks = 0; 
        health = 20;
        bossMode = false;
        lost = false;
        laserOn = false;
    }
}