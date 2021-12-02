package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;


public class Day2 implements InputHandler {
	
	static private Logger logger = Logger.getLogger(Day2.class.getName());
	
	int hPos = 0;
	int depth = 0;
	
	@Override
	public void handleInput(String line) {
		String[] cmd = line.split(" ");
		if (cmd.length != 2) {
			logger.error("Unable to parse input: "+line);
			return;
		}
		String direction = cmd[0].trim().toLowerCase();
		Integer distance = null;
		try {
			distance = Integer.parseInt(cmd[1]);
		} catch (NumberFormatException ex) {
			logger.error(ex.toString(), ex);
			return;
		}
		
		switch(direction) {
		case "forward":
			hPos += distance; break;
		case "down":
			depth += distance; break;
		case "up":
			depth -= distance; break;
		default:
			logger.error("Unknown command: "+direction);
		}
		
		if (depth < 0) {
			depth = 0;
			logger.error("Submaring is going into the air!");
		}
		
	}

	@Override
	public void output() {
		logger.info("Horizontal Distance: "+hPos);
		logger.info("Depth: "+depth);	
		logger.info("Product: "+(hPos * depth));
	}

}
