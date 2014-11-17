package andrew.quantumScoreboard.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ScoreBoard {
	
	public int fps;
	public int ups;
	public Screen screen;
	
	private static boolean[] pocketed = new boolean[15];
	
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
	
	public static class Screen extends JPanel {
		
		private static final long serialVersionUID = -388857311692367268L;
		
		private static final int XBORDER = 10;
		private static final int YBORDER = 10;
		private static final int MESSAGEBOX_HEIGHT = 100;
		private static int tableWidth = Main.WINDOW_WIDTH - 2 * XBORDER;
		private static int tableHeight = Main.WINDOW_HEIGHT - 2 * YBORDER - MESSAGEBOX_HEIGHT;
		private static int cellWidth = tableWidth / 7;
		private static int cellHeight = tableHeight / 6;
		private static String[] names = new String[7];
		private static String instr;
		private static Cell[][] cells = new Cell[7][6];
		private static String[][] cellText = new String[7][6];
		private static boolean[] pocketed = new boolean[15];
		private static boolean[][] playerPocketed = new boolean[5][5];
		private static int currentPlayer;
		
		public static void nextPlayer() {
			if (ScoreBoard.Screen.currentPlayer == 5) {
				ScoreBoard.Screen.currentPlayer = 1;
			} else {
				ScoreBoard.Screen.currentPlayer++;
			}
		}
		
		public static void setInstr(String instr) {
			ScoreBoard.Screen.instr = instr;
		}
		
		public static void setName(int player, final String name) {
			names[player + 2] = name;
		}
		
		public static void pocket(int ball) {
			pocket(ScoreBoard.Screen.currentPlayer, ball);
		}
		
		private static void pocket(int player, int ball) {
			pocketed[ball - 1] = true;
			playerPocketed[player][ball / 3] = true;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.clearRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
			
			render(g);
		}
		
		private void render(Graphics g) {
			
			cellText[0] = names;
			
			cellText[0][0] = "Player";
			cellText[6][0] = "Score\nHP   VP";
			
			this.drawNumbers(g);
			
			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 6; y++) {
					cells[x][y] = new Cell(XBORDER + x * cellWidth, YBORDER + y * cellHeight, cellWidth, cellHeight);
					cells[x][y].render(g);
					cells[x][y].drawText(g, cellText[x][y]);
				}
			}
			
			this.drawInstr(g, instr);
		}
		
		private void drawInstr(Graphics g, final String text) {
			Font font = Font.decode("consolas-24");
			g.setFont(font);
			if (text != null) {
				g.drawString(text, XBORDER, YBORDER * 2 + tableHeight + Cell.TEXTMARGIN_VERT + g.getFontMetrics().getHeight());
			}
		}
		
		private void drawNumbers(Graphics g) {
			Font font = Font.decode("consolas-48");
			g.setFont(font);
			for (int x = 0; x < 15; x++) {
				g.setColor(pocketed[x] ? Color.LIGHT_GRAY : Color.BLACK);
				g.drawString(String.valueOf(x + 1), XBORDER + cellWidth + (x * cellWidth / 3) + Cell.TEXTMARGIN_HORIZ, YBORDER + Cell.TEXTMARGIN_VERT + g.getFontMetrics().getHeight());
			}
		}
		
		private class Cell {
			
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
									out[row + 1] = out[row].substring(out[row].lastIndexOf(' ') + 1) + out[row + 1];
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
}
