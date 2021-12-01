package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day1b implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day1b.class.getName());
	
	int m1 = -1;
	int m2 = -1;
	int m3 = -1;
	
	int curWindow = -1;
	int prevWindow = -1;
	
	int increases = 0;
	
	@Override
	public void handleInput(String line) {
		try {
			Integer depth = Integer.parseInt(line);
			if (m1 == -1) {
				m1 = depth;
			} else if (m2 == -1) {
				m2 = depth;
			} else {
				m3 = depth;
			}
			if (m3 != -1) {
				curWindow = m1 + m2 + m3;
				m1 = m2;
				m2 = m3;
			}
			if (prevWindow != -1 && curWindow != -1 && curWindow > prevWindow) {
				increases++;
			}
			if (curWindow != -1) {
				prevWindow = curWindow;
			}
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}
	}

	@Override
	public void output() {
		logger.info("Increases: "+increases);
	}

}
