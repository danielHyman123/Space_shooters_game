/**
 * Created by Peter Frach 
 * Calculates the points earned for hitting an asteroid
 */
public class Points extends SpaceShooter_Main {
	public static int points;
	public final static int basePoints = 10;

	// increments points based on tier of power up
	public static void incrementPoints() {
		points += basePoints;

		if (PowerUps.curBoost == PowerUps.Boost.POINTS) {
			switch (PowerUps.curTier) {
			case TIER2:
				points += basePoints * 2;
				break;
			case TIER3:
				points += basePoints * 5;
				break;
			default:
				break;
			}
		}
		b.displayMessage("Points: " + points);
	}
	
	public static void resetPoints() {
		points = 0;
	}

}
