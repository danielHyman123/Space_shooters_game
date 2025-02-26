import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

/**
 * Created by Daniel Hyman edited by John Yoon
 * Asteroid class produces and removes asteroids on the screen, 
 * The asteroids fall from the top to the bottom of the screen
 * Extends the SpaceShooter_Main class
 */
public class Asteroids extends SpaceShooter_Main implements ActionListener {
   final static int RATE = 3;   //rate at which asteroids spawn
    static int col, row, ticks = 0;  //the individual columns, and rows of each asteroid, and the collective amount of jumps the asteroids take across the screen
    
    // All asteroid coordinates are stored in an ArrayList of type Coordinate
    static ArrayList<Coordinate> asteroidNum = new ArrayList<Coordinate>(); //Number of asteroids on the screen

    public static Timer clock;  //Calls the Action listener and keeps track of ticks

    /*Edited by John 20240117 start
     * Makes the counter for the amount of asteroids destroyed
     */
    private static int cntOverboundary = 0;
    private static int MaxY = 0;

    public static void setMaxY(int iMaxY) {
        MaxY = iMaxY;
    }
   
    /* Edited by John 20240117 end
     * Returns the count over boundary to increment the amount of asteroids destroyed 
     */
    public static int rtnCntOverboundary() {
        return cntOverboundary;
    }

    /*
     * Asteroid constructor 
     * Initiates the random and clock objects
     * Initiates the random column of each asteroid
     * Initiates the row of each asteroid at the top of the screen
     */
    public Asteroids() throws InterruptedException {
        Random rand = new Random();
        clock = new Timer(200, this);
        resetAsteroids();
    }

    //Creates the asteroids at random x values as they fall down across the screen
    public static void makeAsteroid() throws InterruptedException{
        Asteroids a = new Asteroids();
        asteroidNum.add(new Coordinate(row,col));
        Coordinate c;
        clock.start();
    }

    /*
     * Any class that "implements ActionListener" must
     * have this method. The Timer object called "clock"
     * will automatically call this method every time it "fires".
     */
    public void actionPerformed(ActionEvent e) {
        if (!BossFight.bossMode) {
            ticks++;

            // moves the asteroids down
            for (int i = 0; i < asteroidNum.size(); i++) {
                b.removePeg(asteroidNum.get(i).getRow(), asteroidNum.get(i).getCol());
                asteroidNum.get(i).moveDown();
               
                /*edited by John 20240117 start
                 * Increments the count over boundary by 1 each time the asteriodNum is equal to MaxY
                 */
                if (asteroidNum.get(i).getRow() == MaxY) {
                    cntOverboundary += 1;
                }
                /*edited by John 20240117 end
                 * make sure it doesn't go out of bounds
                 */
                if (asteroidNum.get(i).getRow() < b.getRows()) {
                    b.putPeg("black", asteroidNum.get(i).getRow(), asteroidNum.get(i).getCol());
                } else { 
                    asteroidNum.remove(i);
                    i--;
                }
            }

            //Every RATE seconds, this if statement adds a new asteroid in a random column, at the top of the screen
            if (ticks % RATE == 0) {
                asteroidNum.add(new Coordinate(0, rand.nextInt(SIZE - 2) + 1));
            }
        }
    }
    
    public static void resetAsteroids(){
    	col = rand.nextInt(SIZE - 2) + 1;
        row = 0;
        ticks = 0;
        asteroidNum = new ArrayList<Coordinate>();
        cntOverboundary = 0;
        MaxY = 0;
    }
}
