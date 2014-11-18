package andrew.quantumScoreboard.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import andrew.quantumScoreboard.main.Main.State;

public class ConsoleMan implements KeyListener {
	
	private static String message = "";
	private static int player = 0;
	
	public static void setInstr() {
		switch (Main.state) {
		case SCORING:
			Screen.setInstr("Enter scoring method; H for Horizontal Probability or V for Vertical Probability.");
			break;
		case NAMES:
			Screen.setInstr("Name for Player " + (player + 1) + ":");
			break;
		case GAME:
			Screen.setInstr("If a ball goes into a pocket, type that number and press enter. Otherwise, press enter to end your turn.");
			break;
		default:
			break;
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		switch (Main.state) {
		case SCORING:
			if (Character.toUpperCase(e.getKeyChar()) == 'H') {
				message = "H";
				Screen.setUMessage(message);
			} else if (Character.toUpperCase(e.getKeyChar()) == 'V') {
				message = "V";
				Screen.setUMessage(message);
			}
			break;
		case NAMES:
			if (Character.isLetter(e.getKeyChar()) ||
					Character.isDigit(e.getKeyChar()) ||
					e.getKeyChar() == ' ') {
				message += e.getKeyChar();
				Screen.setUMessage(message);
			}
			break;
		case GAME:
			if (Character.isDigit(e.getKeyChar())) {
				message += e.getKeyChar();
				Screen.setUMessage(message);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			message = message.substring(0, message.length() - 1);
			Screen.setUMessage(message);
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			switch (Main.state) {
			case SCORING:
				if (message == "H") {
					ScoreBoard.setScoringMethod(ScoreBoard.Mode.HP);
					resetMessage();
					Main.state = Main.State.NAMES;
				} else if (message == "V") {
					ScoreBoard.setScoringMethod(ScoreBoard.Mode.VP);
					resetMessage();
					Main.state = Main.State.NAMES;
				}
				break;
			case NAMES:
				ScoreBoard.setName(player, message);
				resetMessage();
				if (player == 4) {
					Main.state = State.GAME;
				} else {
					player++;
				}
				break;
			case GAME:
				if (message.length() != 0 && message != null) {
					int ball = Integer.valueOf(message);
					if (ball >= 1 && ball <= 15) {
						ScoreBoard.pocket(ball);
					}
				} else {
					ScoreBoard.nextPlayer();
				}
				resetMessage();
				break;
			case FINISHED:
				
				break;
			}
		}
	}
	
	private void resetMessage() {
		message = "";
		Screen.setUMessage(message);
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
}