import java.io.IOException;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Created by John Yoon edited by Peter Frach and Daniel Hyman 
 * Main class makes the spaceship and lasers and calls the method of the subclasses to interact with the spaceship
 */

public class SpaceShooter_Main {
    static final int SIZE = 15, HEIGHT = SIZE;
    static Board b;//Creates 2D board
    static boolean playing = true;
    static int SHIPY = SIZE - 2, SHIPX = SIZE/2;
    public static Random rand = new Random();
    static int col = rand.nextInt(SIZE - 2 ) + 1, row = 0;
    static int powerUpCol;
    static int a;
    static boolean bossMode = false;
    static int numShots = 0;
    static int cntDestroyed = 0;
    static String flagTarget;
    static int cntOB = 0;
    static String name;

    /**
     * @param args
     * Main method for game
     * @throws InterruptedException
     * @throws IOException
     */
    
    public static void main(String[] args) throws InterruptedException, IOException {
    	JOptionPane.showMessageDialog(null, "Press A to move left, D to move right, W to shoot ", "Space Shooters instructions", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(null, "Win conditions: Destroy 10 asteriods || Lose conditions: Get hit by an asteriod or miss 10 asteriods", "Space Shooters instructions", JOptionPane.INFORMATION_MESSAGE);
        name = JOptionPane.showInputDialog("Please enter your name: ", JOptionPane.OK_OPTION);
        setUp();
        game();
    }
    public static void game() throws InterruptedException, IOException {
    	
        /*Instructions
         * Outputs JOptionPane 
         */

    	b = new Board(SIZE, SIZE); //Creates 2D board
        b.isFocusable(); //Gives focus to the board so that the KeyListener can work properly
        b.putPeg("spaceship", SHIPY, SHIPX);

        Asteroids.makeAsteroid();
//edited 20240117
        Asteroids.setMaxY(SHIPY);
//edited 20240117
        PowerUps.placePowerUp();
        PowerUps.setStartTime();


        while(playing) {
            char key = b.getKey();


            /*Created by John, makes Daniel's boss, edited by Hyman
            *If 25 asteroids are destroyed, the boss spawns
            */
            if (cntDestroyed >= 25){
                BossFight.makeBoss();
                Asteroids.clock.stop();
                bossMode = true;
            }
            
            /* 
             * Calls the movement of the spaceship
             * Spawns the lasers on command 
             * Sets the speed of the spaceship
             */
            switch(key) {
                case 'a': if (SHIPX > 1 && SHIPX < SIZE - 1) {
                    moveLeft();
                }
                    break;
                case 'd': if (SHIPX >= 0 && SHIPX < SIZE - 2) {
                    moveRight();
                }
                    break;
                case 'w': shoot();
                    break;
                default:
                    break;
            }
            Thread.sleep(100);
            PowerUps.decideToDraw();

            //add up overboundary count
            cntOB = Asteroids.rtnCntOverboundary();


            //Game over by max overboundary count
            if (cntOB == 60) {
                Asteroids.clock.stop();
                playing = false;
                JOptionPane.showMessageDialog(null, "Game Over >> Reach max over boundary limit", "You lose", JOptionPane.INFORMATION_MESSAGE);

            }
            /* Game over by boss laser collision
            * Added by Daniel
            */
            if (BossFight.lost) {
                playing = false;
                JOptionPane.showMessageDialog(null, "Game Over >> Lost to Boss", "You lose", JOptionPane.INFORMATION_MESSAGE);
            }

            for(int i = 0; i<Asteroids.asteroidNum.size(); i++){
            	//Game over by asteroid hitting
                if ((Asteroids.asteroidNum.get(i).getRow() == SHIPY) && (Asteroids.asteroidNum.get(i).getCol() == SHIPX)){
                    Asteroids.clock.stop();
                    playing = false;
                    JOptionPane.showMessageDialog(null, "Game Over >> Spaceship hit by astroid", "You lose", JOptionPane.INFORMATION_MESSAGE);

                }
            }
        }
        ScoreBoard.addScore();
        JOptionPane.showMessageDialog(null, ScoreBoard.finalScores, "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(null, ScoreBoard.bestName, "HighScore", JOptionPane.INFORMATION_MESSAGE);
        playAgain();
    }


    // Method to make the spaceship move right
    public static void moveRight() {
        b.removePeg(SHIPY, SHIPX);
        SHIPX = SHIPX + 1;
        b.putPeg("spaceship", SHIPY, SHIPX);
    }
    
    // Method to make the spaceship move left
    public static void moveLeft() {
        b.removePeg(SHIPY, SHIPX);
        SHIPX = SHIPX - 1;
        b.putPeg("spaceship", SHIPY, SHIPX);
    }
    
    // Method to make the spaceship shoot while calling the powerup tiers.
    public static void shoot () throws InterruptedException {
        switch (PowerUps.curBoost) {
            case NUM_LASERS:
                switch (PowerUps.curTier) {
                    case TIER3:
                        if (numShots > 10) {
                            PowerUps.curTier = PowerUps.Tier.TIER1;
                            numShots = 0;
                        }
                        break;
                    case TIER2:
                        if (numShots > 15) {
                            PowerUps.curTier = PowerUps.Tier.TIER1;
                            numShots = 0;
                        }
                        break;
                }
        }

        boolean delPowerUp = false;

        // Calls the power up methods and spawns them in the board
        switch (PowerUps.curBoost){
            case NUM_LASERS:
            	
            	/* No break statements because the higher tiers includes the lower tiers
            	 * Makes the power ups random
            	 */
                switch (PowerUps.curTier){
                	// Spawns in 5 lasers when destroyed 
                    case TIER3: b.drawLine(SHIPY, SHIPX + 2, 0, SHIPX + 2);
                        b.drawLine(SHIPY, SHIPX - 2, 0, SHIPX - 2);
                        Thread.sleep(50);
                        if (BossFight.col>=SHIPX-2 && BossFight.col<=SHIPX+2){  //Boss losing health, added By Daniel
                            BossFight.health--;
                        }
                        b.removeLine(SHIPY, SHIPX + 2, 0, SHIPX + 2);
                        b.removeLine(SHIPY, SHIPX - 2, 0, SHIPX - 2);
                     // Spawns in 3 lasers
                    case TIER2: b.drawLine(SHIPY, SHIPX + 1, 0, SHIPX + 1);
                        b.drawLine(SHIPY, SHIPX - 1, 0, SHIPX - 1);
                        Thread.sleep(50);
                        if (BossFight.col>=SHIPX-1 && BossFight.col<=SHIPX+1){  //Boss losing health, added By Daniel
                            BossFight.health--;
                        }
                        b.removeLine(SHIPY, SHIPX + 1, 0, SHIPX + 1);
                        b.removeLine(SHIPY, SHIPX - 1, 0, SHIPX - 1);
                        numShots++;
                    // The normal lasers
                    case TIER1: b.drawLine(SHIPY, SHIPX, 0, SHIPX);
                        Thread.sleep(50);
                        if (BossFight.col==SHIPX){  //Boss losing health, added by Daniel
                            BossFight.health--;
                        }
                        b.removeLine(SHIPY, SHIPX, 0, SHIPX);
                        break;
                }
        }


        a = 0;

        /*Determine if the lasers actually hits the power ups
         *  Collecting the power ups
         */
        switch (PowerUps.curBoost){
            case NUM_LASERS:
                switch (PowerUps.curTier) {
                    case TIER3:
                        if (SHIPX + 2 == PowerUps.saveC || SHIPX - 2 == PowerUps.saveC) {
                            if (PowerUps.powerUpSpawned) {delPowerUp = true; }
                        }
                    case TIER2:
                        if (SHIPX + 1 == PowerUps.saveC || SHIPX - 1 == PowerUps.saveC) {
                            if (PowerUps.powerUpSpawned) {delPowerUp = true; }
                        }
                    case TIER1:
                        if (SHIPX == PowerUps.saveC) {
                            if (PowerUps.powerUpSpawned) {delPowerUp = true; }
                        }
                        break;
                }
        }


        switch (PowerUps.curTier) {
            case TIER2: a = 1;
                break;
            case TIER3: a = 2;
                break;
        }


        // Removes asteroids when made contact with the lasers
        for(int i=0; i<Asteroids.asteroidNum.size(); i++){
            if (Asteroids.asteroidNum.get(i).getCol() >= SHIPX - a && Asteroids.asteroidNum.get(i).getCol() <= SHIPX + a) {
                b.removePeg(Asteroids.asteroidNum.get(i).getRow(), Asteroids.asteroidNum.get(i).getCol());
                Asteroids.asteroidNum.remove(i);
                cntDestroyed = cntDestroyed + 1;
                Points.incrementPoints();
                i--;
            }
        }

        //Game over by max hitting count
        if (cntDestroyed == 1000){
            playing = false;


            JOptionPane.showMessageDialog(null, "Win Hitting count >> " + cntDestroyed, "You win", JOptionPane.INFORMATION_MESSAGE);
        }

        // move the power up
        if (delPowerUp) {
            PowerUps.deletePowerUp();
            PowerUps.setPowerUp();
        }


        Thread.sleep(50);
    }
    
    // Prompts the user to try again
    public static void playAgain() throws InterruptedException, IOException {
    	JFrame playAgain = new JFrame();
    	Object[] options = { "Yes", "No" };
    	int question = JOptionPane.showOptionDialog(playAgain, "Play again?",
				null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
    	if (question == JOptionPane.YES_OPTION) {
    		playAgain = new JFrame();
    		question = JOptionPane.showOptionDialog(playAgain, "Are you " + name + "?",
    				null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
    		if (question == JOptionPane.YES_OPTION) {
    			setUp();
    			game();
    		} else {
    			main(null);
    		}
		} else {
			System.exit(0);
		}
    }

    /*
     * Sets up the whole game for playing
     * Resets fields
     * @throws InterruptedException
     * @throws IOException 
     */
    public static void setUp() throws InterruptedException, IOException {
    	BossFight.resetBossFight();
    	Points.resetPoints();
    	PowerUps.resetPowerUps();
    	ScoreBoard.resetGameScores();
        playing = true;
        SHIPY = SIZE - 2;
        SHIPX = SIZE/2;
        powerUpCol = 0;
        a = 0;
        col = rand.nextInt(SIZE - 2 ) + 1;
        row = 0;
        bossMode = false;
        numShots = 0;
        cntDestroyed = 0;
        cntOB = 0;
    }
}