package andrew.quantumScoreboard.main;

public class ScoreBoard {
	
	public int fps;
	public int ups;
	public Screen screen;
	
	private static int currentPlayer = 1;
	private static String[] names = new String[7];
	private static boolean[] pocketed = new boolean[15];
	
	private static boolean[][] playerPocketed = new boolean[5][5];
	private static Mode scoringMethod;
	
	public BoardThread thread;
	
	public ScoreBoard() {
		thread = new BoardThread();
		
		for (int x = 0; x < 15; x++) {
			pocketed[x] = false;
		}
	}
	
	public void update() {
		
	}
	
	public void start() {
		thread.start();
	}
	
	public static void nextPlayer() {
		if (ScoreBoard.currentPlayer == 5) {
			ScoreBoard.currentPlayer = 1;
		} else {
			ScoreBoard.currentPlayer++;
		}
		if (ScoreBoard.calculateScoreInverse(scoringMethod)[ScoreBoard.currentPlayer - 1] == 0) {
			nextPlayer();
		}
	}
	
	public boolean checkGameEnd() {
		int groupsLeft = 5;
		for (int group = 0; group < 5; group++) {
			if (isGroupOut(group)) {
				groupsLeft--;
			}
		}
		
		if (groupsLeft == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isGroupOut(int group) {
		return pocketed[group * 3] && pocketed[group * 3 + 1] && pocketed[group * 3 + 2];
	}
	
	private static void pocket(int player, int ball) {
		if (!getPocketed(ball)) {
			pocketed[ball - 1] = true;
			playerPocketed[(ball - 1) / 3][player - 1] = true;
		}
	}
	
	public static void pocket(int ball) {
		pocket(ScoreBoard.getCurrentPlayer(), ball);
	}
	
	public static int[] calculateScoreInverse(Mode mode) {
		if (mode == null) {
			return null;
		}
		switch (mode) {
		case HP:
			int[] scoresHP = new int[5];
			
			for (int player = 0; player < 5; player++) {
				
				boolean winning = false;
				int possGroup = 0;
				
				for (int group = 0; group < 5; group++) {
					
					if (!isGroupOut(group) && !playerPocketed[group][player]) {
						winning = true;
					}
					
					if (!playerPocketed[group][player]) {
						possGroup++;
					}
				}
				
				scoresHP[player] = winning ? possGroup : 0;
			}
			return scoresHP;
		case VP:
			int[] scoresVP = new int[5];
			boolean[] winners = new boolean[5];
			int scorePer = 0;
			
			for (int player = 0; player < 5; player++) {
				
				for (int group = 0; group < 5; group++) {
					if (!isGroupOut(group) && !playerPocketed[group][player]) {
						winners[player] = true;
						scorePer++;
					}
				}
			}
			
			for (int player = 0; player < 5; player++) {
				if (winners[player]) {
					scoresVP[player] = scorePer;
				}
			}
			
			return scoresVP;
		case RP:
			return null;
		}
		return null;
	}
	
	public static int getCurrentPlayer() {
		return currentPlayer;
	}
	
	public static void setName(int player, final String name) {
		names[player + 1] = name;
	}
	
	/**
	 * 
	 * @param player
	 *            ONE-INDEXED player number
	 * @return the name of the indicated player.
	 */
	public static String getName(int player) {
		return names[player - 1];
	}
	
	public static String[] getNames() {
		return names;
	}
	
	/**
	 * 
	 * @param player
	 *            ONE-INDEXED player number
	 * @param group
	 *            ONE-INDEXED group number
	 * @return true if the indicated player has sunk a ball in the indicated
	 *         group.
	 */
	public static boolean getPlayerPocketed(int player, int group) {
		return playerPocketed[group - 1][player - 1];
	}
	
	/**
	 * 
	 * @param ball
	 *            Number written on the ball (i.e. ONE-INDEXED ball number)
	 * @return true if the indicated ball has been sunk.
	 */
	public static boolean getPocketed(int ball) {
		return pocketed[ball - 1];
	}
	
	public static void setScoringMethod(Mode mode) {
		scoringMethod = mode;
	}
	
	public static Mode getScoringMethod() {
		return scoringMethod;
	}
	
	public static void checkCompletion() {
		for (int player = 0; player < 5; player++) {
			int left = 5;
			int lastLeft = 0;
			for (int group = 0; group < 5; group++) {
				if (playerPocketed[group][player]) {
					left--;
				} else {
					lastLeft = group;
				}
			}
			
			if (left == 1) {
				for (int p = 0; p < 5; p++) {
					if (p != player) {
						playerPocketed[lastLeft][p] = true;
					}
				}
			}
		}
	}
	
	public static boolean checkEndOfGame() {
		int alive = 5;
		
		for (int group = 0; group < 5; group++) {
			if (!isGroupOut(group)) {
				alive--;
			}
		}
		
		if (alive == 1)
			return true;
		else
			return false;
	}
	
	public static enum Mode {
		HP, VP, RP;
	}
}
