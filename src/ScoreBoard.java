import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Peter Frach 
 * Stores the results of games
 * Displays the highest score
 */

public class ScoreBoard extends SpaceShooter_Main {

	static String filePath1 = "scoreboard";
	static FileWriter fw1;
	static BufferedWriter bw1;
	static BufferedReader br1;
	static String line1;
	static String[] arr1;
	static String scores;
	static String finalScores;
	static String[] rndScores;
	static int[] s;
	
	static int greatestScore = 0;
	static String bestName = null;

	//Adds the current score to the record
	public static void addScore() throws IOException {
		finalScores = "";

		br1 = new BufferedReader(new FileReader(filePath1));
		line1 = br1.readLine();

		fw1 = new FileWriter(filePath1);
		bw1 = new BufferedWriter(fw1);

		scores = line1 + " " + name + ": " + Points.points + ","; // to re-copy previous scores 

		bw1.write(scores);
		bw1.close();

		readScores();
	}

	// puts the existing scores into an array
	public static void readScores() throws IOException {
		arr1 = scores.split(",", 0);
		br1.close();
		sort();
	}

	// Sorts the scores to find the highest one
	public static void sort() {
		
		/*
		 * puts all of the points into an int array
		 * separates points from names
		 */
		rndScores = new String[arr1.length];
		s = new int[arr1.length];
		for (int i = 0; i < arr1.length; i++) {
			arr1[i] = arr1[i].trim();
			rndScores[i] = arr1[i].substring(arr1[i].indexOf(":") + 1);
			rndScores[i] = rndScores[i].trim();
			s[i] = Integer.valueOf(rndScores[i]);
		}
		
		// high score is the only score
		if (s.length == 1) {
			greatestScore = s[0];
			bestName = arr1[0];
		}
		
		// finds the highest score and saves it
		for (int a = 0; a < s.length - 1; a++) {
			if (s[a] < s[a+1]) {
				greatestScore = s[a+1];
				bestName = arr1[a+1];
			}
		}
		
		// formats the scores to display them
		for (int i = 0; i < arr1.length; i++) {
			finalScores += arr1[i] + "\n";
		}
	}
	
	public static void resetGameScores() {
		finalScores = "";
		greatestScore = 0;
		bestName = "";
	}
}