/**
 * Created by Peter Frach 
 * Manages the power ups (rainbow stars)
 * Calculates the frequency for each power up
 * Includes a timer and methods to space them out
 */
public class PowerUps extends SpaceShooter_Main {
    final static int NUM_POWERUPS = 2, TIME_PER_POWERUP = 2000, TIME_BETWEEN_POWERUPS = 5000;
    static int width = 0, height = 0, time = 1, r, c, saveR, saveC, tier = 1, numDrawn = 1;
    static boolean powerUpSpawned = false;
    private static long end, start;
    private static int timeElapsed;

    enum Boost {NUM_LASERS, POINTS}
    static Boost curBoost = Boost.NUM_LASERS;

    enum Tier {TIER1, TIER2, TIER3}
    static Tier curTier = Tier.TIER1;
  
    // ensures that the higher tier power up is less likely to occur
    public static void numLasers (int tier) {
        switch (tier) {
            case 1:
            case 2: curTier = Tier.TIER2;
                break;
            case 3: curTier = Tier.TIER3;
                break;
            default:
            	break;
        }
    }

    // same as above for points
    public static void pointMultiplier(int tier) {
        switch(tier){
            case 4:
            case 5:
                curTier = Tier.TIER2;
                break;
            case 6:
                curTier = Tier.TIER3;
                break;
            default:
            	break;
        }
    }

    // random power up effect
    public static void setPowerUp() {
        frequency(rand.nextInt(3) + 1);
    }

    // sets the frequency of each power up and tier
    public static void frequency (int tier) {
        int num = (rand.nextInt(5) + 1);
        switch (num) {
            case 1:
            case 2:
            case 3: numLasers(tier);
                break;
            case 4:
            case 5:
            case 6: pointMultiplier(tier);
                break;
            default:
            	break;
        }
    }

    // displays the powerup and saves the location, because it doesn't move 
    public static void placePowerUp() {
        r = rand.nextInt(SIZE - 2);
        c = rand.nextInt(SIZE - 2) + 1;
        b.putPeg("rainbowStar", r, c);
        saveR = r;
        saveC = c;
        time++;
        powerUpSpawned = true;
    }
    

    public static void deletePowerUp () {
        b.removePeg(saveR, saveC);
        saveR = 0;
        saveC = 0;
        powerUpSpawned = false;
    }

    public static void setStartTime() {
        start = System.currentTimeMillis();
    }

    public static void setEndTime() {
        end = System.currentTimeMillis();
    }
    
    
    // draws a power up every time interval, ensuring only one on the screen
    public static void decideToDraw() {
        setEndTime();
        timeElapsed = (int)(end - start);
        if (powerUpSpawned) {
            if (timeElapsed / 1000 * 1000 == TIME_PER_POWERUP) {
                deletePowerUp();
                setStartTime();
            }
        } else if (!powerUpSpawned) {
            if (timeElapsed / 1000 * 1000 == TIME_BETWEEN_POWERUPS) {
                placePowerUp();
                setStartTime();
            }
        }
    }
     
    public static void resetPowerUps() {
        width = 0; 
        height = 0; 
        time = 1; 
        r = 0;
        c = 0;
        saveR = 0;
        saveC = 0;
        tier = 1;
        numDrawn = 1;
        powerUpSpawned = false;
        end = 0;
        start = 0;
        timeElapsed = 0;
        curBoost = Boost.NUM_LASERS;
        curTier = Tier.TIER1;
    }
}