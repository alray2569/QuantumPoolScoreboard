package andrew.quantumScoreboard.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Screen extends JPanel {
	
	private static final long serialVersionUID = -388857311692367268L;
	
	private static final int XBORDER = 10;
	private static final int YBORDER = 10;
	private static final int MESSAGEBOX_HEIGHT = 100;
	private static final char CURSOR = '_';
	
	private static int tableWidth = Main.WINDOW_WIDTH - 2 * XBORDER;
	private static int tableHeight = Main.WINDOW_HEIGHT - 2 * YBORDER - MESSAGEBOX_HEIGHT;
	private static int cellWidth = tableWidth / 7;
	private static int cellHeight = tableHeight / 6;
	
	private static String umessage = String.valueOf(CURSOR);
	private static String instr;
	
	private static Screen.Cell[][] cells = new Screen.Cell[7][6];
	private static String[][] cellText = new String[7][6];
	
	public static void setUMessage(final String str) {
		umessage = str + CURSOR;
	}
	
	public static void setInstr(String instr) {
		Screen.instr = instr;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		
		render(g);
	}
	
	private void render(Graphics g) {
		
		cellText[0] = ScoreBoard.getNames();
		
		cellText[0][0] = "Player";
		cellText[6][0] = "Score";
		
		this.drawNumbers(g);
		
		ScoreBoard.checkCompletion();
		
		if (Main.state == Main.State.GAME) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(XBORDER, YBORDER + ScoreBoard.getCurrentPlayer() * cellHeight, tableWidth, cellHeight);
			g.setColor(Color.BLACK);
		}
		
		if (ScoreBoard.getScoringMethod() != null) {
			int[] scores = ScoreBoard.calculateScoreInverse(ScoreBoard.getScoringMethod());
			for (int x = 0; x < 5; x++) {
				cellText[6][x + 1] = (scores[x] != 0 && scores[x] != 1
						? "1/"
						: "") + String.valueOf(scores[x]);
				if (scores[x] == 0) {
					g.setColor(Color.getHSBColor(7.7f / 360f, .5804f, 1f));
					g.fillRect(XBORDER, YBORDER + (x + 1) * cellHeight, tableWidth, cellHeight);
					g.setColor(Color.BLACK);
				} else if (scores[x] == 1) {
					g.setColor(Color.getHSBColor(138.37f / 360f, .7034f, .8196f));
					g.fillRect(XBORDER, YBORDER + (x + 1) * cellHeight, tableWidth, cellHeight);
					g.setColor(Color.BLACK);
				}
			}
		}
		
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				cells[x][y] = new Cell(XBORDER + x * cellWidth, YBORDER + y * cellHeight, cellWidth, cellHeight);
				cells[x][y].render(g);
				cells[x][y].drawText(g, cellText[x][y]);
			}
		}
		
		drawCrosses(g);
		
		ConsoleMan.setInstr();
		this.drawInstr(g, instr);
		this.drawUMessage(g, umessage);
	}
	
	private void drawCrosses(Graphics g) {
		for (int player = 1; player <= 5; player++) {
			for (int group = 1; group <= 5; group++) {
				if (ScoreBoard.getPlayerPocketed(player, group)) {
					cells[group][player].drawCross(g);
				}
			}
		}
	}
	
	private void drawInstr(Graphics g, final String text) {
		Font font = Font.decode("consolas-24");
		g.setFont(font);
		if (text != null) {
			g.drawString(text, XBORDER, YBORDER * 2 + tableHeight + Cell.TEXTMARGIN_VERT + g.getFontMetrics().getHeight());
		}
	}
	
	private void drawUMessage(Graphics g, final String text) {
		Font font = Font.decode("consolas-24");
		g.setFont(font);
		if (text != null) {
			g.drawString(text, XBORDER, YBORDER * 2 + tableHeight + Cell.TEXTMARGIN_VERT + g.getFontMetrics().getHeight() * 2);
		}
	}
	
	private void drawNumbers(Graphics g) {
		Font font = Font.decode("consolas-48");
		g.setFont(font);
		for (int x = 0; x < 15; x++) {
			g.setColor(ScoreBoard.getPocketed(x + 1)
					? Color.LIGHT_GRAY
					: Color.BLACK);
			g.drawString(String.valueOf(x + 1), XBORDER + cellWidth + (x * cellWidth / 3) + Cell.TEXTMARGIN_HORIZ, YBORDER + Cell.TEXTMARGIN_VERT + g.getFontMetrics().getHeight());
		}
	}
	
	class Cell {
		
		private final int left, top, width, height;
		
		private static final int TEXTMARGIN_HORIZ = 2;
		private static final int TEXTMARGIN_VERT = -15;
		
		public Cell(int left, int top, int width, int height) {
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}
		
		public void render(Graphics g) {
			g.drawRect(this.left, this.top, this.width, this.height);
		}
		
		public void drawCross(Graphics g) {
			g.drawLine(this.left, this.top, this.left + this.width, this.top + this.height);
			g.drawLine(this.left + this.width, this.top, this.left, this.top + this.height);
		}
		
		public void drawText(Graphics g, final String text) {
			Font font = Font.decode("consolas-48");
			g.setFont(font);
			int y = this.top + TEXTMARGIN_VERT;
			if (text != null) {
				String[] formatted;
				formatted = format(g, text, cellWidth - 2 * TEXTMARGIN_HORIZ, cellHeight - 2 * TEXTMARGIN_VERT);
				for (String row : formatted) {
					g.getFontMetrics().getHeight();
					y += g.getFontMetrics().getHeight();
					if (row != null) {
						g.drawString(row, this.left + TEXTMARGIN_HORIZ, y);
					}
				}
			}
		}
		
		private String[] format(Graphics g, final String text, int maxWidth, int maxHeight) {
			int rows = maxHeight / g.getFontMetrics().getHeight();
			String out[] = new String[rows];
			out[0] = text;
			for (int row = 0; row < rows; row++) {
				if (out[row] == null) {
					continue;
				}
				if (out[row].contains("\n")) {
					try {
						out[row + 1] = out[row].substring(out[row].indexOf('\n'));
					} catch (IndexOutOfBoundsException e) {
					} finally {
						out[row] = out[row].substring(0, out[row].indexOf('\n'));
					}
				}
				while (g.getFontMetrics().stringWidth(out[row]) > maxWidth) {
					try {
						if (out[row].contains(" ")) {
							if (out[row + 1] != null) {
								out[row + 1] = out[row].substring(out[row].lastIndexOf(' ') + 1) + ' ' + out[row + 1];
							} else {
								out[row + 1] = out[row].substring(out[row].lastIndexOf(' ') + 1);
							}
						} else {
							if (out[row + 1] != null) {
								out[row + 1] = out[row].charAt(out[row].length() - 1) + out[row + 1];
							} else {
								out[row + 1] = String.valueOf(out[row].charAt(out[row].length() - 1));
							}
						}
					} catch (IndexOutOfBoundsException e) {
					} finally {
						if (out[row].contains(" ")) {
							out[row] = out[row].substring(0, out[row].lastIndexOf(' '));
						} else {
							out[row] = out[row].substring(0, out[row].length() - 1);
						}
					}
					
				}
			}
			return out;
		}
	}
}