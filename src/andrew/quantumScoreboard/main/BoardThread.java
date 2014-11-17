package andrew.quantumScoreboard.main;

public class BoardThread extends Thread implements Runnable {
	
	public static int UPDATES_PER_SECOND = 50;
	public static int FRAMES_PER_SECOND = 100;
	public static long MAX_FRAMESKIP = 5;
	
	@Override
	public void run() {
		long skipTicks = 1000 / UPDATES_PER_SECOND;
		long skipFrames = 1000 / FRAMES_PER_SECOND;
		
		long nextTick = System.currentTimeMillis();
		long nextFrame = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		
		int loops;
		
		int frames = 0;
		int updates = 0;
		
		while (true) {
			loops = 0;
			while (System.currentTimeMillis() > nextTick && loops < MAX_FRAMESKIP) {
				Main.SCOREBOARD.update();
				
				nextTick += skipTicks;
				updates++;
				loops++;
			}
			
			if (System.currentTimeMillis() > nextFrame) {
				nextTick += skipFrames;
				Main.SCOREBOARD.screen.repaint();
				frames++;
			}
			
			if (time + 1000 <= System.currentTimeMillis()) {
				time += 1000;
				
				Main.SCOREBOARD.fps = frames;
				Main.SCOREBOARD.ups = updates;
				updates = frames = 0;
			}
		}
	}
}
