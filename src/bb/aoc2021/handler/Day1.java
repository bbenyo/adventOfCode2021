package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;

public class Day1 implements InputHandler {

	static private Logger logger = Logger.getLogger(Day1.class.getName());
	
	int increases;
	int prev = -1;
	
	@Override
	public void handleInput(String line) {
		try {
			Integer depth = Integer.parseInt(line);
			if (prev != -1 && (depth > prev)) {
				increases++;
			}
			prev = depth;
		} catch (Exception ex) {
			logger.error(ex.toString(), ex);
		}

	}

	@Override
	public void output() {
		logger.info("Increases: "+increases);
	}

}
