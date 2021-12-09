package bb.aoc2021.handler;

import org.apache.log4j.Logger;

public class Day6b extends Day6 {
	static private Logger logger = Logger.getLogger(Day6b.class.getName());
	
	@Override
	public void output() {
		simulate(256);
		logger.info("Final Lantern fish count after 256 days: "+countFish());
	}	
}
