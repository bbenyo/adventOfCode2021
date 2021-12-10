package bb.aoc2021.handler;

import org.apache.log4j.Logger;

import bb.aoc2021.InputHandler;
import bb.aoc2021.Utilities;

public class Day7 implements InputHandler {
	static private Logger logger = Logger.getLogger(Day7.class.getName());
	
	int crabs[];
	
	@Override
	public void handleInput(String line) {
		crabs = Utilities.stringListToInts(line, ",");
	}
	
	protected int computeFuelUsage(int median) {
		int fuelUsed = 0;
		for (int i=0; i<crabs.length; ++i) {
			fuelUsed += Math.abs(crabs[i] - median);
		}
		return fuelUsed;
	}

	
	@Override
	public void output() {
		int median = Utilities.median(crabs);
		int fuel = computeFuelUsage(median);
		logger.info("Median value: "+median+" fuelUsage: "+fuel);
	}

}
