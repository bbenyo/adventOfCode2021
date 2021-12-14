package bb.aoc2021.handler;

import org.apache.log4j.Logger;

public class Day11b extends Day11 {
	static private Logger logger = Logger.getLogger(Day11b.class.getName());
	
	int lastStep = 0;
	
	@Override
	protected void simulate(int steps) {
		logger.info("Simulating until everyone flashes");
		
		int i = 1;
		while (true && i < 100000) { // give up after 100k
			flashes = 0;
			simulateNextStep();
			logger.info("After "+i+" steps, flashes: "+flashes);
			if (flashes == 100) {
				// 10x10, everyone flashed
				lastStep = i;
				break;
			}
			i++;
		}		
		printGrid(lastStep);
	}
	
	@Override
	public void output() {
		simulate(0);
		logger.info("Flashes: "+flashes);
		logger.info("Last Step: "+lastStep);
	}
	
}
