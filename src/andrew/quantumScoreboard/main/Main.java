package andrew.quantumScoreboard.main;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
	
	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;
	
	public static int WINDOW_WIDTH;
	public static int WINDOW_HEIGHT;
	
	public static ScoreBoard SCOREBOARD;
	
	static State state;
	
	public static void main(String[] args) {
		state = State.SCORING;
		
		// Update GUI correctly
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Main();
			}
		});
	}
	
	public Main() {
		SCOREBOARD = new ScoreBoard();
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (SCREEN_WIDTH == 0)
			SCREEN_WIDTH = toolkit.getScreenSize().width;
		if (SCREEN_HEIGHT == 0)
			SCREEN_HEIGHT = toolkit.getScreenSize().height;
		
		JFrame frame = new JFrame();
		frame.setTitle("Quantum Pool Scoreboard");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		frame.setVisible(true);
		WINDOW_WIDTH = frame.getWidth();
		WINDOW_HEIGHT = frame.getHeight();
		SCOREBOARD.screen = new Screen();
		SCOREBOARD.start();
		
		// Frame Listeners
		frame.addKeyListener(new ConsoleMan());
		
		// Screen
		frame.add(SCOREBOARD.screen);
	}
	
	public enum State {
		SCORING, NAMES, GAME, FINISHED;
	}
	
}
